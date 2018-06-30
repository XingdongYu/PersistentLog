package com.robog.loglib.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.robog.loglib.LogBean
import com.robog.loglib.LogBeanPool
import com.robog.loglib.SLog
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_ID
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_MESSAGE
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_PRIORITY
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_STACK_TRACE
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_TAG
import com.robog.loglib.database.LogDbHelper.Companion.COLUMN_TIME
import com.robog.loglib.database.LogDbHelper.Companion.TABLE_NAME
import java.util.ArrayList

/**
 * Created by yuxingdong on 2018/6/29.
 */
internal class LogBeanDaoImpl private constructor(
        context: Context
) : LogBeanDao {

    private val dbHelper = LogDbHelper(context)

    companion object {

        private const val TAG = "LogBeanDaoImpl"
        private const val ORDER_DESC = "id desc"
        private val PROJECTION = arrayOf(COLUMN_ID, COLUMN_PRIORITY,
                COLUMN_TAG, COLUMN_MESSAGE, COLUMN_TIME, COLUMN_STACK_TRACE)

        @Volatile
        private var instance: LogBeanDaoImpl? = null

        fun getInstance(context: Context): LogBeanDaoImpl =
                instance ?: synchronized(this) {
                    instance ?: LogBeanDaoImpl(context).also { instance = it }
                }
    }

    @Synchronized
    override fun getAll(): List<LogBean> {
        val beanList = ArrayList<LogBean>()
        val db = dbHelper.readableDatabase

        val c = db.query(
                TABLE_NAME, PROJECTION, null, null, null, null, ORDER_DESC)

        if (c != null && c.count > 0) {

            while (c.moveToNext()) {
                val id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID))
                val priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRIORITY))
                val tag = c.getString(c.getColumnIndexOrThrow(COLUMN_TAG))
                val message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))
                val time = c.getString(c.getColumnIndexOrThrow(COLUMN_TIME))
                val stackTrace = c.getString(c.getColumnIndexOrThrow(COLUMN_STACK_TRACE))
                val logBean = LogBeanPool.get()

                logBean.id = id
                logBean.priority = priority
                logBean.tag = tag
                logBean.message = message
                logBean.time = time
                logBean.stackTrace = stackTrace

                beanList.add(logBean)
            }
        }

        c?.close()
        db.close()
        return beanList
    }

    @Synchronized
    override fun save(logBeans: List<LogBean>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        for (logBean in logBeans) {
            saveLogBean(logBean, db)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
    }

    private fun saveLogBean(bean: LogBean, db: SQLiteDatabase) {
        val values = ContentValues()
        values.put(COLUMN_PRIORITY, bean.priority)
        values.put(COLUMN_TAG, bean.tag)
        values.put(COLUMN_MESSAGE, bean.message)
        values.put(COLUMN_TIME, bean.time)
        values.put(COLUMN_STACK_TRACE, bean.stackTrace)

        db.insert(TABLE_NAME, null, values)

    }

    @Synchronized
    override fun checkThreshold(): Boolean {
        var isHit = false
        // 1. 查询
        val queryDb = dbHelper.readableDatabase
        queryDb.beginTransaction()
        val beanList = queryThresholdData(queryDb)
        queryDb.setTransactionSuccessful()
        queryDb.endTransaction()
        queryDb.close()

        if (beanList.isNotEmpty()) {
            isHit = true
            // 2. 删除
            val deleteDb = dbHelper.writableDatabase
            deleteThresholdData(beanList!!, deleteDb)
            deleteDb.setTransactionSuccessful()
            deleteDb.endTransaction()
            deleteDb.close()
        }
        return isHit
    }


    private fun deleteThresholdData(beanList: List<LogBean>, writableDb: SQLiteDatabase) {
        writableDb.beginTransaction()
        val selection = "$COLUMN_ID LIKE ?"
        for ((id) in beanList) {
            val whereArgs = arrayOf(id.toString())
            writableDb.delete(TABLE_NAME, selection, whereArgs)
        }
    }

    private fun queryThresholdData(readableDb: SQLiteDatabase): List<LogBean> {

        var beanList = ArrayList<LogBean>()

        val c = readableDb.query(
                TABLE_NAME, PROJECTION, null, null, null, null, null)
        if (c != null && c.count > SLog.dbThreshold) {
            beanList = ArrayList()
            var count = 1
            while (c.moveToNext() && count <= c.count - SLog.dbThreshold) {

                count++

                val id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID))
                val priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRIORITY))
                val tag = c.getString(c.getColumnIndexOrThrow(COLUMN_TAG))
                val message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE))
                val time = c.getString(c.getColumnIndexOrThrow(COLUMN_TIME))
                val stackTrace = c.getString(c.getColumnIndexOrThrow(COLUMN_STACK_TRACE))
                val logBean = LogBeanPool.get()

                logBean.id = id
                logBean.priority = priority
                logBean.tag = tag
                logBean.message = message
                logBean.time = time
                logBean.stackTrace = stackTrace

                beanList.add(logBean)
            }
        }

        c?.close()
        return beanList
    }

    @Synchronized
    override fun deleteAll() {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }
}