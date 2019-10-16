package hxb.xb_testandroidfunction.test_apk_update.retrofit_update;

import java.io.File;

/**
 * Created by hxb on 2018/8/24
 */
public interface AppApiCallback {
    interface DownloadCallBack{
        //下载中
        void progressOfValue(long currentLength,long totalLength);
        //下载成功
        void downloadSuccess(File fileDir);
        //下载失败
        void downloadFailed();
    }
}
