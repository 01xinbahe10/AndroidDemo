package hxb.xb_testandroidfunction.test_pic_crop.zoom_image;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
//import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hxb.xb_testandroidfunction.R;

public class ImageMagnification extends FragmentActivity {
    private ZoomImageView imageView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.setTheme(R.style.translucent);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_magnification);
        Log.e("TAG", "onCreate:当前界面的类型    "+getClass().getCanonicalName());
//        if (Build.VERSION.SDK_INT >= 19) {
//            ImmersionBar.with(this)
//                    .statusBarDarkFont(false)   //状态栏字体是深色，不写默认为亮色
//                    .transparentStatusBar().init();
//        }

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏



        EventBus.getDefault().register(this);
        imageView=  findViewById(R.id.imageurl);
        Glide.with(this).load(getIntent().getStringExtra("imageUrl")).into(imageView);

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(ZoomBus zoomBus) {
        finish();
        overridePendingTransition(R.anim.anim_preview_pic_enter,R.anim.anim_preview_pic_exit);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.anim_preview_pic_enter,R.anim.anim_preview_pic_exit);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
