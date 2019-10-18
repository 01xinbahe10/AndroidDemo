package hxb.xb_testandroidfunction.test_different_display.util;

import android.os.Handler;
import android.os.Message;
import android.system.ErrnoException;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class ControlBoardUtil {
    public static final long CLOSE_DELAYED = -1L;

    public static final int OK = 0;

    public static final int UP = 1;

    public static final int DOWN = 2;

    public static final int LEFT = 3;

    public static final int RIGHT = 4;

    public static final int EXIT = 5;

    public static final int PIP = 6;

    public static final int VIEW_BEFORE = 1;

    public static final int VIEW_CURRENT = 2;

    public static final int VIEW_AFTER_DELAY = 3;

    private volatile ViewGroup rootViewGroup;
    private volatile ViewGroup tempViewGroup;
    private volatile View preTempChildView;
    private volatile View tempChildView;
    private volatile LinkedList<ViewGroup> level;

    private volatile long delayed = CLOSE_DELAYED;
    private volatile boolean[] isShowBefore = {false, false};
    private volatile EventCall eventCall;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (delayed <= CLOSE_DELAYED) {
                        return false;

                    }
                    isShowBefore[1] = true;
                    if (eventCall != null) {
                        eventCall.onViewChange(VIEW_AFTER_DELAY, tempViewGroup, preTempChildView, tempChildView);
                    }
                    break;
            }
            return false;
        }
    });

    public static ControlBoardUtil init(ViewGroup rootViewGroup, String firstChildTag) {
        return new ControlBoardUtil(rootViewGroup, firstChildTag);
    }


    public static ControlBoardUtil init(ViewGroup rootViewGroup, int firstChildId) {
        return new ControlBoardUtil(rootViewGroup, firstChildId);
    }

    private ControlBoardUtil(ViewGroup rootViewGroup, String firstChildTag) {
        this.rootViewGroup = rootViewGroup;
        this.level = new LinkedList<>();
        this.level.addFirst(this.rootViewGroup);
        this.tempViewGroup = this.level.getLast();
        if (this.tempViewGroup.getChildCount() > 0) {
            this.tempChildView = this.tempViewGroup.findViewWithTag(firstChildTag);
        } else {
            throw new NullPointerException("Did not find the first labeled child control");
        }

    }

    private ControlBoardUtil(ViewGroup rootViewGroup, int firstChildId) {
        this.rootViewGroup = rootViewGroup;
        this.level = new LinkedList<>();
        this.level.addFirst(this.rootViewGroup);
        this.tempViewGroup = this.level.getLast();
        if (this.tempViewGroup.getChildCount() > 0) {
            this.tempChildView = this.tempViewGroup.findViewById(firstChildId);
        } else {
            throw new NullPointerException("Did not find the first id child control");
        }

    }

    /*刷新列表视图滚动状态(适用于单列或单行)*/
    private void updateListScrollState() {
        if (!(this.tempViewGroup instanceof RecyclerView))
            return;
        RecyclerView recyclerView = (RecyclerView) this.tempViewGroup;
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager))
            return;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int listWidth = recyclerView.getWidth();
        int listHeight = recyclerView.getHeight();

        if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            int childRight = this.tempChildView.getRight();
            int childLeft = this.tempChildView.getLeft();
            int middlePlaceH = listWidth / 2;
            int childMidToLeft = (childRight - childLeft) / 2 + childLeft;
            if (childMidToLeft > middlePlaceH) {
                recyclerView.smoothScrollBy((childMidToLeft - middlePlaceH), 0);
            } else {
                recyclerView.smoothScrollBy(-(middlePlaceH - childMidToLeft), 0);
            }
        } else {
            int childTop = this.tempChildView.getTop();
            int childBottom = this.tempChildView.getBottom();
            int middlePlaceV = listHeight / 2;
            int childMidToTop = (childBottom - childTop) / 2 + childTop;
            if (childMidToTop > middlePlaceV) {
                recyclerView.smoothScrollBy(0, (childMidToTop - middlePlaceV));
            } else {
                recyclerView.smoothScrollBy(0, -(middlePlaceV - childMidToTop));
            }
        }
    }


    /*更新延时后的视图状态*/
    private void updateSelectStatus() {
        this.isShowBefore[1] = false;
        if (this.delayed > CLOSE_DELAYED) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, this.delayed);
        }
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
    public void setEventCall(EventCall eventCall) {
        this.eventCall = eventCall;
    }


    /*设置点击动作*/
    public void setAction(int action) {


        if (this.isShowBefore[0] && this.isShowBefore[1]) {
            if (eventCall != null) {
                eventCall.onViewChange(VIEW_BEFORE, this.tempViewGroup, this.preTempChildView, this.tempChildView);
            }
            updateSelectStatus();
            return;
        }

        View view = null;
        switch (action) {
            case OK:
                if (this.tempChildView instanceof RecyclerView) {
                    this.level.addLast((ViewGroup) this.tempChildView);
                    this.tempViewGroup = this.level.getLast();
                    if (this.tempViewGroup.getChildCount() > 0) {
                        view = this.tempViewGroup.getChildAt(0);
                        Log.e("TAG", "setAction: " + tempViewGroup.getChildCount());
                    }
                }
                break;
            case UP:
                view = FocusFinder.getInstance().findNextFocus(this.tempViewGroup, this.tempChildView, View.FOCUS_UP);
                break;
            case DOWN:
                view = FocusFinder.getInstance().findNextFocus(this.tempViewGroup, this.tempChildView, View.FOCUS_DOWN);
                break;
            case LEFT:
                view = FocusFinder.getInstance().findNextFocus(this.tempViewGroup, this.tempChildView, View.FOCUS_LEFT);
                break;
            case RIGHT:
                view = FocusFinder.getInstance().findNextFocus(this.tempViewGroup, this.tempChildView, View.FOCUS_RIGHT);
                break;
            case EXIT:
                if (this.level.size() > 1) {
                    if (this.level.getLast() instanceof RecyclerView) {
                        //复原
                        ((RecyclerView) this.level.getLast()).scrollToPosition(0);
                        view = this.level.removeLast();
                        this.tempViewGroup = this.level.getLast();
                    }
                }
                break;
        }

        if (view != null) {
            if (this.eventCall == null)
                return;
            Log.e("TAG", "setAction:?????????//////////   " + tempViewGroup.indexOfChild(view));
            //验证当前选中的视图是否在当前的视图容器中
            if (this.tempViewGroup.indexOfChild(view) == -1)
                return;

            this.preTempChildView = this.tempChildView;
            this.tempChildView = view;
            updateListScrollState();
            this.eventCall.onViewChange(VIEW_CURRENT, this.tempViewGroup, this.preTempChildView, this.tempChildView);
            updateSelectStatus();
        }

        if (eventCall != null)
            eventCall.onAction(action, this.tempViewGroup, this.tempChildView);

    }


    public interface EventCall {
        void onViewChange(int viewOperationSequence, ViewGroup currentViewGroup, View preChildView, View currentChildView);

        void onAction(int action, View currentViewGroup, View currentChildView);
    }

}
