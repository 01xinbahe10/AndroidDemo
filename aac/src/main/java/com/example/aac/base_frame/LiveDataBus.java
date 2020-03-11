package com.example.aac.base_frame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hxb on  2020/3/11
 * 事件注册订阅及发送
 *
 * LiveData的优点:
 * 1,UI和实时数据保持一致 因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，更新UI。
 * 2,避免内存泄漏 观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动清理自身的数据。
 * 3,不会再产生由于Activity处于stop状态而引起的崩溃。例如：当Activity处于后台状态时，是不会收到LiveData的任何事件的。
 * 4,不需要再解决生命周期带来的问题 LiveData可以感知被绑定的组件的生命周期，只有在活跃状态才会通知数据变化。
 * 5,实时数据刷新 当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。
 * 6,解决Configuration Change问题 在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。
 */
public final class LiveDataBus {
    private final Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    public <T> MutableLiveData<T> with(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(key);
    }

    public MutableLiveData<Object> with(String key) {
        return with(key, Object.class);
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return;
                }
                observer.onChanged(t);
            }
        }

        private boolean isCallOnObserve() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement element : stackTrace) {
                    if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                            "observeForever".equals(element.getMethodName())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class BusMutableLiveData<T> extends MutableLiveData<T> {

        private Map<Observer, Observer> observerMap = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            try {
                hook(observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void observeForever(@NonNull Observer<? super T> observer) {
            if (!observerMap.containsKey(observer)) {
                observerMap.put(observer, new ObserverWrapper(observer));
            }
            super.observeForever(observer);
        }

        @Override
        public void removeObserver(@NonNull Observer<? super T> observer) {
            Observer realObserver = null;
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer);
            } else {
                realObserver = observer;
            }
            super.removeObserver(realObserver);
        }

        /*
        * 解决订阅者会收到订阅之前发布的消息(通过反射拿到LifecycleBoundObserver，
        * 再把LifecycleBoundObserver的version设置成和LiveData一致即可。)
        *
        * 产生的原因:对于LiveData，其初始的version是-1，当我们调用了其setValue或者postValue，其vesion会+1；
        * 对于每一个观察者的封装ObserverWrapper，其初始version也为-1，也就是说，每一个新注册的观察者，其version为-1；
        * 当LiveData设置这个ObserverWrapper的时候，如果LiveData的version大于ObserverWrapper的version，
        * LiveData就会强制把当前value推送给Observer。
        *
        * */
        private void hook(@NonNull Observer observer) throws Exception {
            //get wrapper's version
            Class<LiveData> classLiveData = LiveData.class;
            Field fieldObservers = classLiveData.getDeclaredField("mObservers");
            fieldObservers.setAccessible(true);
            Object objectObservers = fieldObservers.get(this);
            Class<?> classObservers = objectObservers.getClass();
            Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
            methodGet.setAccessible(true);
            Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
            Object objectWrapper = null;
            if (objectWrapperEntry instanceof Map.Entry) {
                objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
            }
            if (objectWrapper == null) {
                throw new NullPointerException("Wrapper can not be bull!");
            }
            Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
            Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
            fieldLastVersion.setAccessible(true);
            //get livedata's version
            Field fieldVersion = classLiveData.getDeclaredField("mVersion");
            fieldVersion.setAccessible(true);
            Object objectVersion = fieldVersion.get(this);
            //set wrapper's version
            fieldLastVersion.set(objectWrapper, objectVersion);
        }
    }
}
