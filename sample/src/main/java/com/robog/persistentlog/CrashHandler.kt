package com.robog.persistentlog

import com.robog.loglib.SLog

/**
 * Created by yuxingdong on 2018/6/29.
 */
class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    private val defaultCrashHandler: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private const val TAG = "CrashHandler"

        private var instance: CrashHandler? = null

        fun init() = instance ?: CrashHandler().also { instance = it }
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        SLog.crash(TAG, e)
        defaultCrashHandler.uncaughtException(t, e)
    }
}