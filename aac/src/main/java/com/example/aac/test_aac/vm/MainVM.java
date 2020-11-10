package com.example.aac.test_aac.vm;


import android.app.Application;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.aac.BR;
import com.example.aac.R;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.base_frame.IBaseRepository;
import com.example.aac.base_frame.SingleLiveEvent;
import com.example.aac.base_frame.adapter.AdapterBinding;
import com.example.aac.base_frame.adapter.IAdapterBinding;
import com.example.aac.base_frame.sample2_adapter.ItemBinding;
import com.example.aac.base_frame.sample2_adapter.OnItemBind;
import com.example.aac.test_aac.data.repository.MainRepository;
import com.example.aac.test_aac.data.vo.CityVO;
import com.example.aac.databinding.ItemMainBinding;


/**
 * Created by hxb on 2019-08-23.
 */
public class MainVM extends BaseViewModel<MainRepository> implements View.OnClickListener{
    private String TAG = "MainVM";

    private Handler handler = new Handler();

    public ObservableField<String> test = new ObservableField<>("");

    public ObservableField<String> cityName = new ObservableField<>("");

    public ObservableField<String> cityId = new ObservableField<>("");

    private SingleLiveEvent<Boolean> singleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ObservableArrayList<CityVO>> adapterData = new SingleLiveEvent<>();

    public View.OnClickListener onClickListener;

    public MainVM(@NonNull Application application, MainRepository repository) {
        super(application, repository);
        test.set(" 核心板");
        onClickListener = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        repository.onCleared();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv:
                if (null == singleLiveEvent.getValue()){
                    singleLiveEvent.setValue(true);
                    return;
                }
                singleLiveEvent.setValue(!singleLiveEvent.getValue());
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e(TAG, "onCleared: .............////////////////  " );
    }

    /*
    * 设置 观察者观察view
    * */
    public void setObserverViewState(LifecycleOwner owner, Observer<Boolean> observer) {
        singleLiveEvent.observe(owner, observer);
    }


    public void setAdapterData(LifecycleOwner owner, Observer<ObservableArrayList<CityVO>> observer){
        adapterData.observe(owner,observer);
    }

    /*
    * 获取数据
    * */
    public void getData(){
        Log.e(TAG, "getData: KKKKKKKKKKKKKKKKKKKKK    "+arrayList.size() );
        repository.addNotifyUpdate("11", new IBaseRepository.Notify() {
            @Override
            public void onDataChange(String key) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        arrayList.addAll(repository.getList());
                        adapterData.setValue(repository.getList());

                    }
                });


            }
        });
        repository.getData();

        for (int i = 0; i < 100; i++) {
            ItemVM itemVM = new ItemVM(getApplication());
            itemVM.cityName.set("name:  "+i);
            itemVM.cityId.set("id:   "+i);
            observableList.add(itemVM);
        }
    }



    public ItemBinding<ItemVM> itemBinding = ItemBinding.of(new OnItemBind<ItemVM>() {
        @Override
        public int itemCount() {
            return 10;
        }

        @Override
        public void onItemBind(ItemBinding itemBinding, int position) {
            itemBinding.set(BR._all, R.layout.item_main);
        }

        @Override
        public ItemVM viewModel(int position) {
            ItemVM itemVM = new ItemVM(getApplication());
            itemVM.cityName.set("name:  "+position);
            itemVM.cityId.set("id:   "+position);
            return itemVM;
        }

    });

    //给RecyclerView添加ObservableList
    public ObservableList<ItemVM> observableList = new ObservableArrayList<>();






    private ObservableArrayList<CityVO> arrayList = new ObservableArrayList<>();
    public AdapterBinding<ItemMainBinding, ItemVM> itemBinding2 = AdapterBinding.of(new IAdapterBinding<ItemMainBinding, ItemVM>() {
        @Override
        public int itemCount() {
            return arrayList.size();
        }

        @Override
        public void itemViewType(AdapterBinding adapterBinding, int position) {
            adapterBinding.set(BR._all,R.layout.item_main);
        }

        @Override
        public void viewBinding(@NonNull ItemMainBinding viewDataBinding,int position) {
            Log.e("TAG", "onBindViewHolder: ===================   "+(viewDataBinding != null));
            if (position == 1|| position == 9 || position == 6 || position == 20){
                viewDataBinding.tvCityName.setVisibility(View.GONE);
                viewDataBinding.tvCityId.setVisibility(View.GONE);
            }else {
                viewDataBinding.tvCityId.setVisibility(View.VISIBLE);
                viewDataBinding.tvCityName.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public ItemVM viewModel(int position) {


            CityVO cityVO = arrayList.get(position);
            ItemVM itemVM = new ItemVM(getApplication());
            itemVM.cityName.set(cityVO.getCityName());
            itemVM.cityId.set(cityVO.getCityId());



            return itemVM;
        }
    });


}
