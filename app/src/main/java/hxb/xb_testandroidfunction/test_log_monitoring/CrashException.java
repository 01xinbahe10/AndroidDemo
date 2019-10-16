package hxb.xb_testandroidfunction.test_log_monitoring;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hxb on 2019/3/6.
 */
public class CrashException implements Thread.UncaughtExceptionHandler {

    private final String TAG = "CrashException";
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultUncaught;
    private File logFile = new File(Environment.getExternalStorageDirectory(), "crashLog.trace");
    private Timer mTimer = null;

    public static void init(Context context) {
        new CrashException(context);
    }

    private CrashException(Context context) {
        this.mContext = context;
        this.mDefaultUncaught = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this); // 设置为当前线程默认的异常处理器
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 打印当前的异常信息
        e.printStackTrace();

        // 如果我们没处理异常，并且系统默认的异常处理器不为空，则交给系统来处理
        if (!handlelException(e) && mDefaultUncaught != null) {
            mDefaultUncaught.uncaughtException(t, e);
        } else {
            Log.e(TAG, "uncaughtException: ---->  " + e.getMessage());

            // 已经记录完log, 提交服务器
//            upLoadErrorFileToServer(logFile);

//            Intent in = new Intent(mContext, MainActivity.class);
//            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果设置了此标志，这个activity将成为一个新task的历史堆栈中的第一个activity
//            mContext.startActivity(in);

            for (int i = 0; i < 10; i++) {
                Log.e(TAG, "uncaughtException: 测试是否可以执行其它的操作----->>>  " + i);
            }

            // 杀死我们的进程
            if (null != mTimer) {
                mTimer.cancel();
                mTimer = null;
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
//

                    Process.killProcess(Process.myPid());// 杀死线程
//                        System.exit(0);
                }
            }, 2 * 1000);


        }

    }

    /**
     * 记录异常信息
     */
    private boolean handlelException(Throwable ex) {
        // TODO Auto-generated method stub
        if (ex == null) {
            return false;
        }

        PrintWriter pw = null;
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            pw = new PrintWriter(logFile);

            // 收集手机及错误信息
            collectInfoToSDCard(pw, ex);
            pw.close();
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 收集记录错误信息
     *
     * @throws PackageManager.NameNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void collectInfoToSDCard(PrintWriter pw, Throwable ex) throws PackageManager.NameNotFoundException, IllegalAccessException, IllegalArgumentException {
        // TODO Auto-generated method stub
        PackageManager pm = mContext.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        pw.println("time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 记录错误发生的时间
        pw.println("versionCode: " + mPackageInfo.versionCode); // 版本号
        pw.println("versionName: " + mPackageInfo.versionName); // 版本名称

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            pw.print(field.getName() + " : ");
            pw.println(field.get(null).toString());
        }
        ex.printStackTrace(pw);
    }
}
