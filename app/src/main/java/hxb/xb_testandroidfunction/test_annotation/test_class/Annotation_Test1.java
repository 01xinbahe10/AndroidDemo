package hxb.xb_testandroidfunction.test_annotation.test_class;

import androidx.annotation.CallSuper;
import android.util.Log;

/**
 * Created by hxb on 2018/4/20.
 * 父类
 */

public class Annotation_Test1 {
    private String TAG = "Annotation_Test1";

    /**
     * 1--->强制方法调用类型：
     * 作用：指示所有Override该方法的方法，都需要调用调用该方法。
     *
     * Annotations：@CallSuper
     *
     * 使用场景：你设计了一个框架，里面有一些基础方法，各子类需要覆写该方法，
     * 但是一定要调用父类的方法。这时候可以使用这个Annotation来强制子类调用父类的方法。
     * */
    @CallSuper
    public void fun(){
        Log.e(TAG, "fun: ");
    }
}
