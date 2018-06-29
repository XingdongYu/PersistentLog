package com.robog.loglib;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yuxingdong on 2018/6/22.
 */
public final class LogMode {

    public static final int DATABASE = 0;
    public static final int FILE = 1;

    @IntDef({DATABASE, FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Check {

    }
}
