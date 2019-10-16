package com.cdct.cmdim.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import com.cdct.cmdim.R;
import com.cdct.cmdim.utils.KeyBordDisplayUtils;
import com.cdct.cmdim.utils.ScreenUtils;


/**
 * Created by hxb on 2017/10/30.
 * 聊天界面操作
 */

public class ChatScreenView {
    /**
     * 接口
     */
    public TheEvent theEvent;
    public void setTheEvent(TheEvent theEvent) {
        this.theEvent = theEvent;
    }
    public interface TheEvent {

        void isKeyShow(boolean isKeyShow, int keyCode);//键盘是否显示事件接口
        void keyboardMeasure(boolean isMeasure);//检测键盘高度是否测量完成
    }
    public interface Unregister{//取消注销
        void unregister();
    }

    private Context mContext;
    private FragmentActivity mActivity;

    private LinearLayout mLlParentPanel;
    private LinearLayout.LayoutParams ll_lp_parentPanel;
    private ListenerKeyBackEditTextView editTextView;

    private LinearLayout mLlToolBar;

    public static ChatScreenView initialize(FragmentActivity activity, Context context) {
        ChatScreenView presenter = new ChatScreenView();
        presenter.mActivity = activity;
        presenter.mContext = context;
        presenter.initDisplay(context);
        return presenter;
    }


    public ChatScreenView setParentPanel(LinearLayout llParentPanel, LinearLayout llToolBar) {
        this.mLlParentPanel = llParentPanel;
        ll_lp_parentPanel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ll_lp_parentPanel.weight = 0f;
        ll_lp_parentPanel.height = 0;
        mLlParentPanel.setLayoutParams(ll_lp_parentPanel);

        this.mLlToolBar = llToolBar;
        return this;
    }

    public LinearLayout.LayoutParams getLl_lp_parentPanel() {
        return ll_lp_parentPanel;
    }
    public LinearLayout getmLlParentPanel() {
        return mLlParentPanel;
    }


    private int mOldHeightDifference = 0;
    public static final int KEY_CODE_DEF = -1;
    public static final int KEY_CODE_BACK = 1;//表示用键盘的返回键点击关闭了键盘
    /**表示用了第三方键盘自带的按钮关闭键盘（这里并不是监听了第三方键盘，而是通过布局监听推断出键盘的显示
      * 或隐藏，再排除系统和自身能控制键盘的显示和隐藏，剩下的就是第三方的某个键是关闭键盘的）
      */
    public static final int KEY_CODE_THIRD_PARTY = 2;
    private int keyCode = KEY_CODE_DEF;


    //为了能获取editText的监听事件，mLlParentPanel能跟随键盘一起隐藏。
    // return 1是为了取消拦截editText的监听事件不能向上级传递的作用。
    private ListenerKeyBackEditTextView.BackKeyTag backKeyTag = new ListenerKeyBackEditTextView.BackKeyTag() {

        @Override
        public int backKeyTag(int tag) {
            if (ll_lp_parentPanel.height > 0) {
                KeyBordDisplayUtils.isShowSoftInput(editTextView,mActivity,1);
                diePanel();
                keyCode = KEY_CODE_BACK;
                return tag;
            }
            return 1;
        }
    };

    /**
     * 测量键盘高度
     */
    public ChatScreenViewUnregister followKeyboardDisplay(final View view) {

        if (mActivity == null){
            return null;
        }
        editTextView = view.findViewById(R.id.etChatContent);
        editTextView.setmBackKeyTag(backKeyTag);//设置监听

        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("key_bord", Context.MODE_PRIVATE);

        final View  activityRootView =((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
        final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpened = false;
            @Override
            public void onGlobalLayout() {
                final Rect r = new Rect();
                //获取当前界面可视部分
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int mScreenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                final int heightDifference = mScreenHeight - r.bottom;
                //判断并获取虚拟返回键的高度（其实可以根据以上代码测出来，但为了精准获取和不必要的逻辑处理选择以下方案）
                if (sharedPreferences.getInt("virtual_return_height", 0) == 0 && ScreenUtils.hasNavBar(mContext)) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("key_bord", Context.MODE_PRIVATE).edit();
                    editor.putInt("virtual_return_height", ScreenUtils.getNavigationBarHeight(mContext));
                    editor.apply();
//                    Log.e("测试虚拟按键高度", "onGlobalLayout: "+ ScreenUtils.getNavigationBarHeight(mContext));
                }

                //保证只有一次值
                boolean isOpen = heightDifference > (sharedPreferences.getInt("virtual_return_height", 0) + 100);
                if (isOpen == wasOpened){
                    return;
                }
                wasOpened = isOpen;

//                    Log.e("测试--", "onGlobalLayout:     "+isOpen);
                if (isOpen) {
                    if ( sharedPreferences.getInt("height", 0) < heightDifference) {//取最大的一次
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("key_bord", Context.MODE_PRIVATE).edit();
                        editor.putInt("height", heightDifference);
                        editor.apply();
                        theEvent.keyboardMeasure(true);//键盘高度已测量完成
                    }

                }
                if (null == theEvent){
                    return;
                }

                theEvent.isKeyShow(isOpen, KEY_CODE_DEF);

                if (!isOpen){
                    if ((mLlParentPanel.getVisibility() == View.INVISIBLE) && ll_lp_parentPanel.height > 0) {
                        if (keyCode != KEY_CODE_BACK) {
                            theEvent.isKeyShow(isOpen, KEY_CODE_THIRD_PARTY);
                        }
                    }
                }
            }

        };
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        return new ChatScreenViewUnregister(mActivity,globalLayoutListener);
    }


