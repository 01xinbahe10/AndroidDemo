package hxb.xb_testandroidfunction.test_class;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.test_class.simulated_class.Class1;
import hxb.xb_testandroidfunction.test_class.simulated_class.ClassB;
import hxb.xb_testandroidfunction.test_class.simulated_class.ClassE;

/**
 * Created by hxb on 2019/1/10
 */
public class TestClassActivity extends FragmentActivity{
    private final String TAG = "TestClassActivity";

    private Class1 class1;
    private Class1 class2;
    private Class1 class3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        testDollsClass();
//        testClassExtendsFun();
        testAbstractClassFun();
    }

    /**
     * 测试套娃类
     * */
    private void testDollsClass(){
        class1 = new Class1();
        class1.name = "class1";
        class1.class1 = new Class1();
        class1.class1.name = "class1_1";
        class1.class1.class1 = new Class1();
        class1.class1.class1.name = "class1_1_1";
        class1.class1.class1.class1 = new Class1();


        class2 = class1.class1;
        String lastClassName = "";
        while (null !=  class2){//定位到最后一个class
            Log.e(TAG, "onCreate: 111111111111   "+class2.name);
            if (!TextUtils.isEmpty(class2.name)) {
                lastClassName = class2.name;
            }
            class2 = class2.class1;
        }

        class3 = class1.class1;
        while (null != class3){//找到最后一个
            if (TextUtils.equals(class3.name,lastClassName)){
                break;
            }
            class3 = class3.class1;
        }

        Log.e(TAG, "onCreate: 2222222222222  "+class3.name );
        class3.name = "class1-1-1";
        Log.e(TAG, "onCreate: 2222222222222  "+class1.class1.class1.name );

        List<Class1> list = new ArrayList<>();
        Class1 class4 = class1.class1;
        while (null != class4){//取上一个对象
            if (class4.name.equals("class1-1-1")){
                break;
            }

            list.add(class4);


            class4 = class4.class1;
        }
        String name ;
        if (list.size() == 0){
            name = class1.name;
        }else {
            name = list.get(list.size() -1).name;
        }

        Log.e(TAG, "onCreate: 444444444444444  size: "+list.size() +"    name:"+name );
    }


    /**
     * 测试类继承 的方法问题
     * */

    private void testClassExtendsFun(){
        ClassB classB = new ClassB();

    }

    /**
     * 抽象类的方法问题
     * */
    private void testAbstractClassFun(){
        ClassE classE = new ClassE();

    }

}
