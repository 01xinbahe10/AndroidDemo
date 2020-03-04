package com.example.aac.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.example.aac.base_frame.BaseViewModel;

/**
 * Created by hxb on 2019-08-28.
 */
public class ItemViewModel extends BaseViewModel {

    public ObservableField<String> cityName = new ObservableField<>("");

    public ObservableField<String> cityId = new ObservableField<>("");

    public ItemViewModel(@NonNull Application application)
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
