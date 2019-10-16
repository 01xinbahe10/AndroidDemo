package hxb.xb_testandroidfunction.test_different_display;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hxb.xb_testandroidfunction.R;

/**
 * 测试Android多屏异显示功能
 * */
public class DifferentDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_display);
    }

}
