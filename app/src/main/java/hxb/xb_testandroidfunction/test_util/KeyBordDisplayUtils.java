package hxb.xb_testandroidfunction.test_util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.IntDef;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxb on 2017/10/30.
 */

public final class KeyBordDisplayUtils {

    public final static int HIDE = 1;
    public final static int SHOW = 2;

    @IntDef({HIDE, SHOW})
    @Retention(RetentionPolicy.CLASS)
    private @interface Type {
    }

    private static InputMethodManager imm = null;

    public static void isShowSoftInput(final View view, final Activity activity, @Type int type) {
        /**
         * 1  为隐藏键盘
         * 2  为显示键盘
         * */

        if (null == imm) {
            imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        if (type == 1) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//强制隐藏
            view.postDelayed(new Runnable() {
                @Override
                public void run() {//防止键盘隐藏时该属性会快一步生效影响布局抖动
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }, 200);

        } else if (type == 2) {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//强制显示

        }

    }

    /**
     * 适用于 某个view 单独控制EditText的键盘显示和隐藏
     * */
    public static void isShowSoftInput2(final View view, final Activity activity, @Type int type) {
        /**
         * 1  为隐藏键盘
         * 2  为显示键盘
         * */

        if (null == imm) {
            imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        if (type == 1) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//强制隐藏
        } else if (type == 2) {
//            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//强制显示
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        }

    }
    /**
     * 适用于整个窗口级别的键盘显示和隐藏
     * */
    public static void isShowSoftInput3(final Activity activity, @Type int type) {
        /**
         * 1  为隐藏键盘
         * 2  为显示键盘
         * */

        if (type == 1) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//隐藏
        } else if (type == 2) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//显示
        }

    }


    public static class Observer implements android.view.ViewTreeObserver.OnGlobalLayoutListener {
        /*
         * 注意：
         * AndroidManifest.xml 中指定Activity的标签加入该属性
         * <activity android:windowSoftInputMode="adjustResize|stateHidden"></activity>
         *
         * 该测量和监听键盘才有效
         * */
        public static KeyBordDisplayUtils.Observer init(Activity activity) {
            KeyBordDisplayUtils.Observer observer = new KeyBordDisplayUtils.Observer();
            observer.activity = activity;
            return observer;
        }

        private Observer() {
        }

        private int keyboardHeight = 0;
        private int barHeight = 0;
        private boolean wasOpened = false;
        private Activity activity;
        private KeyBordDisplayUtils.Observer.KeyboardSate keyboardSate;

        public interface KeyboardSate {
            void isOpen(boolean isOpen, int keyboardHeight, int navigationBarHeight);
        }

        public void setKeyboardSate(KeyBordDisplayUtils.Observer.KeyboardSate keyboardSate) {
            this.keyboardSate = keyboardSate;
        }


        @Override
        public void onGlobalLayout() {
            int navigationBarHeight = ScreenUtils.getNavigationBarHeight(activity);
            if (navigationBarHeight > barHeight) {
                barHeight = navigationBarHeight;
            }


            final Rect r = new Rect();
            //获取当前界面可视部分
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int mScreenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            final int heightDifference = mScreenHeight - r.bottom;
            if (keyboardHeight < heightDifference) {
                keyboardHeight = heightDifference;
            }
            //保证只有一次值
            boolean isOpen = heightDifference > barHeight + 100;
            if (isOpen == wasOpened) {
                return;
            }
            wasOpened = isOpen;

            keyboardSate.isOpen(isOpen, keyboardHeight, barHeight);

        }

        public void onDestroy(View... views) {
            for (View view : views) {
                if (null != view) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        }
    }
}
