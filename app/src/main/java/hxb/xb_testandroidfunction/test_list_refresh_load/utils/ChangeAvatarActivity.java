package hxb.xb_testandroidfunction.test_list_refresh_load.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/5/31.
 */

public class ChangeAvatarActivity extends Activity {
    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;

    private TextView tvFromGallery;
    private TextView tvTakePhoto;
    private File img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_revise);

        initViews();
    }

    private void initViews() {
        tvTakePhoto = (TextView) findViewById(R.id.tv_take_photo);
        tvFromGallery = (TextView) findViewById(R.id.tv_chose_gallery);

        //拍照
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //从相册选
        tvFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();//点击窗口外部区域 弹窗消失
        return true;
    }
}
