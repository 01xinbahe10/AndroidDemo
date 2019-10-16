package hxb.xb_testandroidfunction.test_language_change.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Created by hxb on 2018/7/10.
 */

public class KillSelfService extends Service{

    /**关闭应用后多久重新启动*/
    private static  long stopDelayed=2000;
    private Handler handler;
    private String PackageName;
    public KillSelfService() {
        handler=new Handler();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopDelayed=intent.getLongExtra("Delayed",2000);
        PackageName=intent.getStringExtra("PackageName");
//        handler.postDelayed(()->{
//            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
//            startActivity(LaunchIntent);
//            KillSelfService.this.stopSelf();
//            System.exit(0);
//        },stopDelayed);

        return super.onStartCommand(intent, flags, startId);

    }
}
