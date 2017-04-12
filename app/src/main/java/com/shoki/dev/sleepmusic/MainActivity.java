package com.shoki.dev.sleepmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.shoki.dev.sleepmusic.widget.MorphingButton;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;

    private final int PLAY = 1;
    private final int PAUSE = 0;
    private final int START = -1;

    private int state = START;

    private MorphingButton stateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stateBtn = (MorphingButton) findViewById(R.id.startPlayerBtn);

        Button restartPlayerBtn = (Button) findViewById(R.id.restartPlayerBtn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        stateBtn.setOnClickListener(view -> {
            if(state == START) playBtn();
            else if(state == PLAY) pauseBtn();
            else playBtn();
        });

        restartPlayerBtn.setOnClickListener(view -> {
            //MediaPlayer 객체가 존재하고 현재 실행중이 아닐때
            showAlert();
        });

        playBtn();
    }

    private void playBtn() {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_height_56)) // 56 dp
                .width(dimen(R.dimen.mb_height_56)) // 56 dp
                .height(dimen(R.dimen.mb_height_56)) // 56 dp
                .color(color(R.color.mb_blue)) // normal state color
                .colorPressed(color(R.color.mb_blue_dark))
                .animationListener(() -> {
                    if(state == START || state == PAUSE) {
                        try {
                            if(state == START) playLocalAudio();
                            else restartLocalAudio();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        state = PLAY;
                    }
                }); // pressed state color
        stateBtn.morph(circle);
    }
    private void pauseBtn() {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_height_56)) // 56 dp
                .width(dimen(R.dimen.mb_height_56)) // 56 dp
                .height(dimen(R.dimen.mb_height_56)) // 56 dp
                .color(color(R.color.mb_purple)) // normal state color
                .colorPressed(color(R.color.mb_purple_dark))
                .animationListener(() -> {
                    if(state == PLAY) {
                        try {
                            pauseLocalAudio();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        state = PAUSE;
                    }
                }); // pressed state color
        stateBtn.morph(circle);
    }


    private void playLocalAudio() throws Exception {
        //어플리케이션에 내장되 있는 자원을 호출해서 MediaPlayer객체 생성
        ShokiMusicPlayer.getInstance().createMusic(this);
    }

    private void restartLocalAudio() throws Exception {
        ShokiMusicPlayer.getInstance().restart();
    }

    private void pauseLocalAudio() throws Exception {
        ShokiMusicPlayer.getInstance().pause();
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
                (dialog, id) -> {
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
                });
        alertBuilder.show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShokiMusicPlayer.getInstance().killMediaPlayer();
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
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