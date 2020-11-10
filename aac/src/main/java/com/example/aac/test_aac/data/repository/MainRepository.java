package com.example.aac.test_aac.data.repository;

import androidx.databinding.ObservableArrayList;
import android.util.Log;

import com.example.aac.base_frame.BaseRepository;
import com.example.aac.test_aac.data.network.IApiCallBack;
import com.example.aac.test_aac.data.network.Simulation;
import com.example.aac.test_aac.data.vo.CityVO;

/**
 * Created by hxb on 2019-08-23.
 *
 * 数据管理代理类（作用是：切换网络数据、缓存数据、数据库数据的切换）
 */
public class MainRepository extends BaseRepository {

    private ObservableArrayList<CityVO> list;
    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Simulation.getData(new IApiCallBack<ObservableArrayList<CityVO>, ObservableArrayList<CityVO>>() {
                    @Override
                    public void onSuccess(ObservableArrayList<CityVO> O1) {
                        list = O1;
                        Log.e("TAG", "onSuccess: ?????????????/    size:::  "+list.size() );
                        notifyUpdateAll();
                    }

                    @Override
                    public void onFail(ObservableArrayList<CityVO> O2) {

                    }
                });
            }
        }).start();
    }


    public ObservableArrayList<CityVO> getList(){
        return list;
    }

    @Override
    public void onCleared() {
        super.onCleared();
    }
}
