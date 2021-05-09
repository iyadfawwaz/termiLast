package sy.iyad.server.Utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import sy.e.server.R;


public class Logger {

    public static int PORT = 5141;
    private final Context context;
    private final ArrayList<String> arrayList;
    private volatile boolean isReceiving =false;
    RecyclerView.Adapter<LogViewHolder> adapter;
    private final LinearLayoutManager linearLayoutManager;

    public Logger(Context context, @NonNull RecyclerView recyclerView){

        this.context = context;
        arrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(context);


        adapter = new RecyclerView.Adapter<LogViewHolder>() {
            @NonNull
            @Override
            public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new LogViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.log_list, parent, false));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

                holder.textView.setText("" + arrayList.get(position));
                linearLayoutManager.scrollToPosition(position);
            }

            @Override
            public int getItemCount() {
                return arrayList.size();
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void startReceiving(int port){
        isReceiving = true;
       openSocket(port).start();
    }

    private Thread openSocket(int port) {

        return new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
                socket.receive(datagramPacket);

                while (isReceiving) {
                    Thread.sleep(1000);
                    ((Activity) context).runOnUiThread(() -> {
                        arrayList.add(new String(datagramPacket.getData()).trim());
                        adapter.notifyDataSetChanged();
                    });
                }

                } catch(SocketException e){
                    arrayList.add(e.getMessage());
                    adapter.notifyDataSetChanged();
                } catch(IOException e){
                    arrayList.add(e.getMessage());
                    adapter.notifyDataSetChanged();
                } catch(InterruptedException e){
                    arrayList.add(e.getMessage());
                    adapter.notifyDataSetChanged();
                }
        });
    }
}
