package hxb.xb_testandroidfunction.test_custom_view.scrollview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.cdct.cmdim.utils.ScreenUtils;

/**
 * A ScrollView which can scroll to (0,0) when pull down or up.
 *
 * @author markmjw
 * @date 2014-04-30
 */
public class StretchScrollView extends ScrollView {
    private static final int MSG_REST_POSITION = 0x01;

    /** The max scroll height. */
    private static final int MAX_SCROLL_HEIGHT = 400;
    /** Damping, the smaller the greater the resistance */
    private static final float SCROLL_RATIO = 0.4f;

    private View mChildRootView;

    private float mTouchY;
    private boolean mTouchStop = false;

    private int mScrollY = 0;
    private int mScrollDy = 0;
    private SharedPreferences sharedPreferences;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (MSG_REST_POSITION == msg.what) {
                if (mScrollY != 0 && mTouchStop) {
                    mScrollY -= mScrollDy;

                    if ((mScrollDy < 0 && mScrollY > 0) || (mScrollDy > 0 && mScrollY < 0)) {
                        mScrollY = 0;
                    }

                    mChildRootView.scrollTo(0, mScrollY);
                    // continue scroll after 20ms
                    mHandler.sendEmptyMessageDelayed(MSG_REST_POSITION, 20);
                }
            }
            if (msg.what == 2){
                if (null != mChildRootView) {
                    int scrollY2 = getScrollY();
                    int childBottomToParentBottomDistance = (int)msg.obj - scrollY2;
                    int keyHeight = sharedPreferences.getInt("height",0);
                    if (keyHeight > 0) {
                        mChildRootView.scrollBy(0, childBottomToParentBottomDistance - keyHeight);
                    }else {
                        mChildRootView.scrollBy(0, childBottomToParentBottomDistance - 900);

                    }

//                    Log.e("TAG", "handleMessage: getScrollY(): "+scrollY2 );
//                    Log.e("TAG", "handleMessage: childBottomToParentBottomDistance: "+(childBottomToParentBottomDistance -900) );

                }
            }
            return false;
        }
    });

    public StretchScrollView(Context context) {
        super(context);

        init();
    }

    public StretchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public StretchScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }


    private void init() {
        // set scroll mode
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            // when finished inflating from layout xml, get the first child view
            mChildRootView = getChildAt(0);

        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchY = ev.getY();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (null != mChildRootView) {
            doTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void doTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                mScrollY = mChildRootView.getScrollY();
                if (mScrollY != 0) {
                    mTouchStop = true;
                    mScrollDy = (int) (mScrollY / 10.0f);
                    mHandler.sendEmptyMessage(MSG_REST_POSITION);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                int deltaY = (int) (mTouchY - nowY);
                mTouchY = nowY;
                if (isNeedMove()) {
                    int offset = mChildRootView.getScrollY();
                    if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
                        mChildRootView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
                        mTouchStop = false;
                    }
                }
                break;

            default:
                break;
        }
    }


    private boolean isNeedMove() {
        int viewHeight = mChildRootView.getMeasuredHeight();
        int scrollHeight = getHeight();
        int offset = viewHeight - scrollHeight;
        int scrollY = getScrollY();

        return scrollY == 0 || scrollY == offset;
    }



    public void childEditTextFollowKey(final Activity activity,View view){//设置scrollView中的子编辑框元素跟随键盘高度

        sharedPreferences = activity.getSharedPreferences("key_bord", Context.MODE_PRIVATE);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpened = false;
            @Override
            public void onGlobalLayout() {
                final Rect r = new Rect();
                //获取当前界面可视部分
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int mScreenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                final int heightDifference = mScreenHeight - r.bottom;

                if (sharedPreferences.getInt("virtual_return_height", 0) == 0 && ScreenUtils.hasNavBar(activity)) {
                    SharedPreferences.Editor editor = activity.getSharedPreferences("key_bord", Context.MODE_PRIVATE).edit();
                    editor.putInt("virtual_return_height", ScreenUtils.getNavigationBarHeight(activity));
                    editor.apply();
//                    Log_Mvp.e("测试虚拟按键高度", "onGlobalLayout: "+ ScreenUtils.getNavigationBarHeight(mContext));
                }

                //保证只有一次值
                boolean isOpen = heightDifference > (sharedPreferences.getInt("virtual_return_height", 0) + 100);
                if (isOpen == wasOpened){
                    return;
                }
                wasOpened = isOpen;


                if (isOpen) {
                    if ( sharedPreferences.getInt("height", 0) < heightDifference) {//取最大的一次
                        SharedPreferences.Editor editor = activity.getSharedPreferences("key_bord", Context.MODE_PRIVATE).edit();
                        editor.putInt("height", heightDifference);
                        editor.apply();
                        //键盘高度已测量完成
                        theEvent.keyboardMeasure(true);
                    }

                }

            }
        });
    }


    public void scrollToDistance(int distance){

        Message message = mHandler.obtainMessage();
        message.what = 2;
        message.obj = distance;
        mHandler.sendMessage(message);

    }


    /**
     * 接口
     */
    private TheEvent theEvent;
    public void setTheEvent(TheEvent theEvent) {
        this.theEvent = theEvent;
    }
    public interface TheEvent {

        void isKeyShow(boolean isKeyShow, int keyCode);//键盘是否显示事件接口
        void keyboardMeasure(boolean isMeasure);//检测键盘高度是否测量完成
    }

}