package com.cdct.cmdim.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by hxb on 2017/10/30.
 */

public class KeyBordDisplayUtils  {
    private static InputMethodManager imm = null;
    public static void isShowSoftInput(final View view, final Activity activity, int type) {
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
            },200);

        } else if (type == 2) {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//强制显示

        }

    }
}
