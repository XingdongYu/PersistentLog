package com.robog.persistentlog

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.robog.loglib.LogConfig
import com.robog.loglib.SLog

/**
 * Created by yuxingdong on 2018/6/29.
 */
class LogService : Service() {

    private lateinit var checkServiceStateThread: CheckServiceStateThread

    companion object {

        private const val SERVICE_CHECK_INTERVAL = 60 * 60 * 1000L

        fun init(context: Context) {
            init(context, LogConfig.Builder().build())
        }

        fun init(context: Context, logConfig: LogConfig) {
            SLog.init(context, logConfig)
            context.startService(Intent(context, LogService::class.java))
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkServiceStateThread = CheckServiceStateThread()
        checkServiceStateThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        checkServiceStateThread.interrupt()
        SLog.destroy()
    }

    /**
     * 定时向服务器发送数据。如果服务器有返回需要回捞日志的标识，则向服务器发送数据库中的日志信息。
     * 具体可跟后台约定
     */
    internal class CheckServiceStateThread : Thread("Log Checker") {

        init {
            isDaemon = true
        }

        override fun run() {
            while (true) {
                try {
                    // 与后台交互
                    Thread.sleep(SERVICE_CHECK_INTERVAL)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }

            }
        }
    }
}