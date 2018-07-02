package com.robog.loglib

import android.content.Context
import android.os.Process
import com.robog.loglib.database.LogBeanDao
import com.robog.loglib.database.LogBeanDaoImpl

/**
 * Created by yuxingdong on 2018/6/29.
 * <p>
 * 将Log对象存入数据库，并开启数据库监测，定期检查数据库数据数量是否达到上限。
 * </p>
 */
internal class DatabaseLog private constructor(
        context: Context
) : AbstractLog(), Savable {

    private val logBeanDao: LogBeanDao
    private var databaseWatchDog: DatabaseThresholdWatchDog? = null

    init {

        logBeanDao = LogBeanDaoImpl.getInstance(context)
        databaseWatchDog = DatabaseThresholdWatchDog(logBeanDao)
        databaseWatchDog!!.start()

        LogCache.get().savable = this
    }

    companion object {
        private var instance: DatabaseLog? = null

        fun create(context: Context): DatabaseLog = instance
                ?: DatabaseLog(context).also { instance = it }
    }

    override fun destroy() {
        instance = null
        databaseWatchDog?.interrupt()
        databaseWatchDog = null
    }

    override fun save(logBeans: List<LogBean>) {
        logBeanDao.save(logBeans)
    }

    internal class DatabaseThresholdWatchDog(
            private val innerLogBeanDao: LogBeanDao
    ) : Thread("Check database thread") {

        init {
            isDaemon = true
        }

        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            while (true) {
                try {
                    Thread.sleep(SLog.dbCheckInterval)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    break
                }
                innerLogBeanDao.checkThreshold()
            }
        }
    }
}