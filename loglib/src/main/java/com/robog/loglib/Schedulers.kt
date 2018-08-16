package com.robog.loglib

import java.util.concurrent.Executors

/**
 * Created by yuxingdong on 2018/6/29.
 */
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()
private val WORK_EXECUTOR = Executors.newCachedThreadPool()

fun io(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun work(f: () -> Unit) {
    WORK_EXECUTOR.execute(f)
}