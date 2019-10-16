package com.example.aac.home;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.Observer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.aac.BR;
import com.example.aac.R;
import com.example.aac.base_frame.BaseActivity;
import com.example.aac.base_frame.adapter.BaseRecycler2Adapter;
import com.example.aac.data.vo.CityVO;
import com.example.aac.databinding.ActivityMainBinding;
import com.example.aac.home.adapter.MainAdapter;
import com.example.aac.view_model.MainViewModel;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private MainAdapter adapter;

    private BaseRecycler2Adapter adapter2;
    @Override
    protected int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initViewModelId() {
        return BR.mainViewModel;
    }


    @Override
    public void initData() {
//        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
//        adapter  = new MainAdapter(this);
//        viewDataBinding.recyclerView.setAdapter(adapter);

//        viewDataBinding.setAdapter(new BindingRecyclerViewAdapter());


        adapter2 = new BaseRecycler2Adapter(this);
        viewDataBinding.setAdapter2(adapter2);

        baseViewModel.getData();

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        baseViewModel.setObserverViewState(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.e("TAG", "onChanged: ------->>>>>>>>  "+aBoolean );
                if (null == aBoolean){
                    return;
                }
                if (aBoolean){
                    viewDataBinding.tv.setText("你好啊");
                }else {
                    viewDataBinding.tv.setText("很高兴认识你");
                }
            }
        });

        baseViewModel.setAdapterData(this, new Observer<ObservableArrayList<CityVO>>() {
            @Override
            public void onChanged(ObservableArrayList<CityVO> cityVOS) {
                Log.e("TAG", "onChanged: ???????????????????   "+cityVOS.size() );
//                adapter.getItemsVO().addAll(cityVOS);
//                adapter.notifyDataSetChanged();

                adapter2.notifyDataSetChanged();
            }
        });
    }

}
