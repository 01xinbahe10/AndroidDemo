package hxb.xb_testandroidfunction.test_different_display.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_different_display.Event;
import hxb.xb_testandroidfunction.test_different_display.util.ControlBoardUtil;

public class VideoPresentation extends Presentation implements Event.ItemEvent, ControlBoardUtil.EventCall {
    private static ControlBoardUtil controlBoardUtil;
    private ConstraintLayout mClParent;
    private RecyclerView mRvMovie,mRvApp;
    private VideoAdapter mMovieAdapter,mAppAdapter;


    private static Event.ItemEvent itemEvent;
    public static Event.ItemEvent getItemEvent(){
       return itemEvent;
    }

    public VideoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public VideoPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemEvent = this;
        setContentView(R.layout.view_presentation_video);
        mClParent = findViewById(R.id.clParent);
        mRvMovie = findViewById(R.id.rvVideoList);
        mRvApp = findViewById(R.id.rvAppList);


        mRvMovie.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration movieItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        movieItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.d_shape_divider_item_decoration_v));
        mRvMovie.addItemDecoration(movieItemDecoration);
        mMovieAdapter = new VideoAdapter(getContext(),VideoAdapter.ITEM_MOVIE_LIST);
        mRvApp.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DividerItemDecoration appItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL);
        appItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.d_shape_divider_item_decoration_h));
        mRvApp.addItemDecoration(appItemDecoration);
        mAppAdapter = new VideoAdapter(getContext(),VideoAdapter.ITEM_APP_LIST);

        mRvMovie.setAdapter(mMovieAdapter);
        mRvApp.setAdapter(mAppAdapter);

        simulationData();


        controlBoardUtil = ControlBoardUtil.init(mClParent,R.id.rvVideoList).showCurrentFlyingFrame(true).delayCloseFlyingFrame(2000L);
        controlBoardUtil.setEventCall(VideoPresentation.this);

    }

    @Override
    public void onItemClick(int action) {
        Log.e("TAG", "onItemClick: bbbbbbbttttt  "+action );
        if (null != controlBoardUtil) {
            controlBoardUtil.setAction(action);
        }
    }

    @Override
    public void onViewChange(int viewOperationSequence, ViewGroup currentViewGroup, View preChildView, View currentChildView) {
        switch (viewOperationSequence){
            case ControlBoardUtil.VIEW_BEFORE:
                setViewState(currentChildView,true);
                break;
            case ControlBoardUtil.VIEW_CURRENT:
                setViewState(preChildView,false);
                setViewState(currentChildView,true);
                break;
            case ControlBoardUtil.VIEW_AFTER_DELAY:
                setViewState(currentChildView,false);
                break;
        }
    }

    @Override
    public void onAction(int action, int currentViewId) {

    }


    private void setViewState(View paramView, boolean paramBoolean) {
        if (paramView == null)
            return;

        Log.e("TAG", "setViewState:    "+(paramView.getId() == R.id.rvVideoList));
        paramView.setSelected(paramBoolean);
    }


    private void simulationData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoVO.MovieListVO> movieList = new ArrayList<>();
                List<VideoVO.AppListVO> appList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    VideoVO.MovieListVO movieListVO  = new VideoVO.MovieListVO();
                    movieListVO.setMovieTitle("电影"+i);
                    movieListVO.setMovieIntroduction("电影简介。。  "+i);
                    movieList.add(movieListVO);

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
            }
        }).start();
    }
}
