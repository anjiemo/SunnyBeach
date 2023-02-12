package com.aliyun.player.alivcplayerexpand.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 观看历史数据库帮助类
 */
public class DatabaseHistoryHelper extends SQLiteOpenHelper {

    private static DatabaseHistoryHelper mInstance = null;

    /**
     * 数据库版本
     */
    private static int DATABASE_VERSION = 1;


    public DatabaseHistoryHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    public static DatabaseHistoryHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DatabaseHistoryHelper.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseHistoryHelper(context, DatabaseManager.HISTORY_DB_NAME, 1);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String longVideoCreateTable = DatabaseManager.CREATE_TABLE_SQL_WATCH_HISTORY;
        db.execSQL(longVideoCreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
