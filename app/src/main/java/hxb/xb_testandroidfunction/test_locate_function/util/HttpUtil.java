package hxb.xb_testandroidfunction.test_locate_function.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hxb on 2018/11/26
 */
public class HttpUtil {
    private static final String TAG="POSITION";
    public static String sendHttpRequest(Context context, String address){
        HttpURLConnection connection=null;
        try{
            URL url=new URL(address);
            Log.d(TAG, "HttpUtil: request="+address);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            Log.d(TAG, "HttpUtil: getting the inputStream...");
            InputStream in=connection.getInputStream();
            Log.d(TAG, "HttpUtil: got the inputStream...");
            //下面对获取到的流进行读取
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder response=new StringBuilder();
            String line;
            while((line=reader.readLine())!=null){
                response.append(line);
            }
            Log.d(TAG, "HttpUtil: Got the response...");
            return response.toString();
        } catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "HttpUtil: Some thing wrong....");
            return e.getMessage();
        } finally{
            if(connection!=null){
                connection.disconnect();
            }
        }
    }
}
