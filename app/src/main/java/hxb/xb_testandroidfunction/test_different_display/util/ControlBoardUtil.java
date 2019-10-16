package hxb.xb_testandroidfunction.test_different_display.util;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;

public class ControlBoardUtil {
    public static final long CLOSE_DELAYED = -1L;

    public static final int DOWN = 2;

    public static final int EXIT = 5;

    public static final int LEFT = 3;

    public static final int OK = 0;

    public static final int PIP = 6;

    public static final int RIGHT = 4;

    public static final int UP = 1;

    public static final int VIEW_AFTER_DELAY = 3;

    public static final int VIEW_BEFORE = 1;

    public static final int VIEW_CURRENT = 2;


    private volatile ViewGroup rootViewGroup;

    private volatile long delayed = CLOSE_DELAYED;
    private volatile boolean[] isShowBefore = {false,false};
    private volatile EventCall eventCall;

    public static ControlBoardUtil init(ViewGroup rootViewGroup){
       return new ControlBoardUtil(rootViewGroup);
    }

    private ControlBoardUtil(ViewGroup rootViewGroup){

    }

    /*设置是否显示当前的飞框*/
    public ControlBoardUtil showCurrentFlyingFrame(boolean paramBoolean) {
        this.isShowBefore[0] = paramBoolean;
        this.isShowBefore[1] = paramBoolean;
        return this;
    }

    /*设置延时关闭飞框*/
    public ControlBoardUtil delayCloseFlyingFrame(@IntRange(from = -1L, to = 3000L) long paramLong) {
        this.delayed = paramLong;
        return this;
    }

    /*设置事件回调*/
    public void setEventCall(EventCall eventCall){
        this.eventCall = eventCall;
    }


    /*设置点击动作*/
    public void setAction(int action){

        if (this.isShowBefore[0] && this.isShowBefore[1]) {
            if (eventCall != null)
//                eventCall.onViewChange(1, this.tempViewGroup, this.preTempChildView, this.tempChildView);
            return;
        }

    }


    public interface EventCall {
        void onViewChange(int action, ViewGroup currentViewGroup, View preChildView, View currentChildView);

        void onAction(int action, int currentViewId);
    }

}
