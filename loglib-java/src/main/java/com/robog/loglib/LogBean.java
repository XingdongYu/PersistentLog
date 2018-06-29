package com.robog.loglib;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public class LogBean {

    public int id;

    /**
     * Log等级
     */
    public int priority;

    /**
     * Log的TAG
     */
    public String tag;

    /**
     * Log信息
     */
    public String message;

    /**
     * Log时间
     */
    public String time;

    /**
     * 堆栈信息
     */
    public String stackTrace;

    LogBean next;

    @Override
    public String toString() {
        return id + " -> " + time + " " + tag + ": " + message;
    }
}
