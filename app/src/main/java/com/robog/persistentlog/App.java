package com.robog.persistentlog;

import android.app.Application;

import com.robog.loglib.LogConfig;
import com.robog.loglib.SLog;

import java.util.UUID;

/**
 * Created by yuxingdong on 2018/6/20.
 */
public class App extends Application {

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

        SLog.init(this, logConfig);
    }
}
