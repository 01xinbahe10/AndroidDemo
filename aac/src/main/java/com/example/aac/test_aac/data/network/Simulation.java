package com.example.aac.test_aac.data.network;

import androidx.databinding.ObservableArrayList;

import com.example.aac.test_aac.data.vo.CityVO;

/**
 * Created by hxb on 2019-08-23.
 */
public class Simulation {
    public static void getData(IApiCallBack<ObservableArrayList<CityVO>,ObservableArrayList<CityVO>> callBack){
        ObservableArrayList<CityVO> list = new ObservableArrayList<>();
        for (int i = 0; i < 1000; i++) {
            CityVO cityVO = new CityVO();
            cityVO.setCityName("name:"+i);
            cityVO.setCityId("id:"+i);
            list.add(cityVO);
        }
        callBack.onSuccess(list);
    }
}
