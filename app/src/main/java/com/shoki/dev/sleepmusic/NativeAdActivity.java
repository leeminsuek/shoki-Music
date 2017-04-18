package com.shoki.dev.sleepmusic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shoki on 2017. 4. 18..
 */

public class NativeAdActivity extends AppCompatActivity {

    public interface NativeAdListener {
        void onNativeFinish();
    }

    public static NativeAdListener listener;

    private LinearLayout nativeAdContainer;
    private LinearLayout adLinearView;
    private NativeAd nativeAd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativead);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width = (int) (display.getWidth() * 0.9);

        setFinishOnTouchOutside(false);

        initView();
        showNativeAd();
    }

    private void initView() {
        Button buttonFinish = (Button) findViewById(R.id.native_ad_finish_btn);
        buttonFinish.setOnClickListener(view -> {
            finish();

            if(listener != null) {
                listener.onNativeFinish();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.native_ad_cancel_btn);
        buttonCancel.setOnClickListener(view -> {
            finish();
        });
    }

    public static void showNativeAdActivity(Context context, NativeAdListener listener) {
        NativeAdActivity.listener = listener;
        Intent intent = new Intent(context, NativeAdActivity.class);
        context.startActivity(intent);
    }


    private void showNativeAd(){
        nativeAd = new NativeAd(this, "1661645547475594_1664082847231864");
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != nativeAd) {
                    return;
                }
                nativeAdContainer = (LinearLayout) findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                adLinearView = (LinearLayout) inflater.inflate(R.layout.native_ad, nativeAdContainer, false);
                nativeAdContainer.addView(adLinearView);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adLinearView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = (TextView) adLinearView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = (MediaView) adLinearView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = (TextView) adLinearView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = (TextView) adLinearView.findViewById(R.id.native_ad_body);
                Button nativeAdCallToAction = (Button) adLinearView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdTitle());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdBody.setText(nativeAd.getAdBody());
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Download and display the cover image.
                nativeAdMedia.setNativeAd(nativeAd);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(getApplicationContext(), nativeAd, true);
                adChoicesContainer.addView(adChoicesView);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);
                nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }
        });

        nativeAd.loadAd();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
