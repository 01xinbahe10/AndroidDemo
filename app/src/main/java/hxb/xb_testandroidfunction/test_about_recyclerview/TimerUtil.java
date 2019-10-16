package hxb.xb_testandroidfunction.test_about_recyclerview;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxb on 2019/4/28.
 */
public class TimerUtil {
    public static TimerUtil init() {
        return new TimerUtil();
    }

    private long mStartTime, mDuration = 0;
    private int mTimingOrder = STATE.INCREMENT;//时间增长方式，默认是递增
    private boolean mTermination = true;//判断是否终止
    private final long mMaxTimeLimit = 3600;//最大时长限制
    private long mTimingSec = 0;//计时秒
    private Feedback mFeedback;

    private TimerUtil() {
//        mStartTime = System.currentTimeMillis();
    }

    public TimerUtil setDuration(@IntRange(from = 0,to = mMaxTimeLimit) long duration)  {
        if (duration > mMaxTimeLimit) {
            mTermination = false;//终止本次操作
        }
        mDuration = duration;
        return this;
    }

    public TimerUtil setFeedback(Feedback feedback) {
        mFeedback = feedback;
        return this;
    }

    @IntDef({STATE.INCREMENT, STATE.DECREMENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface state {
    }

    public TimerUtil setTimingOrder(@state int order) {
        mTimingOrder = order;
        return this;
    }

    public void start() {
        if (!mTermination) {
            return;
        }
        if (mTimingOrder == STATE.DECREMENT) {
            mTimingSec = mDuration;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!mTermination) {
                        return;
                    }
                    if (null != mFeedback){
                        if (mFeedback.onFeedback(mTimingSec)){
                            return;
                        }
                    }
                    if (mTimingOrder == STATE.DECREMENT) {
                        mTimingSec--;
                        if (mTimingSec < 0) {
                            return;
                        }
                    } else {
                        mTimingSec++;
                        if (mTimingSec > mDuration) {
                            return;
                        }
                    }


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public void cancel(){
        mTermination = false;
    }


    public interface Feedback {
        boolean onFeedback(long currentTime);
    }

    public interface STATE {
        int INCREMENT = 1;//递增
        int DECREMENT = 2;//递减
    }
}
