package hxb.xb_testandroidfunction.test_annotation.test_class;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.MainThread;
import androidx.annotation.Size;
import androidx.annotation.WorkerThread;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxb on 2018/4/20.
 * 子类
 */

public class Annotation_Test2 extends Annotation_Test1 {
    private String TAG = "Annotation_Test2";
    @Override
    public void fun() {
//        super.fun();
        super.fun();
        Log.e(TAG, "fun: ");
    }


    /**
     * 2--->Annotation指定固定常量类型：
     * 1，如果一个方法只想接收这两个常量（常量名称，而不是常量的值）作为参数，那么可以这样使用
     * 2，如果调用fun2()时，不传递这两个常量，比如f(2)。那么就会检测出错误：Must be one of: INT_1, INT_2。
     * 3，不安指定的常量传也可以，只不过会对该方法产生不必要的错误。
     * */
    //定义常量
    public static final int INT_1 = 1;
    public static final int INT_2 = 2;
    //指定该Annotation描述的对象，只能使用这两个常量
    @IntDef({INT_1,INT_2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MyType{}
    public void fun2(@MyType int time) {
        Log.e(TAG, "fnu: " +time);
    }




    @IntDef({MotionEvent.ACTION_DOWN,MotionEvent.ACTION_UP,MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_OUTSIDE,
            MotionEvent.ACTION_POINTER_DOWN,MotionEvent.ACTION_POINTER_UP})
    @Retention(RetentionPolicy.CLASS)
    public @interface ActionEvent{}


    public void UseQuickActingSwitch(int i){
        @ActionEvent
        int action = i;
        switch (action) {//使用该ActionEvent会快速生成以下开关
//            case MotionEvent.ACTION_CANCEL:
//                break;
//            case MotionEvent.ACTION_DOWN:
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            case MotionEvent.ACTION_OUTSIDE:
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
        }
    }


    /**
     * 3--->资源对象的值类型指定型：
     * 作用：指示所描述的参数、字段、返回值必须是指定的资源类型的引用。
     * Annotations：
     *
     * @AnimatorRes
     * @AnimRes
     * @AnyRes
     * @ArrayRes
     * @AttrRes
     * @BoolRes
     * @ColorRes
     * @DimenRes
     * @DrawableRes
     * @FractionRes
     * @IdRes
     * @IntegerRes@InterpolatorRes
     * @LayoutRes
     * @MenuRes
     * @PluralsRes
     * @RawRes
     * @StringRes
     * @StyleableRes
     * @StyleRes
     * @TransitionRes
     * @XmlRes
     *
     * 使用场景：如果你有一个方法，需要一个 R.id 的值作为参数，或者一个方法，
     * 希望返回值是 R.anim 类型的引用，那么这个时候就可以使用这些Annotation。
     * */

    //希望接收一个id作为参数
    public void fun3(@IdRes int id){

    }
    //但是实际上不小心传递了一个layout进去。
    //fun3(R.layout.day_view)
    //那么这个时候就会报错：Expected resource of type id。

    /**
     * 4--->取值范围型
     * 作用：指示一个int、float或者double类型的值，合理的取值范围。
     *  Annotations：
     *  @FloatRange
     *  @IntRange
     *  使用场景：比如有一个方法，接收一个int型的透明度参数，那么该方法
     *  就会希望传进来的值应该是0~255，这时就可以使用该Annotation。
     *
     * */
    //希望接收一个[0, 255]的值作为透明度
    public void fun4_1(@IntRange(from = 0,to = 255) int alpha){

    }
    public void fun4_2(@FloatRange(from = 0.0f,to = 255.0f) float x){

    }

    /**
     * 5--->线程标识型
     *
     * 作用：指示某个对象、构造器或者方法，应该在指定的类型的线程运行。
     * Annotations：
     *
     * @BinderThread
     * @MainThread
     * @UiThread
     * @WorkerThread
     *
     * 使用场景：如果有一个方法，比较耗时，所以它应该放在工作线程执行。这时候可以
     * 用@WorkerThread来标注该方法，当在主线程调用该方法的时候，就会报错。
     * */

    //该方法比较耗时，所以放在子线程执行
    @WorkerThread
    public void fun5_1(){

    }
    @MainThread
    public void fun5_2(){
        //在主线程调用该方法，会报错。
        //fun5_1();
    }

    //那么这个时候就会报错：Method shouldRunOnWorkerThread must be called from the worker thread, currently inferred thread is main。

    //像 Activity 的onCreate方法，都是被标注为主线程执行的。
    //@MainThread
    //@CallSuper
    //protected void onCreate(@Nullable Bundle savedInstanceState) {
    //  .......................;
    //}


    /**
     * 6--->空指针指示型
     *
     * 作用：指示某个参数、返回值等是否可以为空。
     * Annotations：
     * @NonNull
     * @Nullable
     * 使用场景：比如一个方法，可能返回一个null，可以使用@Nullable来标注，
     * 那么使用该方法的地方，一般都要做null判断。
     * */



    /**
     * 8--->参数长度限定类型
     *  作用：指示被描述的对象应该有明确的大小。
     *  Annotations：
     *  @Size
     *  使用场景：当一个参数，需要最小的大小时，使用该Annotation，比如，
     *  一个数组最少有2个元素、一个String最低长度为3等。
     *
     * */
    //要求param的长度最小为2
    public void fun8_1(@Size(min = 2) String name){

    }
    //fun8_1("h");//传递长度只有1的"s"时，会报错



    /**
     * 9--->其他类型
     *
     * 作用：指示一个int值是颜色类型的值（AARRGGBB）。
     * Annotations：
     * @ColorInt
     *
     * */
    //标明该常量代表颜色值
    @ColorInt public static final int BLACK = 0xFF000000;
    @ColorInt public static final int YELLOW = 0xFF784899;
    //标明该方法希望接收一个代表颜色值的参数
    public void fun9_1(@ColorInt int color){

    }




}




/**
 * 7--->混淆指示类型
 *  作用：指示被描述的对象，在编译的时候不要被混淆。
 *  Annotations：
 *  @Keep
 *  使用场景：当一个方法、属性、对象等，需要被反射使用时，比如通过反射来实现工厂。这时候
 *  可以使用该Annotation来标记，这样在编译的时候，就不会被混淆（如果开了混淆的话）。
 * */

//抽象产品，避免被混淆
@Keep
class Product {}

//产品A，避免被混淆
@Keep
class ProductA extends Product{}

//产品B，避免被混淆
@Keep
class ProductB extends Product{
    private Product getProduct(String productName) {
        //使用反射创建具体的Product
        return null;
    }
}


