package hxb.xb_testandroidfunction.test_different_display.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

import hxb.xb_testandroidfunction.test_different_display.Event;

public class EventConstrainLayout extends ConstraintLayout {
    public EventConstrainLayout(Context context) {
        super(context);
    }

    public EventConstrainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventConstrainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "onInterceptTouchEvent: >>>>>>>>>   "+event.getAction() );
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if (null != VideoPresentation.getItemEvent()){
                    VideoPresentation.getItemEvent().onItemClick(Event.ItemEvent.OK);
                }
               break;
        }
        return super.onTouchEvent(event);
    }
}
