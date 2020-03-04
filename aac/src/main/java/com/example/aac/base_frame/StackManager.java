package com.example.aac.base_frame;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Stack;

/**
 * Created by hxb on  2020/1/2
 * 堆栈式管理
 */
public class StackManager {

    private static StackManager stackManager = null;
    private Stack<Activity> mStackActivity;

    @IntDef({ActivesStatus.CREATED,
            ActivesStatus.STARTED,
            ActivesStatus.RESUMED,
            ActivesStatus.PAUSED,
            ActivesStatus.STOPPED,
            ActivesStatus.NO_STATUS})
    @Retention(RetentionPolicy.CLASS)
    public @interface ActivesStatus{
        int CREATED = 0;
        int STARTED = 1;
        int RESUMED = 2;
        int PAUSED = 3;
        int STOPPED = 4;
        int DESTROYED = 5;
        int NO_STATUS = -1;
    }

    private StackManager(){
        mStackActivity = new Stack<>();
    }

    /**
     * 添加/修改Activity状态到堆栈
     */
    private void _putActivity(android.app.Activity activity,@ActivesStatus int status){
        if (null == activity){
            return;
        }
        if (mStackActivity.size() == 0){
            mStackActivity.add(Activity.init().setActivity(activity).setStatus(status));
            return;
        }
        boolean isSameActivity = true;//侦测是否相同对象的activity
        for (Activity a:mStackActivity) {
            if (null == a || null == a.activity){
                continue;
            }
            if (activity == a.activity){
                a.setStatus(status);
                isSameActivity = true;
                break;
            }
            isSameActivity = false;
        }
        if (!isSameActivity){
            mStackActivity.add(Activity.init().setActivity(activity).setStatus(status));
        }

    }

    /**
     * 移除指定的Activity
     */
    private boolean _removeActivity(Class<?> clazz, boolean isFinish) {
        for (Activity a:mStackActivity) {
            if (null == a || null == a.activity){
                continue;
            }
            if (clazz == a.activity.getClass()){
                if (isFinish && !a.activity.isFinishing()){
                    a.activity.finish();
                }
                return mStackActivity.remove(a);
            }
        }

        return false;
    }

    /**
     * 获取指定的Activity
     */
    private Activity _getActivity(Class<?> clazz){
        for (Activity a:mStackActivity) {
            if (null == a || null == a.activity){
                continue;
            }
            if (clazz == a.activity.getClass()){
                return a;
            }
        }
        return null;
    }

    /**
     * 结束指定类名的Activity
     */
    private void _finishActivity(Class<?> clszz) {
        for (Activity a : mStackActivity) {
            if (null == a || null == a.activity){
                continue;
            }
            if (clszz == a.activity.getClass()){
                if (!a.activity.isFinishing()) {
                    a.activity.finish();
                }
                mStackActivity.remove(a);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    private void _finishAllActivity() {
        for (int i = 0, size = mStackActivity.size(); i < size; i++) {
           Activity a = mStackActivity.get(i);
           if (null == a || null == a.activity){
               continue;
           }
           if (!a.activity.isFinishing()){
               a.activity.finish();
           }
        }
        mStackActivity.clear();
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity getCurrentActivity(){
        if (null == stackManager){
            return null;
        }
        return stackManager.mStackActivity.lastElement();
    }


    public static StackManager init(){
        if (null == stackManager){
            stackManager = new StackManager();
        }
        return stackManager;
    }

    public static void putActivity(android.app.Activity activity, @ActivesStatus int status){
        if (null == stackManager){
            return;
        }
        stackManager._putActivity(activity,status);
    }

    public static boolean removeActivity(Class<?> clazz){
        if (null == stackManager){
            return false;
        }
        return stackManager._removeActivity(clazz,false);
    }

    public static Activity getActivity(Class<?> clazz){
        if (null == stackManager){
            return null;
        }
        return stackManager._getActivity(clazz);
    }


    public static void finishActivity(Class<?> clazz){
        if (null == stackManager){
            return;
        }
        stackManager._removeActivity(clazz,true);
    }

    public static void AppExit(){
        if (null == stackManager){
            return;
        }
        try {
            stackManager._finishAllActivity();
//            System.exit(0);
        }catch (Exception e){
            stackManager.mStackActivity.clear();
            e.printStackTrace();
        }

    }


    public final static class Activity{
        public android.app.Activity activity;
        public int activeStatus = ActivesStatus.NO_STATUS;

        public static Activity init(){
            return new Activity();
        }

        public Activity setActivity(android.app.Activity activity){
            this.activity = activity;
            return this;
        }

        public Activity setStatus(@ActivesStatus int status){
            this.activeStatus = status;
            return this;
        }
    }



}
