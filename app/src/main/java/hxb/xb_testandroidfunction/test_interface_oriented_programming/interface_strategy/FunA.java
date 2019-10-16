package hxb.xb_testandroidfunction.test_interface_oriented_programming.interface_strategy;

/**
 * Created by hxb on 2018/12/12
 * 接口功能实现的业务类
 */
public class FunA {
    private final String TAG = "FunA";
    private IBase mIBase;
    public FunA(){

    }
    public void setFunA(IBase iBase){
        this.mIBase = iBase;
    }

    public IBase getFunA(){
        return mIBase;
    }
}
