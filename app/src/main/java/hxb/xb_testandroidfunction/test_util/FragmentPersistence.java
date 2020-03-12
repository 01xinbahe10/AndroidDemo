package hxb.xb_testandroidfunction.test_util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Size;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import hxb.xb_testandroidfunction.R;


/**
 * Created by hxb on 2018/3/22.
 * fragment 在当前的activity界面持久化
 */

public final class FragmentPersistence {
    private final String TAG = "FragmentPersistence";
    private ArrayList<Fragment> fragments;
    private FragmentManager fragmentManager;
    private int idRes;

    /**
     * ---------------new-----------start
     */
    private Config mConfig;

    private int[] anims = {R.anim.anim_fragment_right_in, R.anim.anim_fragment_left_out};
    private int[] anims4 = {R.anim.anim_fragment_right_in, R.anim.anim_fragment_left_out, R.anim.anim_fragment_left_in, R.anim.anim_fragment_right_out};
    private boolean mIsStartAnim = false;

    private FragmentTransaction transaction;
    //之前显示的fragment
    private Fragment beforeFragment = null;
    //当前的Fragment
    private Fragment currentFragment = null;
    private Class<? extends Fragment> currentFragmentClass = null;


    private FragmentPersistence() {
    }

    private FragmentPersistence(Config config) {
        mConfig = config;
    }

    /**
     * ---------------new-----------end
     */


    @Deprecated
    public static FragmentPersistence init(FragmentManager manager, int idRes, ArrayList<Fragment> list) {
        FragmentPersistence fragmentPersistence = new FragmentPersistence();
        fragmentPersistence.fragmentManager = manager;
        fragmentPersistence.idRes = idRes;
        if (null == fragmentPersistence.fragments) {
            fragmentPersistence.fragments = new ArrayList<>();
        }
        fragmentPersistence.fragments.addAll(list);
        return fragmentPersistence;
    }


    @Deprecated
    public ArrayList<Fragment> getFragments() {
        return fragments;
    }

    @Deprecated
    public void switchFragment(int i) {
        // 已优化fragment 当前activity持久缓存问题
        if (beforeFragment != fragments.get(i)) {
            transaction = fragmentManager.beginTransaction();
//            Log_Mvp.e("测试", "switchFragment:   beforeFragment:"+(beforeFragment == null)+"   transaction:"+(transaction == null)+"  fragments:"+(fragments.get(i) == null ));

            if (mIsStartAnim && (null != anims && anims.length == 2)) {
                transaction.setCustomAnimations(anims[0], anims[1]);
            }

            if (mIsStartAnim && (null != anims && anims.length == 4)) {
                transaction.setCustomAnimations(anims[0], anims[1], anims[2], anims[3]);
            }


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
            beforeFragment.onDestroyView();
        }
    }

    @Deprecated
    public void onDestroy() {
        fragments = null;
        fragmentManager = null;
    }


    /**
     * 新方法
     * 解释：
     * 由于原方法(FragmentPersistence init(FragmentManager manager, int idRes, ArrayList<Fragment> list))
     * 是将Fragment全部实例化才能使用，造成Activity初始化时会有卡顿。
     * <p>
     * 优化：1,多个Fragment 可以部分实例化,未实例化的在使用时初始化
     * 2,可根据Class类型，指定Fragment,也可随意插入新的Fragment;避免原方法根据脚标进行盲找
     */
    public static Config init2() {
        return init2(3);

    }

    public static Config init2(int initNum) {
        return new Config(initNum);
    }


    public static final class Config {
        private FragmentManager manager;
        private int fragmentId = -1;//fragment布局容器Id
        private Map<String, ClazzParam> noInitLinkedMap;//存放没有实例化的对象容器
        private Map<Class<?>, Fragment> linkedMap;//实例化的对象容器
        private int initFragmentNum = 3;//指首次初始化fragment多少个(默认3个)
        private ArrayList<Class<? extends Fragment>> patternContainer;//Fragment流程模式容器

        private Config(int initNum) {
            noInitLinkedMap = new LinkedHashMap<>();
            linkedMap = new LinkedHashMap<>();
            patternContainer = new ArrayList<>();

            initFragmentNum = initNum;
        }

        public Config setManager(@NonNull FragmentManager manager) {
            this.manager = manager;
            return this;
        }

        public Config setFragmentId(@NonNull int fragmentId) {
            this.fragmentId = fragmentId;
            return this;
        }

        public Config putFragment(Class<? extends Fragment> clazz) {
            return putFragment(clazz, null);
        }

