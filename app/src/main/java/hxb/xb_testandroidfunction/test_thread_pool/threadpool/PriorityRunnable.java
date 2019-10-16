package hxb.xb_testandroidfunction.test_thread_pool.threadpool;

/**
 * Created by hxb on 2018/6/27.
 *
 */

public class PriorityRunnable implements Runnable{
    public enum Priority{
        HIGH, NORMAL, LOW
    }


    public final Priority priority;//任务优先级
    private final Runnable runnable;//任务真正执行者
    /*package*/
    long SEQ;//任务唯一标示

    public PriorityRunnable(Priority priority, Runnable runnable) {
        this.priority = priority == null ? Priority.NORMAL : priority;
        this.runnable = runnable;
    }
    @Override
    public void run() {
        this.runnable.run();
    }
}
