package com.example.aac.test.surfaceview.testcotrol;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestSurfaceViewBinding;

/**
 * Created by hxb on  2020/11/10
 */
public class TestSurfaceViewFt extends BaseFragment<FtTestSurfaceViewBinding, BaseViewModel> implements View.OnClickListener {
    @Override
    protected int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.ft_test_surface_view;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }

    private void configDrawingView2(){
        viewDataBinding.drawingView2.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //设置背景透明:
        viewDataBinding.drawingView2.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        viewDataBinding.drawingView2.setZOrderOnTop(true);

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewDataBinding.btClear.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewDataBinding.drawingView2.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewDataBinding.drawingView2.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_clear:
                viewDataBinding.drawingView2.clearAll();
                break;
        }
    }
}
