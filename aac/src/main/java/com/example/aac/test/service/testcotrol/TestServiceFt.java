package com.example.aac.test.service.testcotrol;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestServiceBinding;
import com.example.aac.test.service.ForegroundService;

public class TestServiceFt extends BaseFragment<FtTestServiceBinding, BaseViewModel> {
    private static final String TAG = "XB_"+TestServiceFt.class.getName();
    private int times = 0;
    private  Handler mHandler;
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
        viewDataBinding.tvStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ForegroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                }
            }
        });
        viewDataBinding.tvStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ForegroundService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().stopService(intent);
                }
            }
        });

        viewDataBinding.tvStartThreadStartService.setOnClickListener((view)->{

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "run: >>>>>>>>>>>>>>>>>>  当前延时  "+times);
                        if (times == 100){
                            Intent intent = new Intent(getContext(),ForegroundService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                getContext().startForegroundService(intent);
                            }
                            break;
                        }

                        times++;


                    }
                }
            }).start();

        });
    }
}
