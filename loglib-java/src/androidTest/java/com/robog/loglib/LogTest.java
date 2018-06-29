package com.robog.loglib;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.robog.loglib.database.LogBeanDao;
import com.robog.loglib.database.LogBeanDaoImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public class LogTest {

    private static final String TAG = "LogTest";

    private Context mContext;
    private LogBeanDao mLogBeanDao;

    @Before
    public void setupLogTest() {
        mContext = InstrumentationRegistry.getContext();
        mLogBeanDao = LogBeanDaoImpl.getInstance(mContext);
        SLog.init(mContext);
    }

    @Test
    public void testNotInitSLog() {
        SLog.d(TAG, "testNotConfigContext");
    }

    @Test
    public void testTime() {
        SLog.clearDb();
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i ++) {
            SLog.d(TAG, "test" + i);
        }
        Log.e(TAG, "time: " + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void testAfterThreadDbSize() {
        SLog.flush();
        Assert.assertEquals(mLogBeanDao.getAll().size(), 84);
    }

}
