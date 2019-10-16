package hxb.xb_testandroidfunction.test_listen_net_state.net_state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;


import hxb.xb_testandroidfunction.test_listen_net_state.TestListenNetStateActivity;

/**
 * Created by hxb on 2018/7/11.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    private NetEvent event = TestListenNetStateActivity.event;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            int netWorkState = NetUtil.getNetWorkState(context);
            if (null != event) {
                //接口回调传过去状态的类型
                event.onNetChange(netWorkState);
            }
            //检测网络是否在线
//            event.isOnline(NetUtil.isOnline2(context));
//            new Thread(()->{
//                event.isOnline(NetUtil.isOnline());
////                event.isOnline(NetUtil.isOnline2(context));
//            }).start();

        }
    }

    public interface NetEvent{
        void onNetChange(int netMobile);
        void isOnline(boolean is);
    }
}
