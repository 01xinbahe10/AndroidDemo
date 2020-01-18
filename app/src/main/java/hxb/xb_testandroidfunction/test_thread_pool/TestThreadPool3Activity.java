package hxb.xb_testandroidfunction.test_thread_pool;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import java.util.concurrent.ExecutorService;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_thread_pool.threadpool.PriorityExecutor;
import hxb.xb_testandroidfunction.test_thread_pool.threadpool.PriorityRunnable;

/**
 * Created by hxb on 2018/6/27.
 *
 */

public class TestThreadPool3Activity extends FragmentActivity{
    private static final int count = Runtime.getRuntime().availableProcessors() * 3 + 2;
//    private static final int count = Runtime.getRuntime().availableProcessors() ;
    public String TAG = "测试线程池";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_thread_pool3);
//       testThreadPriority();
        testThreadLocal();
    }

    //测试线程执行的优先级
    private void testThreadPriority(){
        ExecutorService executorService = new PriorityExecutor(5, false);
        for (int i = 0; i < 20; i++) {
            PriorityRunnable priorityRunnable = new PriorityRunnable(PriorityRunnable.Priority.NORMAL, new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, Thread.currentThread().getName()+"优先级正常");
                }
            });
            if (i % 3 == 1) {
                priorityRunnable = new PriorityRunnable(PriorityRunnable.Priority.HIGH, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, Thread.currentThread().getName()+"优先级高"+count);
                    }
                });
            } else if (i % 5 == 0) {
                priorityRunnable = new PriorityRunnable(PriorityRunnable.Priority.LOW, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, Thread.currentThread().getName()+"优先级低");
                    }
                });
            }
            executorService.execute(priorityRunnable);
        }
    }


    //测试ThreadLocal在各个线程中使用的情况
    private void testThreadLocal(){
        ExecutorService executorService = new PriorityExecutor(5, false);
        for (int i = 0; i < 5; i++) {

            int finalI = i;
            executorService.execute(new PriorityRunnable(PriorityRunnable.Priority.NORMAL, new Runnable() {
                @Override
                public void run() {
                    switch (finalI){
                        case 0://线程一，以下依次类推
                            updateStaticThreadLocal(0);
                            break;
                        case 1:
                            updateStaticThreadLocal(1);
                            break;
                        case 2:
                            updateStaticThreadLocal(2);
                            break;
                        case 3:
                            updateStaticThreadLocal(3);
                            break;
                        case 4:
                            updateStaticThreadLocal(4);
                            break;
                    }
                }
            }));
        }

    }


    //测试静态状态下的ThreadLocal各个线程的值变化
    public static void updateStaticThreadLocal(int testIndex){
        Log.e("TAG", "updateStaticThreadLocal11:  "+threadLocal.get() );
        threadLocal.set(testIndex);
        Log.e("TAG", "updateStaticThreadLocal22:  "+threadLocal.get() );
    }
    /*
    * ThreadLocal是为了保证每个线程中的数据是相互隔离的，互不干扰
    * */
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
        @Nullable
        @Override
        protected Integer initialValue() {
            return 1;
        }
    };
}
