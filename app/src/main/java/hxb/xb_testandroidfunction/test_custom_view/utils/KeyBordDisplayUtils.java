package hxb.xb_testandroidfunction.test_custom_view.utils;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.IntDef;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxb on 2017/10/30.
 */

public class KeyBordDisplayUtils {
    public static final int Hide = 1;
    public static final int Show = 2;
    @IntDef({Hide,Show})
    @Retention(RetentionPolicy.CLASS)
    private @interface showType{}
    private static InputMethodManager imm = null;
    public static void isShowSoftInput(final View view, final Activity activity, @showType int type) {
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
                public void run() {
                    /**
                     * 1,防止键盘隐藏时该属性会快一步生效影响布局抖动
                     * 2,更改键盘显示属性为adjustResize,目的是为了在隐藏键盘时为了全局监听有所动作，
                     * 从而处理一些问题
                     */
                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            },500);

        } else if (type == 2) {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//强制显示

        }

    }
}
