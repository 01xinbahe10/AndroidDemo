package hxb.xb_testandroidfunction.test_interface_oriented_programming.test_module3;

import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IFun2;

/**
 * Created by hxb on 2018/12/12
 */
public class Fun3 implements IFun2.Test1,IFun2.Test2{
    private Fun3A mFun3A;
    private Fun3B mFun3B;
    public Fun3(){
        mFun3A = new Fun3A();
        mFun3B = new Fun3B();
    }
    @Override
    public void test1() {
        mFun3A.Fun3Test1();
    }

    @Override
    public void test2() {
        mFun3B.Fun3Test2();
    }
}
