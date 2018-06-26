package com.robog.loglib.database;

import com.robog.loglib.LogBean;

import java.util.List;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public interface LogBeanDao {

    void save(List<LogBean> logBeans);

    /**
     * 降序
     * @return
     */
    List<LogBean> getAll();

    boolean checkThreshold();

    void deleteAll();
}
