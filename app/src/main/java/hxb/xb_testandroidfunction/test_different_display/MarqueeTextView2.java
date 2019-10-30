package hxb.xb_testandroidfunction.test_different_display;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView2 extends AppCompatTextView {


    public MarqueeTextView2(Context context) {
        this(context, null);
    }

    public MarqueeTextView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public MarqueeTextView2(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
        // 设置单行
        setSingleLine();
//        setMaxLines(1);
        //设置 Ellipsize，setMaxLines(1) 和 setEllipsize 冲突
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //获取焦距
        setFocusable(true);
        //走马灯的重复次数，-1代表无限重复
        setMarqueeRepeatLimit(-1);
        //强制获得焦点
        setFocusableInTouchMode(true);
    }

    /**
     * 使这个 View 永远获得焦距
     * @return true
     */
    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * 用于 EditText 存在时抢占焦点
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused){
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    /**
     * Window与Window间焦点发生改变时的回调
     * 解决 Dialog 抢占焦点问题
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus){
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }
}
