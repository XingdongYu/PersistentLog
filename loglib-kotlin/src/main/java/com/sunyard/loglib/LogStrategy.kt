package com.sunyard.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
interface LogStrategy {
    /**
     * 将Log对象写入缓存池
     * @param logBean 自定义的Log对象
     */
    fun logAsync(logBean: LogBean)

    /**
     * 同步接口，用于crash打印
     * @param logBean 自定义的Log对象
     */
    fun logSync(logBean: LogBean)

    fun destroy()
}