package com.robog.loglib

import android.os.Environment
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

/**
 * Created by yuxingdong on 2018/6/29.
 * <p>
 * 将Log对象存入文件。
 * </p>
 */
internal class FileLog : AbstractLog(), Savable {

    companion object {

        private const val TAG = "FileLog"
        private const val FILE_SIZE = 5 * 1024 * 1024
        private var instance: FileLog? = null
        val FILE_PATH = (Environment.getExternalStorageDirectory().absolutePath
                + File.separator + "slog.txt")

        fun create(): FileLog = instance ?: FileLog().also { instance = it }
    }

    init {
        LogCache.get().savable = this
    }

    override fun save(logBeans: List<LogBean>) {

        val logContent = StringBuilder()

        try {
            val file = createFile(logBeans)
            file.readLines().forEach {
                logContent.append(it.replace("]", ","))
            }

            val jsonArray = buildJSONArray(logBeans)
            logContent.append(jsonArray.toString().substring(1))
            var writeData = logContent.toString()
            if (!writeData.startsWith("[")) {
                writeData = "[$writeData"
            }
            file.writeText(writeData)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    @Throws(IOException::class)
    private fun createFile(logBeans: List<LogBean>): File {
        val f = File(FILE_PATH)
        if (f.exists()) {
            val length = f.length()
            if (length > FILE_SIZE) {
                val deleteSuccess = f.delete()
                //先删除，再递归写入
                save(logBeans)
            }

        } else {
            val createSuccess = f.createNewFile()// 不存在则创建
            Log.d(TAG, "文件不存在, 创建 -> $createSuccess")
        }
        return f
    }

    @Throws(JSONException::class)
    private fun buildJSONArray(logBeans: List<LogBean>): JSONArray {
        val jsonArray = JSONArray()
        var jsonObject: JSONObject
        for ((_, priority, tag, message, time, stackTrace) in logBeans) {
            jsonObject = JSONObject()
            jsonObject.put("priority", priority)
            jsonObject.put("tag", tag)
            jsonObject.put("message", message)
            jsonObject.put("time", time)
            jsonObject.put("stackTrace", stackTrace.replace("\n".toRegex(), ""))
            jsonArray.put(jsonObject)
        }
        return jsonArray
    }

    override fun destroy() {
        instance = null
    }
}