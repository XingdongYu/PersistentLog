package com.robog.loglib.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by yuxingdong on 2018/6/30.
 */
internal class LogDbHelper(
        context: Context
): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        internal const val DATABASE_NAME = "slog.db"

        internal const val TABLE_NAME = "log_entity"
        internal const val COLUMN_ID = "id"
        internal const val COLUMN_PRIORITY = "priority"
        internal const val COLUMN_TAG = "tag"
        internal const val COLUMN_MESSAGE = "message"
        internal const val COLUMN_TIME = "time"
        internal const val COLUMN_STACK_TRACE = "stack_trace"
        private const val SQL_CREATE_ENTRIES =
                """CREATE TABLE $TABLE_NAME
                    |($COLUMN_ID INTEGER PRIMARY KEY,
                    |$COLUMN_PRIORITY TEXT,
                    |$COLUMN_TAG TEXT,
                    |$COLUMN_MESSAGE TEXT,
                    |$COLUMN_TIME TEXT,
                    |$COLUMN_STACK_TRACE TEXT)"""

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}