        /**
         * @param clazz          需要初始化的Fragment类型的类
         * @param parameterTypes 是继承Fragment的类的有参构造函数的参数类型
         * @param initObjects    是继承Fragment的类的有参构造函数的参数类型的实例化
         */
        public Config putFragment(Class<? extends Fragment> clazz, Class[] parameterTypes, Object... initObjects) {
            if (parameterTypes != null) {//验证构造参数是否跟实例化对象类型长度是否一致
                int length = parameterTypes.length;
                int length2 = initObjects.length;
                if (length != length2) {
                    throw new ArrayIndexOutOfBoundsException("parameterTypes and initObjects array length are not aligned");
                }
            }
            noInitLinkedMap.put(clazz.getName(), new ClazzParam(clazz, parameterTypes, initObjects));
            initFragment(clazz, parameterTypes, initObjects);
            return this;
        }

        private void initFragment(Class<? extends Fragment> clazz, Class[] parameterTypes, Object... initObjects) {

            if (initFragmentNum > 0) {
                try {

                    if (parameterTypes == null || initObjects == null) {
                        //初始化无参构造函数
                        linkedMap.put(clazz, clazz.newInstance());
                    } else {
                        //初始化有参构造函数
                        linkedMap.put(clazz, clazz.getConstructor(parameterTypes).newInstance(initObjects));

                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                initFragmentNum--;
            }

        }


        public FragmentPersistence build() {
            if (manager == null || fragmentId == -1) {
                throw new NumberFormatException("FragmentManager or FrameLayout_id not null");
            }

            return new FragmentPersistence(this);
        }
    }

    /**
     * ClazzParam 目的 ：
     * 存放 一个类的有参构造函数的 参数类类型 和 参数实例化对象
     */
    private static final class ClazzParam {
        public Class<? extends Fragment> aClass;
        public Class[] classes;
        public Object[] objects;

        public ClazzParam(Class<? extends Fragment> aClass, Class[] classes, Object[] objects) {
            this.aClass = aClass;
            this.classes = classes;
            this.objects = objects;
        }
    }

    public void switchFragment2(Class<? extends Fragment> clazz) {
        switchFragment2(clazz, null, true, false);
    }

    public void switchFragment2(Class<? extends Fragment> clazz, Bundle bundle) {
        switchFragment2(clazz, bundle, true, false);
    }


    public void switchFragment2(Class<? extends Fragment> clazz, boolean isInStack) {
        switchFragment2(clazz, null, isInStack, false);
    }

    public void switchFragment2(Class<? extends Fragment> clazz, Bundle bundle, boolean isInStack) {
        switchFragment2(clazz, bundle, isInStack, false);
    }

    /**
     * @param isStartMode 是否开启页面的启动模式，
     * 目前只有一种模式 跟Activity的standard(标准模式)一样。
     * @param isGoBack    是否是开启栈底出栈
     */
    private void switchFragment2(Class<? extends Fragment> clazz, Bundle bundle, boolean isStartMode, boolean isGoBack) {
        currentFragmentClass = clazz;
        if (null == mConfig.linkedMap.get(clazz)) {
            ClazzParam clazzParam = mConfig.noInitLinkedMap.get(clazz.getName());
            if (null == clazzParam) {//如果在noInitLinkedMap中，未找到某个类，则放弃本次操作
                return;
            }
            try {
                //初始化
                if (clazzParam.classes != null) {
                    currentFragment = clazzParam.aClass.getConstructor(clazzParam.classes).newInstance(clazzParam.objects);
                } else {
                    currentFragment = clazzParam.aClass.newInstance();
                }
                mConfig.linkedMap.put(clazzParam.aClass, currentFragment);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        } else {
            currentFragment = mConfig.linkedMap.get(clazz);
        }

        if (null == currentFragment) {
            return;
        }
        if (null != bundle) {
            if (isGoBack) {
                Bundle bundle1 = currentFragment.getArguments();
                if (null == bundle1) {
                    //表示初始化fragment没有设置bundle ，也就导致在GoBack时不能设置
                    Log.e(TAG, "If you need to pass in a bundle when returning, you must pass in the bundle when the fragment is initialized.");
                } else {
                    bundle1.putAll(bundle);
                }

            } else {
                currentFragment.setArguments(bundle);
            }
        }


        // 已优化fragment 当前activity持久缓存问题
        if (beforeFragment != currentFragment) {
            transaction = mConfig.manager.beginTransaction();
//            Log_Mvp.e("测试", "switchFragment:   beforeFragment:"+(beforeFragment == null)+"   transaction:"+(transaction == null)+"  fragments:"+(fragments.get(i) == null ));

            if (mIsStartAnim && (null != anims && anims.length == 2)) {
                transaction.setCustomAnimations(anims[0], anims[1]);
            }

            if (mIsStartAnim && (null != anims && anims.length == 4)) {
                transaction.setCustomAnimations(anims[0], anims[1], anims[2], anims[3]);
            }


            if (!currentFragment.isAdded()) { // 先判断是否被add过
                if (beforeFragment == null) {
                    transaction.add(mConfig.fragmentId, currentFragment).commit();
                } else {
                    transaction.hide(beforeFragment).add(mConfig.fragmentId, currentFragment).commit(); // 隐藏当前的fragment，add下一个到Activity中
                }
            } else {
                if (beforeFragment == null) {
                    transaction.show(currentFragment).commit();
                } else {
                    transaction.hide(beforeFragment).show(currentFragment).commit(); // 隐藏当前的fragment，显示下一个
                }
            }
            beforeFragment = currentFragment;
//            beforeFragment.onDestroyView();
        }

        /*
        * 如果没有开启启动模式 (页面流程记录)，
        * 后面则不执行 入栈记录 或 出栈等释放操作。
        * */
        if (!isStartMode){
            if (null != beforeFragment) {
                beforeFragment.onDestroyView();
            }
            return;
        }

        /*
         * 如果是返回动作，则不记录fragment页面之间的流程。
         * 如果当前fragment 和 patternContainer的最后一个相同则不记录。
         * */
        int size = mConfig.patternContainer.size();
        Class<? extends Fragment> lastClass = null;
        if (size > 0) {
            lastClass = mConfig.patternContainer.get(size - 1);
        }
        //表示入栈操作
        if (!isGoBack && clazz != lastClass) {
            mConfig.patternContainer.add(clazz);
            return;
        }
        //表示出栈操作
        if (isGoBack && null != lastClass) {
            //同时将最后一个进行视图销毁
            Fragment preLastFragment = mConfig.linkedMap.get(lastClass);
            if (null != preLastFragment) {
                preLastFragment.onDestroyView();
            }
        }

    }


    public FragmentPersistence setInOutAnim2(@Size(2) int[] anims) {
        this.anims = anims;
        return this;
    }

    public FragmentPersistence setInOutAnim4(@Size(4) int[] anims) {
        this.anims = anims;
        return this;
    }

    public FragmentPersistence startAnim(boolean isStartAnim) {
        this.mIsStartAnim = isStartAnim;
        return this;
    }


    public void freedFragment(Class<? extends Fragment>... clazzs) {
        for (Class<? extends Fragment> c : clazzs) {
            Fragment fragment = mConfig.linkedMap.get(c);
            if (null != fragment && fragment.isAdded()) {
                transaction = mConfig.manager.beginTransaction();
//                Log.e("TAG", "goBack: ---------------------------   "+mConfig.manager.getBackStackEntryCount());
                //移出并销毁 FragmentTransaction中的Fragment对象
                transaction.remove(fragment).commit();
            }
            //释放掉实例化的Fragment
            mConfig.linkedMap.remove(c);
        }
    }

    public Class<? extends Fragment> getCurrentFtClass() {
        return currentFragmentClass;
    }

    public Fragment getCurrentFt() {
        return currentFragment;
    }

    /*
    * goBack() 方法必须是出栈方式操作
    * */
    public boolean goBack() {
        return goBack(null);
    }

    public boolean goBack(Bundle bundle) {
        int lastIndex = mConfig.patternContainer.size() - 1;
//        Log.e("TAG", "goBack: -----------------11  "+lastIndex );
        if (lastIndex > 0) {//大于0 是需要保留最后一个
            switchFragment2(mConfig.patternContainer.get(lastIndex - 1), bundle, true, true);//选择前一个
            Class<? extends Fragment> clazz = mConfig.patternContainer.get(lastIndex);
//            Log.e("TAG", "goBack: -----------------22  "+lastIndex +"     "+clazz.getName());
            Fragment fragment = mConfig.linkedMap.get(clazz);
            if (null != fragment && fragment.isAdded()) {
                transaction = mConfig.manager.beginTransaction();
//                Log.e("TAG", "goBack: ---------------------------   "+mConfig.manager.getBackStackEntryCount());
                //移出并销毁 FragmentTransaction中的Fragment对象
                transaction.remove(fragment).commit();
            }
            mConfig.linkedMap.remove(clazz);
            mConfig.patternContainer.remove(clazz);//移出最后一个
            return true;
        }
        return false;
    }

    /*
    * 清除界面流转流程及Fragment实例;
    * */
    public void clearPatternContainer(boolean clearInitFragment) {
        if (clearInitFragment) {
            //是需要保留最后一个
            for (int i = mConfig.patternContainer.size() - 1; i > 0; i--) {
                Class<? extends Fragment> clazz = mConfig.patternContainer.get(i);
                Fragment fragment = mConfig.linkedMap.get(clazz);
                if (null != fragment && fragment.isAdded()) {
                    transaction = mConfig.manager.beginTransaction();
                    //移出并销毁 FragmentTransaction中的Fragment对象
                    transaction.remove(fragment).commit();
                }
                mConfig.linkedMap.remove(clazz);
            }
        }
        mConfig.patternContainer.clear();
    }

    public void onDestroy2() {
        mConfig = null;
        transaction = null;
        beforeFragment = null;
        currentFragment = null;
    }

}
