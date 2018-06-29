package com.sunyard.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
internal class LogCache {

    lateinit var savable: Savable

    companion object {

        private var instance: LogCache? = null
        val cache: ArrayList<LogBean> = ArrayList()

        fun get(): LogCache {
            return instance ?: LogCache().also { instance = it }
        }
    }

    fun put(bean: LogBean) {
        cache.add(bean)
        if (cache.size > SLog.cacheSize) {
            saveAndClean()
        }
    }

    fun flush() {
        if (cache.size > 0) {
            saveAndClean()
        }
    }

    private fun saveAndClean() {
        savable.save(cache)
        clear()
    }

    private fun clear() {
        if (cache.size > 0) {
            cache.forEach {
                LogBeanPool.recycle(it)
            }
            cache.clear()
        }
    }

}