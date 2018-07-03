package com.robog.loglib

import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by yuxingdong on 2018/6/29.
 */
object Util {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)

    fun wrapLogBean(tag: String, msg: String, priority: Int): LogBean {
        val logBean = LogBeanPool.get()
        logBean.priority = priority
        logBean.tag = tag
        // TODO: 2018/6/21 message加密
        logBean.message = msg
        logBean.time = logTime()
        logBean.stackTrace = stackTrace()
        return logBean
    }

    fun jsonToLogBean(jsonArray: JSONArray): List<LogBean> {
        val beanList = ArrayList<LogBean>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject = jsonArray.getJSONObject(i)
            val declaredFields = LogBean::class.java.declaredFields
            val logBean = LogBeanPool.get()
            declaredFields.forEach {
                if (jsonObject.has(it.name)) {
                    it.isAccessible = true
                    it.set(logBean, jsonObject.get(it.name))
                }
            }
            beanList.add(logBean)
        }
        return beanList
    }

    private fun stackTrace(): String {

        val currentThread = Thread.currentThread()
        val stackTraces = currentThread.stackTrace
        val sb = StringBuilder(currentThread.name).append(" ")

        stackTraces.filter {
            !it.className.startsWith("com.robog.loglib.") &&
                    !it.className.startsWith("android.") &&
                    !it.className.startsWith("java.") &&
                    !it.className.startsWith("dalvik.") &&
                    !it.className.startsWith("com.android.")
        }.forEach {
            sb.append(it.className)
                    .append(".")
                    .append(it.methodName)
                    .append("\n")
        }
        return sb.toString()
    }

    private fun logTime(): String = DATE_FORMAT.format(Date())

}