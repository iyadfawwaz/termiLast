package sy.iyad.server.Utils;


import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import sy.e.server.R;


public class UsersHolder extends ViewHolder {

    public TextView passArea;
    public TextView profArea;
    public TextView userArea;

    public UsersHolder(@NonNull View itemView) {

        super(itemView);

        userArea =  itemView.findViewById(R.id.username);
        passArea =  itemView.findViewById(R.id.pass);
        profArea =  itemView.findViewById(R.id.prof);

    }
}
