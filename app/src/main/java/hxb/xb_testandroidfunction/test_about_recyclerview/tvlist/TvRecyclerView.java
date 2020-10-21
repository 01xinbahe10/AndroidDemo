package hxb.xb_testandroidfunction.test_about_recyclerview.tvlist;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

/**
 * Created by hxb on  2020/10/19
 */
public class TvRecyclerView extends RecyclerView {
    private static final String TAG = TvRecyclerView.class.getName();

    @Override
    public void focusableViewAvailable(View v) {
        super.focusableViewAvailable(v);
    }

    private TvManager mManager;
    private boolean mHasOverlappingRendering = true;
    private OnTouchInterceptListener mOnTouchInterceptListener;
    private OnMotionInterceptListener mOnMotionInterceptListener;
    private OnKeyInterceptListener mOnKeyInterceptListener;
    private OnUnhandledKeyListener mOnUnhandledKeyListener;
    private OnScrolledListener mOnScrolledListener;

    public TvRecyclerView(@NonNull Context context) {
        this(context, null, -1);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setHasFixedSize(true);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        // Disable change animation by default on leanback.
        // Change animation will create a new view and cause undesired
        // focus animation between the old view and new view.
//        getItemAnimator().setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);



        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int count = TvRecyclerView.this.getChildCount();
                for (int i = 0; i < count; i++) {
                    DebugLog.e(TAG, "onGlobalLayout: "+TvRecyclerView.this.getChildAt(i).hasFocus() );
                }
                int focusPosition = getGridLayoutManager().getSelection();
                View view = getGridLayoutManager().findViewByPosition(focusPosition);
                if (null != view){
                    view.requestFocus();
                }


                DebugLog.e(TAG,"onLayout11  "+(view == null)+"   "+focusPosition);
            }
        });


    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
