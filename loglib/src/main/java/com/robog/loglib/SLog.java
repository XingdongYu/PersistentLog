package com.robog.loglib;

import android.annotation.SuppressLint;
import android.content.Context;

import com.robog.loglib.database.LogBeanDao;
import com.robog.loglib.database.LogBeanDaoImpl;

import java.io.File;
import java.util.List;

import static com.robog.loglib.FileLog.FILE_PATH;
import static com.robog.loglib.Util.wrapLogBean;

/**
 * Created by yuxingdong on 2018/6/21.
 * <p>
 * 需要权限:
 * <li>{@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE}
 * <li>{@link android.Manifest.permission#READ_EXTERNAL_STORAGE}
 * <p/>
 */
public final class SLog {

    public static final String TAG = "SLog";
    private static LogStrategy sLogStrategy;
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private static final int EXCEPTION = -1;
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    public static boolean sDebug;
    public static String sDeviceInfo;
    public static int sDbCheckInterval;
    public static int sDbThreshold;
    public static int sLogMode;
    public static int sCacheSize;

    public static void init(Context context) {
        init(context, new LogConfig.Builder().build());
    }

    public static void init(Context context, LogConfig logConfig) {
        sContext = context.getApplicationContext();

        sDebug = logConfig.debug();
        sDeviceInfo = logConfig.deviceInfo();
        sDbCheckInterval = logConfig.dbCheckInterval();
        sDbThreshold = logConfig.dbThreshold();
        sLogMode = logConfig.logMode();
        sCacheSize = logConfig.cacheSize();

        if (sLogMode == LogMode.DATABASE) {
            sLogStrategy = DatabaseLog.create(sContext);
        } else if (sLogMode == LogMode.FILE) {
            sLogStrategy = FileLog.create();
        }
    }

    public static void v(String tag, Exception e) {
        v(tag, e.toString());
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (sDebug) {
            SLog.v(tag, msg);
        }
        print(tag, msg, VERBOSE);
    }

    public static void d(String tag, Exception e) {
        d(tag, e.toString());
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (sDebug) {
            SLog.d(tag, msg);
        }
        print(tag, msg, DEBUG);
    }

    public static void i(String tag, Exception e) {
        i(tag, e.toString());
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (sDebug) {
            SLog.i(tag, msg);
        }
        print(tag, msg, INFO);
    }

    public static void w(String tag, Exception e) {
        w(tag, e.toString());
    }


    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (sDebug) {
            SLog.e(tag, msg);
        }
        print(tag, msg, WARN);
    }

    public static void e(String tag, Exception e) {
        e(tag, e.toString());
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (sDebug) {
            SLog.e(tag, msg);
        }
        print(tag, msg, ERROR);
    }

    public static int cacheSize() {
        return LogCache.CACHE.size();
    }

    /**
     * 将所有缓存数据推入数据库用于上传
     */
    public static void flush() {
        LogCache.get().flush();
    }

    /**
     * 获取所有数据
     */
    public static List<LogBean> getAllLog() {
        if (sContext == null) {
            throw new RuntimeException("Please init SLog first!");
        }
        final LogBeanDao logBeanDao = LogBeanDaoImpl.getInstance(sContext);
        return logBeanDao.getAll();
    }

    /**
     * 清空数据库
     */
    public static void clearDb() {
        if (sContext == null) {
            throw new RuntimeException("Please init SLog first!");
        }
        final LogBeanDao logBeanDao = LogBeanDaoImpl.getInstance(sContext);
        logBeanDao.deleteAll();
    }

    /**
     * 删除日志文件
     */
    public static void deleteFile() {
        final File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void changeMode(@LogMode.Check int mode) {
        if (sLogMode != mode) {
            sLogMode = mode;
            destroy();
            sLogStrategy = mode == LogMode.DATABASE ? DatabaseLog.create(sContext) : FileLog.create();
        }
    }

    public static void destroy() {
        flush();
        if (sLogStrategy != null) {
            sLogStrategy.destroy();
            sLogStrategy = null;
        }
    }

    public static void crash(String tag, Throwable t) {
        final LogBean logBean = wrapLogBean(tag, t.toString(), EXCEPTION);
        if (sLogStrategy == null) {
            throw new RuntimeException("Please init SLog first!");
        }
        sLogStrategy.logSync(logBean);
        flush();
    }

    private static void print(String tag, String msg, int priority) {
        synchronized (SLog.class) {
            final LogBean logBean = wrapLogBean(tag, msg, priority);
            if (sLogStrategy == null) {
                throw new RuntimeException("Please init SLog first!");
            }
            sLogStrategy.logAsync(logBean);
        }
    }

}
