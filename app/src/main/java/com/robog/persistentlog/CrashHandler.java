package com.robog.persistentlog;

import com.robog.loglib.SLog;

/**
 * Created by yuxingdong on 2018/6/25.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static Thread.UncaughtExceptionHandler sDefaultCrashHandler;
    private static CrashHandler sCrashHandler;

    private CrashHandler() {
        sDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashHandler init() {
        if (sCrashHandler == null) {
            sCrashHandler = new CrashHandler();
        }
        return sCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {

        SLog.crash(TAG, t);

        if (sDefaultCrashHandler != null) {
            sDefaultCrashHandler.uncaughtException(thread, t);
        }
    }
}
