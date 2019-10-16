package hxb.xb_testandroidfunction.test_class.simulated_class;

import android.util.Log;

/**
 * Created by hxb on 2019/1/16
 */
public class ClassC1 {
    private final String TAG = "ClassC1";
    public ClassC1(){
        Log.e(TAG, "ClassC1:  构造" );
        TestFunC1();
    }
    protected void TestFunC1(){
        Log.e(TAG, "ClassC1:  方法TestFunC1()" );
    }
}
