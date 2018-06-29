package com.sunyard.loglib

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yuxingdong on 2018/6/29.
 */
object Util {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)

    fun wrapLogBean(tag: String, msg: String, priority: Int): LogBean {
        val logBean = LogBeanPool.get()
        logBean.priority = priority
        logBean.tag = tag
        logBean.message = msg
        logBean.time = logTime()
        logBean.stackTrace = stackTrace()
        return logBean
    }

    private fun stackTrace(): String {

        val currentThread = Thread.currentThread()
        val stackTraces = currentThread.stackTrace
        val sb = StringBuilder(currentThread.name).append(" ")

        for (stackTrace in stackTraces) {
            if (filterSystemClass(stackTrace)) {
                continue
            }
            sb.append(stackTrace.className)
                    .append(".")
                    .append(stackTrace.methodName)
                    .append("\n")
        }
        return sb.toString()
    }

    private fun logTime(): String {
        val date = Date()
        return DATE_FORMAT.format(date)
    }

    private fun filterSystemClass(stackTrace: StackTraceElement): Boolean {
        return (stackTrace.className.startsWith("com.robog.loglib")
                || stackTrace.className.startsWith("android.")
                || stackTrace.className.startsWith("java.")
                || stackTrace.className.startsWith("dalvik.")
                || stackTrace.className.startsWith("com.android"))
    }
}