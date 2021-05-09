package sy.iyad.server.share;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import sy.e.server.R;

import static sy.iyad.server.LoginActivity.ACTION_LOGIN;


public class SharingActivity extends AppCompatActivity {

    TextView textView;
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        textView = findViewById(R.id.shareView);

        intent = getIntent();

        String string = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (string != null ){

            textView.setText(string);
        }

        if (getIntent().getAction().equals(ACTION_LOGIN)){

            String str = intent.getStringExtra("message");

            if (str != null) {
                textView.setText(str);
            }
        }
    }
}
