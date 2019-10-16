package com.example.aac.base_frame.adapter;

import androidx.annotation.LayoutRes;
import androidx.databinding.ViewDataBinding;

import com.example.aac.base_frame.BaseViewModel;

/**
 * Created by hxb on 2019-08-29.
 */
public class AdapterBinding<V extends ViewDataBinding, VM extends BaseViewModel> {

    public static final int VAR_NONE = 0;
    private static final int VAR_INVALID = -1;
    private static final int LAYOUT_NONE = 0;

    private IAdapterBinding<V, VM> mIBinding;
    private int mVariableId;
    @LayoutRes
    private int mLayoutRes;


    private AdapterBinding(IAdapterBinding<V, VM> binding) {
        this.mIBinding = binding;
    }


    public static <V extends ViewDataBinding, VM extends BaseViewModel> AdapterBinding<V, VM> of(int variableId, @LayoutRes int layoutRes) {
        return new AdapterBinding<V, VM>(null).set(variableId, layoutRes);
    }


    public static <V extends ViewDataBinding, VM extends BaseViewModel> AdapterBinding<V, VM> of(IAdapterBinding<V, VM> onItemBind) {
        if (onItemBind == null) {
            throw new NullPointerException("onItemBind == null");
        }
        return new AdapterBinding<V, VM>(onItemBind);
    }


    public final AdapterBinding<V, VM> set(int variableId, @LayoutRes int layoutRes) {
        this.mVariableId = variableId;
        this.mLayoutRes = layoutRes;
        return this;
    }

    @LayoutRes
    public final int layoutRes() {
        return mLayoutRes;
    }


    int itemCount() {
        if (null != mIBinding) {
            return mIBinding.itemCount();
        }
        return 0;
    }

    void itemViewType(int position) {
        if (null != mIBinding) {
            mVariableId = VAR_INVALID;
            mLayoutRes = LAYOUT_NONE;
            mIBinding.itemViewType(this, position);
            if (mVariableId == VAR_INVALID) {
                throw new IllegalStateException("variableId not set in onItemBind()");
            }
            if (mLayoutRes == LAYOUT_NONE) {
                throw new IllegalStateException("layoutRes not set in onItemBind()");
            }
        }
    }

    boolean bind(V binding, int position) {
        if (mVariableId == VAR_NONE) {
            return false;
        }
        if (null == mIBinding) {
            return false;
        }
        boolean result = binding.setVariable(mVariableId, mIBinding.viewModel(position));
        if (!result) {
            Utils.throwMissingVariable(binding, mVariableId, mLayoutRes);
        }
        mIBinding.viewBinding(binding,position);
        return true;
    }

}
