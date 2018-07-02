package com.robog.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
internal abstract class AbstractLog : LogStrategy {

    private val putTask: PutTask

    init {
        putTask = PutTask()
    }

    override fun logAsync(logBean: LogBean) {
        putTask.logBean = logBean
        io {
            putTask.run()
        }
    }

    override fun logSync(logBean: LogBean) {
        LogCache.get().put(logBean)
    }

    internal class PutTask : Runnable {

        var logBean: LogBean? = null

        override fun run() {
            LogCache.get().put(checkNotNull(logBean))
        }
    }
}
