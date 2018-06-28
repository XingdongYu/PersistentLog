package com.robog.persistentlog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.robog.loglib.LogMode;
import com.robog.loglib.database.LogBeanDao;
import com.robog.loglib.database.LogBeanDaoImpl;
import com.robog.loglib.SLog;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int mPutCount;
    private static final ExecutorService EXECUTORS = Executors.newCachedThreadPool();
    static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "slog.txt";

    private final Handler mCheckHandler = new Handler();

    private final Runnable mCheckRunnable = new Runnable() {
        @Override
        public void run() {
            // 缓存大小
            final int cacheSize = SLog.cacheSize();

            //数据库大小
            final LogBeanDao logBeanDao = LogBeanDaoImpl.getInstance(getApplicationContext());
            final int dbSize = logBeanDao.getAll().size();

            // 文件大小
            final File file = new File(FILE_PATH);
            final long fileLength = file.exists() ? file.length() : 0;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.tv_cache_size)).setText("缓存数据: " + cacheSize);
                    ((TextView) findViewById(R.id.tv_db_size)).setText("数据库数据: " + dbSize);
                    ((TextView) findViewById(R.id.tv_file_size)).setText("文件大小: " + fileLength);
                }
            });
            mCheckHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Switch) findViewById(R.id.bt_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                EXECUTORS.execute(new Runnable() {
                    @Override
                    public void run() {
                        SLog.changeMode(isChecked ? LogMode.FILE : LogMode.DATABASE);
                    }
                });

            }
        });

        checkSelfPermission();
    }

    private void checkSelfPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    0x01);
        } else {
            //TODO
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCheckHandler.post(mCheckRunnable);
    }

    public void clear(View view) {
        mPutCount = 0;
        ((TextView) findViewById(R.id.tv_put_size)).setText("已存入: " + mPutCount);
        SLog.clearDb();
        SLog.deleteFile();
    }

    public void log(View view) {
        mPutCount += 400;
        ((TextView) findViewById(R.id.tv_put_size)).setText("已存入: " + mPutCount);
        EXECUTORS.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    new LogThread().start();
                }
            }
        });
    }

    public void flush(View view) {
        SLog.flush();
    }

    public void makeCrash(View view) {
        String msg = null;
        msg.length();
    }

    private static class LogThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 4; i++) {
                SLog.d(TAG, getName() + " Log -> " + i);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCheckHandler.removeCallbacks(mCheckRunnable);
    }
}
