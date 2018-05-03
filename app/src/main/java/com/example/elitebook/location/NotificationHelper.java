package com.example.elitebook.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper{

    public static final String Channel = "ChannelID";
    public static final String ChannelName = "Geofence Notifications";
    private NotificationManager mManger;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

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

        return new Notification.Builder(getApplicationContext(),Channel)
                .setContentTitle(title)
                .setContentText(message);
    }

}
