package hxb.xb_testandroidfunction.test_about_recyclerview.tvlistview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by hxb on  2019/12/26
 *
 * 问题：当该CustomizeGridRecyclerView 中有一定数量的子view时
 * 且 布局未完成\状态为View.GONE时 会有很大几率造成焦点飞掉。
 * 假想优化：保证CustomizeGridRecyclerView 中一定数量的子view 能快速布局\绘制完成。
 * 解决状态：未解决
 *
 * 弃用  2020-10-21
 */
@Deprecated
public final class CustomizeGridRecyclerView extends RecyclerView {
    private static final String TAG = "CustomizeRecyclerView";

    private Context mContext;
    //LayoutManager配置
    private static ManagerConfig mManagerConfig = null;
    //LayoutManager
    private CustomGridLayoutManager mGridLayoutManager;
    //按键快速滚动增量
    private final int FAST_SCROLL_INR = 1;
    private int fastScrollIncrement = FAST_SCROLL_INR;
    //是否可以纵向移出
    private boolean mCanFocusOutVertical = true;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;
    //焦点移出recyclerview的事件监听
    private FocusLostListener mFocusLostListener;
    //焦点移入recyclerview的事件监听
    private FocusGainListener mFocusGainListener;
    private OnTouchInterceptListener mOnTouchInterceptListener;
    private OnMotionInterceptListener mOnMotionInterceptListener;
    private OnKeyInterceptListener mOnKeyInterceptListener;
    private OnScrolledListener mOnScrolledListener;
    //默认第一次选中第一个位置
    private int mCurrentFocusPosition = 0;
    //是否滚动
    private boolean isScrolling = false;
    //记录滚动状态
    private int mScrolledStatus = OnScrolledListener.NO_SCROLLED;
    //临时记录当前焦点的view
    private View[] mTempRecordFocusViews = new View[2];
    //检测是否有手触碰选单
    private boolean isFinger = false;
    //记录上一次时间（limitKeySpeed()）
    private long beforeTime = 0;

    public CustomizeGridRecyclerView(@NonNull Context context) {
        this(context, null, 0);
    }

    public CustomizeGridRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomizeGridRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;

        /*setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
        this.setFocusable(true);*/


        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        setWillNotDraw(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setChildrenDrawingOrderEnabled(true);

        setClipChildren(false);
        setClipToPadding(false);

        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        /**
         防止RecyclerView刷新时焦点不错乱bug的步骤如下:
         (1)adapter执行setHasStableIds(true)方法
         (2)重写getItemId()方法,让每个view都有各自的id
         (3)RecyclerView的动画必须去掉
         */
        setItemAnimator(null);
    }


