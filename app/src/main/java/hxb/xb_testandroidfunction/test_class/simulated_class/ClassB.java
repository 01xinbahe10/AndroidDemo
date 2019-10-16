package hxb.xb_testandroidfunction.test_class.simulated_class;


import android.util.Log;

/**
 * Created by hxb on 2019/1/16
 */
public class ClassB extends ClassA{
    private final String TAG = "ClassB";
    public ClassB(){
        Log.e(TAG, "ClassB: 构造");
        TestFun1();
    }
    @Override
    protected void TestFun1() {
        Log.e(TAG, "TestFun1:  执行了ClassB方法一");
//        TestFun2();
//        super.TestFun1();
    }

    @Override
    protected int TestFun2() {
        Log.e(TAG, "TestFun2:  执行了ClassB方法二     20" );
        return 20;
    }
}
