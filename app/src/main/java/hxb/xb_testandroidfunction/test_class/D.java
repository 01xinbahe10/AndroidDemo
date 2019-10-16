package hxb.xb_testandroidfunction.test_class;

/**
 * Created by hxb on 2018/11/21
 */
public abstract class D <B1 extends hxb.xb_testandroidfunction.test_class.interface_.B,C1 extends hxb.xb_testandroidfunction.test_class.interface_.C>{
    public abstract void DFun1();
}
/**
 *  <B1 extends hxb.xb_testandroidfunction.test_class.interface_.B,C1 extends hxb.xb_testandroidfunction.test_class.interface_.C>
 *  这么写没有问题的原因是：
 *  当 A extends D<B,C>这其中的B类,C类是实现了该 interface_.B  和 interface_.C的接口，因为接口被某个类实现了，表明接口中的方法也就该实体类的一部分了
 *
 *  上面写法实际是以下这种情况：
 *  A extends D<B1 implements hxb...interface_.B extends hxb...interface_.B,C1 implements hxb...interface_.C extends hxb...interface_.C>
 *  前提是B1得实现相对应的接口，类是不能继承接口的，只有接口才能继承接口
 *
 *  所以：A 继承 D 时 无论D的泛型是什么类，只要实现相对应接口，Java的机制都可以视为同一类型。
 *
 *  好处是：暂时不知
 *
 * */