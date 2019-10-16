package hxb.xb_testandroidfunction.test_surface_view.surface_view_play_video;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/6/28.
 */

public class TestPlayVideoActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "PlayVedioActivity";

    /**
     * 开启当前界面
     */
    public static void startInstance(Context context) {
        context.startActivity(new Intent(context, TestPlayVideoActivity.class));
    }

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/20180628_112751.mp4";//内置sd卡
    private SeekBar videoSeekBar;
    private TextView tvPlayingTime;
    private TextView tvVideoTotalTime;
    private ImageView ivPlay;
    private ImageView ivPause;

    private MediaPlayer mediaPlayer;

    /**
     * 初始化MediaPlayer监听
     */
    private MediaPlayerListener mediaPlayerListener = new MediaPlayerListener();
    private SurfaceView surfaceView;
    private EditText etPath;
    /**
     * 当前播放视频位置
     */
    private int currentPosition;


    /**
     * 内部消息队列类，主要获取updateThread发来的CurrentPosition和MaxPosition设置给SeekBar
     */
    private Handler updateSeekBarHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "handleMessage curPosition = " + mediaPlayer.getCurrentPosition());
            videoSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            tvPlayingTime.setText(getTime(mediaPlayer.getCurrentPosition() / 1000));
            updateSeekBarHandler.sendEmptyMessageDelayed(0, 1000);
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_play_video);
        initView();
        initListener();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagePlay:
                if (!mediaPlayer.isPlaying()) {//播放、继续播放
//                pause = false;
                    mediaPlayer.start();
                }
                break;
            case R.id.iv_pause://暂停
                mediaPlayer.pause();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initData() {
        mediaPlayer = new MediaPlayer();
        // 把输送给surfaceView的视频画面，直接显示到屏幕上,不要维持它自身的缓冲区
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());

    }

    private void initListener() {
        ivPause.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        videoSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }

    private void initView() {
        surfaceView = findViewById(R.id.surfaceView);
        videoSeekBar = findViewById(R.id.videoProgress);
        tvPlayingTime = findViewById(R.id.textHasPalyTime);
        tvVideoTotalTime = findViewById(R.id.video_total_time);
        ivPlay = findViewById(R.id.imagePlay);
        ivPause = findViewById(R.id.iv_pause);
        etPath = findViewById(R.id.et_path);
    }


    /**
     * 重新开始播放
     */
    protected void replay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            Toast.makeText(this, "重新播放", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 暂停或继续
     */
    protected void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(this, "暂停播放", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * SeekBar监听类,监听用户对seekbar滑动的位置，已达到控制视频播放进度
     */
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        /***
         * 一直滑动不断变化的seekBar时，触发
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG, "Changed progress " + progress);
            Log.d(TAG, "Changed getProgress()  " + seekBar.getProgress());
            tvPlayingTime.setText(getTime(seekBar.getProgress() / 1000));
        }

        /**
         * 用户开始滑动seekBar时触发
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "start getProgress()  " + seekBar.getProgress());
        }

        /**
         * 当用户结束对滑块的滑动时，将mediaPlayer播放位置设为滑块结束对应位置
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "Stop getProgress()  " + seekBar.getProgress());
            currentPosition = seekBar.getProgress();
            mediaPlayer.seekTo(currentPosition);
            tvPlayingTime.setText(getTime(seekBar.getProgress() / 1000));
        }
    }

    /**
     * 当SurfaceView所在的Activity离开了前台,SurfaceView会被destroy,
     * 当Activity又回到了前台时，SurfaceView会被重新创建，并且是在OnResume()方法之后被创建
     */
    private class SurfaceCallback implements SurfaceHolder.Callback {
        /**
         * 创建SurfaceView时开始从上次位置播放或重新播放
         *
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 取得最大音量
//            curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 获取当前音量
            // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
//            play("/sdcard/basketball.mp4");
            play(ROOT_PATH);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        /**
         * 离开SurfaceView时停止播放，保存播放位置
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO: 2018/6/28 这里不能处理 mediaPlayer，因为该对象已经为回收消亡了
//            隐藏view的时候销毁SurfaceHolder的时候记录当前的播放位置并停止播放

//            Log.e(TAG, "surfaceDestroyed:   "+(mediaPlayer.isPlaying()) );
//            if ( null != mediaPlayer && mediaPlayer.isPlaying()) {
//                currentPosition = mediaPlayer.getCurrentPosition();
//                mediaPlayer.stop();
//                updateSeekBarHandler.removeMessages(0);
//            }
        }
    }


    /**
     * 开始播放
     * @param playUrl 播放视频的地址
     */
    protected void play(String playUrl){
        // 获取视频文件地址:放在sd卡根目录下的视频地址，这个地址也支持网络地址：比如http://...
//        String path = "storage/sdcard0/test.mp4";
        // 获取视频文件地址
        String path = etPath.getText().toString().trim();
//        String path = "/sdcard/basketball.mp4";
        File file = new File(playUrl);
        if (!file.exists()) {
            Toast.makeText(this, "视频文件路径错误或忘了加sd卡权限了", Toast.LENGTH_LONG).show();
            return;
        }
        path = file.getAbsolutePath();
        Log.d(TAG, "path = " + path);
        try {
            //mediaPlayer= new MediaPlayer(); 这个初始化不能放在这里，因为surfaceView可能会多次执行，导致mediaPlayer重新被初始化，由于监听器用的是同一个导致回调的时候会出现不适同一个mediaplayer的情况
            //此方法创建同步加载,直接start
//            mediaPlayer = MediaPlayer.create(this, Uri.parse(path), null);
            mediaPlayer.reset();
            // 设置播放的视频源
            mediaPlayer.setDataSource(path);
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(mediaPlayerListener);
            mediaPlayer.setOnCompletionListener(mediaPlayerListener);
            mediaPlayer.setOnInfoListener(mediaPlayerListener);
            mediaPlayer.setOnErrorListener(mediaPlayerListener);
            mediaPlayer.setOnBufferingUpdateListener(mediaPlayerListener);
            mediaPlayer.setOnSeekCompleteListener(mediaPlayerListener);
            mediaPlayer.prepareAsync();// 缓冲,装载
//            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class MediaPlayerListener implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener
            , MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener {

        /**
         * 视频装载完成,可以播放
         *
         * @param mp
         */
        public void onPrepared(MediaPlayer mp) {

            Log.d(TAG, "装载完成");
            Log.d(TAG, "装载完成 media same ? " + (mp == mediaPlayer));
            mediaPlayer.seekTo(currentPosition);
            mp.start();
            // 按照初始位置播放
            // 设置进度条的最大进度为视频流的最大播放时长
            videoSeekBar.setMax(mediaPlayer.getDuration());
            tvVideoTotalTime.setText(getTime(mediaPlayer.getDuration() / 1000));
            //更新seekBar进度
            updateSeekBarHandler.sendEmptyMessageDelayed(0, 1000);
            // 开始线程，更新进度条的刻度
            int current = mediaPlayer.getCurrentPosition();
            videoSeekBar.setProgress(current);
        }

        /**
         * 在播放结束被回调
         *
         * @param mp
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion " + mp.getCurrentPosition());
            mediaPlayer.seekTo(currentPosition);
        }

        /**
         * 播放视频，处理视频过程的视频信息
         *
         * @param mp
         * @param what
         * @param extra
         * @return
         */
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "---------onInfo------what---" + what + "--extra--" + extra);
            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                //缓冲视频开始
            } else if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                //网络连接异常
            } else if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                //缓冲完成
            } else if (MediaPlayer.MEDIA_INFO_METADATA_UPDATE == what) {
                Log.d(TAG, "---------MEDIA_INFO_METADATA_UPDATE---------");
            }
            return false;
        }


        /***
         * 播放出错
         *
         * @param mp
         * @param what
         * @param extra
         * @return
         */
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 发生错误重新播放
//            play("");
            Log.d(TAG, "onError");
            if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED ||
                    what == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {// 网络连接异常
                return true;
            }
            return false;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.d(TAG, "percent = " + percent);
        }

        /***
         *
         * seekTo()是定位方法，可以让播放器从指定的位置开始播放，需要注意的是该方法是个异步方法，
         * 也就是说该方法返回时并不意味着定位完成，尤其是播放的网络文件，
         * 真正定位完成时会触发OnSeekComplete.onSeekComplete()，
         * 如果需要是可以调用setOnSeekCompleteListener(OnSeekCompleteListener)设置监听器来处理的。
         * @param mp
         */
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d(TAG, "---------onSeekComplete---------");
            mp.start();
        }
    }


    /**
     * 秒转换成00:00
     * @param ms
     * @return
     */
    private String getTime(int ms) {
        try {
            return appendStrs(ms / 60 + "") + ":" + appendStrs(ms % 60 + "");
        } catch (Exception e) {
        }
        return "00:00";
    }


    /**
     * 字符串前面补加0
     * @param str
     * @return
     */
    private String appendStrs(String str) {
        String appendStr = "00";
        if (null == str || str.trim().isEmpty()) {
            return appendStr;
        }
        if (str.length() == appendStr.length()) {
            return str;
        }
        return appendStr.substring(0, appendStr.length() - str.length()) + str;
    }
}
