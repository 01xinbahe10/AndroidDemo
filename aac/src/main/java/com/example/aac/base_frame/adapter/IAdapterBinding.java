package com.example.aac.base_frame.adapter;

import androidx.annotation.NonNull;

/**
 * Created by hxb on 2019-08-29.
 */
public interface IAdapterBinding<V,VM> {
    int itemCount();

    void itemViewType(AdapterBinding adapterBinding, int position);

    VM viewModel(int position);

    void viewBinding(@NonNull V viewDataBinding, int position);
}
