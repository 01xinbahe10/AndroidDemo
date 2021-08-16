package com.example.testjpeg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

/**
 * Created by hxb on 2019-07-08.
 */
public class MainActivity extends AppCompatActivity {
    //测试图片的存位置
    private String picPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        setContentView(R.layout.activity_main);
        findViewById(R.id.btPicZip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(picPath);

                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                String outPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_compression.jpg");
                MineJni.compress(bitmap, outPath);
            }
        });
    }


    /**
     * 6.0 权限申请
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
//            new Thread(new WritePictureRunnable()).start();
        }
    }

}
