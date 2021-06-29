package kz.app.taýypall.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

import kz.app.taýypall.R;
import kz.app.taýypall.data.SharedPrefsHelper;
import kz.app.taýypall.view.home.messages.MessagePage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    SharedPrefsHelper sharedPrefs;
    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        sharedPrefs = new SharedPrefsHelper(this);

        sharedPrefs.setToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage);

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(getApplicationContext() , MessagePage.class);

        intent.putExtra("noti",1);

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(getBaseContext().NOTIFICATION_SERVICE);

        int notificationID = (new Random()).nextInt();

        createChannel(getApplicationContext(), nm);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri  uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );


        Map<String, String> map = remoteMessage.getData();

        Notification notificationBuilder =
                new NotificationCompat.Builder(
                        getApplicationContext(), "default_notification_channel_id")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(map.get("title"))
                        .setContentText(map.get("body"))
                        .setAutoCancel(true)
                        .setColor(getBaseContext().getResources().getColor(R.color.mainColor))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(uri)
                        .setContentIntent(pendingIntent)
                        .build();


        nm.notify(notificationID, notificationBuilder);


    }

    public void createChannel(Context c, NotificationManager nm){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("default_notification_channel_id", getBaseContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);

            channel.setShowBadge(false);
            nm.createNotificationChannel(channel);
        }


    }

}
