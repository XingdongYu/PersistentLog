package com.robog.loglib.database

import com.robog.loglib.LogBean

/**
 * Created by yuxingdong on 2018/6/29.
 */
interface LogBeanDao {

    fun getAll(): List<LogBean>

    fun save(logBeans: List<LogBean>)

    fun checkThreshold(): Boolean

    fun deleteAll()
}
