package hxb.xb_testandroidfunction.test_thread_pool.runnable;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by hxb on 2018/6/27.
 */

public class MyRunnable implements Runnable {

    private boolean cancleTask = false;

    private boolean cancleException = false;

    private Handler mHandler = null;

    public MyRunnable(Handler handler)
    {
        mHandler = handler;
    }

    @Override
    public void run() {
        Log.i("KKK", "MyRunnable  run() is executed!!! ");
        runBefore();
        if (cancleTask == false)
        {
            running();
            Log.i("KKK", "调用MyRunnable run()方法");
        }

        runAfter();
    }


    /**
     * <Summary Description>
     */
    private void runAfter()
    {
        Log.i("KKK", "runAfter()");
    }

    /**
     * <Summary Description>
     */
    private void running()
    {
        Log.i("KKK", "running()");
        try
        {
            // 做点有可能会出异常的事情！！！
            int prog = 0;
            if (cancleTask == false && cancleException == false)
            {
                while (prog < 101)
                {
                    if ((prog > 0 || prog == 0) && prog < 70)
                    {
                        SystemClock.sleep(100);
                    }
                    else
                    {
                        SystemClock.sleep(300);
                    }
                    if (cancleTask == false)
                    {
                        mHandler.sendEmptyMessage(prog++);
                        Log.i("KKK", "调用 prog++ = " + (prog));
                    }
                }
            }
        }
        catch (Exception e)
        {
            cancleException = true;
        }
    }

    /**
     * <Summary Description>
     */
    private void runBefore()
    {
        // TODO Auto-generated method stub
        Log.i("KKK", "runBefore()");
    }

    public void setCancleTaskUnit(boolean cancleTask)
    {
        this.cancleTask = cancleTask;
        Log.i("KKK", "点击了取消任务按钮 ！！！");
        // mHandler.sendEmptyMessage(0);
    }
}
