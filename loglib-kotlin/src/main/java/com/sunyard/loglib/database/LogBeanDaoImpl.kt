package com.sunyard.loglib.database

import android.content.Context
import com.sunyard.loglib.LogBean

/**
 * Created by yuxingdong on 2018/6/29.
 */
class LogBeanDaoImpl private constructor(
        context: Context
) : LogBeanDao {

    companion object {

        @Volatile
        private var instance: LogBeanDaoImpl? = null

        fun getInstance(context: Context): LogBeanDaoImpl =
                instance ?: synchronized(this) {
                    instance ?: LogBeanDaoImpl(context).also { instance = it }
                }
    }


    override val all: List<LogBean>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun save(logBeans: List<LogBean>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkThreshold(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}