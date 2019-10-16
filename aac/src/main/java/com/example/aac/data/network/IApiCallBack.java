package com.example.aac.data.network;

/**
 * Created by hxb on 2019-08-26.
 */
public interface IApiCallBack<T1,T2> {
    void onSuccess(T1 O1);
    void onFail(T2 O2);
}
