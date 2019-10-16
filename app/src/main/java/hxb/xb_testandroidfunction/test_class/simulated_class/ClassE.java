package hxb.xb_testandroidfunction.test_class.simulated_class;

import android.util.Log;

/**
 * Created by Soft010 on 2019/1/16
 */
public class ClassE extends ClassD {
    private final String TAG = "ClassE";

    public ClassE(){
        super();
    }

    @Override
    protected void TestFun1() {
        super.TestFun1();
        Log.e(TAG, "TestFun1: 子子子");
    }
}
