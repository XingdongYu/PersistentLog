package com.robog.loglib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuxingdong on 2018/6/21.
 */
public final class LogDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "slog.db";
    static final String TABLE_NAME = "log_entity";
    static final String COLUMN_ID = "id";
    static final String COLUMN_PRIORITY = "priority";
    static final String COLUMN_TAG = "tag";
    static final String COLUMN_MESSAGE = "message";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_STACK_TRACE = "stack_trace";

    private static final String SQL_CREATE_ENTRIES =
            new StringBuilder("CREATE TABLE ")
                    .append(TABLE_NAME)
                    .append(" (")
                    .append(COLUMN_ID + " INTEGER PRIMARY KEY, ")
                    .append(COLUMN_PRIORITY + " TEXT, ")
                    .append(COLUMN_TAG + " TEXT, ")
                    .append(COLUMN_MESSAGE + " TEXT, ")
                    .append(COLUMN_TIME + " TEXT, ")
                    .append(COLUMN_STACK_TRACE + " TEXT")
                    .append(" )")
                    .toString();

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
