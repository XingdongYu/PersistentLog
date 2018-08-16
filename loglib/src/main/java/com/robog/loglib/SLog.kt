package com.robog.loglib

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.robog.loglib.FileLog.Companion.FILE_PATH
import com.robog.loglib.Util.wrapLogBean
import com.robog.loglib.database.LogBeanDaoImpl
import java.io.File

/**
 * Created by yuxingdong on 2018/6/29.
 */
@SuppressLint("StaticFieldLeak")
object SLog {

    private const val TAG = "SLog"
    private var logStrategy: LogStrategy? = null
    private var context: Context? = null

    private const val EXCEPTION = -1
    private const val VERBOSE = 2
    private const val DEBUG = 3
    private const val INFO = 4
    private const val WARN = 5
    private const val ERROR = 6

    /**
     * 是否调试模式
     */
    var debug: Boolean = false

    /**
     * 设备标识
     */
    var deviceInfo: String = ""

    /**
     * 数据库检查周期
     */
    var dbCheckInterval: Long = 0

    /**
     * 数据库容量阈值
     */
    var dbThreshold: Int = 0

    /**
     * Log存储模式
     */
    var logMode: LogMode = LogMode.DATABASE

    /**
     * 缓存大小
     */
    var cacheSize: Int = 0


    fun init(context: Context) {
        init(context, LogConfig())
    }

    fun init(context: Context, logConfig: LogConfig) {
        this.context = context.applicationContext

        this.debug = logConfig.debug
        this.deviceInfo = logConfig.deviceInfo
        this.dbCheckInterval = logConfig.dbCheckInterval
        this.dbThreshold = logConfig.dbThreshold
        this.logMode = logConfig.logMode
        this.cacheSize = logConfig.cacheSize

        if (logMode == LogMode.DATABASE) {
            logStrategy = DatabaseLog.create(context.applicationContext)
        } else if (logMode == LogMode.FILE) {
            logStrategy = FileLog.create()
        }
    }

    fun v(tag: String, e: Exception) {
        v(tag, e.toString())
    }

    fun v(msg: String) {
        v(TAG, msg)
    }

    fun v(tag: String, msg: String) {
        if (debug) {
            Log.v(tag, msg)
        }
        print(tag, msg, VERBOSE)
    }

    fun d(tag: String, e: Exception) {
        d(tag, e.toString())
    }

    fun d(msg: String) {
        d(TAG, msg)
    }

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
        print(tag, msg, DEBUG)
    }

    fun i(tag: String, e: Exception) {
        i(tag, e.toString())
    }

    fun i(msg: String) {
        i(TAG, msg)
    }

    fun i(tag: String, msg: String) {
        if (debug) {
            Log.i(tag, msg)
        }
        print(tag, msg, INFO)
    }

    fun w(tag: String, e: Exception) {
        w(tag, e.toString())
    }


    fun w(msg: String) {
        w(TAG, msg)
    }

    fun w(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
        print(tag, msg, WARN)
    }

    fun e(tag: String, e: Exception) {
        e(tag, e.toString())
    }

    fun e(msg: String) {
        e(TAG, msg)
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
        print(tag, msg, ERROR)
    }

    fun cacheSize(): Int {
        return LogCache.cache.size
    }

    /**
     * 将所有缓存数据推入数据库用于上传
     */
    fun flush() {
        LogCache.get().flush()
    }

    /**
     * 获取所有数据
     */
    fun getAllLog(): List<LogBean> {
        if (context == null) {
            throw RuntimeException("Please init SLog first!")
        }
        val logBeanDao = LogBeanDaoImpl.getInstance(context!!.applicationContext)
        return logBeanDao.getAll()
    }

    /**
     * 清空数据库
     */
    fun clearDb() {
        if (context == null) {
            throw RuntimeException("Please init SLog first!")
        }
        work {
            val logBeanDao = LogBeanDaoImpl.getInstance(context!!.applicationContext)
            logBeanDao.deleteAll()
        }
    }

    /**
     * 删除日志文件
     */
    fun deleteFile() {
        val file = File(FILE_PATH)
        if (file.exists()) {
            work {
                file.delete()
            }
        }
    }

    fun changeMode(mode: LogMode) {
        if (logMode != mode) {
            logMode = mode
            destroy()
            logStrategy =
                    if (mode == LogMode.DATABASE) DatabaseLog.create(context!!.applicationContext)
                    else FileLog.create()
        }
    }

    fun destroy() {
        flush()
        logStrategy?.destroy()
        logStrategy = null
    }

    fun crash(tag: String, t: Throwable?) {
        val logBean = wrapLogBean(tag, t.toString(), EXCEPTION)
        if (logStrategy == null) {
            throw RuntimeException("Please init SLog first!")
        }
        logStrategy!!.logSync(logBean)
        flush()
    }

    private fun print(tag: String, msg: String, priority: Int) {
        synchronized(SLog::class.java) {
            work {
                val logBean = wrapLogBean(tag, msg, priority)
                if (logStrategy == null) {
                    throw RuntimeException("Please init SLog first!")
                }
                logStrategy!!.logAsync(logBean)
            }
        }
    }

}