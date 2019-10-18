package hxb.xb_testandroidfunction.test_different_display;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_different_display.presentation.VideoPresentation;

/**
 * 测试Android多屏异显示功能
 */
public class DifferentDisplayActivity extends AppCompatActivity implements View.OnClickListener {

    private DisplayManager displayManager;
    private Display[] displays;
    private VideoPresentation presentation;


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        this.displays = displayManager.getDisplays();
        setContentView(R.layout.activity_different_display);
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(displays.length > 1){
            if(getCurrentScreenOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                // 竖屏
                startScreening(true);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            if (getCurrentScreenOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                startScreening(false);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }


//        Intent i = new Intent(this, VideoService.class);
//        startForegroundService(i);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged: +++++++++++  "+newConfig.orientation );
        if(displays.length > 1){
            if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){// 竖屏
                // 竖屏
                startScreening(true);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){// 横屏
                startScreening(false);

            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayManager.registerDisplayListener(displayListener, mHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        displayManager.unregisterDisplayListener(displayListener);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Event.ItemEvent itemEvent = VideoPresentation.getItemEvent();
        if (null == itemEvent) {
            return;
        }
        switch (v.getId()) {
            case R.id.ok:
                itemEvent.onItemClick(Event.ItemEvent.OK);
                break;
            case R.id.up:
                itemEvent.onItemClick(Event.ItemEvent.UP);
                break;
            case R.id.down:
                itemEvent.onItemClick(Event.ItemEvent.DOWN);
                break;
            case R.id.left:
                itemEvent.onItemClick(Event.ItemEvent.LEFT);
                break;
            case R.id.right:
                itemEvent.onItemClick(Event.ItemEvent.RIGHT);
                break;
            case R.id.exit:
                itemEvent.onItemClick(Event.ItemEvent.EXIT);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("TAG", "onKeyDown:            " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (null != VideoPresentation.getItemEvent()) {
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.UP);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (null != VideoPresentation.getItemEvent()) {
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.DOWN);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (null != VideoPresentation.getItemEvent()) {
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.LEFT);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (null != VideoPresentation.getItemEvent()) {
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.RIGHT);
                }
               break;

            case KeyEvent.KEYCODE_BACK:
                if (null != VideoPresentation.getItemEvent()) {
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.EXIT);
                }
                break;
        }

        return true;
    }


    private DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            displays = displayManager.getDisplays();
            Log.e("TAG", "onDisplayAdded: ------------>>>1  "+displays.length);
            changeScreen((displays.length >1));
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            displays = displayManager.getDisplays();
            Log.e("TAG", "onDisplayAdded: ------------>>>2  "+displays.length);
            changeScreen((displays.length >1));
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.e("TAG", "onDisplayAdded: ------------>>>3  ");
        }
    };


    private void changeScreen(boolean isPortrait){
        if (isPortrait){
            if(getCurrentScreenOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            if(getCurrentScreenOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }


    }


    private void startScreening(boolean isStart) {
        if (isStart) {
            if (null != presentation){
                presentation.dismiss();
            }
            presentation = new VideoPresentation(DifferentDisplayActivity.this, displays[1]);
            presentation.show();
        } else {

            if (null != presentation){
                presentation.dismiss();
            }
            presentation = new VideoPresentation(DifferentDisplayActivity.this, displays[0]);
            presentation.show();



        }
    }

    private int getCurrentScreenOrientation(){
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        return mConfiguration.orientation; //获取屏幕方向
    }


}
