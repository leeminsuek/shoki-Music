package com.shoki.dev.sleepmusic;

import android.app.Activity;
import android.media.MediaPlayer;

/**
 * Created by shoki on 2017. 3. 18..
 */
public class ShokiMusicPlayer {
    private static ShokiMusicPlayer ourInstance = new ShokiMusicPlayer();

    public static ShokiMusicPlayer getInstance() {
        return ourInstance;
    }

    private MediaPlayer mediaPlayer;
    private int playbackPosition=0;

    private ShokiMusicPlayer() {
    }

    public void restart() {
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()){
            //음악이 일시정지되기 직전의 position값으로 셋팅
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
        }
    }

    public void pause() {
        if(mediaPlayer!=null){
            //음악을 일시 정지 시킬때 정지되기 직전의 position값 저장
            playbackPosition = mediaPlayer.getCurrentPosition();
            //일시정지
            mediaPlayer.pause();
        }
    }
    public void createMusic(Activity activity) {
        mediaPlayer=MediaPlayer.create(activity, R.raw.videoplayback);
        //MediaPlayer 객체가 가지고 있는 음악 정보를 start
        mediaPlayer.start();
    }
    public void killMediaPlayer() {
        if(mediaPlayer!=null){
            try{
                //MediaPlayer 자원해제
                mediaPlayer.release();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

}
