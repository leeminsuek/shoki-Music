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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gun0912.tedadhelper.TedAdHelper;
import gun0912.tedadhelper.backpress.OnBackPressListener;
import gun0912.tedadhelper.backpress.TedBackPressDialog;
import gun0912.tedadhelper.banner.OnBannerAdListener;
import gun0912.tedadhelper.banner.TedAdBanner;
import gun0912.tedadhelper.front.OnFrontAdListener;
import gun0912.tedadhelper.front.TedAdFront;


public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;

    private static final int PLAY = 1;
    private static final int PAUSE = 0;
    private static final int START = -1;

    public static int state = START;

    private AppCompatButton stateBtn, alarmBtn;
    //    private FaceBook
    private AdView adView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TedAdHelper.setAdmobTestDeviceId("725E60959378FE3BE611A2D28F4B9DF9");
//        TedAdHelper.setFacebookTestDeviceId("c4e421ddcb9f143df943dfd8cc208481");

        stateBtn = (AppCompatButton) findViewById(R.id.startPlayerBtn);
        alarmBtn = (AppCompatButton) findViewById(R.id.alarmBtn);


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        stateBtn.setOnClickListener(view -> {
            if (state == START) playBtn();
            else if (state == PLAY) pauseBtn();
            else playBtn();
        });

        alarmBtn.setOnClickListener(view -> {
            showAlert();
        });


        KenBurnsView imageView = (KenBurnsView) findViewById(R.id.bg);
        adinit();
        alarmBtn();
        defaultBtn();

        if (ShokiMusicPlayer.getInstance().isPlaying()) {
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
        FrameLayout adViewContainer = (FrameLayout) findViewById(R.id.adViewContainer);
        TedAdBanner.showBanner(
                adViewContainer,
                "1661645547475594_1661645704142245",
                "ca-app-pub-2694703339687591/5631952240",
                new Integer[]{TedAdHelper.AD_FACEBOOK, TedAdHelper.AD_ADMOB}
                , new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {

                    }

                    @Override
                    public void onLoaded(int adType) {

                    }

                    @Override
                    public void onAdClicked(int adType) {

                    }

                    @Override
                    public void onFacebookAdCreated(com.facebook.ads.AdView facebookBanner) {
                        adView = facebookBanner;
                    }

                });
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
        if (state == START || state == PAUSE) {
            try {
                if (state == START) {
                    playLocalAudio();
                    showAlert();
                } else restartLocalAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
            state = PLAY;
        }
    }

    private void pauseBtn() {
        stateBtn.setText(string(R.string.main_play_btn_txt));
        if (state == PLAY) {
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
        intent.setAction(key ? Constants.ACTION.SERVICE_START_ACTION : Constants.ACTION.SERVICE_STOP_ACTION);
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
        if (timer == 0) {
            Toast.makeText(getApplicationContext(), "반복재생 됩니다.", Toast.LENGTH_LONG).show();
        } else {
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

            Date date = new Date(calendar.getTimeInMillis());
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm까지 재생됩니다.", Locale.getDefault());
            String dateFormatted = formatter.format(date);
            Toast.makeText(getApplicationContext(), dateFormatted, Toast.LENGTH_LONG).show();

            TedAdFront.showFrontAD(
                    this,
                    "1661645547475594_1708386839468131",
                    "ca-app-pub-2694703339687591/5009969869",
                    new Integer[]{TedAdHelper.AD_FACEBOOK, TedAdHelper.AD_ADMOB},
                    new OnFrontAdListener() {
                        @Override
                        public void onDismissed(int adType) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }

                        @Override
                        public void onLoaded(int adType) {

                        }

                        @Override
                        public void onAdClicked(int adType) {

                        }

                        @Override
                        public void onFacebookAdCreated(InterstitialAd facebookFrontAD) {
                        }
                    });
        }
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
                            setMusicStopAlarm(Calendar.HOUR, 1);
                            break;
                        case 1:
                            setMusicStopAlarm(Calendar.HOUR, 3);
                            break;
                        case 2:
                            setMusicStopAlarm(Calendar.HOUR, 5);
                            break;
                        case 3:
                            setMusicStopAlarm(Calendar.HOUR, 8);
                            break;
                        case 4:
                            setMusicStopAlarm(Calendar.HOUR, 0);
                            break;
                    }
                    alarmBtn.setVisibility(View.VISIBLE);
                    return true;
                })
                .positiveText(string(R.string.alert_select_txt))
                .show();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        TedBackPressDialog.startDialog(this,
                getString(R.string.app_name),
                "1661645547475594_1664082847231864",
                "ca-app-pub-2694703339687591/5795603772",
                new Integer[]{TedAdHelper.AD_FACEBOOK, TedAdHelper.AD_ADMOB},
                true,
                new OnBackPressListener() {
                    @Override
                    public void onReviewClick() {
                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                    }

                    @Override
                    public void onLoaded(int adType) {
                    }

                    @Override
                    public void onAdClicked(int adType) {
                    }
                });

//        NativeAdActivity.showNativeAdActivity(this, this::finish);
//        super.onBackPressed();
    }
}