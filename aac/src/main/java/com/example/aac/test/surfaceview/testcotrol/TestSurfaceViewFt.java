package com.example.aac.test.surfaceview.testcotrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestSurfaceViewBinding;

/**
 * Created by hxb on  2020/11/10
 */
public class TestSurfaceViewFt extends BaseFragment<FtTestSurfaceViewBinding, BaseViewModel> {
    @Override
    protected int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.ft_test_surface_view;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }
}
