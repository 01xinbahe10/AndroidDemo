package hxb.xb_testandroidfunction.test_interface_oriented_programming.test_module1;

import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IFun;

/**
 * Created by hxb on 2018/12/12
 */
public class Fun1 implements IFun {
    private Fun1A mFun1A;
    public Fun1(){
        mFun1A = new Fun1A();
    }

    @Override
    public void funA() {
        mFun1A.fun1();
    }

    @Override
    public void funB() {
        mFun1A.fun2();
    }
}