//        DebugLog.e(TAG, "getChildDrawingOrder--> childCount:"+childCount+"  i:"+i );
//        return super.getChildDrawingOrder(childCount, i);
        return getGridLayoutManager().getChildDrawingOrder(this, childCount, i);
    }


    @Override
    public View focusSearch(int direction) {
        DebugLog.e(TAG, "focusSearch--> direction:" + direction);

        if (isFocused()) {
            // focusSearch(int) is called when GridView itself is focused.
            // Calling focusSearch(view, int) to get next sibling of current selected child.
            View view = getGridLayoutManager().findViewByPosition(getGridLayoutManager().getSelection());
            if (view != null) {
                return focusSearch(view, direction);
            }
        }
        // otherwise, go to mParent to perform focusSearch
        return super.focusSearch(direction);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        DebugLog.e(TAG, "onFocusChanged--> gainFocus:" + gainFocus + "  direction:" + direction + "  previouslyFocusedRect:" + previouslyFocusedRect.top + "  " + previouslyFocusedRect.bottom + "  " + previouslyFocusedRect.left + "   " + previouslyFocusedRect.right);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        getGridLayoutManager().onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


    /**
     * 向子view询问焦点的逻辑，区分正反两种查找方向。只要有一个view成功获取到焦点，就返回true。
     */
    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
//        DebugLog.e(TAG, "onRequestFocusInDescendants--> direction:" + direction + "  previouslyFocusedRect:" + previouslyFocusedRect.top + "  " + previouslyFocusedRect.bottom + "  " + previouslyFocusedRect.left + "  " + previouslyFocusedRect.right);
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mOnKeyInterceptListener != null && mOnKeyInterceptListener.onInterceptKeyEvent(event)) {
            return true;
        }
        if (super.dispatchKeyEvent(event)) {
            return true;
        }
        return mOnUnhandledKeyListener != null && mOnUnhandledKeyListener.onUnhandledKey(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnTouchInterceptListener != null) {
            if (mOnTouchInterceptListener.onInterceptTouchEvent(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        if (mOnMotionInterceptListener != null) {
            if (mOnMotionInterceptListener.onInterceptMotionEvent(event)) {
                return true;
            }
        }
        return super.dispatchGenericFocusedEvent(event);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return super.hasOverlappingRendering();
    }

    /**
     * 该方法用来标记当前view是否存在过度绘制，存在返回ture，不存在返回false，默认返回为true。
     * 返回false后，android会自动进行合理的优化，避免使用offscreen buffer
     */
    public void setHasOverlappingRendering(boolean hasOverlapping) {
        mHasOverlappingRendering = hasOverlapping;
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        DebugLog.e(TAG, "onRtlPropertiesChanged--> layoutDirection:" + layoutDirection);
    }

    @Override
    public void scrollToPosition(int position) {
//        super.scrollToPosition(position);
        int positionSpan = Math.abs(getGridLayoutManager().getSelection() - position);
        getGridLayoutManager().smoothScrollToCenter(position, true, positionSpan);
    }

    @Override
    public void smoothScrollToPosition(int position) {
//        super.smoothScrollToPosition(position);
        int positionSpan = Math.abs(getGridLayoutManager().getSelection() - position);
        getGridLayoutManager().smoothScrollToCenter(position, true, positionSpan);
    }


    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }


    @Override
    public void onScrolled(int dx, int dy) {
        int scrollingState = OnScrolledListener.NO_SCROLLED;
        String msg = "无滚动方向";
        if (getGridLayoutManager().getOrientation() == RecyclerView.HORIZONTAL) {
            boolean isCanScrolling = this.canScrollHorizontally(dx);

            if (dx < 0) {//向右滚动
                scrollingState = isCanScrolling ? OnScrolledListener.CAN_SCROLLED : OnScrolledListener.SCROLLED_START;
                msg = "从左往右滚动→";
            } else if (dx > 0) {//向左滚动
                scrollingState = isCanScrolling ? OnScrolledListener.CAN_SCROLLED : OnScrolledListener.SCROLLED_END;
                msg = "从右往左滚动←";
            }

//            DebugLog.d(TAG, "onScrolled: HORIZONTAL:" + msg + "  是否滚动:" + isCanScrolling + "  dx:" + dx + "  scrollingState:" + scrollingState);

        } else {
            boolean isCanScrolling = this.canScrollVertically(dy);
            if (dy < 0) {//往下滚动
                scrollingState = isCanScrolling ? OnScrolledListener.CAN_SCROLLED : OnScrolledListener.SCROLLED_START;
                msg = "从上往下滚动↓";
            } else if (dy > 0) {//向上滚动
                scrollingState = isCanScrolling ? OnScrolledListener.CAN_SCROLLED : OnScrolledListener.SCROLLED_END;
                msg = "从下往上滚动↑";
            }
//            DebugLog.d(TAG, "onScrolled: VERTICAL:" + msg + "  是否滚动:" + isCanScrolling + "  dy:" + dy + "  scrollingState:" + scrollingState);
        }

        refreshScrollingState(scrollingState);
    }


    /**
     * 刷新滚动状态
     */
    private void refreshScrollingState(int scrollingState) {
        if (null == mOnScrolledListener) {
            return;
        }
        mOnScrolledListener.onScrolled(scrollingState);
    }

    /**
     * LayoutManager
     */
    public TvManager getGridLayoutManager() {
        if (null != mManager) {
            return mManager;
        }

        if (getLayoutManager() instanceof TvManager) {
            mManager = (TvManager) getLayoutManager();
            return mManager;
        } else {
            if (null == mManager) {
                throw new NullPointerException("LayoutManager cannot be empty ");
            } else {
                throw new ClassCastException("LayoutManager type must be TvManager");
            }
        }
    }

    /**
     * 获取具有焦点的view的position
     */
    public int selectFocusPosition() {
        int selected = getGridLayoutManager().getSelection();
        return selected == NO_POSITION ? 0 : selected;
    }

    /**
     * Listener for intercepting touch dispatch events.
     */
    public interface OnTouchInterceptListener extends IBaseListener {
        /**
         * Returns true if the touch dispatch event should be consumed.
         */
        boolean onInterceptTouchEvent(MotionEvent event);
    }

    /**
     * Listener for intercepting generic motion dispatch events.
     */
    public interface OnMotionInterceptListener extends IBaseListener {
        /**
         * Returns true if the touch dispatch event should be consumed.
         */
        boolean onInterceptMotionEvent(MotionEvent event);
    }


    /**
     * Listener for intercepting key dispatch events.
     */
    public interface OnKeyInterceptListener extends IBaseListener {
        /**
         * Returns true if the key dispatch event should be consumed.
         */
        boolean onInterceptKeyEvent(KeyEvent event);
    }

    public interface OnUnhandledKeyListener extends IBaseListener {
        /**
         * Returns true if the key event should be consumed.
         */
        boolean onUnhandledKey(KeyEvent event);
    }


    /*
     * 监听滚动状态（滚动中，滚动到顶，滚动到底）
     * */
    public interface OnScrolledListener extends IBaseListener {
        int NO_SCROLLED = -1;//无滚动状态
        int SCROLLED_START = 1;//滚动停止在列表开始位置
        int SCROLLED_END = 2;//滚动停止在列表末尾位置
        int CAN_SCROLLED = 3;//可以滚动

        void onScrolled(int scrolled);
    }


    /**
     * Sets the touch intercept listener.
     *
     * @param listener The touch intercept listener.
     */
    public void setOnTouchInterceptListener(OnTouchInterceptListener listener) {
        mOnTouchInterceptListener = listener;
    }

    /**
     * Sets the generic motion intercept listener.
     *
     * @param listener The motion intercept listener.
     */
    public void setOnMotionInterceptListener(OnMotionInterceptListener listener) {
        mOnMotionInterceptListener = listener;
    }

    /**
     * Sets the key intercept listener.
     *
     * @param listener The key intercept listener.
     */
    public void setOnKeyInterceptListener(OnKeyInterceptListener listener) {
        mOnKeyInterceptListener = listener;
    }

    /**
     * Sets the unhandled key listener.
     *
     * @param listener The unhandled key intercept listener.
     */
    public void setOnUnhandledKeyListener(OnUnhandledKeyListener listener) {
        mOnUnhandledKeyListener = listener;
    }


    /**
     * 设置监听滚动状态
     */
    public void setOnScrolledListener(OnScrolledListener listener) {
        mOnScrolledListener = listener;
    }


}
