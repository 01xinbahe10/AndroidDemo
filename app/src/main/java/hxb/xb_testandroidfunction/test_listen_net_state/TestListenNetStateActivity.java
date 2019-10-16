package hxb.xb_testandroidfunction.test_listen_net_state;

import android.Manifest;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import hxb.xb_testandroidfunction.test_listen_net_state.net_state.NetBroadcastReceiver;
import hxb.xb_testandroidfunction.test_listen_net_state.net_state.NetUtil;

/**
 * Created by hxb on 2018/7/11.
 */

public class TestListenNetStateActivity extends FragmentActivity implements NetBroadcastReceiver.NetEvent {

    public static NetBroadcastReceiver.NetEvent event;
    private TextView mTv;

    private NetBroadcastReceiver mNetBroacastReceiver;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mTv.setText(msg.what+"");
            return false;
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},10000);
        mTv = new TextView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTv.setLayoutParams(layoutParams);
        mTv.setText("测试");

        setContentView(mTv);

        event = this;
        mNetBroacastReceiver = new NetBroadcastReceiver();
        registerReceiver();
        Log.e("TAG", "onCreate: "+NetUtil.getNetWorkState(this));

    }


    @Override
    public void onNetChange(int netMobile) {
//        mTv.setText(netMobile);
        Log.e("TAG", "onNetChange: "+netMobile );
        mHandler.sendEmptyMessage(netMobile);
    }

    @Override
    public void isOnline(boolean is){
        Log.e("TAG", "isOnline:   是否在线： "+is );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    private void registerReceiver(){
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//添加动态广播的Action
        registerReceiver(mNetBroacastReceiver, dynamic_filter);//注册自定义动态广播消息
    }

    private void unregisterReceiver(){
        this.unregisterReceiver(mNetBroacastReceiver);
    }
}
