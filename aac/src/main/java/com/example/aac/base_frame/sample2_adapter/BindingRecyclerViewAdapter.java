package com.example.aac.base_frame.sample2_adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableList;
import androidx.databinding.OnRebindCallback;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} that binds items to layouts using the given {@link ItemBinding}.
 * If you give it an {@link ObservableList} it will also updated itself based on changes to that
 * list.
 */
public class BindingRecyclerViewAdapter<VM> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BindingCollectionAdapter<VM> {
    private static final Object DATA_INVALIDATION = new Object();

    private ItemBinding<VM> itemBinding;
    private final WeakReferenceOnListChangedCallback<VM> callback = new WeakReferenceOnListChangedCallback<>(this);
//    private List<VM> items;
    private LayoutInflater inflater;
    private ItemIds itemIds;
    private ViewHolderFactory viewHolderFactory;
    // Currently attached recyclerview, we don't have to listen to notifications if null.
    @Nullable
    private RecyclerView recyclerView;


    @Override
    public void setItemBinding(ItemBinding<VM> itemBinding) {
        this.itemBinding = itemBinding;
    }

    @Override
    public ItemBinding<VM> getItemBinding() {
        return itemBinding;
    }

    @Override
    public void setItems(@Nullable List<VM> items) {
//        if (this.items == items) {
//            return;
//        }
//        // If a recyclerview is listening, set up listeners. Otherwise wait until one is attached.
//        // No need to make a sound if nobody is listening right?
//        if (recyclerView != null) {
//            if (this.items instanceof ObservableList) {
//                ((ObservableList<VM>) this.items).removeOnListChangedCallback(callback);
//            }
//            if (items instanceof ObservableList) {
//                ((ObservableList<VM>) items).addOnListChangedCallback(callback);
//            }
//        }
//        this.items = items;
//        notifyDataSetChanged();
    }


//    @Override
//    public VM getAdapterItem(int position) {
//        return items.get(position);
//    }


//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        if (this.recyclerView == null && items != null && items instanceof ObservableList) {
//            ((ObservableList<VM>) items).addOnListChangedCallback(callback);
//        }
//        this.recyclerView = recyclerView;
//    }

//    @Override
//    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
//        if (this.recyclerView != null && items != null && items instanceof ObservableList) {
//            ((ObservableList<VM>) items).removeOnListChangedCallback(callback);
//        }
//        this.recyclerView = null;
//    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int layoutId) {
        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }
        ViewDataBinding binding = onCreateBinding(inflater, layoutId, viewGroup);
        final RecyclerView.ViewHolder holder = onCreateViewHolder(binding);
        binding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                return recyclerView != null && recyclerView.isComputingLayout();
            }

            @Override
            public void onCanceled(ViewDataBinding binding) {
                if (recyclerView == null || recyclerView.isComputingLayout()) {
                    return;
                }
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position, DATA_INVALIDATION);
                }
            }
        });
        return holder;
    }

    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        return DataBindingUtil.inflate(inflater, layoutId, viewGroup, false);
    }



    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewDataBinding binding = DataBindingUtil.getBinding(viewHolder.itemView);
        onBindBinding(binding, itemBinding.variableId(), itemBinding.layoutRes(), position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isForDataBinding(payloads)) {
            ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
            binding.executePendingBindings();
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int variableId, @LayoutRes int layoutRes, int position) {
        if (itemBinding.bind(binding,position)) {
            binding.executePendingBindings();
        }
    }

    private boolean isForDataBinding(List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            return false;
        }
        for (int i = 0; i < payloads.size(); i++) {
            Object obj = payloads.get(i);
            if (obj != DATA_INVALIDATION) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        itemBinding.onItemBind(position);
        return itemBinding.layoutRes();
    }



    @Override
    public int getItemCount() {
        return itemBinding.itemCount();
    }



    /**
     * Constructs a view holder for the given databinding. The default implementation is to use
     * {@link ViewHolderFactory} if provided, otherwise use a default view holder.
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewDataBinding binding) {
        if (viewHolderFactory != null) {
            return viewHolderFactory.createViewHolder(binding);
        } else {
            return new BindingViewHolder(binding);
        }
    }

    private static class BindingViewHolder extends RecyclerView.ViewHolder {
        public BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
        }
    }


    @Override
    public long getItemId(int position) {
        return itemIds == null ? position : itemIds.getItemId(position);
    }

    private static class WeakReferenceOnListChangedCallback<VM> extends ObservableList.OnListChangedCallback<ObservableList<VM>> {
        final WeakReference<BindingRecyclerViewAdapter<VM>> adapterRef;

        WeakReferenceOnListChangedCallback(BindingRecyclerViewAdapter<VM> adapter) {
            this.adapterRef = new WeakReference<>(adapter);
        }

        @Override
        public void onChanged(ObservableList sender) {
            BindingRecyclerViewAdapter<VM> adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, final int positionStart, final int itemCount) {
            BindingRecyclerViewAdapter<VM> adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, final int positionStart, final int itemCount) {
            BindingRecyclerViewAdapter<VM> adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, final int fromPosition, final int toPosition, final int itemCount) {
            BindingRecyclerViewAdapter<VM> adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            for (int i = 0; i < itemCount; i++) {
                adapter.notifyItemMoved(fromPosition + i, toPosition + i);
            }
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, final int positionStart, final int itemCount) {
            BindingRecyclerViewAdapter<VM> adapter = adapterRef.get();
            if (adapter == null) {
                return;
            }
            Utils.ensureChangeOnMainThread();
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    public interface ItemIds {
        long getItemId(int position);
    }

    public interface ViewHolderFactory {
        RecyclerView.ViewHolder createViewHolder(ViewDataBinding binding);
    }


    /**
     * Set the item id's for the items. If not null, this will set {@link
     * RecyclerView.Adapter#setHasStableIds(boolean)} to true.
     */
    public void setItemIds(@Nullable ItemIds itemIds) {
        if (this.itemIds != itemIds) {
            this.itemIds = itemIds;
            setHasStableIds(itemIds != null);
        }
    }

    /**
     * Set the factory for creating view holders. If null, a default view holder will be used. This
     * is useful for holding custom state in the view holder or other more complex customization.
     */
    public void setViewHolderFactory(@Nullable ViewHolderFactory factory) {
        viewHolderFactory = factory;
    }

}
