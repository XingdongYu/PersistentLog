package com.sunyard.loglib

import java.util.concurrent.Executors

/**
 * Created by yuxingdong on 2018/6/29.
 */
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

fun io(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}