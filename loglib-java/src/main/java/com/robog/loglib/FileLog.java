package com.robog.loglib;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by yuxingdong on 2018/6/22.
 * <p>
 * 将Log对象存入文件，若文件大小达到设定值则进行删除。
 * </p>
 */
final class FileLog extends AbstractLog implements Savable {

    private static final String TAG = "FileLog";
    private static FileLog INSTANCE = null;
    private static final int FILE_SIZE = 5 * 1024 * 1024;
    static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "slog.txt";

    public static FileLog create() {
        if (INSTANCE == null) {
            INSTANCE = new FileLog();
        }
        return INSTANCE;
    }

    private FileLog() {
        LogCache.get().setSavable(this);
    }

    @Override
    public void destroy() {
        INSTANCE = null;
    }

    @Override
    public void save(List<LogBean> logBeans) {
        // 原有内容
        String existContent;
        StringBuilder logContent = new StringBuilder();

        try {

            File f = createFile(logBeans);
            BufferedReader input = new BufferedReader(new FileReader(f));

            while ((existContent = input.readLine()) != null) {
                existContent = existContent.substring(0, existContent.length() - 1);
                logContent.append(existContent).append(",");
            }
            input.close();

            // 将原有json数组跟当前json数组进行拼接
            JSONArray jsonArray = buildJSONArray(logBeans);
            logContent.append(jsonArray.toString().substring(1));
            String writeData = logContent.toString();
            if (!writeData.startsWith("[")) {
                writeData = "[" + writeData;
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(writeData);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @NonNull
    private File createFile(List<LogBean> logBeans) throws IOException {
        final File f = new File(FILE_PATH);
        if (f.exists()) {
            long length = f.length();
            Log.d(TAG, "文件存在:" + length);
            if (length > FILE_SIZE) {

                // TODO: 2018/6/22 不能整体删除，可分多个文件
                final boolean deleteSuccess = f.delete();
                Log.d(TAG, "超过大小删除 -> " + deleteSuccess);
                //先删除，再递归写入
                save(logBeans);
            }

        } else {
            final boolean createSuccess = f.createNewFile();// 不存在则创建
            Log.d(TAG, "文件不存在, 创建 -> " + createSuccess);
        }
        return f;
    }

    @NonNull
    private JSONArray buildJSONArray(List<LogBean> logBeans) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (LogBean logBean : logBeans) {
            jsonObject = new JSONObject();
            jsonObject.put("priority", logBean.priority);
            jsonObject.put("tag", logBean.tag);
            jsonObject.put("message", logBean.message);
            jsonObject.put("time", logBean.time);
            jsonObject.put("stackTrace", logBean.stackTrace.replaceAll("\n", ""));
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
