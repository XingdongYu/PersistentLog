package com.robog.loglib;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public interface LogStrategy {

    /**
     * 将Log对象写入缓存池
     * @param logBean 自定义的Log对象
     */
    void logAsync(LogBean logBean);

    /**
     * 同步接口，用于crash打印
     * @param logBean 自定义的Log对象
     */
    void logSync(LogBean logBean);

    void destroy();
}
