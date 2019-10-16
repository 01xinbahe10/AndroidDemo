package hxb.xb_testandroidfunction.test_class.simulated_class;

import android.util.Log;

/**
 * Created by Soft010 on 2019/1/16
 */
public abstract  class ClassD extends ClassC2 {
    private final String TAG = "ClassD";
    @Override
    protected void TestFun1() {
        Log.e(TAG, "TestFun1: 父父父父父父父  ");
    }
}
