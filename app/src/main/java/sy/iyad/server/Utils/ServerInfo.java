package sy.iyad.server.Utils;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ServerInfo {

    public static final DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference().child("serverLogger/tokens");
    public static volatile boolean isRegistered = true;
   // public static final String SHARED_KEY="Ac09052021X";
    public static String[] ETHER = new String[13];
    public static int VIP = 0xFFC22B55;
    public static String SKIPPING = "SKIPPED";

}
