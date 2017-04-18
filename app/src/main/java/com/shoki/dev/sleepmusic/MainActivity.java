package com.shoki.dev.sleepmusic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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


        KenBurnsView imageView = (KenBurnsView) findViewById(R.id.bg);
        adinit();
        alarmBtn();
        defaultBtn();

        if(ShokiMusicPlayer.getInstance().isPlaying()) {
            state = PLAY;
            alarmBtn.setVisibility(View.VISIBLE);
            stateBtn.setText(R.string.main_pause_btn_txt);
        }

        Glide
                .with(this)
                .load(R.drawable.bg4)
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    private void adinit() {
        adView = new AdView(this, "1661645547475594_1661645704142245", AdSize.BANNER_HEIGHT_50);
        RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
        adViewContainer.addView(adView);
        adView.loadAd();
    }


    private String string(@StringRes int id) {
        return getResources().getString(id);
    }

    private void alarmBtn() {
        alarmBtn.setText(string(R.string.main_alarm_btn_txt));
    }

    private void defaultBtn() {
        stateBtn.setText(string(R.string.main_play_btn_txt));
    }

    private void playBtn() {
        stateBtn.setText(string(R.string.main_pause_btn_txt));
        if(state == START || state == PAUSE) {
            try {
                if(state == START) {
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
        stateBtn.setText(string(R.string.main_play_btn_txt));
        if(state == PLAY) {
            try {
                pauseLocalAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = PAUSE;
        }
    }

    private void createService(boolean key) {
        Intent intent = new Intent(this, ShokiService.class);
        intent.setAction(key? Constants.ACTION.SERVICE_START_ACTION : Constants.ACTION.SERVICE_STOP_ACTION);
        startService(intent);
    }

    private void playLocalAudio() throws Exception {
        createService(true);
    }

    private void restartLocalAudio() throws Exception {
        Intent intent = new Intent(this, ShokiService.class);
        intent.setAction(Constants.ACTION.SERVICE_RESTART_ACTION);
        startService(intent);
        showAlert();
    }

    private void pauseLocalAudio() throws Exception {
        Intent intent = new Intent(this, ShokiBroadCast.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(sender);
        sender.cancel();
        createService(false);
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

        AppCompatTextView textView = (AppCompatTextView) findViewById(R.id.timer_txtv);
        Date date = new Date(calendar.getTimeInMillis());
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm까지 재생됩니다.", Locale.getDefault());
        String dateFormatted = formatter.format(date);
        textView.setText(dateFormatted);
    }

    public void showAlert() {
        new MaterialDialog.Builder(this)
                .title(string(R.string.alert_title_txt))
                .items(
                        string(R.string.alert_items_1),
                        string(R.string.alert_items_2),
                        string(R.string.alert_items_3),
                        string(R.string.alert_items_4),
                        string(R.string.alert_items_5)
                )
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
                .positiveText(string(R.string.alert_select_txt))
                .show();
    }

    private void startNativeAd() {
        Intent intent = new Intent(this, NativeAdActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        if(adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        NativeAdActivity.showNativeAdActivity(this, this::finish);
//        super.onBackPressed();
    }
}