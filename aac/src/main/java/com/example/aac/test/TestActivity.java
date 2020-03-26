package com.example.aac.test;

import android.os.Bundle;

import com.example.aac.R;
import com.example.aac.base_frame.BaseActivity;
import com.example.aac.base_frame.utils.FragmentPersistence;
import com.example.aac.test.rvpage.RvPageTurningEffectFt;

/**
 * Created by hxb on  2020/3/26
 */
public class TestActivity extends BaseActivity {

    private FragmentPersistence persistence;
    @Override
    protected int initContentView(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        return R.layout.act_test;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        persistence = FragmentPersistence.init2()
                .setFragmentId(R.id.fl_test)
                .setManager(getSupportFragmentManager())
                .putFragment(RvPageTurningEffectFt.class)
                .build();

        persistence.switchFragment2(RvPageTurningEffectFt.class);
    }
}
