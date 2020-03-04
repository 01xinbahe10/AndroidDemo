package com.example.aac.base_frame;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hxb on 2019-08-26.
 * BaseRepository：属于MVVM中的 Model层,
 * 主要作用是统一模块的数据仓库，包含网络数据和本地数据（一个应用可以有多个Repositor）
 */
public class BaseRepository implements IBaseRepository {

    private volatile Map<String, IBaseRepository.Notify> map;

    public BaseRepository() {
        map = new LinkedHashMap<>();
    }

    @Override
    public void onCleared() {
        map.clear();
    }

    public void addNotifyUpdate(String key, IBaseRepository.Notify notify) {
        map.put(key, notify);
    }

    protected void notifyUpdate(String key) {
        IBaseRepository.Notify notify = map.get(key);
        if (null != notify) {
            notify.onDataChange(key);
        }
    }

    protected void notifyUpdateAll() {
        for (String key : map.keySet()) {
            IBaseRepository.Notify notify = map.get(key);
            if (null == notify) {
                continue;
            }
            notify.onDataChange(key);
        }
    }

}
