package com.robog.loglib

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.robog.loglib.Util.jsonToLogBean
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
        private const val FILE_SIZE = 1000
        private var instance: FileLog? = null
        val FILE_PATH = (Environment.getExternalStorageDirectory().absolutePath
                + File.separator + "slog.txt")

        fun create(): FileLog = instance ?: FileLog().also { instance = it }
    }

    init {
        LogCache.get().savable = this
    }

    @Synchronized
    override fun save(logBeans: List<LogBean>) {
        val logContent = StringBuilder()
        try {

            val file = createFile()
            val readText = file.readText()
            val allBeanList = ArrayList<LogBean>()

            if (!TextUtils.isEmpty(readText)) {

                val formerJsonArray = JSONArray(readText)
                val formerBeanList = jsonToLogBean(formerJsonArray)

                if (formerBeanList.size > FILE_SIZE) {
                    for (i in 500 until formerBeanList.size) {
                        allBeanList.add(formerBeanList[i])
                    }
                } else{
                    allBeanList.addAll(formerBeanList)
                }
            }

            allBeanList.addAll(logBeans)

            val jsonArray = buildJSONArray(allBeanList)
            logContent.append(jsonArray.toString())
            val writeData = logContent.toString()

            file.writeText(writeData)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        val f = File(FILE_PATH)
        if (!f.exists()) {
            f.createNewFile()
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