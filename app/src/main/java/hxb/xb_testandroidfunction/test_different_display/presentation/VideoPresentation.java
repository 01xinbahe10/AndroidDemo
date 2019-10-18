package hxb.xb_testandroidfunction.test_different_display.presentation;

import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_different_display.Event;
import hxb.xb_testandroidfunction.test_different_display.util.ControlBoardUtil;

public class VideoPresentation extends Presentation implements Event.ItemEvent, ControlBoardUtil.EventCall {
    private String TAG = "VideoPresentation";

    private static ControlBoardUtil controlBoardUtil;
    private ConstraintLayout mClParent;
    private RecyclerView mRvMovie, mRvApp;
    private VideoAdapter mMovieAdapter, mAppAdapter;

    private volatile boolean mIsPlaying = false;
    private volatile int mCurrentPlayPosition = 0;
    private volatile String mCurrentPlayPath = "";

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;


    private static Event.ItemEvent itemEvent;

    public VideoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public VideoPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    public static Event.ItemEvent getItemEvent() {
        return itemEvent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemEvent = this;
        setContentView(R.layout.view_presentation_video);
        mClParent = findViewById(R.id.clParent);
        surfaceView = findViewById(R.id.videoView);
        mRvMovie = findViewById(R.id.rvVideoList);
        mRvApp = findViewById(R.id.rvAppList);


        mRvMovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration movieItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        movieItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.d_shape_divider_item_decoration_v));
        mRvMovie.addItemDecoration(movieItemDecoration);
        mMovieAdapter = new VideoAdapter(getContext(), VideoAdapter.ITEM_MOVIE_LIST);
        mRvApp.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DividerItemDecoration appItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        appItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.d_shape_divider_item_decoration_h));
        mRvApp.addItemDecoration(appItemDecoration);
        mAppAdapter = new VideoAdapter(getContext(), VideoAdapter.ITEM_APP_LIST);

        mRvMovie.setAdapter(mMovieAdapter);
        mRvApp.setAdapter(mAppAdapter);

        simulationData();


        controlBoardUtil = ControlBoardUtil.init(mClParent, R.id.rvVideoList).showCurrentFlyingFrame(true).delayCloseFlyingFrame(2000L);
        controlBoardUtil.setEventCall(VideoPresentation.this);


        initMediaPlayer(surfaceView);


        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                terminatePlayer();
            }
        });

    }

    @Override
    public void onItemClick(int action) {
        Log.e(TAG, "onAction: ////////////////////////////////////////   "+action );
        if (null != controlBoardUtil) {
            controlBoardUtil.setAction(action);
        }
    }

    @Override
    public void onViewChange(int viewOperationSequence, ViewGroup currentViewGroup, View preChildView, View currentChildView) {
        switch (viewOperationSequence) {
            case ControlBoardUtil.VIEW_BEFORE:
                setViewState(currentChildView, true);
                break;
            case ControlBoardUtil.VIEW_CURRENT:
                setViewState(preChildView, false);
                setViewState(currentChildView, true);
                break;
            case ControlBoardUtil.VIEW_AFTER_DELAY:
                setViewState(currentChildView, false);
                break;
        }
    }

    @Override
    public void onAction(int action, View currentViewGroup, View currentChildView) {
        if (currentViewGroup instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) currentViewGroup;
            switch (currentViewGroup.getId()) {
                case R.id.rvVideoList:
                    if (action != OK) {
                        break;
                    }
                    int position = recyclerView.getChildAdapterPosition(currentChildView);
                    VideoVO.MovieListVO movieListVO = mMovieAdapter.getMovieListVOS().get(position);
                    playVideo(movieListVO.getUrl());
                    break;
            }
        }

        switch (currentChildView.getId()) {
            case R.id.rlVideoView:
                if (action != OK) {
                    break;
                }
                if (mIsPlaying) {
                    pauseVideo();
                } else {
                    playVideo(mCurrentPlayPath);
                }
                break;
        }

    }


    private void setViewState(View paramView, boolean paramBoolean) {
        if (paramView == null)
            return;

        Log.e("TAG", "setViewState:    " + (paramView.getId() == R.id.rvVideoList));
        paramView.setSelected(paramBoolean);
    }


    /*初始化播放器*/
    private void initMediaPlayer(SurfaceView surfaceView) {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    /*
     * 播放视频
     * */
    private void playVideo(String url) {
        if (TextUtils.equals(mCurrentPlayPath, url) && null != mediaPlayer) {
            if (!mIsPlaying) {
                mediaPlayer.seekTo(mCurrentPlayPosition);
                mediaPlayer.start();
                initPlayerListener(mediaPlayer);
            }
            return;
        }

        mediaPlayer.reset();//重启
        mCurrentPlayPath = url;//记录当前播放的视频地址
        initPlayerListener(mediaPlayer);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();//准备
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mIsPlaying = true;
    }

    /*
     * 暂停播放
     */
    private void pauseVideo() {
        mIsPlaying = false;
        if (null == mediaPlayer){
            return;
        }
        mCurrentPlayPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    /*终止播放,并清理*/
    private void terminatePlayer() {
        mIsPlaying = false;
        if (null == mediaPlayer){
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    //初始化播放监听
    private void initPlayerListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrentPlayPosition = 0;
//                stopPlayback();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mCurrentPlayPosition = 0;
//                stopPlayback();
                return false;
            }
        });
    }


    private void simulationData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoVO.MovieListVO> movieList = new ArrayList<>();
                VideoVO.MovieListVO movieListVO = new VideoVO.MovieListVO();
                movieListVO.setMovieTitle("电影测试");
                movieListVO.setMovieIntroduction("电影简介。。  ");
                movieListVO.setUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");

                VideoVO.MovieListVO movieListVO2 = new VideoVO.MovieListVO();
                movieListVO2.setMovieTitle("电影测试");
                movieListVO2.setMovieIntroduction("电影简介。。  ");
                movieListVO2.setUrl("https://dd.robot-elf.com/Upload/edu_film/zh_CN/2018-08-15/060.mp4");

                VideoVO.MovieListVO movieListVO3 = new VideoVO.MovieListVO();
                movieListVO3.setMovieTitle("电影测试");
                movieListVO3.setMovieIntroduction("电影简介。。  ");
                movieListVO3.setUrl("Upload/edu_film/zh_CN/2018-08-15/080.mp4");

                VideoVO.MovieListVO movieListVO4 = new VideoVO.MovieListVO();
                movieListVO4.setMovieTitle("电影测试");
                movieListVO4.setMovieIntroduction("电影简介。。  ");
                movieListVO4.setUrl("https://dd.robot-elf.com/Upload/edu_film/zh_TW/2018-08-15/%E5%81%A5%E8%A8%BAb2005.mp4");

                VideoVO.MovieListVO movieListVO5 = new VideoVO.MovieListVO();
                movieListVO5.setMovieTitle("电影测试");
                movieListVO5.setMovieIntroduction("电影简介。。  ");
                movieListVO5.setUrl("https://dd.robot-elf.com/Upload/edu_film/zh_TW/2018-08-15/%E5%81%A5%E8%A8%BAb6001.mp4");

                movieList.add(movieListVO);
                movieList.add(movieListVO2);
                movieList.add(movieListVO3);
                movieList.add(movieListVO4);
                movieList.add(movieListVO5);


                List<VideoVO.AppListVO> appList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    VideoVO.AppListVO appListVO = new VideoVO.AppListVO();
                    appListVO.setAppIcon(R.mipmap.ic_launcher);
                    appList.add(appListVO);
                }

                mClParent.post(new Runnable() {
                    @Override
                    public void run() {
                        mMovieAdapter.addMovieList(movieList);
                        mAppAdapter.addAppList(appList);



                    }
                });

                mClParent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playVideo(mMovieAdapter.getMovieListVOS().get(0).getUrl());
                    }
                },3000);
            }
        }).start();
    }

}
