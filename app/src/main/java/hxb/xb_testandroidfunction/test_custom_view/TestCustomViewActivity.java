package hxb.xb_testandroidfunction.test_custom_view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_custom_view.view.CircleImageView;

/**
 * Created by hxb on 2018/10/9
 */
public class TestCustomViewActivity extends FragmentActivity{
    private boolean isStop = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_custom_view);

    }

    /**
     * 测试CircleImageView
     * */
    private void testCircleImageView(){
        Glide.with(this)
                .load("http://img2.imgtn.bdimg.com/it/u=3376453184,4156782514&fm=26&gp=0.jpg")
                .dontAnimate()
                .placeholder(R.drawable.biao_chi_tu2)
                .error(R.drawable.biao_chi_tu2)
                .into(((CircleImageView)findViewById(R.id.iv)));

        Button button = findViewById(R.id.btn1);
        Button button2 = findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!button.isSelected()){
//                    button2.setSelected(true);
//                    button.setSelected(true);
                }else {
//                    button2.setSelected(false);
//                    button.setSelected(false);
                }
                isStop = false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStop) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (button2.isSelected()) {
                                button2.setSelected(false);
                            } else {
                                button2.setSelected(true);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 测试GridLayout
     * */
    private void testGridLayout(){

    }

}
