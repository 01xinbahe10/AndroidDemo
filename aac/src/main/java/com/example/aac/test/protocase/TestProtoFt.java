package com.example.aac.test.protocase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestProtoBinding;
import com.example.aac.test.stack.TestStackUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import proto.data.ProtoCase;

public class TestProtoFt extends BaseFragment<FtTestProtoBinding, BaseViewModel> {
    private final String TAG = "XB_"+TestProtoFt.class.getName();

    private ViewTreeObserver.OnWindowFocusChangeListener windowFocusChangeListener;
    @Override
    protected int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.ft_test_proto;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }



    @Override
    public void initViewObservable() {
        super.initViewObservable();

        ProtoCase.Person.Builder builder = ProtoCase.Person.newBuilder();
        builder.setId(1);
        builder.setEmail("1614526981@qq.com");
        ProtoCase.Person person = builder.build();
        byte[] bytes = person.toByteArray();

        Log.d(TAG, "initViewObservable: "+bytes);

        try {
            person = ProtoCase.Person.parseFrom(bytes);

            Log.d(TAG, "initViewObservable: "+person.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        viewDataBinding.tvText.setText(person.toString());

        viewDataBinding.tvText.setOnClickListener(v -> {
            Log.e(TAG, "initViewObservable: >>>>>>>>>>>>>>>>>> " );
               new AlertDialog.Builder(getContext())
                       .setTitle("测试")
                       .setOnCancelListener(new DialogInterface.OnCancelListener() {
                           @Override
                           public void onCancel(DialogInterface dialog) {
                               dialog.dismiss();
                           }
                       })
                       .create().show();
        });
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        imm.showSoftInput(viewDataBinding.getRoot(),0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            windowFocusChangeListener = hasFocus -> {

                Log.e(TAG, "initViewObservable: >>>>>>>>>  "+hasFocus+"    "+(getLifecycle().getCurrentState()== Lifecycle.State.RESUMED));
//                if (imm.isActive()) { //如果开启
//                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); //关闭软键盘，开启方法相同
//
//                    imm.showSoftInput(viewDataBinding.getRoot(),0);//显示键盘
//                    imm.hideSoftInputFromWindow(viewDataBinding.getRoot().getWindowToken(),0);//隐藏键盘
//                }

//                if (hasFocus && !imm.isActive() || !hasFocus && imm.isActive()){
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS);
//                }

//                if (getView().hasFocusable()){
//
//                }
//                if(getView().hasFocus()){
//
//                }
//                getView().setFocusable(true);
//                getView().setFocusableInTouchMode(true);
//                getView().requestFocus();

            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            viewDataBinding.getRoot().getViewTreeObserver().addOnWindowFocusChangeListener(windowFocusChangeListener);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        TestStackUtil.printStackSize(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
        TestStackUtil.printStackSize(TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
    }

    @Override
    public void onDestroyView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            viewDataBinding.getRoot().getViewTreeObserver().removeOnWindowFocusChangeListener(windowFocusChangeListener);
        }
        super.onDestroyView();
    }
}
