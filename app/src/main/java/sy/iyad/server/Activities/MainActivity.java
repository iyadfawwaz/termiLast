package sy.iyad.server.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sy.e.server.R;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.server.Utils.Logger;

import static sy.iyad.server.LoginActivity.ACTION_RUN;
import static sy.iyad.server.ServerInformations.CPU_COMMAND;
import static sy.iyad.server.ServerInformations.RUNNING_TRUE;
import static sy.iyad.server.ServerInformations.UPTIME_COMMAND;
import static sy.iyad.server.ServerInformations.VOLTAGE_COMMAND;
import static sy.iyad.server.Utils.ServerInfo.ETHER;
import static sy.iyad.server.Utils.ServerInfo.SKIPPING;


public class MainActivity extends AppCompatActivity {

    Button cmd;
    Button gen;
    Button loggerBtn;
    FloatingActionButton disco;

    ListView listView;

    TextView cput;
    TextView uptimet;
    TextView voltaget;

    RadioButton ether1;
    RadioButton ether10;
    RadioButton ether11;
    RadioButton ether12;
    RadioButton ether13;
    RadioButton ether2;
    RadioButton ether3;
    RadioButton ether4;
    RadioButton ether5;
    RadioButton ether6;
    RadioButton ether7;
    RadioButton ether8;
    RadioButton ether9;


    private class ReUpdateNow implements Runnable {

        String cmdx;
        String keys;
        TextView textViewx;
        boolean isRun = true;

        public ReUpdateNow(@NonNull TextView textViewx, @NonNull String cmdx, @NonNull String keys) {

            this.textViewx = textViewx;
            this.cmdx = cmdx;
            this.keys = keys;

        }

