package com.robog.persistentlog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.robog.loglib.LogConfig;
import com.robog.loglib.SLog;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public class LogService extends Service {

    private static final int SERVICE_CHECK_INTERVAL = 60 * 60 * 1000;
    private CheckServiceStateThread mCheckServiceStateThread;

    public static void init(Context context) {
        init(context, new LogConfig.Builder().build());
    }

    public static void init(Context context, LogConfig logConfig) {
        SLog.init(context, logConfig);
        context.startService(new Intent(context, LogService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCheckServiceStateThread = new CheckServiceStateThread();
        mCheckServiceStateThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 定时向服务器发送数据。如果服务器有返回需要回捞日志的标识，则向服务器发送数据库中的日志信息。
     * 具体可跟后台约定
     */
    static class CheckServiceStateThread extends Thread {

        private CheckServiceStateThread() {
            super("Log Checker");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 与后台交互

                    Thread.sleep(SERVICE_CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCheckServiceStateThread.interrupt();
        mCheckServiceStateThread = null;
        SLog.destroy();
    }
}
