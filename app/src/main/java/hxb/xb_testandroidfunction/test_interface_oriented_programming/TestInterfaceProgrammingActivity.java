package hxb.xb_testandroidfunction.test_interface_oriented_programming;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.FunA;
import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IBase;
import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IFun;
import hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy.IFun2;
import hxb.xb_testandroidfunction.test_interface_oriented_programming.test_module2.Fun2;
import hxb.xb_testandroidfunction.test_interface_oriented_programming.test_module3.Fun3;

/**
 * Created by hxb on 2018/12/12
 * 测试面向接口编程
 */
public class TestInterfaceProgrammingActivity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FunA funA = new FunA();
//        funA.setFunA(new Fun1());//设置Fun1实体类依赖注入
        funA.setFunA(new Fun2());

        IBase ibase = funA.getFunA();//获取接口
        if (ibase instanceof IFun){
            IFun iFun = (IFun) ibase;
            iFun.funA();//用接口执行Fun2类中的方法
            iFun.funB();

        }


        funA.setFunA(new Fun3());
        IBase iBase2 = funA.getFunA();
        if (iBase2 instanceof IFun2.Test1){
            IFun2.Test1 iFun2Test1 = (IFun2.Test1) iBase2;
            iFun2Test1.test1();

        }

        if (iBase2 instanceof IFun2.Test2){
            IFun2.Test2 iFun2Test2 = (IFun2.Test2) iBase2;
            iFun2Test2.test2();
        }




    }

}