    /**
     * 解决4.4版本抢焦点的问题
     *
     * @return
     */
    @Override
    public boolean isInTouchMode() {
        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !super.isInTouchMode());
        } else {
            return super.isInTouchMode();
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }



    @Override
    public View focusSearch(int direction) {
        return super.focusSearch(direction);
    }

    //覆写focusSearch寻焦策略
    @Override
    public View focusSearch(View focused, int direction) {
        Log.i(TAG, "focusSearch " + focused + ",direction= " + direction);
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }
        if (view != null) {
            //该方法返回焦点view所在的父view,如果是在recyclerview之外，就会是null.所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    //屏蔽焦点纵向移出recyclerview
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
                    //屏蔽焦点横向移出recyclerview
                    return focused;
                }
                //调用移出的监听
                if (mFocusLostListener != null) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }
                return view;
            }
        }
        return view;
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        Log.i(TAG, "nextchild= " + child + ",focused = " + focused);
        if (!hasFocus()) {
            //recyclerview 子view 重新获取焦点，调用移入焦点的事件监听
            if (mFocusGainListener != null) {
                mFocusGainListener.onFocusGain(child, focused);
            }
        }
        Log.i(TAG, "focusPos 2= " + mCurrentFocusPosition);
        int focusPosition = getChildViewHolder(child).getLayoutPosition();
        Log.i(TAG, "focusPos = " + focusPosition);
        /*
         * 当上一次选中的position 和 当前带有焦点的position 的绝对值大于spanCount
         * 则认为已远离上一次position的位置，需要滚动到上一次选中的position。
         * 如果有手势操作则放弃这次操作。
         * */
        if (Math.abs(mCurrentFocusPosition - focusPosition) > mGridLayoutManager.getSpanCount() && !isFinger) {
            mTempRecordFocusViews[0] = child;
            mTempRecordFocusViews[1] = focused;
            mGridLayoutManager.smoothScrollToPosition(mCurrentFocusPosition, true);
        } else {
            super.requestChildFocus(child, focused);//执行过super.requestChildFocus之后hasFocus会变成true
            mCurrentFocusPosition = focusPosition;
        }

    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus()  || (view = getLayoutManager().findViewByPosition(mCurrentFocusPosition)) == null) {
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(view);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    /**
     *    * 控制当前焦点最后绘制，防止焦点放大后被遮挡
     *    * 原顺序123456789，当4是focus时，绘制顺序变为123567894
     *    * @param childCount
     *    * @param i
     *    * @return
     *    
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedChild = getFocusedChild();
        Log.i(TAG, "focusedChild =" + (focusedChild == null));
        if (focusedChild == null) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            int index = indexOfChild(focusedChild);
            Log.i(TAG, " index = " + index + ",i=" + i + ",count=" + childCount);
            if (i == childCount - 1) {
                return index;
            }
            if (i < index) {
                return i;
            }
            return i + 1;
        }

    }

    @Override
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        super.dispatchVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged: 显示是否改变  "+visibility );
        super.onVisibilityChanged(changedView, visibility);



    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d(TAG, "onWindowFocusChanged: 窗口是否有焦点 "+hasWindowFocus );
        super.onWindowFocusChanged(hasWindowFocus);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        /*防止焦点飞掉*/
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        boolean isConsumeFocus = false;
        if (action == KeyEvent.ACTION_DOWN) {
            View focusedView = getFocusedChild();  // 获取当前获得焦点的view
            View nextFocusView = null;
            boolean isNextFocusView = true;
            // 通过findNextFocus获取下一个需要得到焦点的view
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_RIGHT);
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_UP);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    nextFocusView = FocusFinder.getInstance().findNextFocus(this, focusedView, View.FOCUS_DOWN);
                    break;
                default:
                    isNextFocusView = false;//如果不是找下一个view的操作，就为false
                    break;
            }

            //检测是否请求过布局操作
            if (this.isLayoutRequested() && isNextFocusView){
                focusedView.requestFocus();
                return true;
            }


            // 如果获取失败（也就是说需要交给系统来处理焦点， 消耗掉事件，不让系统处理， 并让先前获取焦点的view获取焦点）
            if ((nextFocusView == null) && isNextFocusView) {
                isConsumeFocus = true;
                focusedView.requestFocus();
                /*
                * 目的是为了解决:
                * 以至于怎么按键方向操作，都不能获取下一个焦点view，而采取用滚动距离的办法使隐藏的view绘制显示出来。
                * 出现的原因：
                * 1，按键快速操作recyclerview的item滚动时，会飞掉焦点，这是因为下一个view 未绘制好，就没有焦点，
                * 事件就重新给系统去分配。
                * 2，按键操作recyclerview的item滚动时，滚动的距离刚好使下一个item卡在滚动方向上的边界上，
                * 而下一个完全遮挡的view是没有绘制，也就没有焦点的。
                * */
                if (mGridLayoutManager.isSmoothScrolling()){
                    return isConsumeFocus;
                }
                switch (keyCode){
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        isScrolling = true;
                        mGridLayoutManager.smoothScrollToPosition(mCurrentFocusPosition,false);
                        break;
                }
//                Log.e(TAG, "dispatchKeyEvent: ...................   "+fastScrollIncrement+"      "+focusedView.getVisibility());
            }
