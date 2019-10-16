package hxb.xb_testandroidfunction.test_custom_view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_custom_view.scrollview.StretchScrollView;
import hxb.xb_testandroidfunction.test_custom_view.utils.KeyBordDisplayUtils;

/**
 * Created by hxb on 2018/6/11.
 */

public class TestScrollView1Activity extends FragmentActivity implements View.OnClickListener ,StretchScrollView.TheEvent,EditText.OnFocusChangeListener{
    // TODO: 2018/6/11 测试scrollView中的子元素在键盘显示时 始终保持在键盘之上失败 
    private StretchScrollView mScrollView;
    private View linearLayout;
    private EditText viewTest4,viewTest6, viewTest15,viewTest16,viewTest17;
    private float viewTest4Offset,viewTest6Offset,viewTest15Offset,viewTest16Offset,viewTest17Offset ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scrollview1);

        mScrollView = findViewById(R.id.scrollView1);
        mScrollView.setTheEvent(this);
        mScrollView.childEditTextFollowKey(this,findViewById(R.id.rl));
        mScrollView.setVerticalScrollBarEnabled(false);

        findViewById(R.id.btn1).setOnClickListener(this);
        linearLayout = findViewById(R.id.ll);
        viewTest4 = findViewById(R.id.viewTest4);
        viewTest6 = findViewById(R.id.viewTest6);
        viewTest15 = findViewById(R.id.viewTest15);
        viewTest16 = findViewById(R.id.viewTest16);
        viewTest17 = findViewById(R.id.viewTest17);
        viewTest4.setOnFocusChangeListener(this);
        viewTest6.setOnFocusChangeListener(this);
        viewTest15.setOnFocusChangeListener(this);
        viewTest16.setOnFocusChangeListener(this);
        viewTest17.setOnFocusChangeListener(this);


        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Log.e("TAG", "onGlobalLayout: -------------------> scrollViewHeight"+mScrollView.getHeight() );
//                Log.e("TAG", "onGlobalLayout: -------------------> linearLayout :"+linearLayout.getHeight() );
//                Log.e("TAG", "onGlobalLayout: -------------------> viewTest1:"+viewTest15.getY());
//                Log.e("TAG", "onGlobalLayout: -------------------> viewTest2:"+viewTest16.getY());
//                Log.e("TAG", "onGlobalLayout: -------------------> viewTest3:"+viewTest17.getY());
//                Log.e("TAG", "onGlobalLayout: -------------------> viewTest3高度:"+viewTest17.getHeight());
//                viewTest3Offset = linearLayout.getHeight() - viewTest3.getY()+viewTest3.getHeight();
                viewTest4Offset = viewTest4.getY()+viewTest4.getHeight();
                viewTest6Offset = viewTest6.getY()+viewTest6.getHeight();
                viewTest15Offset = viewTest15.getY()+viewTest15.getHeight();
                viewTest16Offset = viewTest16.getY()+viewTest16.getHeight();
                viewTest17Offset = viewTest17.getY()+viewTest17.getHeight();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:

//                mScrollView.scrollToDistance(-300);

                break;
            case R.id.viewTest4:
                mScrollView.scrollToDistance((int) viewTest4Offset);
                break;
            case R.id.viewTest6:
                mScrollView.scrollToDistance((int) viewTest6Offset);
                break;
            case R.id.viewTest15:
                mScrollView.scrollToDistance((int) viewTest15Offset);
                break;
            case R.id.viewTest16:
                mScrollView.scrollToDistance((int) viewTest16Offset);
                break;
            case R.id.viewTest17:
                mScrollView.scrollToDistance((int) viewTest17Offset);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (viewTest4.hasFocus()){
            Log.e("TAG", "onFocusChange:              viewTest4" );
        }
        if (viewTest6.hasFocus()){
            Log.e("TAG", "onFocusChange:              viewTest6" );
        }
        if (viewTest15.hasFocus()){
            Log.e("TAG", "onFocusChange:              viewTest15" );
            KeyBordDisplayUtils.isShowSoftInput(viewTest15,this,KeyBordDisplayUtils.Show);
        }
    }

    @Override
    public void isKeyShow(boolean isKeyShow, int keyCode) {

    }

    @Override
    public void keyboardMeasure(boolean isMeasure) {
        Log.e("TAG", "keyboardMeasure:      "+isMeasure );
        if (isMeasure){
            KeyBordDisplayUtils.isShowSoftInput(viewTest17,TestScrollView1Activity.this, KeyBordDisplayUtils.Show);
        }
    }
}
