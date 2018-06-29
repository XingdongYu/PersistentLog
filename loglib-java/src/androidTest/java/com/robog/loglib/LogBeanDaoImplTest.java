package com.robog.loglib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.robog.loglib.database.LogBeanDao;
import com.robog.loglib.database.LogBeanDaoImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuxingdong on 2018/6/22.
 */
@RunWith(AndroidJUnit4.class)
public class LogBeanDaoImplTest {

    private static final String TAG = "LogBeanDaoImplTest";

    private LogBeanDao mLogBeanDao;
    private Context mContext;

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getContext();
    }

    @Test
    public void testDeleteAll() {
        mLogBeanDao = LogBeanDaoImpl.getInstance(mContext);
        mLogBeanDao.deleteAll();
    }

    @Test
    public void testSave() {
        mLogBeanDao = LogBeanDaoImpl.getInstance(mContext);

        List<LogBean> logBeans = new ArrayList<>();

        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i ++) {
            final LogBean logBean = new LogBean();
            logBeans.add(logBean);
        }
        mLogBeanDao.save(logBeans);

        Log.d(TAG, "testSave: " + (System.currentTimeMillis() - start));
        Assert.assertEquals(mLogBeanDao.getAll().size(), 10000);
    }

    @Test
    public void testDeleteOutOfThreshold() {
        mLogBeanDao = LogBeanDaoImpl.getInstance(mContext);
        mLogBeanDao.checkThreshold();

        Assert.assertEquals(mLogBeanDao.getAll().size(), 0);

    }

    @Test
    public void testMutipleThread() {
        mLogBeanDao = LogBeanDaoImpl.getInstance(mContext);

        for (int i = 0; i < 100; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<LogBean> logBeans = new ArrayList<>();
                    logBeans.add(new LogBean());
                    mLogBeanDao.save(logBeans);
                }
            }).start();
        }

    }
}