package com.robog.persistentlog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.robog.loglib.LogMode
import com.robog.loglib.SLog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by yuxingdong on 2018/6/29.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
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
                tvCacheSize.text = String.format("缓存数据: %d", cacheSize)
                tvDbSize.text = String.format("数据库数据: %d", dbSize)
                tvFileSize.text = String.format("文件大小: %d", fileLength)
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

    override fun onResume() {
        super.onResume()
        checkHandler.post(checkRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        checkHandler.removeCallbacks(checkRunnable)
    }

    private fun initView() {
        btSwitch.setOnCheckedChangeListener { _, isChecked ->
            EXECUTOR.execute({
                SLog.changeMode(if (isChecked) LogMode.FILE else LogMode.DATABASE)
            })
        }

        btClear.setOnClickListener {
            putCount = 0
            tvPutSize.text = String.format("已存入: %d", putCount)
            SLog.clearDb()
            SLog.deleteFile()
        }

        btLog.setOnClickListener {
            putCount += 400
            tvPutSize.text = String.format("已存入: %d", putCount)
            EXECUTOR.execute({
                for (i in 0..99) {
                    LogThread().start()
                }
            })
        }

        btFlush.setOnClickListener {
            SLog.flush()
        }

        btCrash.setOnClickListener {
            throw RuntimeException("Exception by user!")
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

    internal class LogThread : Thread() {

        override fun run() {
            for (i in 0..3) {
                SLog.d(TAG, "$name Log -> $i")
            }
        }
    }

}