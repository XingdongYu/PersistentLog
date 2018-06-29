package com.sunyard.loglib

import android.support.annotation.WorkerThread

interface Savable {

    @WorkerThread
    fun save(logBeans: List<LogBean>)
}