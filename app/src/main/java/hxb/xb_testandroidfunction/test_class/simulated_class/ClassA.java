package hxb.xb_testandroidfunction.test_class.simulated_class;

import android.util.Log;

import javax.security.auth.login.LoginException;

/**
 * Created by hxb on 2019/1/16
 */
public class ClassA {
    private final String TAG = "ClassA";
    public ClassA(){
        Log.e(TAG, "ClassA: 构造" );
        TestFun1();
    }
    protected void TestFun1(){
        Log.e(TAG, "TestFun1: 执行了ClassA方法一" );
        TestFun2();
    }

    protected int TestFun2(){
        int i = 10;
        Log.e(TAG, "TestFun2: 执行了ClassA方法二   "+i );
        return i;
    }
}
