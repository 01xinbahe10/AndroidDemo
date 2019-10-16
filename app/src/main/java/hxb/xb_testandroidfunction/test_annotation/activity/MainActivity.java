package hxb.xb_testandroidfunction.test_annotation.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_annotation.test_class.Annotation_Test2;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
               Annotation_Test2 test2 = new Annotation_Test2();
               test2.fun();
                break;
            case R.id.btn2:
                Annotation_Test2 test2_2 = new Annotation_Test2();
                test2_2.fun2(Annotation_Test2.INT_1);//往该方法传值时，会检查是否是规定的类型
                test2_2.fun9_1(Annotation_Test2.BLACK);
                break;
        }
    }
}
