package com.cdct.cmdim.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by hxb on 2017/8/23.
 */

public class ListenerKeyBackEditTextView extends EditText{
    public ListenerKeyBackEditTextView(Context context) {
        super(context);
    }

    public ListenerKeyBackEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenerKeyBackEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        Log.e("测试返回按钮", "onKeyPreIme:   1" );
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT ){
            if (null != mBackKeyTag && mBackKeyTag.backKeyTag(2) == 2) {
//                Log.e("测试返回按钮", "onKeyPreIme:   2" );
                return true;
            }
        }
//        Log.e("测试返回按钮", "onKeyPreIme:   3" );
        return super.onKeyPreIme(keyCode, event);
    }


    public interface BackKeyTag{
        int backKeyTag(int tag);
    }
    private BackKeyTag mBackKeyTag;

    public void setmBackKeyTag(BackKeyTag mBackKeyTag) {
        this.mBackKeyTag = mBackKeyTag;
    }

}
