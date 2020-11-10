package com.example.aac.base_frame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by hxb on 2019-08-27.
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends Fragment implements IBaseView, LifecycleOwner {
    private static final String TAG = "BaseFragment";
    protected V viewDataBinding;
    private int baseViewModelId;
    protected VM baseViewModel;
//    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        if (null != viewDataBinding) {
            return viewDataBinding.getRoot();
        }

        return LayoutInflater.from(container.getContext()).inflate(initContentView(inflater, container, savedInstanceState),container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //注入观察生命周期（针对数据在不同的生命周期中的操作）
//        lifecycleRegistry.markState(Lifecycle.State.CREATED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }

    @Override
    public void onStart() {
        super.onStart();
//        lifecycleRegistry.markState(Lifecycle.State.STARTED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    public void onResume() {
        super.onResume();
//        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onPause() {
        super.onPause();
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
//        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
//        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        getLifecycle().removeObserver(baseViewModel);
        if (null != viewDataBinding) {
            viewDataBinding.unbind();
        }
    }


    /**
     * 注入绑定
     */
    private void initViewDataBinding() {
        baseViewModelId = initViewModelId();

        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {//判断当前BaseFragment是否有泛型
            //获取当前BaseFragment第二个泛型类(BaseViewModel.class)
            Class modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            baseViewModel = (VM) createViewModel(modelClass);
        }

        if (null == baseViewModel) {
            //如果没有指定泛型参数 或 有泛型生成失败，则默认使用BaseViewModel
            baseViewModel = (VM) createViewModel(BaseViewModel.class);
        }

        if (null != viewDataBinding) {
            viewDataBinding.setVariable(baseViewModelId, baseViewModel);
        }
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
    protected abstract int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);


    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    protected abstract int initViewModelId();


    @Override
    public void initParam() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    /**
     * 创建ViewModel
     *
     * @param viewModelCls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(Class<T> viewModelCls) {
        com.example.aac.base_frame.BaseFactory baseFactory = com.example.aac.base_frame.BaseFactory.instance(getActivity().getApplication());
        Log.v(TAG, "createViewModel: 通过ViewModelProvider获取已缓存的ViewModel，没有则自动通过BaseFactory中创建");
        return new ViewModelProvider(this, baseFactory).get(viewModelCls);
    }

}
