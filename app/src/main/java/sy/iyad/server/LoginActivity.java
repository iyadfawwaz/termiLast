package sy.iyad.server;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ConnectionEventListener;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.mikrotik.Utils.Api;
import sy.iyad.server.Activities.MainActivity;
import sy.iyad.server.FirebaseUtils.Token;
import sy.iyad.server.Utils.ServerInfo;
import static android.os.Build.VERSION_CODES.Q;
import static sy.iyad.server.ServerInformation.ADMIN;
import static sy.iyad.server.ServerInformation.ETHERSNAME_COMMAND;
import static sy.iyad.server.ServerInformation.IP;
import static sy.iyad.server.ServerInformation.PASSWORD;
import static sy.iyad.server.Utils.ServerInfo.*;


public class LoginActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 201;
    public static final int CAMERA_REQ = 202;

    public static final String ACTION_LOGIN = "AcXFU2M0842021";
    // public static final String ACTION_RUN = "ArXFU2M0842021";
    public static final String SHARED_KEY = "Sk0842021ChineScood";

    EditText admin, ip, pass;
    Button login;
    TextView textView;
    CheckBox checkBox;
    Toolbar toolbar;
    SurfaceView surfaceView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    ToneGenerator toneGenerator;

    FloatingActionButton actionButton;

    @SuppressLint("RestrictedApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        askPermissions();
        if (!isRegistered) {
            registerId();
        }
        login = findViewById(R.id.connect);
        ip = findViewById(R.id.ip);
        admin = findViewById(R.id.admin);
        pass = findViewById(R.id.password);
        textView = findViewById(R.id.textView);
        actionButton = findViewById(R.id.floatingActionButton);
        checkBox = findViewById(R.id.checkBox);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setCollapseIcon(R.drawable.avatarx);

        surfaceView = findViewById(R.id.surfaceView);

        toolbar.setCollapsible(true);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        laodMainInfo();
      requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQ);
        initialiseDetectorsAndSources();

        login.setOnClickListener(v -> {

            String ips = ip.getText().toString();
            String admins = admin.getText().toString();
            String passs = pass.getText().toString();

            doLogin(ips, admins, passs);

        });

        login.setOnLongClickListener(v -> {

            registerId();
            return true;
        });

        actionButton.setOnClickListener(v -> {
            ip.setText(loadInfo("ip"));
            admin.setText(loadInfo("admin"));
            pass.setText(loadInfo("password"));

        });
        actionButton.setOnLongClickListener(v -> {
                    showHappens("your vip : " + VIP);
                    textView.setBackgroundColor(VIP);
                    isRegistered = false;
                    registerId();
                    return true;
                }
        );

    }

    private void askPermissions() {

        if (Build.VERSION.SDK_INT >= Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 207);
        }
    }


    private void initialiseDetectorsAndSources() {

        toneGenerator = new ToneGenerator(AudioManager.AUDIOFOCUS_GAIN,100);
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(holder);
                    } else {
                        ActivityCompat.requestPermissions(LoginActivity.this, new
                                String[]{Manifest.permission.CAMERA}, CAMERA_REQ);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    textView.post((Runnable) () -> {

                        if (barcodes.valueAt(0).url != null) {
                            textView.removeCallbacks(null);
                            String barcodeData = barcodes.valueAt(0).url.url;
                            textView.setText(barcodeData);

                            toneGenerator.startTone(ToneGenerator.TONE_SUP_PIP, 150);
                        } else {

                            String barcodeData = barcodes.valueAt(0).displayValue;
                            textView.setText(barcodeData);
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                        }
                    });

                }
            }
        });
    }


    private void laodMainInfo() {
        ip.setText(IP);
        admin.setText(ADMIN);
        pass.setText("");
    }

    private void doLogin(@NonNull String ipAddress, @NonNull String adminUsername, String adminPassword) {

        MikrotikServer.connect(ipAddress, adminUsername, adminPassword)
        .addConnectionEventListener(new ConnectionEventListener() {
            public void onConnectionSuccess(Api api) {
                saveInfoPref(ipAddress,adminUsername,adminPassword);
                showHappens("success as : "+ api.toString());
                getEtherNameAndSaveToServerInfo();
                SKIPPING = "manual action";
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setAction(SKIPPING);
                startActivity(intent);
                finish();
            }

            public void onConnectionFailed(@NonNull Exception exp) {
                if (textView != null) {
                    textView.setText(exp.getMessage());
                }
                showHappens(exp.getMessage());
            }
        });
    }

    private void getEtherNameAndSaveToServerInfo() {
        ArrayList<String> localArrayList = new ArrayList<>();
        MikrotikServer.execute(ETHERSNAME_COMMAND).addExecutionEventListener(
                new ExecutionEventListener() {
                    @Override
                    public void onExecutionSuccess(List<Map<String, String>> mapList) {

                        for (Map<String,String> map : mapList){
                           localArrayList.add(map.get("name"));
                        }

                        for (int i=0;i<localArrayList.size();i++) {
                            ETHER[i] = localArrayList.get(i);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onExecutionFailed(Exception exception) {

                        textView.setText("failed saving : "+exception.getMessage());
                        showHappens("failed to execute saving cmd ..."+exception.getMessage());
                        getEtherNameAndSaveToServerInfo();
                    }
                }
        );
    }

    public void registerId() {

        if (!isRegistered) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ServerInfo.tokenReference.push().setValue(new Token(task.getResult()));
                            ServerInfo.isRegistered = true;
                            if (textView != null){
                                textView.setText(task.getResult());
                            }
                            showHappens(task.getResult());
                        } else if (!task.isSuccessful() && task.getException() != null) {
                            showHappens(task.getException().getMessage());
                            if (textView != null) {
                                textView.setText(task.getException().getMessage());
                            }
                            showHappens("threre found some connection problem" + task.getException().getMessage());
                        }
                    });
        }
    }

    private void showHappens(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private @NonNull String loadInfo(@NonNull String key){
      SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_KEY,MODE_PRIVATE);
      return sharedPreferences.getString(key,null);
    }

    private void saveInfo(@NonNull String key,@NonNull String value){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_KEY,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    private void saveInfoPref(@NonNull String ip, @NonNull String username, @NonNull String password){

            IP = ip;
            ADMIN = username;
        saveInfo("ip",ip);
        saveInfo("admin",username);
        if (checkBox.isChecked()) {
            PASSWORD = password;
            saveInfo("password",password);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ip",ip);
            jsonObject.put("username",username);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            textView.setText("json : "+e.getMessage());
            showHappens("json : "+e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dele) {
            deleteSaved();
        }
        return true;
    }

    private void deleteSaved() {
        getSharedPreferences(SHARED_KEY,MODE_PRIVATE).edit()
                .clear().apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MikrotikServer.getApi() != null && MikrotikServer.getApi().isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (MikrotikServer.getApi() != null && MikrotikServer.getApi().isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (MikrotikServer.getApi() != null && MikrotikServer.getApi().isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
