package com.robog.persistentlog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.robog.loglib.LogConfig;
import com.robog.loglib.LogMode;
import com.robog.loglib.SLog;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yuxingdong on 2018/6/25.
 */
public class ServiceTest {

    private Context mContext;

    @Test
    public void testInit() {
        mContext = InstrumentationRegistry.getContext();

        final LogConfig.Builder builder = new LogConfig.Builder();
        final LogConfig logConfig = builder
                .debug(false)
                .serviceCheckInterval(3000)
                .dbCheckInterval(2000)
                .dbThreshold(1000)
                .build();
        LogService.init(mContext, logConfig);

        Assert.assertEquals(SLog.sDebug, false);
        Assert.assertEquals(SLog.sServiceCheckInterval, 3000);
        Assert.assertEquals(SLog.sDbCheckInterval, 2000);
        Assert.assertEquals(SLog.sDbThreshold, 1000);
        Assert.assertEquals(SLog.sLogMode, LogMode.DATABASE);
    }
}