    /**
     * 是否显示面板
     */
    public void showPanel(View view, boolean isShow) {
        ListenerKeyBackEditTextView et = view.findViewById(R.id.etChatContent);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("key_bord", Context.MODE_PRIVATE);
        int keyBordHeight = sharedPreferences.getInt("height", 0);
        int keyVirtualReturnHeight = sharedPreferences.getInt("virtual_return_height", 0);
//        Log.e("测试showPanel", "showPanel: " + keyBordHeight);
        keyCode = KEY_CODE_DEF;
        if (keyBordHeight == 0) {
            ll_lp_parentPanel.height = dp2px(300f);//默认面板高度
        } else {
            ll_lp_parentPanel.height = keyBordHeight - keyVirtualReturnHeight;
        }
        if (isShow) {
            mLlParentPanel.setLayoutParams(ll_lp_parentPanel);
            mLlParentPanel.setVisibility(View.VISIBLE);
        } else {
            mOldHeightDifference = 0;
            mLlParentPanel.setLayoutParams(ll_lp_parentPanel);
            mLlParentPanel.setVisibility(View.INVISIBLE);
        }
//        et.setmBackKeyTag(backKeyTag);
    }

    /**
     * 消亡面板
     */
    public void diePanel() {
        mLlParentPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                //这里做延时处理是为了键盘属性WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE不影响到布局
                mLlParentPanel.setVisibility(View.INVISIBLE);//为了切换两个以上的fragment时防止留影上一个的视觉优化
                mLlParentPanel.setVisibility(View.GONE);
                mOldHeightDifference = 0;
                ll_lp_parentPanel.height = mOldHeightDifference;
            }
        },200);

    }



    /**
     * 初始化像素转换方法
     */
    private DisplayMetrics mDisplayMetrics;
    private final int THUMBNAIL_SIZE = 320;
    private final int MAX_THUMBNAIL_SIZE = 640;
    private int mThumbnailSize;

    public void initDisplay(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        mThumbnailSize = dp2px(THUMBNAIL_SIZE);
        if (mThumbnailSize > MAX_THUMBNAIL_SIZE) {
            mThumbnailSize = MAX_THUMBNAIL_SIZE;
        }
    }

    private int dp2px(float dp) {
        return (int) (mDisplayMetrics.density * dp + 0.5f);
    }

    private int px2dip(int px) {
        return (int) (px / mDisplayMetrics.density + 0.5f);
    }


    public static class ChatScreenViewUnregister implements Unregister{
        /**
         * 解绑类
         * */

        private WeakReference<Activity> mActivityWeakReference;
        private WeakReference<ViewTreeObserver.OnGlobalLayoutListener> mOnGlobalLayoutListenerWeakReference;
        public ChatScreenViewUnregister(Activity activity,ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener) {
            mActivityWeakReference = new WeakReference<>(activity);
            mOnGlobalLayoutListenerWeakReference = new WeakReference<>(globalLayoutListener);
        }


        @Override
        public void unregister() {
//            Log.e("测试是否解绑", "unregister: " );
            Activity activity = mActivityWeakReference.get();
            ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = mOnGlobalLayoutListenerWeakReference.get();
            if (null != activity && null != globalLayoutListener) {
                View activityRoot = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityRoot.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(globalLayoutListener);
                } else {
                    activityRoot.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(globalLayoutListener);
                }
            }

            mActivityWeakReference.clear();
            mOnGlobalLayoutListenerWeakReference.clear();
        }
    }
}
