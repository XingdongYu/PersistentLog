package com.robog.persistentlog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.robog.loglib.LogMode
import com.robog.loglib.SLog
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by yuxingdong on 2018/6/29.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        private val EXECUTOR = Executors.newCachedThreadPool()
    }

    private var putCount = 0
    private val checkHandler = Handler()
    private val checkRunnable = object : Runnable {
        override fun run() {
            val cacheSize = SLog.cacheSize()
            val dbSize = SLog.getAllLog().size

            val file = File(Environment.getExternalStorageDirectory().absolutePath
                    + File.separator + "slog.txt")
            val fileLength = file.length()

            runOnUiThread {
                findViewById<TextView>(R.id.tv_cache_size).text = "缓存数据: $cacheSize"
                findViewById<TextView>(R.id.tv_db_size).text = "数据库数据: $dbSize"
                findViewById<TextView>(R.id.tv_file_size).text = "文件大小: $fileLength"
            }
            checkHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        checkSelfPermission()
    }

    private fun initView() {
        (findViewById<View>(R.id.bt_switch) as Switch).setOnCheckedChangeListener { buttonView, isChecked ->
            EXECUTOR.execute({
                SLog.changeMode(if (isChecked) LogMode.FILE else LogMode.DATABASE)
            })
        }
    }

    private fun checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    0x01)
        } else {
            //TODO
        }
    }


    fun clear(view: View) {
        putCount = 0
        (findViewById<View>(R.id.tv_put_size) as TextView).text = "已存入: $putCount"
        SLog.clearDb()
        SLog.deleteFile()
    }

    fun log(view: View) {
        putCount += 400
        (findViewById<View>(R.id.tv_put_size) as TextView).text = "已存入: $putCount"
        EXECUTOR.execute({
            for (i in 0..99) {
                LogThread().start()
            }
        }
        )
    }

    fun flush(view: View) {
        SLog.flush()
    }

    fun makeCrash(view: View) {
        throw RuntimeException("Exception by user!")
    }

    private class LogThread : Thread() {

        override fun run() {
            for (i in 0..3) {
                SLog.d(TAG, "$name Log -> $i")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkHandler.post(checkRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        checkHandler.removeCallbacks(checkRunnable)
    }
}