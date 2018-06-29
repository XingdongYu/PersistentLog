package com.robog.loglib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuxingdong on 2018/6/21.
 */
final class LogCache {

    private static final String TAG = "LogCache";
    static final List<LogBean> CACHE = new ArrayList<>();

    private Savable mSavable;

    private static LogCache sLogCache = null;

    private LogCache() {

    }

    public static LogCache get() {
        if (sLogCache == null) {
            sLogCache = new LogCache();
        }
        return sLogCache;
    }

    /**
     * 1. 将数据存入缓存
     * 2. 若缓存大于预定值则将缓存中的数据存入数据库
     *
     * @param bean
     */
    void put(final LogBean bean) {
        CACHE.add(bean);
        if (CACHE.size() > SLog.sCacheSize) {
            saveAndClean();
        }
    }

    /**
     * 将所有缓存的数据推入数据库
     */
    void flush() {
        if (CACHE.size() > 0) {
            saveAndClean();
        }
    }

    /**
     * 保存并清除缓存
     */
    private void saveAndClean() {

        if (null != mSavable) {
            // 1.保存
            mSavable.save(CACHE);
            // 2.清除缓存
            clear();
        }
    }

    /**
     * 清除缓存，回收LogBean
     */
    private void clear() {
        if (CACHE.size() > 0) {
            for (LogBean bean : CACHE) {
                LogBeanPool.recycle(bean);
            }
            CACHE.clear();
        }
    }

    void setSavable(Savable savable) {
        mSavable = savable;
    }
}
