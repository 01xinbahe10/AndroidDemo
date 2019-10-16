package com.example.aac.base_frame.adapter;

import android.content.Context;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.base_frame.sample2_adapter.LayoutManagers;

/**
 * Created by hxb on 2019-08-29.
 */
public class BindingRecyclerViewAdapters {
    // RecyclerView
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"itemBinding", "adapter"}, requireAll = false)
    public static <V extends ViewDataBinding,VM extends BaseViewModel> void setAdapter(RecyclerView recyclerView, AdapterBinding<V,VM> adapterBinding, BaseRecycler2Adapter<V,VM> adapter) {
        if (adapterBinding == null) {
            throw new IllegalArgumentException("itemBinding must not be null");
        }
        BaseRecycler2Adapter oldAdapter = (BaseRecycler2Adapter) recyclerView.getAdapter();
        if (adapter == null) {
            if (oldAdapter == null) {
                Context context = recyclerView.getContext();
                adapter = new BaseRecycler2Adapter<>(context);
            } else {
                adapter = oldAdapter;
            }
        }
        adapter.setAdapterBinding(adapterBinding);

        if (oldAdapter != adapter) {
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter("layoutManager")
    public static void setLayoutManager(RecyclerView recyclerView, LayoutManagers.LayoutManagerFactory layoutManagerFactory) {
        recyclerView.setLayoutManager(layoutManagerFactory.create(recyclerView));
    }
}
