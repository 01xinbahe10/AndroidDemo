package hxb.xb_testandroidfunction.test_about_recyclerview.tvlist;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * Created by hxb on  2020/10/19
 */
public class TvManager extends GridLayoutManager {
    private static final String TAG = TvManager.class.getName();
    private static final boolean DEBUG = true;
    private Context mContext;
    private RecyclerView mRecyclerView;

    private volatile int mFocusPosition = NO_POSITION;

    public TvManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public TvManager(Context context, int spanCount) {
        super(context, spanCount);
        mContext = context;
    }

    public TvManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        mContext = context;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
    }

    /**
     * 该方法是修改绘制顺序（不是位置）
     * <p>
     * 将有焦点的子view放到最后一个绘制，避免有焦点的子view进行放大复位时出现覆盖
     */
    int getChildDrawingOrder(RecyclerView recyclerView, int childCount, int i) {
        View view = findViewByPosition(mFocusPosition);
        if (view == null) {
            return i;
        }
//        DebugLog.e(TAG, "getChildDrawingOrder--> childCount:"+childCount+"  i:"+i );
        int focusIndex = recyclerView.indexOfChild(view);
        // supposely 0 1 2 3 4 5 6 7 8 9, 4 is the center item
        // drawing order is 0 1 2 3 9 8 7 6 5 4
        if (i < focusIndex) {
            return i;
        } else if (i < childCount - 1) {
            return focusIndex + childCount - 1 - i;
        } else {
            return focusIndex;
        }
    }

    public int getSelection() {
        return mFocusPosition;
    }


    /**
     * 当焦点发生改变在RecyclerView容器上时，如果mFocusPosition在可视范围内则在焦点就落在
     * 等于mFocusPosition的脚标的view上，否则默认第一个子view;
     */
    void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            // if gridview.requestFocus() is called, select first focusable child.
            for (int i = mFocusPosition; ; i++) {
                View view = findViewByPosition(i);
                if (view == null) {
                    break;
                }
                if (view.getVisibility() == View.VISIBLE && view.hasFocusable()) {
                    view.requestFocus();
                    break;
                }
            }
        }
    }


    @Override
    public boolean onAddFocusables(@NonNull RecyclerView recyclerView, @NonNull ArrayList<View> views, int direction, int focusableMode) {
        if (recyclerView.hasFocus()) {
            final View focused = recyclerView.findFocus();
            final int focusedIndex = findImmediateChildIndex(focused);
            final int focusedPos = getAdapterPositionByIndex(focusedIndex);
            // TODO: 2020/10/19 经验证当  ArrayList<View> size != 0时 说明焦点已经没有在该recyclerView容器上了，表示焦点移出 
//            DebugLog.e(TAG, "onAddFocusables: >>>>>>>>>>>>>>>>>  focusedIndex: " + focusedIndex + "    focusedPos: " + focusedPos+"   "+views.size()+"  "+focusableMode);
        } else {
//            DebugLog.e(TAG, "onAddFocusables: 没有焦点  ");
        }
        return super.onAddFocusables(recyclerView, views, direction, focusableMode);
    }


    @Override
    public boolean onRequestChildFocus(@NonNull RecyclerView parent, @NonNull RecyclerView.State state, @NonNull View child, @Nullable View focused) {
//        DebugLog.e(TAG, "onRequestChildFocus: >>> 111 " );
        if (isScrolling.get()) {
            return true;
        }
        int position = getAdapterPositionByView(child);
        if (position == NO_POSITION) {
            // This is could be the last view in DISAPPEARING animation.
            //表示可能是最后一个子视图了
            return true;
        }
//        DebugLog.e(TAG, "onRequestChildFocus: >>> 222 " );
        //表示有焦点从外部进入该容器
        boolean isFocusEntry = !mRecyclerView.hasFocus() && child.hasFocus();
        //表示上一次真实的位置与当前获得焦点的view的位置之间的相差多少个position
        int positionSpan = Math.abs(mFocusPosition - position);
        if (isFocusEntry) {
            position = mFocusPosition == NO_POSITION ? 0 : mFocusPosition;
            mTempRecordFocusViews[0] = child;
        }

        smoothScrollToCenter(position, isFocusEntry, positionSpan);


//        DebugLog.e(TAG, "onRequestChildFocus: --> " + mFocusPosition + "   " + isFocusEntry+"  "+child.hasFocus()+"   "+focused.hasFocus());

        return true;
    }


    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View view, Rect rect,
                                                 boolean immediate) {
        DebugLog.v(TAG, "requestChildRectangleOnScreen " + view + " " + rect);
        return false;
    }

    private int findImmediateChildIndex(View view) {
        if (mRecyclerView != null && view != mRecyclerView) {
            view = findContainingItemView(view);
            if (view != null) {
                for (int i = 0, count = getChildCount(); i < count; i++) {
                    if (getChildAt(i) == view) {
                        return i;
                    }
                }
            }
        }
        return NO_POSITION;
    }

    private int getAdapterPositionByIndex(int index) {
        return getAdapterPositionByView(getChildAt(index));
    }


    private int getAdapterPositionByView(View view) {
        if (view == null) {
            return NO_POSITION;
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (params == null || params.isItemRemoved()) {
            // when item is removed, the position value can be any value.
            return NO_POSITION;
        }
        return params.getViewLayoutPosition();
    }


    /**
     * 滚动到指定位置
     *
     * @param position 指定位置
     * @param isRequestFocus 滚动到指定位置是否请求焦点
     */
    private AtomicBoolean isScrolling = new AtomicBoolean();

    protected void smoothScrollToCenter(int position, boolean isRequestFocus, int positionSpan) {
        isScrolling.set(true);
        mRecyclerView.stopScroll();
        boolean isFast = false;
        //如果position跨距大于了当前视图的个数，就先滚动到指定position，再实现居中滚动
        if (positionSpan > mRecyclerView.getChildCount()) {
            /*
             * scrollToPosition(int position)这个方法不是真正的滚动而是
             * 通过重新请求布局来实现到指定的位置。
             * 问题：会清理掉焦点
             * 解决：重写RecyclerView 的 onLayout()方法，
             * 在方法中通过GridManager来找到指定view的位置，
             * 进行焦点请求，这样焦点就不会逃离到其它控件上。
             * */
            scrollToPosition(position);
//            mRecyclerView.stopScroll();
            isRequestFocus = false;
//            ((CenterScroller) smoothScroller).setSlidingLevel( CenterScroller.SPEED_EXTREMELY_FAST);
            DebugLog.e(TAG, "smoothScrollToCenter:1111 >>  isRequestFocus：" + isRequestFocus);
            isFast = true;
        }

        CenterScroller smoothScroller = new CenterScroller(mContext, isRequestFocus);

        if (isFast) {
            smoothScroller.setSlidingLevel(CenterScroller.SPEED_EXTREMELY_SLOW);
        } else {
            smoothScroller.setSlidingLevel(limitKeySpeed(300) ? CenterScroller.SPEED_FAST : CenterScroller.SPEED_DEF);

        }
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
        DebugLog.e(TAG, "smoothScrollToCenter:2222 >>：" + isRequestFocus);
    }


    /**
     * 自定义滚动效果的Scroller
     */
    //临时记录当前焦点的view
    private View[] mTempRecordFocusViews = new View[2];

    @IntDef({CenterScroller.SPEED_DEF, CenterScroller.SPEED_SLOW,
            CenterScroller.SPEED_FAST, CenterScroller.SPEED_EXTREMELY_FAST,
            CenterScroller.SPEED_EXTREMELY_SLOW})
    @Retention(RetentionPolicy.CLASS)
    public @interface SpeedLevel {
    }

    private class CenterScroller extends LinearSmoothScroller {

        private final static int MIN_MS_SMOOTH_SCROLL_MAIN_SCREEN = 30;

        /*
         * 这里只是简单的将滑动速率分为这几个级数，如果以后要求更多，
         * 可自行增加
         * */

        public final static int SPEED_DEF = 0;//默认
        public final static int SPEED_SLOW = 1;//慢滑
        public final static int SPEED_FAST = 2;//快滑
        public final static int SPEED_EXTREMELY_FAST = 3;//极快滑
        public final static int SPEED_EXTREMELY_SLOW = 4;//极慢滑
        private int mSlidingLevel = 0;//表示滑动级数

        private boolean isRequestFocus;

        public CenterScroller(Context context, boolean isRequestFocus) {
            super(context);
            this.isRequestFocus = isRequestFocus;
        }

        public void setSlidingLevel(@SpeedLevel int level) {
            mSlidingLevel = level;
        }

        // 这里计算滚动到中部的偏移量
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            float rollingDistance = (boxStart + (boxEnd - boxStart) / 2f) - (viewStart + (viewEnd - viewStart) / 2f);
//            DebugLog.e(TAG, " 计算滚动到中部的偏移量   " + rollingDistance);
            return (int) rollingDistance;
        }

        //计算减速时间
        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return super.calculateTimeForDeceleration(dx);
        }

        //计算滚动时间
        @Override
        protected int calculateTimeForScrolling(int dx) {
            int ms = super.calculateTimeForScrolling(dx);
//            DebugLog.e(TAG, "calculateTimeForScrolling: 11>>    " + dx + "   " + ms);
            return ms;


            /*
             * 以下计算方式是效仿androidx.leanback.widget.GridLayoutManager类部内
             * GridLinearSmoothScroller类中的calculateTimeForScrolling(int dx)
             * 计算方法。
             * 经过验证，以下方法计算的滚东时间跟super.calculateTimeForScrolling(dx)
             * 的滚动时间一致，以下方法就此注释
             * */
            /*int sideLength = 0;
            int orientation = TvManager.this.getOrientation();
            if (orientation == RecyclerView.VERTICAL){
                sideLength = mRecyclerView.getHeight();
            }else {
                sideLength = mRecyclerView.getWidth();
            }
            if (sideLength > 0){
                float minMs = (float) MIN_MS_SMOOTH_SCROLL_MAIN_SCREEN
                        / sideLength * dx;
                if (ms < minMs) {
                    ms = (int) minMs;
                }
                DebugLog.e(TAG, "calculateTimeForScrolling: 22>>    " + dx + "   " + ms);
            }

            return ms;
            */
        }

        // 滚动速度控制
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            switch (mSlidingLevel) {
                case SPEED_SLOW:
                    return 33f / displayMetrics.densityDpi;

                case SPEED_FAST:
                    return 17f / displayMetrics.densityDpi;

                case SPEED_EXTREMELY_FAST:
                    return 9f / displayMetrics.densityDpi;

                case SPEED_EXTREMELY_SLOW:
                    return 41f / displayMetrics.densityDpi;

                default:
                    return super.calculateSpeedPerPixel(displayMetrics);
            }
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            super.onTargetFound(targetView, state, action);

        }


        @Override
        protected void onStop() {
            DebugLog.e(TAG, "onStop >> isRequestFocus:" + isRequestFocus);
            if (isRequestFocus) {
                /*
                 * 滚动到指定位置请求焦点。
                 * 在RecyclerView的可视范围内(也就是RecyclerView.getChildCount())
                 * */
                /*for (View view : mTempRecordFocusViews) {
                    if (null == view) {
                        continue;
                    }
                    if (mFocusPosition == NO_POSITION || mFocusPosition == getTargetPosition()) {
                        continue;
                    }
                    view.clearFocus();
                }*/

                View targetView = findViewByPosition(getTargetPosition());
                if (targetView != null) {
                    targetView.requestFocus();
                }

            }

            super.onStop();
            int currentFocusPosition = getTargetPosition();
            if (mFocusPosition != currentFocusPosition) {
                mFocusPosition = currentFocusPosition;
            }

            setSlidingLevel(SPEED_DEF);//复位
            isScrolling.set(false);

            DebugLog.e(TAG, "结束！！！！！！！！！！！！！！！  " + this.isRunning());


        }
    }


    /**
     * 限制按键速度
     *
     * @param limitTime 毫秒数
     */
    //记录上一次时间
    private long beforeTime = 0;

    private boolean limitKeySpeed(long limitTime) {
        long nowTime = SystemClock.elapsedRealtime();
        if (nowTime - beforeTime <= limitTime) {
//            DebugLog.d(TAG, "limitKeySpeed: ........限制住了" );
            beforeTime = nowTime;
            return true;
        }
//        DebugLog.d(TAG, "limitKeySpeed: 没限制住" );
        beforeTime = nowTime;
        return false;
    }

}
