package com.example.aac;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aac.base_frame.utils.StackManager;
import com.example.aac.test_aac.MainActivity;
import com.example.aac.test.TestActivity;

/**
 * Created by hxb on  2020/3/26
 * 做测试界面更换
 */
public class RootActivity extends AppCompatActivity {

    interface ActMark{
        int AAC_USE  = 1;//acc 使用平台测试
        int TEST_FUN = 2;//各种功能测试平台测试
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switchAct(ActMark.TEST_FUN);
    }

    private void switchAct(int actMark){
        Intent intent = null;
        switch (actMark){
            case ActMark.AAC_USE:
                intent = new Intent(this, MainActivity.class);
                break;
            case ActMark.TEST_FUN:
                intent = new Intent(this, TestActivity.class);
                break;
        }
        if (null != intent){
            startActivity(intent);
            StackManager.finishActivity(RootActivity.class);
        }
    }
}
