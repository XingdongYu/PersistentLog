package com.sunyard.loglib.database

import com.sunyard.loglib.LogBean

/**
 * Created by yuxingdong on 2018/6/29.
 */
interface LogBeanDao {

    val all: List<LogBean>

    fun save(logBeans: List<LogBean>)

    fun checkThreshold(): Boolean

    fun deleteAll()
}
