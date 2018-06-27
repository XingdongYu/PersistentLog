package com.robog.persistentlog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.robog.loglib.LogConfig;
import com.robog.loglib.SLog;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public class LogService extends Service {

    private static final String TAG = "LogService";
    private CheckServiceStateThread mCheckServiceStateThread;

    public interface CheckFunction {

        void apply();
    }

    public static void init(Context context) {
        init(context, new LogConfig.Builder().build());
    }

    public static void init(Context context, LogConfig logConfig) {
        SLog.init(context, logConfig);
        context.startService(new Intent(context, LogService.class));
    }

    public static void bind(Context context, LogConfig logConfig, ServiceConnection connection) {
        SLog.init(context, logConfig);
        context.bindService(new Intent(context, LogService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public class InnerBinder extends Binder {

        public void checkService(CheckFunction f) {
            onServiceCheck(f);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mCheckServiceStateThread = new CheckServiceStateThread();
        mCheckServiceStateThread.start();
        return new InnerBinder();
    }

    private void onServiceCheck(CheckFunction f) {
        if (mCheckServiceStateThread != null) {
            mCheckServiceStateThread.setCheckFunction(f);
        }
    }

    /**
     * 定时向服务器发送数据。如果服务器有返回需要回捞日志的标识，则向服务器发送数据库中的日志信息。
     * 具体可跟后台约定
     */
    static class CheckServiceStateThread extends Thread {

        private CheckFunction mCheckFunction;

        private void setCheckFunction(CheckFunction checkFunction) {
            mCheckFunction = checkFunction;
        }

        private CheckServiceStateThread() {
            super("Log Checker");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (mCheckFunction != null) {
                        mCheckFunction.apply();
                    }
                    Thread.sleep(SLog.sServiceCheckInterval);
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
