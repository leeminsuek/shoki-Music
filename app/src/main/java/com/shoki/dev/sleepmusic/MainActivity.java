package com.shoki.dev.sleepmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startPlayerBtn = (Button) findViewById(R.id.startPlayerBtn);
        Button pausePlayerBtn = (Button) findViewById(R.id.pausePlayerBtn);
        Button restartPlayerBtn = (Button) findViewById(R.id.restartPlayerBtn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        startPlayerBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                try {
                    playLocalAudio();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        pausePlayerBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ShokiMusicPlayer.getInstance().pause();
            }
        });

        restartPlayerBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                //MediaPlayer 객체가 존재하고 현재 실행중이 아닐때
                showAlert();
            }
        });
    }

    private void playLocalAudio() throws Exception {
        //어플리케이션에 내장되 있는 자원을 호출해서 MediaPlayer객체 생성
        ShokiMusicPlayer.getInstance().createMusic(this);
    }

    private void setMusicStopAlarm(int timerType, int timer) {
        Intent intent = new Intent(this, ShokiBroadCast.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(timerType, timer);

        PendingIntent sender = PendingIntent.getBroadcast(this, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 23)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        else {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            }
        }
    }

    public void showAlert() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light);
        alertBuilder.setTitle("언제쯤 폰을 재워드릴까요?");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_singlechoice);
        adapter.add("1분 뒤");
        adapter.add("5분 뒤");
        adapter.add("30분 뒤");
        adapter.add("1시간 뒤");
        adapter.add("3시간 뒤");

        alertBuilder.setAdapter(adapter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id) {
                            case 0:
                                setMusicStopAlarm(Calendar.MINUTE, 1);
                                break;
                            case 1:
                                setMusicStopAlarm(Calendar.MINUTE, 5);
                                break;
                            case 2:
                                setMusicStopAlarm(Calendar.MINUTE, 30);
                                break;
                            case 3:
                                setMusicStopAlarm(Calendar.HOUR, 1);
                                break;
                            case 4:
                                setMusicStopAlarm(Calendar.HOUR, 3);
                                break;
                        }
                    }
                });
        alertBuilder.show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShokiMusicPlayer.getInstance().killMediaPlayer();
//        killMediaPlayer();
    }

//    public void killMediaPlayer() {
//        if(mediaPlayer!=null){
//            try{
//                //MediaPlayer 자원해제
//                mediaPlayer.release();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//
//    }
}