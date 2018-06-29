package com.sunyard.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
class LogConfig(
        val debug: Boolean = false,
        val deviceInfo: String = "",
        val dbThreshold: Int = DB_THRESHOLD,
        val cacheSize: Int = MAX_CACHE_SIZE,
        val logMode: LogMode = LogMode.DATABASE,
        val dbCheckInterval: Long = DB_CHECK_INTERVAL)
{
    companion object {
        private const val DB_THRESHOLD = 1000
        private const val MAX_CACHE_SIZE = 50
        private const val DB_CHECK_INTERVAL = 60 * 60 * 1000L
    }
}
