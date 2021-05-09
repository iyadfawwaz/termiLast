package sy.iyad.server.Utils;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sy.e.server.R;


public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

    private ArrayList<String> arrayList;

    public LogAdapter() {
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.log_list,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {

        holder.textView.setText(""+arrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
