package com.example.aac.test.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.aac.R;

public class ForegroundService extends Service {
    private static String TAG = "XB_"+ForegroundService.class.getName();

    private final String channel_id = "APP_RUNNING_CHANNEL_ID";

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        sendServiceRunningNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

//        clearServiceNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void sendServiceRunningNotification() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        NotificationManager manager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return;
        }

        Log.d(TAG, "sendServiceRunningNotification: ");

        final String channel_name = "yep!yep!";
        NotificationChannel channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_MIN);
        manager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("hello")
                .setContentText("simba")
                .build();
        startForeground(Integer.MAX_VALUE, notification);
    }

    private void clearServiceNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        Log.d(TAG, "clearServiceNotification: ");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.cancel(Integer.MAX_VALUE);
    }
}
