package com.example.aac.base_frame;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hxb on  2020/3/5
 * 通过该工厂获取缓存ViewModel对象或是创建新的ViewModel对象并返回
 */
public final class BaseFactory extends ViewModelProvider.NewInstanceFactory{
    private final String TAG = "BaseFactory";
    /**
     * 说明：测试发现当屏幕发生旋转或Activity显示隐藏或Fragment显示隐藏，
     * 当前页的ViewModel才在缓存在ViewModelStore中；所以需要注意的是
     * ViewModel中的数据是需要合理清理，不然会造成数据重复
     */
    @NonNull
    private final Application application;

    private BaseFactory(@NonNull Application application) {
        this.application = application;
    }

    public static BaseFactory instance(@NonNull Application application){
        return new BaseFactory(application);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.v(TAG, "BaseFactory create(Class<T> modelClass): 创建新的viewModel");
        if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
            try {
                Type type1 = modelClass.getGenericSuperclass();
                if (type1 instanceof ParameterizedType) {//判断当前BaseViewModel是否有泛型
                    Type[] params1 = ((ParameterizedType) type1).getActualTypeArguments();
                    Class repositoryClass1 = (Class) params1[0];//获取BaseViewModel<?> 泛型类
                    //根据反射 实例化(VM)BaseViewModel 及 泛型类
                    return modelClass.getConstructor(Application.class, repositoryClass1)
                            .newInstance(application, repositoryClass1.newInstance());
                } else {
                    return modelClass.getConstructor(Application.class).newInstance(application);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return super.create(modelClass);
    }
}
