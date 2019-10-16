package hxb.xb_testandroidfunction.test_interface_oriented_programming.test_module2;

import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IFun;

/**
 * Created by hxb on 2018/12/12
 */
public class Fun2 implements IFun {
    private Fun2A mFun2A;
    public Fun2(){
        mFun2A = new Fun2A();
    }

    @Override
    public void funA() {
        mFun2A.fun1();
    }

    @Override
    public void funB() {
        mFun2A.fun2();
    }
}
