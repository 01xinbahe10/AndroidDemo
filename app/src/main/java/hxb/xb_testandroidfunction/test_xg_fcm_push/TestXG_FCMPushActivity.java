package hxb.xb_testandroidfunction.test_xg_fcm_push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_xg_fcm_push.xg_push.NotificationService;

/**
 * Created by hxb on 2018/7/20.
 * 测试腾讯XG推送 和 FCM推送
 */

public class TestXG_FCMPushActivity extends FragmentActivity{

    private NotificationService notificationService;
    private int allRecorders = 0;// 全部记录数
    private MsgReceiver updateListViewReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_xg_fcm_push);

        //开启信鸽的日志输出，线上版本不建议调用
        XGPushConfig.enableDebug(this,true);
        XGPushConfig.getToken(this);
        XGPushConfig.setMiPushAppId(this,"ebf471a46feb0");
        XGPushConfig.setMiPushAppKey(this,"fb61d8f873ac0616215e0baa1c100f89");


        XGPushConfig.setReportNotificationStatusEnable(getApplicationContext(),false);

        Log.e("hxb", "onCreate:       isNotificationOpened::::  "+XGPushManager.isNotificationOpened(getApplicationContext()) );
        //打开第三方推送
        XGPushConfig.enableOtherPush(getApplicationContext(), true);


        //注册数据更新监听器
//        updateListViewReceiver = new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
//        registerReceiver(updateListViewReceiver, intentFilter);


         /*
        注册信鸽服务的接口
        如果仅仅需要发推送消息调用这段代码即可
        */
        XGPushManager.registerPush(getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.e("hxb", "Activity -- onSuccess: "+data+"    flag:"+flag );
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.e("hxb", "Activity -- onFail: "+data+"     errCode:"+errCode+"   msg:"+msg );
                    }
                });

        // 获取token
        XGPushConfig.getToken(this);
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            allRecorders = notificationService.getCount();
        }
    }


    /**
     *
     * 测试是否有Google服务
     * @return
     */
    private boolean checkFcm() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("===========>>>>>", "checkFcm: 不支持FCM");
            return false;
        }
        Log.e("===========", "checkFcm: 支持FCM");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            String channelId  = getString(R.string.default_notification_channel_id);
//            String channelName = getString(R.string.default_notification_channel_name);
//            NotificationManager notificationManager =
//                    getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
//                    channelName, NotificationManager.IMPORTANCE_LOW));
//        }
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.e("===****=====", "checkFcm==="+token );
        return true;
    }
}
