package sy.iyad.server;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sy.e.server.R;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ConnectionEventListener;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.mikrotik.Utils.Api;
import sy.iyad.server.Activities.MainActivity;
import sy.iyad.server.FirebaseUtils.Token;
import sy.iyad.server.Utils.ServerInfo;

import static android.os.Build.VERSION_CODES.Q;
import static sy.iyad.server.ServerInformations.ADMIN;
import static sy.iyad.server.ServerInformations.ETHERSNAME_COMMAND;
import static sy.iyad.server.ServerInformations.IP;
import static sy.iyad.server.ServerInformations.PASSWORD;
import static sy.iyad.server.Utils.ServerInfo.ETHER;
import static sy.iyad.server.Utils.ServerInfo.VIP;
import static sy.iyad.server.Utils.ServerInfo.isRegistered;


public class LoginActivity extends AppCompatActivity {


    public static final int REQUEST_CODE = 201;
    public static final String ACTION_LOGIN = "AcXFU2M0842021";
    public static final String ACTION_RUN = "ArXFU2M0842021";

    EditText admin,ip,pass;
    Button login;
    TextView textView;
    SwitchMaterial switchMaterial;

    FloatingActionButton actionButton,registerToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isRegistered) {
            registerId();
        }
        Intent intent = getIntent();
        if (intent.getAction().equals(ACTION_LOGIN)){
            doLogin(intent.getStringExtra("ip"),intent.getStringExtra("admin"),intent.getStringExtra("password"),ACTION_RUN);
        }

        setContentView( R.layout.activity_login);


            login =  findViewById(R.id.connect);
            ip =  findViewById(R.id.ip);
            admin =  findViewById(R.id.admin);
            pass =  findViewById(R.id.password);
            textView =  findViewById(R.id.textView);
            actionButton = findViewById(R.id.floatingActionButton);
            registerToken = findViewById(R.id.reges);
            switchMaterial = findViewById(R.id.switch1);

            requerPerms();
            laodInfo();

            login.setOnClickListener(v ->  {

                    String ips = ip.getText().toString();
                    String admins = admin.getText().toString();
                    String passs = pass.getText().toString();

                    doLogin(ips, admins, passs,"manual action");

                });

            login.setOnLongClickListener(v ->  {

                    registerId();
                                             return false;
                                     });

            actionButton.setOnClickListener(v ->  {
                    ip.setText(IP);
                    admin.setText(ADMIN);
                    pass.setText(PASSWORD);

            });
            actionButton.setOnLongClickListener(v -> {
                        showHappens("your vip : " + VIP );
                        textView.setBackgroundColor(VIP);
                        return true;
                    }
            );

            registerToken.setOnClickListener(v -> {
                isRegistered = false;
                registerId();
            });

            registerToken.setOnLongClickListener(v -> {

                return true;
            });

    }

    private void requerPerms() {

        if (Build.VERSION.SDK_INT >= Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE},207);
        }
    }


    private void laodInfo() {
        ip.setText(IP);
        admin.setText(ADMIN);
        pass.setText("");
    }

    private void doLogin(@NonNull String aips, @NonNull String aadmins, String apasss, @NonNull String action) {

        MikrotikServer.connect(aips, aadmins, apasss)
        .addConnectionEventListener(new ConnectionEventListener() {
            public void onConnectionSuccess(Api api) {
                loadSetupInfo(aips,aadmins,apasss);
                showHappens("success as : "+ api.toString());
                getEtherNameAndSaveToServerInfo();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setAction(action);
                startActivity(intent);
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
                            showHappens(task.getResult());
                        } else if (!task.isSuccessful() && task.getException() != null) {
                            showHappens(task.getException().getMessage());
                            if (textView != null) {
                                showHappens("threre found some connection problem" + task.getException().getMessage());
                                textView.setText(task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    private void showHappens( String message) {

        Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(Color.RED);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM,0,0);
        toast.setMargin(getResources().getDimension(R.dimen.d5),getResources().getDimension(R.dimen.d4));
        toast.show();
    }

    @SuppressLint("SetTextI18n")
    private void loadSetupInfo(@NonNull String ip, @NonNull String username, @NonNull String password){

       // if (switchMaterial.isChecked()) {
            IP = ip;
            ADMIN = username;
            PASSWORD = password;
      //  }
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
    protected void onStart() {
        super.onStart();

        if (MikrotikServer.getApi() != null && MikrotikServer.isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (MikrotikServer.getApi() != null && MikrotikServer.isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MikrotikServer.getApi() != null && MikrotikServer.isConnected()){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
