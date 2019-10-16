package hxb.xb_testandroidfunction.test_pic_crop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_pic_crop.crop_image.view.CropImageView;
import hxb.xb_testandroidfunction.test_pic_crop.surfaceview.MineSurfaceView;

/**
 * Created by hxb on 2018/5/30.
 */

public class TestPicReviewActivity extends AppCompatActivity{

    private final String TAG = "TAG";
    private CropImageView mCropImageView;

    private MineSurfaceView mSurfaceView;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 10:
                    mCropImageView.setRatio(4,3);
                    mCropImageView.refresh();
                    break;
                case 12:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pic_review);
        mCropImageView = findViewById(R.id.ivCrop);

        mSurfaceView = findViewById(R.id.surfaceView);
        Glide.with(this).load("http://img17.3lian.com/201612/27/c0e0b81d5066c275d6f8c954b1202386.jpg").asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                mCropImageView.setImageBitmap(resource);
//                mSurfaceView.setBitmap(resource);

            }
        });

        Glide.with(this).load("http://img17.3lian.com/201612/27/c0e0b81d5066c275d6f8c954b1202386.jpg").asBitmap().listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                Log.e(TAG, "onException: 00000000000000000000000");
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e(TAG, "onResourceReady: 1111111111111111111111" );
                return false;
            }
        }).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

            }
        });

//        mHandler.sendEmptyMessageDelayed(10,6000);



        mSurfaceView = findViewById(R.id.surfaceView);
        mHandler.sendEmptyMessageDelayed(12,6000);
    }

    public void ClickBtn1(View view){
//        Intent intent = new Intent(this, ImageMagnification.class);
//        intent.putExtra("imageUrl","http://img17.3lian.com/201612/27/c0e0b81d5066c275d6f8c954b1202386.jpg");
//        startActivity(intent);
//        overridePendingTransition(R.anim.anim_preview_pic_enter,R.anim.anim_preview_pic_exit);

//        Bitmap bitmap = mCropImageView.getCroppedImage();
//        mCropImageView.setImageBitmap(bitmap);


        mSurfaceView.setBitmap(mSurfaceView.getCroppedImage());

        //测试集合动态移出元素
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");

        List<String> list2 = new ArrayList<>();
        list2.add("5");
        list2.add("1");
        list2.add("3");
        list2.add("2");
        list2.add("4");
        list2.add("9");

        for (int i = 0; i < list2.size(); i++) {
            String s2 = list2.get(i);

            for (int j = 0;j<list1.size();j++){
                String s1 = list1.get(j);

                Log.e(TAG, "ClickBtn1: -------------1111  s2:"+s2+"    s1:"+s1 );
                if (TextUtils.equals(s1,s2)){
                    list2.remove(s2);
                    list1.remove(s1);
                    Log.e(TAG, "ClickBtn1: -------------222  s2:"+s2+"    s1:"+s1 );
                    i = -1;
                    break;
                }

            }
        }

        Log.e(TAG, "ClickBtn1: -------------333  list2:"+list2.size()+"    list1:"+list1.size() );
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
        Log.e("TAG", "overridePendingTransition: 是否执行了     " );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
