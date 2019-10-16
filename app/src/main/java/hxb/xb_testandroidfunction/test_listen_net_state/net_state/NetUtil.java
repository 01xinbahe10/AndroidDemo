package hxb.xb_testandroidfunction.test_listen_net_state.net_state;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hxb on 2018/7/11.
 */

public class NetUtil {
    //没有连接网络
    public static final int NETWORK_NONE = -1;
    //移动网络
    public static final int NETWORK_MOBILE = 0;
    //无线网络
    public static final int NETWORK_WIFI = 1;

    public static int getNetWorkState(Context context) {
        //得到连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE;
            }
        }
        return NETWORK_NONE;
    }


    /**
     * 判断检查外网的连通性
     */
    public static boolean isOnline() {
        //需在线程中使用
        try {
            Runtime runtime = Runtime.getRuntime();
            //国内使用114.114.114.114，如果全球通用google：8.8.8.8
            Process ipProcess = runtime.exec("ping -c 3 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            Log.e("Avalible", "Process:" + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;


    }


    // TODO: 2018/7/11 该方法不能使用，原因未知
    /**
     * 通过socket检查外网的连通性
     */

    public static boolean isOnline2(Context context) {
        Socket s = null;
        //需在线程中使用
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = (null != activeNetworkInfo) && activeNetworkInfo.isConnected();
        if (!connected) return false;
        boolean routeExists;
        try {
            s = new Socket();

            //国内使用114.114.114.114，如果全球通用google：8.8.8.8
            InetAddress host = InetAddress.getByName("8.8.8.8");
            s.connect(new InetSocketAddress(host, 80), 5000);//google:53
            routeExists = true;
//            s.close();
        } catch (IOException e) {
            routeExists = false;
        }finally {
            if (null != s){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("TAG", "isOnline2:   connected:"+connected+"     routeExists:"+routeExists );
        return connected && routeExists;
    }


    public static boolean isOnline3(Context context){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //得到连接管理器
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return  networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }

        return false;

    }

}
