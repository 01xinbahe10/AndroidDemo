package com.example.aac.base_frame;

/**
 * Created by hxb on 2019-08-26.
 */
public interface IBaseRepository {

    /**
     * ViewModel销毁时清除Model，与ViewModel共消亡。Model层同样不能持有长生命周期对象
     */
    void onCleared();

    interface Notify{
        void onDataChange(String key);
    }

}
