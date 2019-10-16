package hxb.xb_testandroidfunction.test_progress_keep_alive;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import hxb.xb_testandroidfunction.test_progress_keep_alive.service.KeepLiveService;


/**
 * Created by hxb on 2018/7/24.
 *
 */
// TODO: 2018/7/24 测试失败 
public class TestProgressKeepAliveActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //连续启动Service
        Intent intentOne = new Intent(this, KeepLiveService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentOne);
        } else {
            // Pre-O behavior.
            startService(intentOne);
        }
        ContentResolver cv = this.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        try{

        }catch (Exception e){

        }
        Log.e("activity"," -------------------> "+strTimeFormat);

    }
}
