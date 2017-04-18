package com.shoki.dev.sleepmusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * Created by shoki on 2017. 4. 18..
 */

public class NotiUtil {

    public final static int NOTIFI_ID = 112;

    public static Notification createNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.noti_message))
                .build();

        noti.flags = Notification.FLAG_NO_CLEAR;

        return noti;
//        notificationManager.notify(NOTIFI_ID, noti);
    }

    public static void deleteNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFI_ID);
    }

}
