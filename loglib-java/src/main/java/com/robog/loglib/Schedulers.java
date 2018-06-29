package com.robog.loglib;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yuxingdong on 2018/6/23.
 */
final class Schedulers {

    private static final Executor IO_EXECUTOR = Executors.newSingleThreadExecutor();

    static void io(Runnable runnable) {
        IO_EXECUTOR.execute(runnable);
    }
}
