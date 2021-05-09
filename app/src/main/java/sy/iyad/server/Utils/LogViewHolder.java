package sy.iyad.server.Utils;


import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sy.e.server.R;


public class LogViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public LogViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.log_);
    }
}
