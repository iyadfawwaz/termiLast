package sy.iyad.server.share;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sy.e.server.R;

import static sy.iyad.server.LoginActivity.ACTION_LOGIN;


public class SharingActivity extends AppCompatActivity {

    TextView textView;
    Intent intent;
    VideoView videoView;
    ArrayAdapter<String> arrayAdapter;

    FloatingActionButton actionButton;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        textView = findViewById(R.id.shareView);
        videoView = findViewById(R.id.videoView);
        actionButton = findViewById(R.id.floatingActionButton2);
        intent = getIntent();

        String string = intent.getStringExtra(Intent.EXTRA_TEXT);
       // string.substring(string.indexOf("https")-,string.length());
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,pullLinks(string));
        arrayAdapter.notifyDataSetChanged();

        /*
        for (String str : extractUrls(string)) {
            Log.e("err",str);
        }
        String path = string.trim();

        Uri uri = intent.getData();
        if (string != null ){

/*

         */
            textView.setText(string);
            /*
        }


         */

        if (getIntent().getAction().equals(ACTION_LOGIN)){

            String str = intent.getStringExtra("message");

            if (str != null) {
                textView.setText(str);
            }
        }
        actionButton.setOnClickListener(v -> download(pullLinks(string).get(0)));
    }


    public ArrayList<String> pullLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while(m.find())
        {
            String urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }

            links.add(urlStr);
        }

        return links;
    }

    private void download(String path) {
        Downloader downloader = new Downloader();
        downloader.execute(path);
    }

    @SuppressLint("StaticFieldLeak")
    class Downloader extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(),"downloading...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            final String vidurl = strings[0];
            downloadHelper(vidurl);
            return vidurl;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            textView.setText("loading..."+values[0]+" downloaded..%");
        }

        @Override
        protected void onPostExecute(String s) {
           Toast.makeText(getApplicationContext(),"video downloaded from : "+s,Toast.LENGTH_LONG).show();
        }
    }

    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();

        String[] words = input.split("\\s+");


        Pattern pattern = Patterns.WEB_URL;
        for(String word : words)
        {
            if(pattern.matcher(word).find())
            {
                if(!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://"))
                {
                    word = "http://" + word;
                }
                result.add(word);
            }
        }

        return result;
    }

    private void downloadHelper(String vidurl) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
        String date = sd.format(new Date());
        String name = "video" + date + ".mp4";

        try {
            String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    + File.separator + "My_Video";
            File rootFile = new File(rootDir);
            rootFile.mkdir();
            URL url = new URL(vidurl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    name));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }


    }
}
