package com.example.aac.test_aac.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.example.aac.base_frame.BaseViewModel;

/**
 * Created by hxb on 2019-08-28.
 */
public class ItemVM extends BaseViewModel {

    public ObservableField<String> cityName = new ObservableField<>("");

    public ObservableField<String> cityId = new ObservableField<>("");

    public ItemVM(@NonNull Application application)
    {
        super(application);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cityName.set("");
        cityId.set("");
    }
}
