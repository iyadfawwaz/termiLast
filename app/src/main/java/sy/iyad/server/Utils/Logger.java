package sy.iyad.server.Utils;


import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;


public class Logger {

    public static int PORT = 5141;

    public Context context;
    private final ArrayList<String> arrayList;
    public static ArrayAdapter<String> arrayAdapter;

    public Logger(Context context){
        this.context = context;
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,arrayList);
    }

    public void startRecieving(){
        Logger logger = new Logger(context);
       logger.openSocket(PORT).start();
    }

    private Thread openSocket(int port){

        return new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DatagramSocket socket = new DatagramSocket(port);
                            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024],1024);

                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    arrayList.add(new String(datagramPacket.getData()).trim());
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            });



                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
