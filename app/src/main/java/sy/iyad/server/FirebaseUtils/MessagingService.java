package sy.iyad.server.FirebaseUtils;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import sy.e.server.R;
import sy.iyad.server.LoginActivity;
import sy.iyad.server.Utils.ServerInfo;
import sy.iyad.server.share.SharingActivity;

import static sy.iyad.server.LoginActivity.ACTION_LOGIN;
import static sy.iyad.server.LoginActivity.REQUEST_CODE;


public class MessagingService extends FirebaseMessagingService {


    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        notificationBuild(remoteMessage);
    }

    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        ServerInfo.tokenReference.push().setValue(new Token(token));
        startActivity(new Intent(this, LoginActivity.class));
    }


    private void notificationBuild(@NonNull RemoteMessage remoteMessage) {


        String sender = remoteMessage.getData().get("sender");
        String message = remoteMessage.getData().get("message");
        String msgID = remoteMessage.getMessageId();
        String admin = remoteMessage.getData().get("admin");
        String ip = remoteMessage.getData().get("ip");
        String password = remoteMessage.getData().get("password");
        @SuppressLint("SimpleDateFormat")
        String sentTime = new SimpleDateFormat("yy:M:dd:hh:mm").format(new Date( remoteMessage.getSentTime()));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("sy","sy", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel1 = new NotificationChannel("syria","syria", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel1);
        }

        Intent intent = new Intent(this, SharingActivity.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra("ip",ip);
        intent.putExtra("admin",admin);
        intent.putExtra("password",password);
        intent.putExtra("message",message);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,REQUEST_CODE,intent,PendingIntent.FLAG_ONE_SHOT);

        Action action = new Action(R.drawable.remain,"فتح التطبيق",pendingIntent);

        NotificationCompat.Style style = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(sentTime)
                .bigText(message)
                .setSummaryText(msgID);

        Builder notificationCompat = new Builder(this, "sy");
        notificationCompat.setVibrate(new long[]{500, 600, 700, 800})
                .setSmallIcon(R.drawable.avatarx)
                .setContentText( remoteMessage.getData().get("message"))
                .setAutoCancel(true)
             //   .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message")))
              //  .setStyle(new BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.syavatar)))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.symain))
                .setContentTitle( remoteMessage.getData().get("sender"))
                .setAllowSystemGeneratedContextualActions(true)
                .setColor(Color.RED)
                .addAction(action)
                .setStyle(style);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat
                .from(this);
        notificationManagerCompat.notify(new Random().nextInt(), notificationCompat.build());
    }
}
