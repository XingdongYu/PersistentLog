package com.robog.persistentlog;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.robog.loglib.LogConfig;
import com.robog.loglib.LogMode;
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
                // 是否调试模式
                .debug(false)
                // 设备数据
                .deviceInfo(UUID.randomUUID() + "")
                // 数据库检查周期
                .dbCheckInterval(4000)
                // 数据库容量
                .dbThreshold(1000)
                // 缓存大小
                .cacheSize(50)
                // 日志存储方式
                .logMode(LogMode.DATABASE)
                .build();

        SLog.init(this, logConfig);
    }
}
