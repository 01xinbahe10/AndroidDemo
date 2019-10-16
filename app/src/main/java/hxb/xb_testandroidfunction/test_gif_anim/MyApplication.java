package hxb.xb_testandroidfunction.test_gif_anim;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import hxb.xb_testandroidfunction.test_log_monitoring.CrashException;


/**
 * Application基类
 * Created by shixiaoming on 16/12/6.
 */

public class MyApplication extends Application{

    private static Context mContext;

    @Override public void onCreate() {
        super.onCreate();
//        Fresco.initialize(this);//Fresco初始化
        MyApplication.mContext = getApplicationContext();

//        CrashException.init(this);
    }

    public static Context getAppContext() {
        return MyApplication.mContext;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(
                getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
