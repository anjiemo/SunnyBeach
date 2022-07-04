package com.aliyun.player.alivcplayerexpand.util.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DownloadDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static final String TABLE_NAME = "download_cache";

    private static DownloadDBHelper helper = null;


    public static DownloadDBHelper getDownloadHelper(Context context, int version) {
        if (helper == null) {
            synchronized (DownloadDBHelper.class) {
                if (helper == null) {
                    helper = new DownloadDBHelper(context, version);
                }
            }
        }
        return helper;
    }


    private DownloadDBHelper(Context context, int version) {
        this(context, DB_NAME, null, version);
    }

    private DownloadDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table if not exists download_cache (_id integer primary key autoincrement, vid varchar(50), title varchar(50), "
                        + "cover varchar(255),  size long, progress double, quality varchar(10), state integer,  completeTime long)");

    }

    /**
     * 数据插入
     *
     * @param info  下载的信息
     * @param state 当前下载状态,  0: 下载中或已开始下载, 1:暂停, 2:完成
     */
    public long insert(AliyunDownloadMediaInfo info, int state) {
        if (hasAdded(info)) {
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("vid", info.getVid());
        values.put("title", info.getTitle());
        values.put("cover", info.getCoverUrl());
        values.put("size", info.getSize());
        values.put("progress", info.getProgress());
        values.put("quality", info.getQuality());
        values.put("state", state);
        //values.put("completeTime", );

        long insert = db.insert(TABLE_NAME, null, values);
        db.close();
        return insert;
    }

    public long insertOnly(String columnName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return 0;
    }

    private boolean hasAdded(AliyunDownloadMediaInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where vid = ? and quality = ? and size = ?",
                new String[]{info.getVid(), info.getQuality(),
                        String.valueOf(info.getSize())});
        boolean exist = cursor.moveToNext();
        cursor.close();
        return exist;
    }

    /**
     * 数据删除
     */
    public int delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int delete = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        return delete;
    }

    /**
     * 数据修改
     */
    public int update(AliyunDownloadMediaInfo info, String whereClause, String[] whereArgs, int state) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("vid", info.getVid());
        values.put("progress", info.getProgress());
        values.put("state", state);
        int update = db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        return update;
    }

    /**
     * 数据查询
     */
    public Cursor query(String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    /**
     * 数据查询, 不分组, 不排序
     *
     * @param colums
     * @param selection
     * @param selectionArgs
     */
    public Cursor query(String[] colums, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, colums, selection, selectionArgs, null, null, null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 下载状态
     */
    public interface DownloadState {
        /**
         * 下载中
         */
        int STATE_DOWNLOADING = 0;

        /**
         * 暂停
         */
        int STATE_PAUSE = 1;

        /**
         * 完成
         */
        int STATE_COMPLETE = 2;
    }
}
