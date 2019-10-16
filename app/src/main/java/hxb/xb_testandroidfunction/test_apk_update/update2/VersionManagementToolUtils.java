package hxb.xb_testandroidfunction.test_apk_update.update2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hxb on 2018/6/13.
 * apk 版本管理工具
 */

public class VersionManagementToolUtils {
    /**
     * 获取当前程序的版本名
     */
    public static String getVersionName(Context context) throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        Log.e("TAG","版本号"+packInfo.versionCode);
        Log.e("TAG","版本名"+packInfo.versionName);
        return packInfo.versionName;
    }

    /**
     * 获取当前程序的版本号
     */
    public static int getVersionCode(Context context) throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        Log.e("TAG","版本号"+packInfo.versionCode);
        Log.e("TAG","版本名"+packInfo.versionName);
        return packInfo.versionCode;
    }

    /**
     * 下载新版本程序，需要子线程
     */
    public static void loadNewVersionProgress(final Activity activity) {
//        final String uri = "http://www.apk.anzhi.com/data3/apk/201703/14/4636d7fce23c9460587d602b9dc20714_88002100.apk";

//        final String uri = "http:192.168.200.174:80:test_update_apk/TestUpdateApk/app-debug.apk";
        final String uri = "http:192.168.11.213/app-develop-debug.apk";

        final ProgressDialog pd;    //进度条对话框
//        pd = new ProgressDialog(activity);
//        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pd.setMessage("正在下载更新");
//        pd.show();
        //启动子线程下载任务
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(uri);
//                    pd.dismiss(); //结束掉进度条对话框
                    sleep(3000);
                    installApk(file,activity);

                } catch (Exception e) {
                    //下载apk失败
//                    Toast.makeText(activity, "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 从服务器获取apk文件的代码
     * 传入网址uri，进度条对象即可获得一个File文件
     * （要在子线程中执行哦）
     */
//    , ProgressDialog pd
    public static File getFileFromServer(String uri) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(uri);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
//            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time= System.currentTimeMillis();//当前时间的毫秒数
            File file = new File(Environment.getExternalStorageDirectory(), time+"updata.apk");//路径
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
//                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(File file,Activity activity) {
//        Intent intent = new Intent();
//        //执行动作
//        intent.setAction(Intent.ACTION_VIEW);
//        //执行的数据类型
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//        activity.startActivity(intent);


//        // 跳转到系统安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setDataAndType(Uri.fromFile(file),
//                "application/vnd.android.package-archive");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        activity.startActivityForResult(intent, 0);// 如果用户取消安装的话,
//        // 会返回结果,回调方法onActivityResult
//        android.os.Process.killProcess(android.os.Process.myPid());


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Uri uri = Uri.fromFile(file);
            //  BuildConfig.APPLICATION_ID + ".fileprovider" 是在manifest中 Provider里的authorities属性定义的值
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //临时授权
            installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            activity.startActivity(intent);
        } else {
            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // FLAG_ACTIVITY_NEW_TASK 可以保证安装成功时可以正常打开 app
            promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(promptInstall);
        }
    }
}
