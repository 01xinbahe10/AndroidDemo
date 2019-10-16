package com.example.aac.home.adapter;

import android.content.Context;
import android.util.Log;

import com.example.aac.BR;
import com.example.aac.R;
import com.example.aac.base_frame.adapter.BaseRecycler1Adapter;
import com.example.aac.data.vo.CityVO;
import com.example.aac.databinding.ItemMainBinding;

/**
 * Created by hxb on 2019-08-27.
 */
public class MainAdapter extends BaseRecycler1Adapter<ItemMainBinding,CityVO> {
    private final String TAG = "MainAdapter";

    public MainAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_main;
    }

    @Override
    protected int viewModelId() {
        return BR.itemViewModel;
    }

    @Override
    protected void onBindItem(ItemMainBinding viewBinding, CityVO itemVO) {
//        viewBinding.setVariable(BR.mainViewModel,itemVO);
        Log.e(TAG, "onBindItem: >>>>>>>>>>>>>>  "+itemVO.getCityName()+"      "+itemVO.getCityId() +"      "+(viewBinding.tvCityName.getText()));
//        viewBinding.getMianViewModel().cityName.set(itemVO.getCityName());
        viewBinding.tvCityName.setText(itemVO.getCityName());
        viewBinding.tvCityId.setText(itemVO.getCityId());

    }

}
