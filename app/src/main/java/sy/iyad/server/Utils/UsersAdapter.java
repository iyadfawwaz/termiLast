package sy.iyad.server.Utils;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.ArrayList;

import sy.iyad.server.R;


public class UsersAdapter extends Adapter<UsersHolder> {

    ArrayList<Users> arrayList;

    public UsersAdapter(@NonNull ArrayList<Users> arrayList) {
        this.arrayList = arrayList;
    }

    public int getItemCount() {
        return arrayList.size();
    }

    @NonNull
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UsersHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false));

    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {

        Users users = arrayList.get(position);
        holder.userArea.setText(""+users.getUsername());
        holder.passArea.setText(""+users.getPassword());
        holder.profArea.setText(users.getProfile());

    }
}
