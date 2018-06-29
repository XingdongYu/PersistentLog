package com.sunyard.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
object LogBeanPool {

    private val poolSync = Any()
    private var pool: LogBean? = null
    private var poolSize = 0
    private val MAX_POOL_SIZE = 60

    fun get(): LogBean {
        synchronized(poolSync) {
            if (pool != null) {
                val bean = pool
                pool = bean!!.next
                bean.next = null
                poolSize--
                return bean
            }
        }
        return LogBean(next = null)
    }

    internal fun recycle(logBean: LogBean) {

        logBean.priority = 0
        logBean.tag = ""
        logBean.message = ""
        logBean.time = ""
        logBean.stackTrace = ""

        synchronized(poolSync) {
            if (poolSize < MAX_POOL_SIZE) {
                logBean.next = pool
                pool = logBean
                poolSize++
            }
        }
    }
}
