package hxb.xb_testandroidfunction.test_snackbar_toast.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_snackbar_toast.mysnackbar.TSnackbar;

/**
 * Created by hxb on 2018/6/7.
 */

public class TestSnackbarActivity extends AppCompatActivity{

    private TSnackbar mSnackBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_snackbar);

        if (this instanceof AppCompatActivity && getSupportActionBar()!=null){
            Log.e("TAG", "onCreate: 测试是否能判断该对象是属于谁     " );
        }
    }

    public void btn1(View view){
        mSnackBar = TSnackbar.make(findViewById(R.id.rlStateBar), "正在努力清除缓存中...", TSnackbar.LENGTH_SHORT, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        mSnackBar.setBackgroundColor(Color.parseColor("#ff9500"));
        mSnackBar.setMessageTextColor(Color.parseColor("#ffffff"));
        mSnackBar.setMinHeight(20, 20);
        mSnackBar.setMessageTextSize(16);
        mSnackBar.show();
    }
}
