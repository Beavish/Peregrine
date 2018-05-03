package com.example.elitebook.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper{

    public static final String Channel = "ChannelID";
    public static final String ChannelName = "Geofence Notifications";
    private NotificationManager mManger;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }
    // creating a channel for notifications to flow through
    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(Channel,ChannelName, NotificationManager.IMPORTANCE_HIGH);
        channel1.enableLights(true);
        channel1.enableLights(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getmManger().createNotificationChannel(channel1);
    }

    public NotificationManager getmManger(){
        if (mManger == null)
            mManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return mManger;


    }

    public Notification.Builder getChannel1Notification(String title,String message){
        // show notification and when its clicked , open the app
        Intent resultIntent = new Intent(this, MapsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT );
        return new Notification.Builder(getApplicationContext(),Channel)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setContentText(message);
    }

}
