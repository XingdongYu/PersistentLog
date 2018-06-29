package com.robog.persistentlog

import android.app.Application
import com.robog.loglib.LogConfig
import com.robog.loglib.LogMode
import com.robog.loglib.SLog

/**
 * Created by yuxingdong on 2018/6/29.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        CrashHandler.init()

        val logConfig = LogConfig.Builder()
                // 是否调试模式
                .debug(false)
                // 设备数据
                .deviceInfo("Device Info")
                // 数据库检查周期
                .dbCheckInterval(4000)
                // 数据库容量
                .dbThreshold(1000)
                // 缓存大小
                .cacheSize(50)
                // 日志存储方式
                .logMode(LogMode.DATABASE)
                .build()

        SLog.init(this, logConfig)
    }
}