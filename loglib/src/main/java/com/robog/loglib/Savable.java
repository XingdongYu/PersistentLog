package com.robog.loglib;

import android.support.annotation.WorkerThread;

import java.util.List;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public interface Savable {

    /**
     * {@link LogCache#saveAndClean()}中的存储回调方法，可在自定义的LogStrategy实现
     * @param logBeans 缓存中的所有Log对象
     */
    @WorkerThread
    void save(List<LogBean> logBeans);
}
