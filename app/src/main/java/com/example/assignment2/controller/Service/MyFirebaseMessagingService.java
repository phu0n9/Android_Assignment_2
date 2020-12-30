package com.example.assignment2.controller.Service;

import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.assignment2.R;
import com.google.firebase.messaging.RemoteMessage;
//This code for the notification can be found at:
//https://github.com/VaibhavMojidra/Send-Notification-from-one-user-to-another-using-Firebase-using-JAVA.git
//With the modification to fit with the assignment
//However, when the users log in with the new devices, the received token cannot
//be found, therefore, it cannot send the notification right away.(New users can send notification to old users login in
//different devices, but old users cannot)

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    String title,message;
    public static final String CHANNEL_ID = "default_channel_id";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title=remoteMessage.getData().get("Title");
        message=remoteMessage.getData().get("Message");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setSound(Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.sound_effect));
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
