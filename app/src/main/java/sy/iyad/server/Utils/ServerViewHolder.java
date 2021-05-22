package sy.iyad.server.Utils;


import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sy.iyad.server.R;


public class ServerViewHolder extends RecyclerView.ViewHolder {

    TextView all;
    TextView num;

    public ServerViewHolder(@NonNull View itemView) {
        super(itemView);

        all = itemView.findViewById(R.id.cont);
        num = itemView.findViewById(R.id.num);

    }
}
