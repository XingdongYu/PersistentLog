package com.robog.loglib;

/**
 * Created by yuxingdong on 2018/6/22.
 */
abstract class AbstractLog implements LogStrategy {

    private final PutTask mPutTask;

    AbstractLog() {
        mPutTask = new PutTask();
    }

    @Override
    public void logAsync(LogBean logBean) {
        mPutTask.setLogBean(logBean);
        Schedulers.io(mPutTask);
    }

    @Override
    public void logSync(LogBean logBean) {
        LogCache.get().put(logBean);
    }

    static class PutTask implements Runnable {

        private LogBean mLogBean;

        void setLogBean(LogBean logBean) {
            mLogBean = logBean;
        }

        @Override
        public void run() {
            LogCache.get().put(mLogBean);
        }
    }
}
