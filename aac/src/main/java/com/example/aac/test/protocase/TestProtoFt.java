package com.example.aac.test.protocase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtTestProtoBinding;
import com.google.protobuf.InvalidProtocolBufferException;

import proto.data.ProtoCase;

public class TestProtoFt extends BaseFragment<FtTestProtoBinding, BaseViewModel> {
    private final String TAG = "XB_"+TestProtoFt.class.getName();
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
    }
}
