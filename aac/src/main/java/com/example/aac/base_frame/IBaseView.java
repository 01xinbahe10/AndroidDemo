package com.example.aac.base_frame;

/**
 * Created by hxb on 2019-08-26.
 */
public interface IBaseView {

    /**
     * 初始化界面传递参数
     */
    void initParam();
    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化界面观察者的监听
     */
    void initViewObservable();
}
