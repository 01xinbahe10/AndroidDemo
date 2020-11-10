package com.example.aac.test.rvpage.testcontrol;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aac.R;
import com.example.aac.base_frame.BaseFragment;
import com.example.aac.base_frame.BaseViewModel;
import com.example.aac.databinding.FtRvPageTurningEffectBinding;
import com.example.aac.test.rvpage.snaprecycleview.GravityPageChangeListener;
import com.example.aac.test.rvpage.snaprecycleview.GravitySnapHelper;
import com.example.aac.test.rvpage.snaprecycleview.GridPagerUtils;
import com.example.aac.test.rvpage.snaprecycleview.InvertRowColumnDataTransform;
import com.example.aac.test.rvpage.snaprecycleview.PageIndicatorHelper;
import com.example.aac.test.rvpage.view.CircleRecyclerPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxb on  2020/3/26
 * 测试RecyclerView  ViewPager翻页效果
 */
public class RvPageTurningEffectFt extends BaseFragment<FtRvPageTurningEffectBinding, BaseViewModel> {
    @Override
    protected int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.ft_rv_page_turning_effect;
    }

    @Override
    protected int initViewModelId() {
        return 0;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        configLeftRecyclerView(2, 3);
        configRightRecyclerView(2, 3);
        configCenterRecyclerView(2, 4);
        Log.e("TAG", "initViewObservable: >>>>>>>>>>>  " );
    }

    private void configLeftRecyclerView(int row, int column) {
        RecyclerView rvLeft = viewDataBinding.rvLeft;

        //setLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), row,
                LinearLayoutManager.HORIZONTAL, false);
        rvLeft.setLayoutManager(gridLayoutManager);

        //getDataSource
        List<ItemBean> dataList = addItemDatas();
        dataList = GridPagerUtils.transformAndFillEmptyData(
                new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

        //setAdapter
        RvPageTurningEffectAdapter adapter = new RvPageTurningEffectAdapter(getContext());
        adapter.updateDatas(dataList);
        adapter.setItemWidth(getScreenWidth(getContext())/column/5*4);
        rvLeft.setAdapter(adapter);

        //attachToRecyclerView
        GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
        snapHelper.setColumn(column);
        snapHelper.attachToRecyclerView(rvLeft);
        snapHelper.setCanPageScroll(true);

    }

    private void configRightRecyclerView(int row, int column) {

        RecyclerView rvRight = viewDataBinding.rvRight;

        //setLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), row,
                LinearLayoutManager.HORIZONTAL, false);
        rvRight.setLayoutManager(gridLayoutManager);

        //getDataSource
        List<ItemBean> dataList = addItemDatas();
        dataList = GridPagerUtils.transformAndFillEmptyData(
                new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

        //setAdapter
        RvPageTurningEffectAdapter adapter = new RvPageTurningEffectAdapter(getContext());
        adapter.updateDatas(dataList);
        adapter.setItemWidth(getScreenWidth(getContext())/column/5*4);
        rvRight.setAdapter(adapter);

        //attachToRecyclerView
        GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.END);
        snapHelper.setColumn(column);
        snapHelper.attachToRecyclerView(rvRight);

    }


    private void configCenterRecyclerView(int row, int column) {
        RecyclerView rvCenter = viewDataBinding.rvCenter;
        rvCenter.setHasFixedSize(true);


        //setLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), row,
                LinearLayoutManager.HORIZONTAL, false);
        rvCenter.setLayoutManager(gridLayoutManager);

        //getDataSource
        List<ItemBean> dataList = addItemDatas();
        dataList = GridPagerUtils.transformAndFillEmptyData(
                new InvertRowColumnDataTransform<ItemBean>(column,row), dataList);

        //setAdapter
        RvPageTurningEffectAdapter adapter = new RvPageTurningEffectAdapter(getContext());
        adapter.updateDatas(dataList);
        adapter.setItemWidth(getScreenWidth(getContext())/column);
        rvCenter.setAdapter(adapter);

        //attachToRecyclerView
        GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
        snapHelper.setColumn(column);
        snapHelper.attachToRecyclerView(rvCenter);
        snapHelper.setCanPageScroll(true);

        CircleRecyclerPageIndicator crpiCenter = viewDataBinding.crpiCenter;
        crpiCenter.setRecyclerView(rvCenter);
        crpiCenter.setPageColumn(column);


        //加入Indicator监听
        PageIndicatorHelper pageIndicatorHelper = new PageIndicatorHelper();
        pageIndicatorHelper.setPageColumn(column);
        pageIndicatorHelper.setRecyclerView(rvCenter);
        pageIndicatorHelper.setOnPageChangeListener(new GravityPageChangeListener() {
            @Override
            public void onPageSelected(int position,int currentPage,int totalPage) {
                Log.e("MainActivity",currentPage+ "/"+totalPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private static List<ItemBean> addItemDatas() {
        List<ItemBean> dataList = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            ItemBean data = new ItemBean();
            data.title = "标题" + (i + 1);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
