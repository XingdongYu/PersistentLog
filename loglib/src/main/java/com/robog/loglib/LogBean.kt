package com.robog.loglib

/**
 * Created by yuxingdong on 2018/6/29.
 */
data class LogBean(
        var id: Int = 0,
        var priority: Int = 0,
        var tag: String = "",
        var message: String = "",
        var time: String = "",
        var stackTrace: String = "",
        var next: LogBean?) {

    override fun toString(): String {
        return "$id -> $time $tag: $message"
    }
}