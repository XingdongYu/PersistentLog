package com.robog.loglib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.robog.loglib.LogBean;
import com.robog.loglib.LogBeanPool;
import com.robog.loglib.SLog;

import java.util.ArrayList;
import java.util.List;

import static com.robog.loglib.database.LogDbHelper.COLUMN_ID;
import static com.robog.loglib.database.LogDbHelper.COLUMN_MESSAGE;
import static com.robog.loglib.database.LogDbHelper.COLUMN_PRIORITY;
import static com.robog.loglib.database.LogDbHelper.COLUMN_STACK_TRACE;
import static com.robog.loglib.database.LogDbHelper.COLUMN_TAG;
import static com.robog.loglib.database.LogDbHelper.COLUMN_TIME;
import static com.robog.loglib.database.LogDbHelper.TABLE_NAME;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public class LogBeanDaoImpl implements LogBeanDao {

    private static final String TAG = "LogBeanDaoImpl";
    private static volatile LogBeanDaoImpl INSTANCE = null;
    private static final String ORDER_DESC = "id desc";

    private static final String[] PROJECTION = {COLUMN_ID, COLUMN_PRIORITY, COLUMN_TAG,
            COLUMN_MESSAGE, COLUMN_TIME, COLUMN_STACK_TRACE};

    private final LogDbHelper mHelper;

    private LogBeanDaoImpl(Context context) {
        mHelper = new LogDbHelper(context);
    }

    public static LogBeanDaoImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LogBeanDaoImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogBeanDaoImpl(context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public synchronized void save(List<LogBean> logBeans) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransaction();
        for (LogBean logBean : logBeans) {
            saveLogBean(logBean, db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    private void saveLogBean(LogBean bean, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRIORITY, bean.priority);
        values.put(COLUMN_TAG, bean.tag);
        values.put(COLUMN_MESSAGE, bean.message);
        values.put(COLUMN_TIME, bean.time);
        values.put(COLUMN_STACK_TRACE, bean.stackTrace);

        db.insert(TABLE_NAME, null, values);

    }

    @Override
    public synchronized List<LogBean> getAll() {
        final List<LogBean> beanList = new ArrayList<>();
        final SQLiteDatabase db = mHelper.getReadableDatabase();

        final Cursor c = db.query(
                TABLE_NAME, PROJECTION, null, null, null, null, ORDER_DESC);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {

                int id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                int priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRIORITY));
                String tag = c.getString(c.getColumnIndexOrThrow(COLUMN_TAG));
                String message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String time = c.getString(c.getColumnIndexOrThrow(COLUMN_TIME));
                String stackTrace = c.getString(c.getColumnIndexOrThrow(COLUMN_STACK_TRACE));
                final LogBean logBean = LogBeanPool.get();

                logBean.id = id;
                logBean.priority = priority;
                logBean.tag = tag;
                logBean.message = message;
                logBean.time = time;
                logBean.stackTrace = stackTrace;

                beanList.add(logBean);
            }
        }

        if (c != null) {
            c.close();
        }

        db.close();
        return beanList;
    }

    @Override
    public synchronized boolean checkThreshold() {

        boolean isHit = false;
        // 1. 查询
        final SQLiteDatabase queryDb = mHelper.getReadableDatabase();
        queryDb.beginTransaction();
        final List<LogBean> beanList = queryThresholdData(queryDb);
        queryDb.setTransactionSuccessful();
        queryDb.endTransaction();
        queryDb.close();

        if (beanList != null && beanList.size() > 0) {
            isHit = true;
            // 2. 删除
            final SQLiteDatabase deleteDb = mHelper.getWritableDatabase();
            deleteThresholdData(beanList, deleteDb);
            deleteDb.setTransactionSuccessful();
            deleteDb.endTransaction();
            deleteDb.close();
        }
        return isHit;
    }

    private void deleteThresholdData(List<LogBean> beanList, SQLiteDatabase writableDb) {
        writableDb.beginTransaction();
        String selection = COLUMN_ID + " LIKE ?";
        for (LogBean logBean : beanList) {
            String[] whereArgs = {String.valueOf(logBean.id)};
            writableDb.delete(TABLE_NAME, selection, whereArgs);
        }
    }

    private List<LogBean> queryThresholdData(SQLiteDatabase readableDb) {

        List<LogBean> beanList = null;

        final Cursor c = readableDb.query(
                TABLE_NAME, PROJECTION, null, null, null, null, null);
        if (c != null && c.getCount() > SLog.sDbThreshold) {
            beanList = new ArrayList<>();
            int count = 1;
            while (c.moveToNext() && count <= c.getCount() - SLog.sDbThreshold) {

                count ++;

                int id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                int priority = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRIORITY));
                String tag = c.getString(c.getColumnIndexOrThrow(COLUMN_TAG));
                String message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String time = c.getString(c.getColumnIndexOrThrow(COLUMN_TIME));
                String stackTrace = c.getString(c.getColumnIndexOrThrow(COLUMN_STACK_TRACE));
                final LogBean logBean = LogBeanPool.get();

                logBean.id = id;
                logBean.priority = priority;
                logBean.tag = tag;
                logBean.message = message;
                logBean.time = time;
                logBean.stackTrace = stackTrace;

                beanList.add(logBean);
            }
        }

        if (c != null) {
            c.close();
        }
        return beanList;
    }

    @Override
    public synchronized void deleteAll() {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
