package sy.iyad.server.Utils;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import sy.e.server.R;


public class ServerAdapter extends RecyclerView.Adapter<ServerViewHolder> {

    ArrayList<String> arrayList;

    public ServerAdapter(@NonNull ArrayList<String> arrayList) {

        this.arrayList = arrayList;

    }


    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemer,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {

        String string = arrayList.get(position);
        holder.all.setText(string);
        holder.num.setText(""+(position+1));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
