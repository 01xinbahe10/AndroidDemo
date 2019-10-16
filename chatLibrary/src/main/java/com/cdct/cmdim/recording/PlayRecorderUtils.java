package com.cdct.cmdim.recording;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by hxb on 2017/11/15.
 * 播放录音
 */

public class PlayRecorderUtils {
    private MediaPlayer mMediaPlayer;
    private static MediaPlayer mMediaPlayer2;//为了切换正在播放的音频
    public static int PlayerStatus = 0;//检测播放状态；默认0是表示播放完和没有播放,1正在播放
    public static PlayRecorderUtils init() {
        PlayRecorderUtils playRecorderUtils = new PlayRecorderUtils();
        return playRecorderUtils;
    }

    public void startPlay(String filePath) {
        PlayerStatus = 1;
        mMediaPlayer = new MediaPlayer();
        if (null != mMediaPlayer2) {
            Log.e("测试音频", "startPlay=======: " + (mMediaPlayer2.toString()));
            if (mMediaPlayer2.isPlaying()) {
                mMediaPlayer2.stop();
            }
            mMediaPlayer2.release();
            mMediaPlayer2 = null;
        }
        if (null == mMediaPlayer2) {

            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
//            mMediaPlayer.prepareAsync();//需使用异步缓冲
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e("音频是否完工", "onCompletion======:  "+(mMediaPlayer == null)+"    "+(mMediaPlayer2 == null));
                        PlayerStatus = 0;
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        if (null != mMediaPlayer2) {
                            mMediaPlayer2.release();
                            mMediaPlayer2 = null;
                        }

                        Log.e("音频是否完工", "onCompletion------:  "+(mMediaPlayer == null)+"    "+(mMediaPlayer2 == null));
                    }
                });

                mMediaPlayer2 = mMediaPlayer;//记录本次未播放完，而进行下一次的操作

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("测试音频", "startPlay--------: " + (mMediaPlayer.toString()));

    }

    public void stopPlayer(){//为了正在播放时，用户急切关闭了当前界面时或手动点击暂停，并需要释放资源
        PlayerStatus = 0;
        if (null != mMediaPlayer){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (null != mMediaPlayer2){
            mMediaPlayer2.release();
            mMediaPlayer2 = null;
        }
    }


}
