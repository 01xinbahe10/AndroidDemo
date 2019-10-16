package com.cdct.cmdim.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hxb on 2018/3/22.
 * fragment 在当前的activity界面持久化
 */

public class FragmentPersistenceUtils {

    private ArrayList<Fragment> fragments;
    private FragmentManager fragmentManager;
    private int idRes;

    public static FragmentPersistenceUtils init(FragmentManager manager, int idRes, ArrayList<Fragment> list){
        FragmentPersistenceUtils fragmentPersistenceUtils = new FragmentPersistenceUtils();
        fragmentPersistenceUtils.fragmentManager = manager;
        fragmentPersistenceUtils.idRes = idRes;
        if (null == fragmentPersistenceUtils.fragments){
            fragmentPersistenceUtils.fragments = new ArrayList<>();
        }
        fragmentPersistenceUtils.fragments.addAll(list);
        return fragmentPersistenceUtils;
    }

    private FragmentTransaction transaction;
    //之前显示的fragment
    private Fragment beforeFragment = null;
    public void switchFragment(int i) {
        // 已优化fragment 当前activity持久缓存问题
        if (beforeFragment != fragments.get(i)) {
            transaction = fragmentManager.beginTransaction();
            Log.e("测试", "switchFragment:   beforeFragment:"+(beforeFragment == null)+"   transaction:"+(transaction == null)+"  fragments:"+(fragments.get(i) == null ));
            if (!fragments.get(i).isAdded()) { // 先判断是否被add过
                if (beforeFragment == null) {
                    transaction.add(idRes, fragments.get(i)).commit();
                } else {
                    transaction.hide(beforeFragment).add(idRes, fragments.get(i)).commit(); // 隐藏当前的fragment，add下一个到Activity中
                }
            } else {
                if (beforeFragment == null) {
                    transaction.show(fragments.get(i)).commit();
                } else {
                    transaction.hide(beforeFragment).show(fragments.get(i)).commit(); // 隐藏当前的fragment，显示下一个
                }
            }
            beforeFragment = fragments.get(i);
        }
    }
}
