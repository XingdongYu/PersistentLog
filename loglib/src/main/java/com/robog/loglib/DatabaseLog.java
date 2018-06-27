package com.robog.loglib;


import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.robog.loglib.database.LogBeanDao;
import com.robog.loglib.database.LogBeanDaoImpl;

import java.util.List;

import static com.robog.loglib.SLog.TAG;

/**
 * Created by yuxingdong on 2018/6/21.
 * <p>
 * 将Log对象存入数据库，并开启数据库监测，定期检查数据库数据数量是否达到上限。
 * </p>
 */
final class DatabaseLog extends AbstractLog implements Savable {

    private final LogBeanDao mLogBeanDao;
    private DatabaseThresholdWatchDog mDatabaseWatchDog;
    private static DatabaseLog INSTANCE = null;

    public static DatabaseLog create(Context context){
        if (INSTANCE == null){
            INSTANCE = new DatabaseLog(context);
        }
        return INSTANCE;
    }

    private DatabaseLog(Context context) {

        mLogBeanDao = LogBeanDaoImpl.getInstance(context);

        // 开启数据库监测线程
        mDatabaseWatchDog = new DatabaseThresholdWatchDog(mLogBeanDao);
        mDatabaseWatchDog.start();

        // 保存方法实现
        LogCache.get().setSavable(this);
    }

    @Override
    public void destroy() {
        INSTANCE = null;
        mDatabaseWatchDog.interrupt();
        mDatabaseWatchDog = null;
    }

    @Override
    public void save(final List<LogBean> logBeans) {
        mLogBeanDao.save(logBeans);
    }

    /**
     * 周期性检查数据库数据大小是否达到上限
     */
    static class DatabaseThresholdWatchDog extends Thread {

        private final LogBeanDao mLogBeanDao;

        DatabaseThresholdWatchDog(LogBeanDao logBeanDao) {
            super("Check database thread");
            setDaemon(true);
            mLogBeanDao = logBeanDao;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                try {
                    Thread.sleep(SLog.sDbCheckInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                mLogBeanDao.checkThreshold();
            }
        }
    }
}
