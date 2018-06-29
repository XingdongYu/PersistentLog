package com.robog.loglib;

/**
 * Created by yuxingdong on 2018/6/23.
 */
public final class LogBeanPool {

    private static final Object sPoolSync = new Object();
    private static LogBean sPool;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 60;

    public static LogBean get() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                LogBean bean = sPool;
                sPool = bean.next;
                bean.next = null;
                sPoolSize--;
                return bean;
            }
        }
        return new LogBean();
    }

    static void recycle(LogBean logBean) {

        logBean.priority = 0;
        logBean.tag = "";
        logBean.message = "";
        logBean.time = "";
        logBean.stackTrace = "";

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                logBean.next = sPool;
                sPool = logBean;
                sPoolSize++;
            }
        }
    }
}
