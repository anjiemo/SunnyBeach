package com.aliyun.player.alivcplayerexpand.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 下载数据库帮助类
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    public static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AliPlayerDemoDownload/" + DatabaseManager.DB_NAME;

    /**
     * 数据库版本
     */
    private static int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseHelper(context, DatabaseManager.DB_NAME, 1);
                }
            }
        }
        return mInstance;
    }

    public static DatabaseHelper getInstance(Context context, String dbPath) {
        if (mInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (mInstance == null) {
                    if (TextUtils.isEmpty(dbPath)) {
                        mInstance = new DatabaseHelper(context, dbPath, 1);
                    } else {
                        mInstance = new DatabaseHelper(context, DB_PATH, 1);
                    }

                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = DatabaseManager.CREATE_TABLE_SQL;
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
