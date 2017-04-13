package com.shoki.dev.sleepmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;

    private static final int PLAY = 1;
    private static final int PAUSE = 0;
    private static final int START = -1;

    public static int state = START;

    private AppCompatButton stateBtn, alarmBtn;

    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stateBtn = (AppCompatButton) findViewById(R.id.startPlayerBtn);
        alarmBtn = (AppCompatButton) findViewById(R.id.alarmBtn);


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        stateBtn.setOnClickListener(view -> {
            if(state == START) playBtn();
            else if(state == PLAY) pauseBtn();
            else playBtn();
        });

        alarmBtn.setOnClickListener(view -> {
            showAlert();
        });

        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null) {
            String intentState = intent.getExtras().getString(Contants.STATE, "");
            assert intentState != null;
            if(intentState.equals(Contants.EX_PAUSE)) {
                state = START;
                alarmBtn.setVisibility(View.GONE);
            }
        }

        KenBurnsView imageView = (KenBurnsView) findViewById(R.id.bg);
        adinit();
        alarmBtn();
        defaultBtn();

        Glide
                .with(this)
                .load(R.drawable.bg_1)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    private void adinit() {
        adView = new AdView(this, "1661645547475594_1661645704142245", AdSize.BANNER_HEIGHT_50);
        RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
        adViewContainer.addView(adView);
        adView.loadAd();
    }

    private void alarmBtn() {
        alarmBtn.setText("재생시간 설정");
    }

    private void defaultBtn() {
        stateBtn.setText("재생");
    }

    private void playBtn() {
        stateBtn.setText("중지");
        if(state == START || state == PAUSE) {
            try {
                if(state == START) {ㅇ
                    playLocalAudio();
                    showAlert();
                }
                else restartLocalAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = PLAY;
        }
    }
    private void pauseBtn() {
        stateBtn.setText("재생");
        if(state == PLAY) {
            try {
                pauseLocalAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = PAUSE;
        }
    }


    private void playLocalAudio() throws Exception {
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
        new MaterialDialog.Builder(this)
                .title("언제쯤 폰을 재워드릴까요?")
                .items("1분 뒤", "5분 뒤", "30분 뒤", "1시간 뒤", "3시간 뒤")
                .itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                    switch (which) {
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
                    alarmBtn.setVisibility(View.VISIBLE);
                    return true;
                })
                .positiveText("선택")
                .show();
    }
    @Override
    protected void onDestroy() {
        if(adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}