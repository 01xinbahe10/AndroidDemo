package hxb.xb_testandroidfunction.test_xg_fcm_push.xg_push;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by hxb on 2018/7/20.
 * XG推送服务
 */

public class XGPushReceiver extends XGPushBaseReceiver{

    //注册的回调
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        if (context == null || xgPushRegisterResult == null) {
            return;
        }
        String text = "UNKNOW";
        String token = "UNKNOW";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = xgPushRegisterResult + "   注册成功";
            // 在这里拿token
           token = xgPushRegisterResult.getToken();
        } else {
            text = xgPushRegisterResult + "    注册失败错误码：" + i;
        }

        Log.e("hxb", "onRegisterResult: ------------->text: "+text + "    token:  "+token );
    }

    //反注册的回调
    @Override
    public void onUnregisterResult(Context context, int i) {
        if (context == null) {
            return;
        }
        String text = "";
        if (i == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + i;
        }

        Log.e("hxb", "onUnregisterResult: ------------>反注册："+text );

    }

    //设置tag的回调
    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    //删除tag的回调
    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    // 消息透传的回调
    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.e("hxb", "onTextMessage: -------->收到消息："+xgPushTextMessage.toString());

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击。此处不能做点击消息跳转，详细方法请参照官网的Android常见问题文档
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.e("hxb", "onNotifactionClickedResult: ----------->通知点击:"+xgPushClickedResult.toString() );
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        Log.e("hxb", "onNotifactionShowedResult: ------------>通知被展示:"+xgPushShowedResult.toString() );
    }
}
