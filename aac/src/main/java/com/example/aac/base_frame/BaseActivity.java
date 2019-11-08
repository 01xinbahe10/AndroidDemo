package com.example.aac.base_frame;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hxb on 2019-08-23.
 * 前言：AAC 是在MVVM模式上改进而来
 * Lifecycle, LiveData, ViewModel（属于MVVM VM层应用）
 * 以及 Room（属于MVVM Model层应用）
 *
 * 通过它可以非常优雅的让数据与界面交互
 * 并做一些持久化的东西
 * 高度解耦
 * 自动管理生命周期
 * 而且不用担心内存泄漏的问题.
 *
 * BaseActivity：属于AAC 中的View层
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity implements IBaseView, LifecycleOwner {

    protected V viewDataBinding;
    private int baseViewModelId;
    protected VM baseViewModel;
//    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注入观察生命周期（针对数据在不同的生命周期中的操作）
//        lifecycleRegistry.markState(Lifecycle.State.CREATED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

        //页面接受的参数方法
        initParam();
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding(savedInstanceState);
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        lifecycleRegistry.markState(Lifecycle.State.STARTED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        if (null != viewDataBinding) {
            viewDataBinding.unbind();
        }
    }

    /**
     * 注入绑定
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        viewDataBinding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        baseViewModelId = initViewModelId();

        try {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {//判断当前BaseActivity是否有泛型
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                Type type1 = modelClass.getGenericSuperclass();
                if (type1 instanceof ParameterizedType) {//判断当前BaseViewModel是否有泛型
                    Type[] params1 = ((ParameterizedType) type1).getActualTypeArguments();
                    Class repositoryClass1 = (Class) params1[0];//获取BaseViewModel<?> 泛型类
                    //根据反射 实例化(VM)BaseViewModel 及 泛型类
                    baseViewModel = getViewModel(this.getApplication(), modelClass, repositoryClass1);
                } else {
                    baseViewModel = getViewModel(modelClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == baseViewModel) {
            //如果没有指定泛型参数 或 有泛型生成失败，则默认使用BaseViewModel
            baseViewModel = getViewModel(BaseViewModel.class);
        }


        //关联ViewModel
        viewDataBinding.setVariable(baseViewModelId, baseViewModel);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(baseViewModel);

    }

    //刷新布局
    protected void refreshLayout() {
        if (null != baseViewModel) {
            viewDataBinding.setVariable(baseViewModelId, baseViewModel);
        }
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    protected abstract int initContentView(Bundle savedInstanceState);

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    protected abstract int initViewModelId();

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }

    @Override
    public void initParam() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    @SuppressWarnings("unchecked")
    private VM getViewModel(Class viewModelClass) {
        return (VM) createViewModel(this, viewModelClass);
    }

    @SuppressWarnings("unchecked")
    private VM getViewModel(Application application, Class viewModelClass, Class repositoryClass) throws Exception {
        return (VM) viewModelClass.getConstructor(Application.class, repositoryClass)
                .newInstance(application, repositoryClass.newInstance());
    }
}
