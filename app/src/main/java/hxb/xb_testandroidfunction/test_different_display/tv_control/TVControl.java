package hxb.xb_testandroidfunction.test_different_display.tv_control;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.FocusFinder;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hxb.xb_testandroidfunction.test_different_display.tv_control.view.RegLinkFocusView;


public class TVControl {
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


    private volatile long delayed = CLOSE_DELAYED;
    private volatile boolean[] isShowBefore = {false, false};
    private volatile SparseArray<RegLinkFocusView.LinkFocus> mSparseArray;
    private volatile View mRootView;
    private volatile RegLinkFocusView.LinkFocus mCurrentLinkFocus;
//    private volatile RegLinkFocusView.LinkFocus mPreLinkFocus;
    private volatile View mCurrentView, mPreView;


    private StateCallBack mStateCallBack;
    //表示第一次获取List中的第一个Item
    private boolean mZeroItem = true;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (delayed <= CLOSE_DELAYED) {
                        return false;

                    }
                    isShowBefore[1] = true;
                    if (null != mStateCallBack) {
                        mStateCallBack.onViewState(VIEW_AFTER_DELAY, mPreView, mCurrentView);
                    }
                    break;
            }
            return false;
        }
    });


    public static TVControl init() {

        return new TVControl();
    }

    private TVControl() {
    }

    public TVControl setStateCallBack(StateCallBack callBack) {
        this.mStateCallBack = callBack;
        return this;
    }

    public void switchLinkFocus(final RegLinkFocusView view, final int firstId) {
        if (null == view) {
            throw new NullPointerException("Can't be empty, please check the object");
        }
        view.setLifeCycle(new RegLinkFocusView.LifeCycle() {
            @Override
            public void onCreateFinish(int visibility) {
                mRootView = view.getRootView();
                mSparseArray = view.getLinkIds();
                RegLinkFocusView.LinkFocus linkFocus = mSparseArray.get(firstId);
                if (linkFocus == null) {
                    throw new NullPointerException("Did not find the first id child control");
                }

                mCurrentLinkFocus = mSparseArray.get(firstId);
                View view1 = mRootView.findViewById(mCurrentLinkFocus.getSelfId());
                if (view1 instanceof RecyclerView){
                    mCurrentView = ((RecyclerView)view1).getChildAt(0);
                }else if (view1 instanceof ListView){
                    mCurrentView = ((ListView)view1).getChildAt(0);
                }else {
                    mCurrentView = view1;
                }
            }
        });
    }

    public void setEventCode(int eventCode) {

        if (null == mSparseArray || mSparseArray.size() == 0 || null == mStateCallBack) {
            return;
        }

        boolean isList = inspectionProcessingList(mCurrentLinkFocus, eventCode);

        if (this.isShowBefore[0] && this.isShowBefore[1]) {
            mStateCallBack.onViewState(VIEW_BEFORE, this.mPreView, this.mCurrentView);
            updateSelectStatus();
            return;
        }

        if (!isList){
            int view_id = View.NO_ID;
            switch (eventCode) {
                case UP:
                    view_id = mCurrentLinkFocus.getUpId();
                    break;
                case DOWN:
                    view_id = mCurrentLinkFocus.getDownId();
                    break;
                case LEFT:
                    view_id = mCurrentLinkFocus.getLeftId();
                    break;
                case RIGHT:
                    view_id = mCurrentLinkFocus.getRightId();
                    break;
            }

            if (view_id != View.NO_ID) {
//                mPreLinkFocus = mCurrentLinkFocus;
                mCurrentLinkFocus = mSparseArray.get(view_id);
                //如果下一个容器是列表型的，就继续处理
                if (mCurrentLinkFocus.isSelfList()){
                    setEventCode(eventCode);
                    return;
                }

                mPreView = mCurrentView;
                mCurrentView = mRootView.findViewById(mCurrentLinkFocus.getSelfId());

                mStateCallBack.onViewState(VIEW_CURRENT, mPreView, mCurrentView);
                updateSelectStatus();

            }
        }

        mStateCallBack.onAction(eventCode, mCurrentView);
    }



    /*检查并处理列表型控件*/
    private boolean inspectionProcessingList(RegLinkFocusView.LinkFocus linkFocus, final int eventCode) {
        if (!linkFocus.isSelfList()) {
            return false;
        }
        View view = mRootView.findViewById(linkFocus.getSelfId());
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager linearLayoutManager = null;
            GridLayoutManager gridLayoutManager = null;

            if (mZeroItem) {
                mPreView = mCurrentView;
                mCurrentView = recyclerView.getChildAt(0);
                mZeroItem = false;
                mStateCallBack.onViewState(VIEW_CURRENT, mPreView, mCurrentView);
                updateSelectStatus();
                return true;
            }


            View view1 = null;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int orientation = linearLayoutManager.getOrientation();
                if (orientation == RecyclerView.HORIZONTAL) {
                    switch (eventCode) {
                        case LEFT:
                            view1 = FocusFinder.getInstance().findNextFocus(recyclerView, mCurrentView, View.FOCUS_LEFT);
                            break;
                        case RIGHT:
                            view1 = FocusFinder.getInstance().findNextFocus(recyclerView, mCurrentView, View.FOCUS_RIGHT);
                            break;
                    }
                } else {
                    switch (eventCode) {
                        case UP:
                            view1 = FocusFinder.getInstance().findNextFocus(recyclerView, mCurrentView, View.FOCUS_UP);
                            break;
                        case DOWN:
                            view1 = FocusFinder.getInstance().findNextFocus(recyclerView, mCurrentView, View.FOCUS_DOWN);
                            break;


                    }
                }

                int index = recyclerView.indexOfChild(view1);
                if (null == view1 || index == -1) {
                    mZeroItem = true;
                    recyclerView.scrollToPosition(0);
                    return false;
                }

                mPreView = mCurrentView;
                mCurrentView = view1;

                recyclerViewScroll(recyclerView, orientation, mCurrentView);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            mStateCallBack.onViewState(VIEW_CURRENT, mPreView, mCurrentView);
                            updateSelectStatus();
                        }
                    }
                });


            } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager1;



            }
            return true;
        }

        if (view instanceof ListView) {

            return true;
        }

        mZeroItem = true;
        return false;
    }

    /*recyclerView视图滚动*/
    private void recyclerViewScroll(RecyclerView recyclerView, int orientation, View itemView) {
        int listWidth = recyclerView.getWidth();
        int listHeight = recyclerView.getHeight();
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            int childRight = itemView.getRight();
            int childLeft = itemView.getLeft();
            int middlePlaceH = listWidth / 2;
            int childMidToLeft = (childRight - childLeft) / 2 + childLeft;
            if (childMidToLeft > middlePlaceH) {
                recyclerView.smoothScrollBy((childMidToLeft - middlePlaceH), 0);
            } else {
                recyclerView.smoothScrollBy(-(middlePlaceH - childMidToLeft), 0);
            }
        } else {
            int childTop = itemView.getTop();
            int childBottom = itemView.getBottom();
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
    public TVControl showCurrentFlyingFrame(boolean paramBoolean) {
        this.isShowBefore[0] = paramBoolean;
        this.isShowBefore[1] = paramBoolean;
        return this;
    }

    /*设置延时关闭飞框*/
    public TVControl delayCloseFlyingFrame(@IntRange(from = -1L, to = 3000L) long paramLong) {
        this.delayed = paramLong;
        return this;
    }


    public interface StateCallBack {
        void onViewState(int viewOperationSequence, View preView, View currentView);

        void onAction(int action, View currentView);
    }


}
