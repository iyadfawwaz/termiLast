package sy.iyad.server.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.Map;
import sy.iyad.mikrotik.MikrotikServer;
import sy.iyad.mikrotik.Models.ExecutionEventListener;
import sy.iyad.server.R;
import sy.iyad.server.Utils.Logger;

import static sy.iyad.server.ServerInformation.CPU_COMMAND;
import static sy.iyad.server.ServerInformation.DUPLEX;
import static sy.iyad.server.ServerInformation.ETHERNET;
import static sy.iyad.server.ServerInformation.RUNNING_TRUE;
import static sy.iyad.server.ServerInformation.UPTIME_COMMAND;
import static sy.iyad.server.ServerInformation.VOLTAGE_COMMAND;
import static sy.iyad.server.Utils.Logger.PORT;
import static sy.iyad.server.Utils.ServerInfo.ETHER;


public class MainActivity extends AppCompatActivity {

    Button cmd,gen,loggerBtn;
    FloatingActionButton disco;
    RecyclerView recyclerView;
    TextView cpu,uptime,voltage;
    EditText interval;

    TaskLed taskLed;
    UpdateStatus updateStatus;
    RadioButton ether1, ether10, ether11, ether12, ether13,
            ether2, ether3, ether4, ether5,ether6,ether7,
            ether8,ether9;
    TextView l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13;


    private class UpdateStatus implements Runnable {

        private final Runnable runnable;
        private final TextView textView;
        private volatile boolean isRunning = true ;
        private long x;

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

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
                while (isRunning()) {

                    Thread.sleep(getX());
                    runOnUiThread(runnable);
                }
            } catch (InterruptedException e) {
                textView.setText(e.getMessage());
                setRunning(false);
            }
        }

        public void setinterval(long x) {
            this.x = x;
        }

        public long getX() {
            return x;
        }
    }
    private void updateStream(@NonNull TextView textView ,@NonNull String cmd ,String key){
        MikrotikServer.execute(cmd).addExecutionEventListener(new ExecutionEventListener() {
            @Override
            public void onExecutionSuccess(List<Map<String, String>> mapList) {
                textView.setText(mapList.get(0).get(key));
            }

            @Override
            public void onExecutionFailed(Exception exception) {

            }
        });
    }

    class TaskLed implements Runnable {

        private volatile boolean isRunning = true;
        private long x;
        Runnable runnable = MainActivity.this::turnOnOffLed;

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        public void run() {
            while (isRunning()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(x);
                    runOnUiThread(runnable);
                } catch (InterruptedException e) {
                    cpu.setText(e.getMessage());
                    setRunning(false);
                }
            }
        }

        public void setInterval(long x) {
            this.x = x;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        cmd =  findViewById(R.id.commandact);
        gen =  findViewById(R.id.usergen);
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

        l1 = findViewById(R.id.l01);
        l2 = findViewById(R.id.l02);
        l3 = findViewById(R.id.l03);
        l4 = findViewById(R.id.l04);
        l5 = findViewById(R.id.l05);
        l6 = findViewById(R.id.l06);
        l7 = findViewById(R.id.l07);
        l8 = findViewById(R.id.l08);
        l9 = findViewById(R.id.l09);
        l10 = findViewById(R.id.l10);
        l11 = findViewById(R.id.l11);
        l12 = findViewById(R.id.l12);
        l13 = findViewById(R.id.l13);

        interval = findViewById(R.id.interval);

        disco = findViewById(R.id.dis);
        recyclerView = findViewById(R.id.recyclerView);

        loadEthernet();

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
            long x = 1000;
            if (interval.getText() != null && interval.getText().length()>0) {
                x = x * Long.parseLong(interval.getText().toString());

            }
            startLogging(x);
        });
        loggerBtn.setOnLongClickListener(view -> {
            taskLed.setRunning(false);
            updateStatus.setRunning(false);
            return true;
        });

        cmd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CommanderActivity.class)));

        gen.setOnClickListener(v ->  startActivity(new Intent(this, GenerateUserActivity.class)));
    }

    private void loadEthernet() {
        MikrotikServer.execute(ETHERNET).addExecutionEventListener(new ExecutionEventListener() {
            @Override
            public void onExecutionSuccess(List<Map<String, String>> mapList) {
                for (int i = 0; i < mapList.size(); i++){
                    ETHER[i] = mapList.get(i).get("name");
                }

            }

            @Override
            public void onExecutionFailed(Exception exception) {

            }
        });
    }

    private void getRate(){
        TextView[] textViews = new TextView[]{l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13};
        for (int j = 0 ; j < textViews.length; j++){
            updateDuplex(textViews[j],ETHER[j]);
        }


    }
    private void startLogging(long x){
        loadListaVariable(x);
        updateLedStatus(x);
        updateCpu(x);
      getRate();
        updateUptime(x);
        updateVoltage();
    }
    private void loadListaVariable(long x){

        new Logger(this, recyclerView,x)
        .startReceiving(PORT);
    }

    private void updateUptime(long x){
         updateStatus = new UpdateStatus(uptime,UPTIME_COMMAND,"uptime");
         updateStatus.setinterval(x);
        Thread thread = new Thread(updateStatus);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateCpu(long x){
        UpdateStatus reUpdateNow = new UpdateStatus(cpu,CPU_COMMAND,"load");
        reUpdateNow.setinterval(x);
        Thread thread = new Thread(reUpdateNow);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateDuplex(@NonNull TextView textView,String ethernet){
        updateStream(textView,DUPLEX+ethernet+" once","rate");
       // UpdateStatus reUpdateNow = new UpdateStatus(textView,DUPLEX+ethernet+" once","rate");
       // Thread thread = new Thread(reUpdateNow);
       // thread.setDaemon(true);
      //  thread.start();
    }

    private void updateLedStatus(long x) {


        taskLed = new TaskLed();
        taskLed.setInterval(x);
        Thread thread = new Thread(taskLed);
        thread.setDaemon(true);
       // if (isOk) {
            thread.start();
     //   }else {
        //    thread.terminate();
      //  }

    }

    protected void updateVoltage() {

        updateStream(voltage,VOLTAGE_COMMAND,"value");
       // Thread thread = new Thread(reUpdateNow);
       // thread.setDaemon(true);
      //  thread.start();
    }

    private void checkering(RadioButton radioButton, boolean isChecked) {
        radioButton.setChecked(isChecked);
    }

    private void turnOnOffLed() {

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
