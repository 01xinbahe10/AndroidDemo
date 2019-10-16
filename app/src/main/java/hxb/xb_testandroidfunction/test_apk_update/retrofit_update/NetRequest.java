package hxb.xb_testandroidfunction.test_apk_update.retrofit_update;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hxb on 2018/8/24
 * retrofit 下载文件
 */
public class NetRequest {
    /**
     * 下载文件
     */
    public static void downLoadFile(String url, final AppApiCallback.DownloadCallBack callback) {

        if (null == callback) {
            return;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(100000, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
                        .body(new FileResponseBody(originalResponse))
                        .build();
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl("https://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IApiService iApiService = retrofit.create(IApiService.class);
        Call<ResponseBody> responseBodyCall = iApiService.downloadFile(url);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                final File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+"test.apk");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long currentLength = 0;
                        OutputStream os = null;

                        InputStream is = response.body().byteStream();
                        long totalLength = response.body().contentLength();

                        try {
                            os = new FileOutputStream(file);
                            int len;
                            byte[] buff = new byte[1024];
                            while ((len = is.read(buff)) != -1) {
                                os.write(buff, 0, len);
                                currentLength += len;
                                callback.progressOfValue(currentLength,totalLength);
                                Log.e("NetRequest", "writeFile2Disk:  当前进度:" + currentLength + "   总计：" + totalLength);

                            }
                            callback.downloadSuccess(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            callback.downloadFailed();
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.downloadFailed();
                        } finally {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.downloadFailed();
            }
        });
    }

}
