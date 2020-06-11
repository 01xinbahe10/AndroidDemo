package com.example.aac.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.aac.BR;
import com.example.aac.R;
import com.example.aac.base_frame.BaseApplication;
import com.example.aac.base_frame.adapter.BaseRecycler1Adapter;
import com.example.aac.data.vo.CityVO;
import com.example.aac.databinding.ItemMainBinding;
import com.example.aac.view_model.ItemViewModel;

/**
 * Created by hxb on 2019-08-27.
 */
public class MainAdapter extends BaseRecycler1Adapter<ItemMainBinding, CityVO> {
    private final String TAG = "MainAdapter";
    private final static int ITEM_ADAPTER_FUN = 1;
    private Context mContext;
    public MainAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_main;
    }

    @Override
    protected  void viewInstance(ItemMainBinding viewBinding) {

    }

    @Override
    protected int viewModelId() {
        return BR.itemViewModel;
    }

    @Override
    protected void onBindItem(ItemMainBinding viewBinding, CityVO itemVO, int position) {
        Log.e(TAG, "onBindItem: >>>>>>>>>>>>>>  " + itemVO.getCityName() + "      " + itemVO.getCityId() + "      " + (viewBinding.tvCityName.getText()));
        if (position == 1|| position == 9 || position == 6 || position == 20){
            viewBinding.tvCityName.setVisibility(View.GONE);
            viewBinding.tvCityId.setVisibility(View.GONE);
        }else {
            viewBinding.tvCityId.setVisibility(View.VISIBLE);
            viewBinding.tvCityName.setVisibility(View.VISIBLE);
        }
        switch (ITEM_ADAPTER_FUN){
            case 1:
                //方法一(这种可以进行根据activity的生命周期进行管理item显示的数据)
                ItemViewModel itemViewModel = new ItemViewModel(BaseApplication.getInstance());
                viewBinding.setItemViewModel(itemViewModel);
                itemViewModel.cityName.set(itemVO.getCityName());
                itemViewModel.cityId.set(itemVO.getCityId());
                break;
            case 2:
                //方法二(这种直接将数据交给item显示，如需使用则需移除xml中的<data>标签及控件调用了<data>中的对象)
                viewBinding.tvCityName.setText(itemVO.getCityName());
                viewBinding.tvCityId.setText(itemVO.getCityId());
                break;
        }

    }
}
