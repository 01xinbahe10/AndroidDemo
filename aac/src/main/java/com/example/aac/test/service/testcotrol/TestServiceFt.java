package com.example.aac.test.service.testcotrol;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestServiceBinding;
import com.example.aac.test.service.ForegroundService;

public class TestServiceFt extends BaseFragment<FtTestServiceBinding, BaseViewModel> implements View.OnClickListener {
    private static final String TAG = "XB_"+TestServiceFt.class.getName();
    private int times = 0;
    private  Handler mHandler;
    private ToneGenerator mGenerator;
    @Override
    protected int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.ft_test_service;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mHandler = new Handler();
        mGenerator = new ToneGenerator(AudioManager.STREAM_DTMF,80);
        //开启服务
        viewDataBinding.tvStartService.setOnClickListener(this);
        //终止服务
        viewDataBinding.tvStopService.setOnClickListener(this);
        //延时开启线程
        viewDataBinding.tvStartThreadStartService.setOnClickListener(this);
        //开关屏幕录制
        viewDataBinding.tvStartRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.tvStartService:
                intent = new Intent(getContext(),ForegroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                }

                Log.d(TAG, "onClick: >>>>>>>>>>  ");
                break;
            case R.id.tvStopService:
                intent = new Intent(getContext(),ForegroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().stopService(intent);
                }
                break;
            case R.id.tvStartThreadStartService:
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: >>>>>>>>>>>>>>>>>>  当前延时  " + times);
                        if (times == 100) {
                            Intent intent1 = new Intent(getContext(), ForegroundService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getContext().startForegroundService(intent1);
                            }
                            break;
                        }

                        times++;


                    }
                }
                ).start();
                break;
            case R.id.tvStartRecord:
                if (!viewDataBinding.tvStartRecord.isSelected()){
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    viewDataBinding.tvStartRecord.setText("开启系统屏幕录制");
                }else {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                    viewDataBinding.tvStartRecord.setText("停止系统屏幕录制");
                }

                viewDataBinding.tvStartRecord.setSelected(!viewDataBinding.tvStartRecord.isSelected());
                break;
        }
    }
}
