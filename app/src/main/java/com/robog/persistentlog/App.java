package com.robog.persistentlog;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.robog.loglib.LogConfig;

import java.util.UUID;

/**
 * Created by yuxingdong on 2018/6/20.
 */
public class App extends Application {

    private static final String TAG = "App";

    private final LogService.CheckFunction mCheckFunction = new LogService.CheckFunction() {
        @Override
        public void apply() {
            Log.d(TAG, "apply: ");
        }
    };

    private LogService.InnerBinder mInnerBinder;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mInnerBinder = (LogService.InnerBinder) service;
            mInnerBinder.checkService(mCheckFunction);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.init();

        final LogConfig logConfig = new LogConfig.Builder()
                .debug(false)
                // 模拟数据
                .deviceInfo(UUID.randomUUID() + "")
                .serviceCheckInterval(3000)
                .dbCheckInterval(4000)
                .dbThreshold(1000)
                .build();

//        SLog.init(this, logConfig);

        LogService.bind(this, logConfig, mConnection);
    }
}
