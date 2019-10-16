package hxb.xb_testandroidfunction.test_custom_view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2019/1/14
 * 测试ScrollView 嵌套问题
 */
public class TestScrollView2Activity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scrollview2);
    }
}
