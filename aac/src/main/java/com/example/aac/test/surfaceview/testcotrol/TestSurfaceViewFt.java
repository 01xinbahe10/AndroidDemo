package com.example.aac.test.surfaceview.testcotrol;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.base_frame.LiveDataBus;
import com.example.aac.databinding.FtTestSurfaceViewBinding;
import com.example.aac.test.G;
import com.example.aac.test.surfaceview.view.manager.GLPaint;

import static com.example.aac.test.surfaceview.view.manager.GLPaint.Graphic.ERASER;
import static com.example.aac.test.surfaceview.view.manager.GLPaint.Graphic.LINE;
import static com.example.aac.test.surfaceview.view.manager.GLPaint.Graphic.POINT;
import static com.example.aac.test.surfaceview.view.manager.GLPaint.Graphic.POINT_LINE;

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
        sub();
        viewDataBinding.btSave.setOnClickListener(this);
        viewDataBinding.btLine.setOnClickListener(this);
        viewDataBinding.btClear.setOnClickListener(this);
        viewDataBinding.btEraser.setOnClickListener(this);
        viewDataBinding.btPoint.setOnClickListener(this);
        viewDataBinding.btLine2.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewDataBinding.drawingView3.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewDataBinding.drawingView3.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_save:
                viewDataBinding.drawingView3.savePic();
                break;

            case R.id.bt_line:
                GLPaint.setPaintStyle(LINE);
                break;
            case R.id.bt_clear:
//                viewDataBinding.drawingView2.clearAll();
                viewDataBinding.drawingView3.clearAll();
                break;
            case R.id.bt_eraser:
                GLPaint.setPaintStyle(ERASER);
                break;

            case R.id.bt_point:
                GLPaint.setPaintStyle(POINT);
                break;

            case R.id.bt_line2:
                GLPaint.setPaintStyle(POINT_LINE);
                break;
        }
    }

    private void sub(){
        LiveDataBus.get().with(G.TestA.SUB_SURFACE_VIEW_FT,Object[].class).observe(this, new Observer<Object[]>() {
            @Override
            public void onChanged(Object[] objects) {
                if (null == objects || objects.length != 2){
                    return;
                }
                String tag = (String) objects[0];
                Object value = objects[1];
                switch (tag){
                    case G.TestA.tGetBitmap:
                        if (value instanceof Bitmap){
                            viewDataBinding.ivReview.setImageBitmap((Bitmap) value);
                        }
                        break;
                }
            }
        });
    }
}
