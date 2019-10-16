package hxb.xb_testandroidfunction.test_notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_annotation.activity.MainActivity;

/**
 * Created by hxb on 2018/10/8
 */
public class TestNotificationActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_notification);
        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager == null){
                    return;
                }

                //系统默认布局
                Bitmap btm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_app);
                // 点击通知栏跳转的activity
//                Intent resultIntent = new Intent(TestNotificationActivity.this, MainActivity.class);
                Intent resultIntent = new Intent();
                PendingIntent resultPendingIntent = PendingIntent.getActivity(TestNotificationActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("dxy_app_update", "应用更新", NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(channel);
                }

                mBuilder = new NotificationCompat.Builder(TestNotificationActivity.this, "dxy_app_update");
                mBuilder.setSmallIcon(R.drawable.ic_launcher_app)
                        .setContentTitle("5 new message")
                        .setContentText("twain@android.com")
                        .setTicker("New message")//第一次提示消息的时候显示在通知栏上
                        .setNumber(12)
                        .setLargeIcon(btm)
                        .setColor(Color.parseColor("#0094BF"))
                        .setAutoCancel(true)//自己维护通知的消失
                        .setProgress(100,50,false)
                        .setDefaults(Notification.DEFAULT_ALL)// 使用默认提示音
                        .setContentIntent(resultPendingIntent);


                    Log.e("TAG", "onClick: 111111111111111111111   ");
                    mNotificationManager.notify(110, mBuilder.build());

            }
        });

        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager == null){
                    return;
                }

                //自定义显示布局
                RemoteViews contentViews = new RemoteViews(getPackageName(), R.layout.item_test_notification);//通过控件的Id设置属性
                contentViews.setImageViewResource(R.id.imageNo, R.drawable.ic_launcher_app);
                contentViews.setTextViewText(R.id.titleNo, "自定义通知标题");
                contentViews.setTextViewText(R.id.textNo, "自定义通知内容");

                NotificationCompat.Builder mBuilder;

                //点击通知栏跳转的activity
                Intent intent = new Intent(TestNotificationActivity.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(TestNotificationActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("dxy_app_update1", "应用更新", NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(channel);
                }

                mBuilder = new NotificationCompat.Builder(TestNotificationActivity.this, "dxy_app_update1");
                mBuilder.setSmallIcon(R.drawable.ic_launcher_app)
                        .setContentTitle("5 new message")
                        .setContentText("twain@android.com")
                        .setTicker("New message11")
                        //自动管理通知栏消息
                        .setAutoCancel(true)
                        //使用默认提示音
                        .setDefaults(Notification.DEFAULT_ALL)
                        //自定义布局
                        .setContent(contentViews)
//                        .setCustomContentView(contentViews)
                        .setContentIntent(pendingIntent);

                mNotificationManager.notify( 1, mBuilder.build());

                Log.e("TAG", "tvCustomNotification: -------------------------  ");
            }
        });
    }


}
