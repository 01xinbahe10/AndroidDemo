package com.example.aac.base_frame;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.aac.base_frame.utils.StackManager;


/**
 * Created by hxb on 2017/6/15.
 */

public class BaseApplication extends Application {
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        StackManager.init();
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                StackManager.putActivity(activity,StackManager.ActivesStatus.CREATED);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                StackManager.putActivity(activity,StackManager.ActivesStatus.STARTED);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                StackManager.putActivity(activity,StackManager.ActivesStatus.RESUMED);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                StackManager.putActivity(activity,StackManager.ActivesStatus.PAUSED);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                StackManager.putActivity(activity,StackManager.ActivesStatus.STOPPED);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                StackManager.removeActivity(activity.getClass());
            }
        });
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }
}
