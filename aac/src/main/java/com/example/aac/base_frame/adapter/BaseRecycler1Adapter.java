package com.example.aac.base_frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hxb on 2019-08-28.
 *
 * BaseRecycler1Adapter：
 * 优点：1，使用ViewDataBinding 省略findViewById和ViewHolder 。
 *      2，数据模型泛型化不必为了修改数据模型在Adapter中多处修改
 *      3，可自定义
 * 作用：1，针对特殊的适配效果，业务逻辑跟普通使用的Adapter一样也是大部分集中在该Adapter中处理。
 *      2，也是为了辅助 @see BaseRecycler2Adapter 不能达到的效果
 *
 */
public abstract class BaseRecycler1Adapter<V extends ViewDataBinding, VO> extends RecyclerView.Adapter {


    protected Context context;
    protected ObservableArrayList<VO> itemsVO;
    protected ListChangedCallback itemsChangeCallback;

    public BaseRecycler1Adapter(Context context) {
        this.context = context;
        this.itemsVO = new ObservableArrayList<>();
        this.itemsChangeCallback = new ListChangedCallback();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), this.getLayoutResId(viewType), parent, false);
        return new BaseBindingViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        V binding = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(binding, this.itemsVO.get(position));
    }

    @Override
    public int getItemCount() {
        return this.itemsVO.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.itemsVO.addOnListChangedCallback(itemsChangeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.itemsVO.removeOnListChangedCallback(itemsChangeCallback);
    }

    //region 处理数据集变化
    protected void onChanged(ObservableArrayList<VO> newItems) {
        resetItems(newItems);
        notifyDataSetChanged();
    }

    protected void onItemRangeChanged(ObservableArrayList<VO> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeChanged(positionStart, itemCount);
    }

    protected void onItemRangeInserted(ObservableArrayList<VO> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    protected void onItemRangeMoved(ObservableArrayList<VO> newItems) {
        resetItems(newItems);
        notifyDataSetChanged();
    }

    protected void onItemRangeRemoved(ObservableArrayList<VO> newItems, int positionStart, int itemCount) {
        resetItems(newItems);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    protected void resetItems(ObservableArrayList<VO> newItems) {
        this.itemsVO = newItems;
    }

    private class ListChangedCallback extends ObservableArrayList.OnListChangedCallback<ObservableArrayList<VO>> {
        @Override
        public void onChanged(ObservableArrayList<VO> newItems) {
            BaseRecycler1Adapter.this.onChanged(newItems);
        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<VO> newItems, int i, int i1) {
            BaseRecycler1Adapter.this.onItemRangeChanged(newItems, i, i1);
        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<VO> newItems, int i, int i1) {
            BaseRecycler1Adapter.this.onItemRangeInserted(newItems, i, i1);
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<VO> newItems, int i, int i1, int i2) {
            BaseRecycler1Adapter.this.onItemRangeMoved(newItems);
        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<VO> sender, int positionStart, int itemCount) {
            BaseRecycler1Adapter.this.onItemRangeRemoved(sender, positionStart, itemCount);
        }
    }

    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    protected abstract int viewModelId();

    protected abstract void onBindItem(V viewBinding, VO itemVO);

    public ObservableArrayList<VO> getItemsVO() {
        return itemsVO;
    }


    public class BaseBindingViewHolder extends RecyclerView.ViewHolder {
        public BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
