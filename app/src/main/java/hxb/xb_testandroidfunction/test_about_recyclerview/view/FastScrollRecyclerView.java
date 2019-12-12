package hxb.xb_testandroidfunction.test_about_recyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hxb on  2019/11/27
 */
public class FastScrollRecyclerView extends RecyclerView {
    private String Tag = "FastScrollRecyclerView";

    /**
     * 是否长按那件事件
     */
    private boolean isLongKeyPressed;

    /**
     * 是否进行快速滚动
     */
    private boolean isFastScroll;

    /**
     * 是否将要停止快速关东
     */
    private boolean onStoppingScroll;

    /**
     * 是否停止滚动
     */
    private boolean onStopedScroll;

    private LayoutManager layoutManager;

    /**
     * 快速滚动监听
     */
    private FastScrollListener fastScrollListener;

    private boolean canScrollHorizontally;

    private boolean canScrollVertically;

    /**
     * 滚动速度
     */
    private final int fixedDx = 60;

    /**
     * 滚动速度&方向：正值向下或向右,负值向上或向左
     */
    private int dx;

    /**
     * 停止滚动时缓慢效果
     */
    private final int fixedStoppingSlop = 3;

    /**
     * 停止滚动时缓慢效果
     */
    private int onStoppingSlop;

    /**
     * 滚动方向：正值向下或向右
     */
    private int direction;

    /**
     * 是否保留当前的focusChangeListener
     */
    private boolean handlerFocusListener;

    /**
     * 保留的recyclerView 的 focusChangeListener
     */
    private OnFocusChangeListener onfocuschanglistener;

    /**
     * 判断长按事件的repeatCount 阈值
     */
    private int longPressedSlope = 6;

    /**
     * 滑动速度 单位：像素/秒
     */
    private int scrollSpeed = 1;

    public FastScrollRecyclerView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FastScrollRecyclerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        layoutManager = getLayoutManager();
        handlerFocusListener = true;
        isFastScroll = false;
        onStoppingScroll = false;
        onStopedScroll = false;
        direction = 0;
    }

    public void setFastScrollListener(FastScrollListener fastScrollListener) {
        this.fastScrollListener = fastScrollListener;
    }

    /**
     * 开始滚动
     */
    private void startFastScroll() {
        isFastScroll = true;
        onStopedScroll = false;
        onStoppingScroll = false;
        clearFocusListener();
        if (requestFocus()) {
            Log.d(Tag, "startFastScroll  "+ Tag +"   handle focus");
        }
        scrollBy(canScrollHorizontally ? dx : 0, canScrollVertically ? dx : 0);
    }

    /**
     * 停止滚动
     */
    private void stopFastScroll() {
        restoreFocusListener();
        isFastScroll = false;
        onStoppingScroll = false;
        onStopedScroll = false;
        if (fastScrollListener != null) {
            fastScrollListener.stopScroll(0);
        }
    }

    /**
     * 清除当前recyclerView的focusChangeListener
     */
    private void clearFocusListener() {
        handlerFocusListener = false;
        setOnFocusChangeListener(null);
        handlerFocusListener = true;
    }

    /**
     * 回复之前的focusChangeListener，可能为null
     */
    private void restoreFocusListener() {
        setOnFocusChangeListener(onfocuschanglistener);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        super.setOnFocusChangeListener(l);
        if (handlerFocusListener) {
            onfocuschanglistener = l;
        }
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        //滚动时
        if (isLongKeyPressed && isFastScroll)
        {
            onFastScroll();
        }
        //滚动将要停止时
        if (onStoppingScroll) {
            onStoppingScroll();
        }
        //滚动停止时
        if (onStopedScroll) {
            onStoppingScroll = false;
            stopFastScroll();
        }
    }

    /**
     * @return 是否可以继续滑动
     */
    private boolean canScroll() {
        return (canScrollHorizontally && canScrollHorizontally(direction)
                || (canScrollVertically && canScrollVertically(direction))) ;
    }

    private boolean onFastScroll() {
        if (canScroll()) {
            scrollBy(canScrollHorizontally ? dx : 0, canScrollVertically ? dx : 0);
            postInvalidate();//让系统继续重绘，则会继续重复执行computeScroll
            return true;
        } else {
            isFastScroll = false;
            onStoppingScroll = true;
            if (fastScrollListener != null) {
                fastScrollListener.scrollToEnd();
            }
        }
        return false;
    }

    private void onStoppingScroll() {
        if (canScroll()) {
            dx-=onStoppingSlop;
            if (dx > 0 && direction > 0) {
                scrollBy(canScrollHorizontally ? dx : 0, canScrollVertically ? dx : 0);
                postInvalidate();//让系统继续重绘，则会继续重复执行computeScroll
            } else if (dx < 0 && direction < 0) {
                scrollBy(canScrollHorizontally ? dx : 0, canScrollVertically ? dx : 0);
                postInvalidate();//让系统继续重绘，则会继续重复执行computeScroll
            } else {
                onStoppingScroll = false;
                onStopedScroll = true;
            }

        } else {
            onStoppingScroll = false;
            onStopedScroll = true;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int repeatCount = event.getRepeatCount();
        if (repeatCount >= longPressedSlope) {
            prepareFastScroll(event);
            return true;
        }

        if (isLongKeyPressed) {
            Log.d(Tag, "dispatchKeyEvent        normal pressed KeyEvent="+event);
            isLongKeyPressed = false;
            onStoppingScroll = true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        layoutManager = layout;
        canScrollHorizontally = layoutManager.canScrollHorizontally();
        canScrollVertically = layoutManager.canScrollVertically();
    }

    private boolean prepareFastScroll(KeyEvent event) {
        if (canScroll()) {

            if ( (canScrollVertically &&(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP))
                    || (canScrollHorizontally &&(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT))) {
                if (!isLongKeyPressed) {
                    Log.d(Tag, "dispatchKeyEvent      long pressed KeyEvent="+event);
                    isLongKeyPressed = true;
                    if (fastScrollListener != null) {
                        fastScrollListener.startScroll(0);
                    }
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        //向下或向右滚动
                        direction = 1;
                        dx = fixedDx;
                        onStoppingSlop = fixedStoppingSlop;
                    }else {
                        //向上或向左滚动
                        direction = -1;
                        dx = -fixedDx;
                        onStoppingSlop = -fixedStoppingSlop;
                    }
                    startFastScroll();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public interface FastScrollListener{
        void startScroll(int direction) ;
        void stopScroll(int direction) ;
        void scrollToEnd();
    }
}
