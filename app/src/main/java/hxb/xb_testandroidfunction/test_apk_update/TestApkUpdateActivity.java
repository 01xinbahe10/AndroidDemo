package hxb.xb_testandroidfunction.test_apk_update;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cdct.cmdim.utils.PermissionUtils;

import java.io.File;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_apk_update.retrofit_update.AppApiCallback;
import hxb.xb_testandroidfunction.test_apk_update.retrofit_update.NetRequest;
import hxb.xb_testandroidfunction.test_apk_update.update.AppUtils;
import hxb.xb_testandroidfunction.test_apk_update.update.UpdateChecker;
import hxb.xb_testandroidfunction.test_apk_update.update2.VersionManagementToolUtils;

/**
 * Created by hxb on 2018/6/13.
 * 版本更新测试
 *
 */

public class TestApkUpdateActivity extends FragmentActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_apk_update);

        TextView textView = findViewById(R.id.textView1);

        textView.setText("当前版本信息: versionName = " + AppUtils.getVersionName(this) + " versionCode = " + AppUtils.getVersionCode(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.MY_PERMISSION_REQUEST_CODE);
    }

    public void btn1(View view){
        // TODO: 2018/6/13 测试失败
        try {
            Log.e("TAG", "版本名称："+ VersionManagementToolUtils.getVersionName(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.e("TAG", "版本Code："+VersionManagementToolUtils.getVersionCode(this) );
        } catch (Exception e) {
            e.printStackTrace();
        }

//        VersionManagementToolUtils.loadNewVersionProgress(this);
    }

    public void button1(View view){
        //请求安装未知应用来源的权限  手机8.0
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10010);
        // TODO: 2018/6/13 测试成功 
        UpdateChecker.checkForDialog(this);
    }

    public void button2(View view){
        // TODO: 2018/6/13 测试成功 
//        UpdateChecker.checkForNotification(this);

        //retrofit测试下载文件
        NetRequest.downLoadFile("http://192.168.11.213/testUpdate.apk", new AppApiCallback.DownloadCallBack() {
            @Override
            public void progressOfValue(long currentLength, long totalLength) {

            }

            @Override
            public void downloadSuccess(File fileDir) {

            }

            @Override
            public void downloadFailed() {

            }
        });
    }



}
