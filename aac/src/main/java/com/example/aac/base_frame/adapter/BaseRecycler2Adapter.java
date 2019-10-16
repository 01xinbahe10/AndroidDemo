package com.example.aac.base_frame.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aac.base_frame.BaseViewModel;

/**
 * Created by hxb on 2019-08-28.
 *
 * BaseRecycler2Adapter:
 * 优点：1，使用ViewDataBinding 省略findViewById和ViewHolder 。
 *      2，数据适配和view状态处理 集中到 MVVM 中的ViewModel中处理，也很好的实现了MVVM模式。
 *
 * 作用：1，不可继承(如果可继承跟普通Adapter操作是一样)；直接在XML使用
 * 注意：该Adapter部分功能未完善，如需要增设功能到 @see AdapterBinding类设置
 */
public final class BaseRecycler2Adapter<V extends ViewDataBinding,VM extends BaseViewModel> extends RecyclerView.Adapter {


    protected Context context;

    private AdapterBinding<V,VM> adapterBinding;

    public BaseRecycler2Adapter(Context context) {
        this.context = context;
    }

    public void setAdapterBinding(AdapterBinding<V,VM> adapterBinding) {
        this.adapterBinding = adapterBinding;
    }


    @Override
    public int getItemViewType(int position) {
        adapterBinding.itemViewType(position);
        return adapterBinding.layoutRes();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        V binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), layoutId, parent, false);
        return new BaseBindingViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        V binding = DataBindingUtil.getBinding(holder.itemView);
        if (null == binding){
            return;
        }
        if ( adapterBinding.bind(binding, position)){
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return adapterBinding.itemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }


    public class BaseBindingViewHolder extends RecyclerView.ViewHolder {
        public BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