        public void run() {

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MikrotikServer.execute(cmdx).addExecutionEventListener(new ExecutionEventListener() {
                                public void onExecutionSuccess(List<Map<String, String>> mapList) {

                                    textViewx.setText(mapList.get(0).get(keys));

                                }

                                public void onExecutionFailed(Exception exp) {
                                    textViewx.setText(exp.getMessage());
                                    isRun = false;
                                }
                            });
                        }
                    });
                }
            } catch (InterruptedException e) {
                textViewx.setText(e.getMessage());
                isRun = false;
            }
        }
    }

    class TaskLeds implements Runnable {

       public TaskLeds() {
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);

                    runOnUiThread(new Runnable() {

                        public void run() {
                            turnOnOffLeds();
                        }

                    });
                } catch (InterruptedException e) {
                    cput.setText(e.getMessage());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        listView = findViewById(R.id.liste);

        cmd =  findViewById(R.id.commandact);
        gen =  findViewById(R.id.usergenact);
        loggerBtn =  findViewById(R.id.logger);
        cput =  findViewById(R.id.cpu);
        voltaget =  findViewById(R.id.voltage);
        uptimet =  findViewById(R.id.uptime);

        ether1 =  findViewById(R.id.radioButton);
        ether2 =  findViewById(R.id.radioButton2);
        ether3 =  findViewById(R.id.radioButton3);
        ether4 =  findViewById(R.id.radioButton4);
        ether5 =  findViewById(R.id.radioButton5);
        ether6 =  findViewById(R.id.radioButton6);
        ether7 =  findViewById(R.id.radioButton7);
        ether8 =  findViewById(R.id.radioButton8);
        ether9 =  findViewById(R.id.radioButton9);
        ether10 =  findViewById(R.id.radioButton10);
        ether11 =  findViewById(R.id.radioButton11);
        ether12 =  findViewById(R.id.radioButton12);
        ether13 =  findViewById(R.id.radioButton13);

        disco = findViewById(R.id.dis);

        if (SKIPPING.equals(ACTION_RUN)){
            startLogging();
        }

        disco.setOnClickListener(v ->  {

                MikrotikServer.disconnect();
                finish();
        });

        loggerBtn.setOnClickListener(v -> {
            startLogging();
        });

        cmd.setOnClickListener(v ->  {

                startActivity(new Intent(MainActivity.this, CommanderActivity.class));
        });

        gen.setOnClickListener(v ->  {

                startActivity(new Intent(MainActivity.this, GenerateUserActivity.class));
        });
    }

    private Logger logger;

    private void startLogging(){
        loadListaVariable();
        loadTask();
        loadCpu();
        loadUptime();
        getVoltage();
    }
    private void loadListaVariable(){

        logger = new Logger(this);
        logger.startRecieving();
        listView.setAdapter(Logger.arrayAdapter);
    }
    private void loadUptime(){
        ReUpdateNow reUpdateNow = new ReUpdateNow(uptimet,UPTIME_COMMAND,"uptime");
        Thread thread = new Thread(reUpdateNow);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadCpu(){
        ReUpdateNow reUpdateNow = new ReUpdateNow(cput,CPU_COMMAND,"load");
        Thread thread = new Thread(reUpdateNow);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTask() {

       TaskLeds taskLeds = new TaskLeds();
        Thread thread = new Thread(taskLeds);
        thread.setDaemon(true);
        thread.start();

    }

    protected void getVoltage() {

        MikrotikServer.execute(VOLTAGE_COMMAND).addExecutionEventListener(new ExecutionEventListener() {
            public void onExecutionSuccess(List<Map<String, String>> mapList) {
               voltaget.setText( mapList.get(0).get("voltage"));

            }

            public void onExecutionFailed(Exception exp) {
               voltaget.setText(exp.getMessage());
            }
        });
    }

    private void checkarsin(RadioButton radioButton, boolean isChecked) {
        if (isChecked) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }
    }

    private void turnOnOffLeds() {

        boolean[] booleans = new boolean[13];
        RadioButton[] radioButtons = new RadioButton[]{
                ether1,ether2,ether3,ether4,ether5
                        ,ether6,ether7,ether8,ether9,
            ether10,ether11,ether12,ether13
        };
        MikrotikServer.execute(RUNNING_TRUE).addExecutionEventListener(new ExecutionEventListener() {
            public void onExecutionSuccess(List<Map<String, String>> mapList) {

                for (Map<String,String> map : mapList){

                    for (int i=0;i<12;i++){
                        booleans[i] = map.containsValue(ETHER[i]);
                        if (booleans[i]){
                            checkarsin(radioButtons[i],true);
                        }else {
                            checkarsin(radioButtons[i],false);
                        }
                    }
                    /*
                    booleans[1]  = map.containsValue(ETHER[1]);
                    boolean e2 = map.containsValue(ETHER[2]);
                    boolean e3 = map.containsValue(ETHER[3]);
                    boolean e4 = map.containsValue(ETHER[4]);
                    boolean e5 = map.containsValue(ETHER[5]);
                    boolean e6 = map.containsValue(ETHER[6]);
                    boolean e7 = map.containsValue(ETHER[7]);
                    boolean e8 = map.containsValue(ETHER[8]);
                    boolean e9 = map.containsValue(ETHER[9]);
                    boolean e10 = map.containsValue(ETHER[10]);
                    boolean e11 = map.containsValue(ETHER[11]);
                    boolean e12 = map.containsValue(ETHER[12]);
                    boolean e13 = map.containsValue(ETHER[13]);

                     */
                    /*
                    Iterator<Map<String, String>> it2 = it;

                    if (booleans[1]) {
                        checkarsin(ether1, true);
                    } else {
                        checkarsin(ether1, false);
                    }
                    if (e2) {
                        checkarsin(ether2, true);
                    } else {
                        checkarsin(ether2, false);
                    }
                    if (e3) {

                        checkarsin(ether3, true);
                    } else {

                        checkarsin(ether3, false);
                    }
                    if (e4) {

                        checkarsin(ether4, true);
                    } else {

                        checkarsin(ether4, false);
                    }
                    if (e5) {

                        checkarsin(ether5, true);
                    } else {

                        checkarsin(ether5, false);
                    }
                    if (e6) {

                        checkarsin(ether6, true);
                    } else {

                        checkarsin(ether6, false);
                    }
                    if (e7) {

                        checkarsin(ether7, true);
                    } else {

                        checkarsin(ether7, false);
                    }
                    if (e8) {

                        checkarsin(ether8, true);
                    } else {

                        checkarsin(ether8, false);
                    }
                    if (e9) {

                        checkarsin(ether9, true);
                    } else {

                        checkarsin(ether9, false);
                    }
                    if (e10) {

                        checkarsin(ether10, true);
                    } else {

                        checkarsin(ether10, false);
                    }
                    if (e11) {

                        checkarsin(ether11, true);
                    } else {

                        checkarsin(ether11, false);
                    }
                    if (e12) {

                        checkarsin(ether12, true);
                    } else {

                        checkarsin(ether12, false);
                    }
                    if (e13) {

                        checkarsin(ether13, true);
                    } else {

                        checkarsin(ether13, false);
                    }
                    it = it2;

                     */
                }
            }

            public void onExecutionFailed(Exception exp) {
                voltaget.setText(exp.getMessage());
            }
        });
    }
}
