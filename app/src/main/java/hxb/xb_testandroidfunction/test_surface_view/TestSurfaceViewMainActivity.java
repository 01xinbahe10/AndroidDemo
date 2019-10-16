package hxb.xb_testandroidfunction.test_surface_view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_surface_view.surface_view_ondraw.TestSurfaceViewActivity;
import hxb.xb_testandroidfunction.test_surface_view.surface_view_play_video.TestPlayVideoActivity;

/**
 * Created by hxb on 2018/6/28.
 *
 */

public class TestSurfaceViewMainActivity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_surface_view_main);
    }
    public void btn1(View view){
        startActivity(new Intent(this, TestSurfaceViewActivity.class));
    }
    public void btn2(View view){
        TestPlayVideoActivity.startInstance(this);
    }
}
