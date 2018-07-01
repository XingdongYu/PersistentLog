package com.robog.persistentlog

import android.app.Application
import com.robog.loglib.LogConfig
import com.robog.loglib.LogMode
import com.robog.loglib.SLog

/**
 * Created by yuxingdong on 2018/6/29.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CrashHandler.init()

        val logConfig = LogConfig(
                debug = false,
                deviceInfo = "Device Info",
                dbCheckInterval = 4000,
                dbThreshold = 1000,
                cacheSize = 50,
                logMode = LogMode.FILE
        )

        SLog.init(this, logConfig)
    }
}