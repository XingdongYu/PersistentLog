package com.robog.loglib;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuxingdong on 2018/6/21.
 */
final class Util {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    @NonNull
    static LogBean wrapLogBean(String tag, String msg, int priority) {
        final LogBean logBean = LogBeanPool.get();
        logBean.priority = priority;
        logBean.tag = tag;
        // TODO: 2018/6/21 我觉得message有必要加密
        logBean.message = msg;
        logBean.time = logTime();
        logBean.stackTrace = stackTrace();
        return logBean;
    }

    private static String stackTrace() {

        final Thread currentThread = Thread.currentThread();
        final StackTraceElement[] stackTraces = currentThread.getStackTrace();
        final StringBuilder sb = new StringBuilder(currentThread.getName()).append(" ");

        for (StackTraceElement stackTrace : stackTraces) {
            if (filterSystemClass(stackTrace)) {
                continue;
            }
            sb.append(stackTrace.getClassName())
                    .append(".")
                    .append(stackTrace.getMethodName())
                    .append("\n");
        }
        return sb.toString();
    }

    private static String logTime() {
        final Date date = new Date();
        return DATE_FORMAT.format(date);
    }

    private static boolean filterSystemClass(StackTraceElement stackTrace) {
        return stackTrace.getClassName().startsWith("com.robog.loglib")
                || stackTrace.getClassName().startsWith("android.")
                || stackTrace.getClassName().startsWith("java.")
                || stackTrace.getClassName().startsWith("dalvik.")
                || stackTrace.getClassName().startsWith("com.android");
    }
}
