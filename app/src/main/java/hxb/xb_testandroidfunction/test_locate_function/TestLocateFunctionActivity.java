package hxb.xb_testandroidfunction.test_locate_function;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_locate_function.util.HttpUtil;

/**
 * Created by hxb on 2018/11/26
 * 通过获取IP判断国别
 * */
public class TestLocateFunctionActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "TestLocateFunction";
    private Button mButton1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_locate_function);
        mButton1 = findViewById(R.id.btn1);
        mButton1.setOnClickListener(this);

        Log.e(TAG, "onCreate: "+ isCN(this)+"    2:"+isChinaSimCard(this));
        String str = "https://play.google.com/store/apps/details?id=com.robelf.controller";
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = HttpUtil.sendHttpRequest(TestLocateFunctionActivity.this, str);
                Log.e(TAG, "run: "+result );
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                mButton1.setText(getIPAddress(this));
                break;
        }
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }


    /**
     *      * 将得到的int类型的IP转换为String类型
     *      *
     *      * @param ip
     *      * @return
     *      
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
















    /**
     * 判断国家是否是国内用户
     *
     *方法一
     *
     * @return
     */
    public static boolean isCN(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getSimCountryIso();
        boolean isCN = false;//判断是不是大陆
        if (!TextUtils.isEmpty(countryIso)) {
            countryIso = countryIso.toUpperCase(Locale.US);
            if (countryIso.contains("CN")) {
                isCN = true;
            }
        }
        return isCN;

    }


/**
 * 方法二
 */
    /** 查询手机的 MCC+MNC */
    private static String getSimOperator(Context c) {
        TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return tm.getSimOperator();
        } catch (Exception e) {

        }
        return null;
    }


    /** 因为发现像华为Y300，联想双卡的手机，会返回 "null" "null,null" 的字符串 */
    private static boolean isOperatorEmpty(String operator) {
        if (operator == null) {
            return true;
        }


        if (operator.equals("") || operator.toLowerCase(Locale.US).contains("null")) {
            return true;
        }


        return false;
    }


    /** 判断是否是国内的 SIM 卡，优先判断注册时的mcc */
    public static boolean isChinaSimCard(Context c) {
        String mcc = getSimOperator(c);
        if (isOperatorEmpty(mcc)) {
            return false;
        } else {
            return mcc.startsWith("460");
        }
    }

}
