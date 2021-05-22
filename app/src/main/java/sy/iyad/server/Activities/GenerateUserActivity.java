package sy.iyad.server.Activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.server.R;
import sy.iyad.server.Utils.Users;
import sy.iyad.server.Utils.UsersAdapter;
import sy.iyad.server.Utils.UsersIntRandom;

import static sy.iyad.server.ServerInformation.USER_PROFILES;


public class GenerateUserActivity extends AppCompatActivity {


    UsersAdapter usersAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ArrayList<Users> usersArrayList;

    Button button;
    EditText adet;
    EditText plength;
    EditText prefix;
    RecyclerView recyclerView;
    Spinner spinner;
    TextView textView;
    EditText ulength;
    FloatingActionButton actionButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_generator);

        button =  findViewById(R.id.genbtn);
        adet =  findViewById(R.id.adet);
        ulength =  findViewById(R.id.lengthu);
        plength =  findViewById(R.id.lengthp);
        prefix =  findViewById(R.id.prefix);
        spinner =  findViewById(R.id.spinner);
        textView =  findViewById(R.id.wa);
        recyclerView =  findViewById(R.id.listax);
        actionButton = findViewById(R.id.genact);

        init();

        loadProfiles();

        actionButton.setOnClickListener(v -> {
            try {

                int ad = Integer.parseInt(adet.getText().toString());
                int pass = Integer.parseInt(plength.getText().toString());

                createUsers(ad, Integer.parseInt(ulength.getText().toString()), prefix.getText().toString(), pass);
            } catch (NumberFormatException e) {

                textView.setText(e.getMessage());
            }
        });
        button.setOnClickListener(v ->  {
                try {

                    int ad = Integer.parseInt(adet.getText().toString());
                    int pass = Integer.parseInt(plength.getText().toString());

                    createUsers(ad, Integer.parseInt(ulength.getText().toString()), prefix.getText().toString(), pass);
                } catch (NumberFormatException e) {

                    textView.setText(e.getMessage());
            }
        });
    }
    private void init(){

        arrayList = new ArrayList<>();
        usersArrayList = new ArrayList<>();
        usersAdapter = new UsersAdapter(usersArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(usersAdapter);
        usersAdapter.notifyDataSetChanged();
        arrayAdapter  = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        spinner.setAdapter(arrayAdapter);
    }

    private void loadProfiles() {

        MikrotikServer.execute(USER_PROFILES).addExecutionEventListener(new ExecutionEventListener() {

            public void onExecutionSuccess(List<Map<String, String>> mapList) {

                for (Map<String, String> map : mapList) {

                    arrayList.add(map.get("name"));
                }
               arrayAdapter.notifyDataSetChanged();
            }

            public void onExecutionFailed(Exception exp) {
               textView.setText(exp.getMessage());
            }
        });
    }

    private void createUsers(int adet, int userLength, String prefix, int passwordLenth) {


        for (int j = 0; j < adet; j++) {

            String username = prefix + new UsersIntRandom().detectRand(userLength);
            int password = new UsersIntRandom().detectRand(passwordLenth);
            String createUser = "/tool/user-manager/user/add customer=admin username="+ username+ " password="+ password;

            MikrotikServer.execute(createUser);

            String setupProfile= "/tool/user-manager/user/create-and-activate-profile customer=admin numbers="+ username+ " profile="+ spinner.getSelectedItem().toString();

          usersArrayList.add(new Users(username, password, spinner.getSelectedItem().toString()));

            MikrotikServer.execute(setupProfile).addExecutionEventListener(new ExecutionEventListener() {
                @SuppressLint("SetTextI18n")
                public void onExecutionSuccess(List<Map<String, String>> list) {

                    textView.setText(usersAdapter.getItemCount()+"تم بنجاح عدد المستخدمين المضافين");
                    usersAdapter.notifyDataSetChanged();

                }

                public void onExecutionFailed(Exception exp) {
                    textView.setText(exp.getMessage());
                }
            });
        }
        usersAdapter.notifyDataSetChanged();
    }
}
