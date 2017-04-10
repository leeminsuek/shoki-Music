package com.shoki.dev.sleepmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShokiBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        ShokiMusicPlayer.getInstance().pause();
    }
}