package com.shoki.dev.sleepmusic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ShokiService extends Service {

    public static final String SERVICE_KEY = "service.key";

    public ShokiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.SERVICE_START_ACTION)) {
            ShokiMusicPlayer.getInstance().createMusic(this);
            startForeground(NotiUtil.NOTIFI_ID, NotiUtil.createNotification(getApplicationContext()));
        } else if (intent.getAction().equals(Constants.ACTION.SERVICE_STOP_ACTION)) {
            ShokiMusicPlayer.getInstance().pause();
            stopForeground(true);
        } else if (intent.getAction().equals(Constants.ACTION.SERVICE_KILL_ACTION)) {
            ShokiMusicPlayer.getInstance().killMediaPlayer();
            stopForeground(true);
        } else if (intent.getAction().equals(Constants.ACTION.SERVICE_RESTART_ACTION)) {
            ShokiMusicPlayer.getInstance().restart();
            startForeground(NotiUtil.NOTIFI_ID, NotiUtil.createNotification(getApplicationContext()));
        }
        return Service.START_NOT_STICKY;
    }
}