//            Log.e(TAG, "dispatchKeyEvent: IIIIIIIIIIII   "+(nextFocusView == null)+"    "+this.isLayoutRequested() +"    "+this.isInLayout()+"   "+this.isComputingLayout());
        }

        if (mOnKeyInterceptListener != null && mOnKeyInterceptListener.onInterceptKeyEvent(event)) {
            return true;
        }

        if (super.dispatchKeyEvent(event)) {
            return true;
        }

        return isConsumeFocus;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        isFinger = event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER;
        if (mOnTouchInterceptListener != null) {
            if (mOnTouchInterceptListener.onInterceptTouchEvent(event)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchGenericFocusedEvent(MotionEvent event) {
        if (mOnMotionInterceptListener != null) {
            if (mOnMotionInterceptListener.onInterceptMotionEvent(event)) {
                return true;
            }
        }
        return super.dispatchGenericFocusedEvent(event);
    }

    @Override
    public void onScrolled(int dx, int dy) {
//        super.onScrolled(dx, dy);
        if (mGridLayoutManager.getOrientation() == RecyclerView.HORIZONTAL) {
            Log.d(TAG, "onScrolled: HORIZONTAL:      " + this.canScrollHorizontally(dx) + "    dx=" + dx);
        } else {
            if (dy < 0) {//往上滚动
                mScrolledStatus = !this.canScrollVertically(dy) ? mScrolledStatus = OnScrolledListener.SCROLLED_START : OnScrolledListener.CAN_SCROLLED;
                dispatchScrolled();
                Log.d(TAG, "onScrolled: VERTICAL:  往上滚动    " + this.canScrollVertically(dy) + "       dy=" + dy);
            } else if (dy > 0) {//往下滚动
                mScrolledStatus = !this.canScrollVertically(dy) ? mScrolledStatus = OnScrolledListener.SCROLLED_END : OnScrolledListener.CAN_SCROLLED;
                dispatchScrolled();
                Log.d(TAG, "onScrolled: VERTICAL:  往下滚动    " + this.canScrollVertically(dy) + "       dy=" + dy);
            }
        }
//        Log.e(TAG, "onScrolled: >>>>>>>>>>>>>>>>>>>     是否到达底部 = "+isSlideToBottom() +"    "+dy);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == CustomizeGridRecyclerView.SCROLL_STATE_IDLE){
            isScrolling = false;
            fastScrollIncrement = FAST_SCROLL_INR;//还原
        }
        if (state == CustomizeGridRecyclerView.SCROLL_STATE_DRAGGING){
            isScrolling = true;
        }
    }


    /**
     * 限制按键速度
     * */
    private boolean limitKeySpeed(long limitTime){
        long nowTime = SystemClock.elapsedRealtime();
        if(nowTime - beforeTime <= limitTime) {
            Log.d(TAG, "limitKeySpeed: ........限制住了" );
            return true;
        }
        Log.d(TAG, "limitKeySpeed: 没限制住" );
        beforeTime = SystemClock.elapsedRealtime();
        return false;
    }

    /**
     * LayoutManager配置
     */
    private CustomGridLayoutManager getGridLayoutManager() {
        if (null == mManagerConfig) {//没有配置则默认
            mManagerConfig = new ManagerConfig();
            mGridLayoutManager = new CustomGridLayoutManager(mContext, mManagerConfig.spanCount);
            mGridLayoutManager.setOrientation(mManagerConfig.orientation);
        } else {
            mGridLayoutManager = new CustomGridLayoutManager(mContext, mManagerConfig.spanCount);
            mGridLayoutManager.setOrientation(mManagerConfig.orientation);
        }
        return mGridLayoutManager;
    }

    /**
     * 是否到达底部
     */
    private boolean isSlideToBottom() {
        return this.computeVerticalScrollExtent() + this.computeVerticalScrollOffset()
                >= this.computeVerticalScrollRange();
    }

    /**
     * 回调滚动状态
     */
    private void dispatchScrolled() {
        if (null == mOnScrolledListener) {
            return;
        }
        mOnScrolledListener.onScrolled(mScrolledStatus);
    }

    /**
     * 接口
     */
    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }

    /*
     * Listener for intercepting touch dispatch events.
     */
    public interface OnTouchInterceptListener {
        /*
         * Returns true if the touch dispatch event should be consumed.
         */
        boolean onInterceptTouchEvent(MotionEvent event);
    }

    /*
     * Listener for intercepting generic motion dispatch events.
     */
    public interface OnMotionInterceptListener {
        /*
         * Returns true if the touch dispatch event should be consumed.
         */
        boolean onInterceptMotionEvent(MotionEvent event);
    }

    /*
     * Listener for intercepting key dispatch events.
     */
    public interface OnKeyInterceptListener {
        /*
         * Returns true if the key dispatch event should be consumed.
         */
        boolean onInterceptKeyEvent(KeyEvent event);
    }

    /*
     * 监听滚动状态（滚动中，滚动到顶，滚动到底）
     * */
    public interface OnScrolledListener {
        int NO_SCROLLED = -1;//无滚动状态
        int SCROLLED_START = 1;//滚动停止在列表开始位置
        int SCROLLED_END = 2;//滚动停止在列表末尾位置
        int CAN_SCROLLED = 3;//可以滚动

        void onScrolled(int scrolled);
    }

    /**
     * 屏蔽焦点纵向移出recyclerview
     */
    public void setCanFocusOutVertical(boolean canFocusOutVertical) {
        mCanFocusOutVertical = canFocusOutVertical;
    }

    public boolean isCanFocusOutVertical() {
        return mCanFocusOutVertical;
    }

    /**
     * 屏蔽焦点横向移出recyclerview
     */
    public void setCanFocusOutHorizontal(boolean canFocusOutHorizontal) {
        mCanFocusOutHorizontal = canFocusOutHorizontal;
    }

    public boolean isCanFocusOutHorizontal() {
        return mCanFocusOutHorizontal;
    }

    /**
     * 焦点移出recyclerview的事件监听
     */
    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    /**
     * 焦点移入recyclerview的事件监听
     */
    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }

    /**
     * 获取当前选中的脚标
     */
    public int getCurrentFocusPosition() {
        return mCurrentFocusPosition;
    }

    /**
     * 临时记录当前有焦点的view
     */
    protected View[] getTempRecordFocusViews() {
        return mTempRecordFocusViews;
    }

    /**
     * 根据脚标在当前显示的item个数中 找到对应的item，没有则为null
     */
    public View findViewByPosition(int position) {
        int childCount = this.getChildCount();
        int midCount = childCount >>> 1;//相当于childCount / 2;
        for (int i = 0; i <= midCount; i++) {
            View view = this.getChildAt(i);
            if (this.getChildLayoutPosition(view) == position) {
                return view;
            }
            View view2 = this.getChildAt((childCount - 1 - i));
            if (this.getChildLayoutPosition(view2) == position) {
                return view2;
            }
        }

        return null;
    }

    /**
     * 监听选中回调
     */
    public void setOnChildSelectedListener(OnChildSelectedListener listener) {
        mGridLayoutManager.setOnChildSelectedListener(listener);
    }

    /**
     * Sets the touch intercept listener.
     */
    public void setOnTouchInterceptListener(OnTouchInterceptListener listener) {
        mOnTouchInterceptListener = listener;
    }

    /**
     * Sets the generic motion intercept listener.
     */
    public void setOnMotionInterceptListener(OnMotionInterceptListener listener) {
        mOnMotionInterceptListener = listener;
    }

    /**
     * Sets the key intercept listener.
     */
    public void setOnKeyInterceptListener(OnKeyInterceptListener listener) {
        mOnKeyInterceptListener = listener;
    }

    /**
     * 设置监听滚动状态
     */
    public void setOnScrolledListener(OnScrolledListener listener) {
        mOnScrolledListener = listener;
    }

    /**
     * 配置项设置接口
     */
    public ManagerConfig managerConfig() {
        mManagerConfig = new ManagerConfig();
        return mManagerConfig;
    }

    /**
     * LayoutManager配置类
     * 拓展业务时可以在配置类增加其它配置项
     */
    public final class ManagerConfig {
        public int orientation = RecyclerView.VERTICAL;//默认
        public int spanCount = 1;//默认
        public ItemDecoration itemDecoration = null;

        private ManagerConfig() {
        }

        public ManagerConfig setOrientation(@RecyclerView.Orientation int orientation) {
            this.orientation = orientation;
            return this;
        }

        public ManagerConfig setSpanCount(@IntRange(from = 1) int spanCount) {
            this.spanCount = spanCount;
            return this;
        }

        public void done() {
            setLayoutManager(getGridLayoutManager());
        }
    }

}
