package sy.iyad.server.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;
import sy.e.server.R;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.server.Utils.Logger;

import static sy.iyad.server.ServerInformations.CPU_COMMAND;
import static sy.iyad.server.ServerInformations.RUNNING_TRUE;
import static sy.iyad.server.ServerInformations.UPTIME_COMMAND;
import static sy.iyad.server.ServerInformations.VOLTAGE_COMMAND;
import static sy.iyad.server.Utils.Logger.PORT;
import static sy.iyad.server.Utils.ServerInfo.ETHER;


public class MainActivity extends AppCompatActivity {

    Button cmd,gen,loggerBtn;
    FloatingActionButton disco;
    RecyclerView recyclerView;
    TextView cpu,uptime,voltage;

    RadioButton ether1, ether10, ether11, ether12, ether13,
            ether2, ether3, ether4, ether5,ether6,ether7,
            ether8,ether9;


    private class UpdateStatus implements Runnable {

        private final Runnable runnable;
        private final TextView textView;

        public UpdateStatus(@NonNull TextView textView, @NonNull String cmd, @NonNull String key) {

            this.textView = textView;
              runnable = () -> MikrotikServer.execute(cmd).addExecutionEventListener(new ExecutionEventListener() {
                  public void onExecutionSuccess(List<Map<String, String>> mapList) {
                      textView.setText(mapList.get(0).get(key));
                  }
                  public void onExecutionFailed(Exception exp) {
                      textView.setText(exp.getMessage());
                  }
              });
        }
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                    runOnUiThread(runnable);
                }
            } catch (InterruptedException e) {
                textView.setText(e.getMessage());
            }
        }
    }

    class TaskLeds implements Runnable {

        Runnable runnable = MainActivity.this::turnOnOffLeds;
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                    runOnUiThread(runnable);
                } catch (InterruptedException e) {
                    cpu.setText(e.getMessage());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        cmd =  findViewById(R.id.commandact);
        gen =  findViewById(R.id.usergenact);
        loggerBtn =  findViewById(R.id.logger);
        cpu =  findViewById(R.id.cpu);
        voltage =  findViewById(R.id.voltage);
        uptime =  findViewById(R.id.uptime);
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
        recyclerView = findViewById(R.id.recyclerView);

        /*
        if (SKIPPING.equals(ACTION_RUN)){
            startLogging();
        }

         */

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

    private void startLogging(){
        loadListaVariable();
        updateLedsStatus();
        updateCpu();
        updateUptime();
        updateVoltage();
    }
    private void loadListaVariable(){

        new Logger(this, recyclerView)
        .startReceiving(PORT);
    }

    private void updateUptime(){
        UpdateStatus reUpdateNow = new UpdateStatus(uptime,UPTIME_COMMAND,"uptime");
        Thread thread = new Thread(reUpdateNow);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateCpu(){
        UpdateStatus reUpdateNow = new UpdateStatus(cpu,CPU_COMMAND,"load");
        Thread thread = new Thread(reUpdateNow);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateLedsStatus() {

       TaskLeds taskLeds = new TaskLeds();
        Thread thread = new Thread(taskLeds);
        thread.setDaemon(true);
        thread.start();

    }

    protected void updateVoltage() {

        MikrotikServer.execute(VOLTAGE_COMMAND).addExecutionEventListener(new ExecutionEventListener() {
            public void onExecutionSuccess(List<Map<String, String>> mapList) {
               voltage.setText( mapList.get(0).get("voltage"));
            }
            public void onExecutionFailed(Exception exp) {
               voltage.setText(exp.getMessage());
            }
        });
    }

    private void checkering(RadioButton radioButton, boolean isChecked) {
        radioButton.setChecked(isChecked);
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
                        checkering(radioButtons[i], booleans[i]);
                    }
                }
            }
            public void onExecutionFailed(Exception exp) {
                voltage.setText(exp.getMessage());
            }
        });
    }

}
