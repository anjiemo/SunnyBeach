package com.aliyun.player.alivcplayerexpand.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类
 *
 * @author hanyu
 */
public class DatabaseManager {

    /**
     * 下载数据库名称
     */
    public static final String DB_NAME = "Player_Download.db";
    /**
     * 观看历史数据库名
     */
    public static final String HISTORY_DB_NAME = "Player_Watch_History.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "player_download_info";

    /**
     * 观看历史表名
     */
    public static final String WATCH_HISTORY_TABLE_NAME = "player_watch_history_info";

    /**
     * 准备状态
     */
    private static final int PREPARED_STATE = 1;

    /**
     * 下载中状态
     */
    private static final int DOWNLOADING_STATE = 3;

    /**
     * 停止状态
     */
    private static final int STOP_STATE = 4;

    /**
     * 完成状态
     */
    private static final int COMPLETED_STATE = 5;
    /**
     * 等待状态
     */
    private static final int WAIT_STATE = 2;
    /**
     * 未观看
     */
    private static final int NOT_WATCH = 0;
    /**
     * 已观看
     */
    private static final int HAS_WATCHED = 1;
    /**
     * 建表语句
     */
    public static final String CREATE_TABLE_SQL = "create table if not exists " + DatabaseManager.TABLE_NAME +
            " (" + DatabaseManager.ID + " integer primary key autoincrement," +
            DatabaseManager.VID + " text," + DatabaseManager.QUALITY + " text," +
            DatabaseManager.TITLE + " text," + DatabaseManager.COVERURL + " text," +
            DatabaseManager.DURATION + " text," + DatabaseManager.SIZE + " text," +
            DatabaseManager.PROGRESS + " integer," + DatabaseManager.STATUS + " integer," +
            DatabaseManager.PATH + " text," + DatabaseManager.TRACKINDEX + " integer," +
            DatabaseManager.TVID + " text," + DatabaseManager.TVNAME + " text," +
            DatabaseManager.WATCHED + " integer," + DatabaseManager.TVCOVERURL + " text," +
            DatabaseManager.VIDTYPE + " integer," +
            DatabaseManager.FORMAT + " text)";

    /**
     * 观看历史
     */
    public static final String CREATE_TABLE_SQL_WATCH_HISTORY = "create table if not exists " + WATCH_HISTORY_TABLE_NAME +
            " (" + DatabaseManager.ID + " integer primary key autoincrement," + DatabaseManager.VID + " text," +
            DatabaseManager.TITLE + " text," + DatabaseManager.COVERURL + " text," + DatabaseManager.DURATION + " text," +
            DatabaseManager.SIZE + " text," + DatabaseManager.WATCHDURATION + " integer," + DatabaseManager.TVID + " text," +
            DatabaseManager.DESCRIPTION + " text," + DatabaseManager.STATUS + " text," + DatabaseManager.FIRSTFRAMEURL + " text," +
            DatabaseManager.TAGS + " text," + DatabaseManager.TVNAME + " text," + DatabaseManager.DOT + " text," +
            DatabaseManager.SORT + " text," + DatabaseManager.ISVIP + " text," + DatabaseManager.DOWNLOADING + " text," +
            DatabaseManager.UPDATETIME + " text," +
            DatabaseManager.DOWNLOADED + " text," + DatabaseManager.WATCHPERCENT + " integer)";


    /**
     * 查询所有语句
     */
    private static final String SELECT_ALL_SQL = "select * from " + DatabaseManager.TABLE_NAME;

    /**
     * 根据tvId查询对应的电视剧
     */
    private static final String SELECT_ALL_BY_TVID = "select * from " + DatabaseManager.TABLE_NAME + " where tvid=?";

    /**
     * 根据状态查询数据
     */
    private static final String SELECT_WITH_STATUS_SQL = "select * from " + DatabaseManager.TABLE_NAME + " where status=?";

    /**
     * 查询已观看的视频
     */
    private static final String SELECT_WATCHED_SQL = "select * from " + DatabaseManager.TABLE_NAME + " where watched=?";

    public static final String ID = "id";
    public static final String VID = "vid";
    public static final String QUALITY = "quality";
    public static final String TITLE = "title";
    public static final String COVERURL = "coverurl";
    public static final String DURATION = "duration";
    public static final String SIZE = "size";
    public static final String PROGRESS = "progress";
    public static final String STATUS = "status";
    public static final String PATH = "path";
    public static final String FORMAT = "format";
    public static final String TRACKINDEX = "trackindex";
    public static final String TVID = "tvid";
    public static final String TVCOVERURL = "tvcoverurl";
    public static final String WATCHED = "watched";
    public static final String VIDTYPE = "vidtype";

    public static final String WATCHDURATION = "watchduration";
    public static final String DESCRIPTION = "description";
    public static final String FIRSTFRAMEURL = "firstframeurl";
    public static final String TAGS = "tags";
    public static final String TVNAME = "tvname";
    public static final String DOT = "dot";
    public static final String SORT = "sort";
    public static final String ISVIP = "isvip";
    public static final String DOWNLOADING = "downloading";
    public static final String DOWNLOADED = "downloaded";
    public static final String WATCHPERCENT = "watchpercent";
    public static final String UPDATETIME = "updatetime";


    private static DatabaseManager mInstance = null;

    /**
     * 下载数据库帮助类
     */
    private DatabaseHelper databaseHelper;
    /**
     * 下载数据库
     */
    private SQLiteDatabase mSqliteDatabase;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (mInstance == null) {
            synchronized (DatabaseManager.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 创建数据库
     */
    public void createDataBase(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context.getApplicationContext());
        if (mSqliteDatabase == null) {
            synchronized (DatabaseManager.class) {
                if (mSqliteDatabase == null) {
                    mSqliteDatabase = databaseHelper.getWritableDatabase();
                }
            }
        }
    }

    /**
     * 创建数据库
     */
    public void createDataBase(Context context, String dbPath) {
        databaseHelper = DatabaseHelper.getInstance(context, dbPath);
        if (mSqliteDatabase == null) {
            synchronized (DatabaseManager.class) {
                if (mSqliteDatabase == null) {
                    mSqliteDatabase = databaseHelper.getWritableDatabase();
                }
            }
        }
    }

    /**
     * 查询某条数据是否已经插入到数据库中
     */
    public int selectItemExist(AliyunDownloadMediaInfo mediaInfo) {
        Cursor cursor = mSqliteDatabase.query(TABLE_NAME, new String[]{"id"}, "vid=? and quality=?",
                new String[]{mediaInfo.getVid(), mediaInfo.getQuality()}, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public long insert(AliyunDownloadMediaInfo mediaInfo) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(VID, mediaInfo.getVid());
        contentValues.put(QUALITY, mediaInfo.getQuality());
        contentValues.put(TITLE, mediaInfo.getTitle());
        contentValues.put(FORMAT, mediaInfo.getFormat());
        contentValues.put(COVERURL, mediaInfo.getCoverUrl());
        contentValues.put(DURATION, mediaInfo.getDuration());
        contentValues.put(SIZE, mediaInfo.getSize());
        contentValues.put(PROGRESS, mediaInfo.getProgress());
        contentValues.put(STATUS, mediaInfo.getStatus().ordinal());
        contentValues.put(PATH, mediaInfo.getSavePath());
        contentValues.put(TRACKINDEX, mediaInfo.getQualityIndex());
        contentValues.put(TVID, mediaInfo.getTvId());
        contentValues.put(TVNAME, mediaInfo.getTvName());
        contentValues.put(TVCOVERURL, mediaInfo.getTvCoverUrl());
        contentValues.put(WATCHED, mediaInfo.getWatched());
        contentValues.put(VIDTYPE, mediaInfo.getVidType());
        return mSqliteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public int delete(AliyunDownloadMediaInfo mediaInfo) {
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        return mSqliteDatabase.delete(TABLE_NAME, "vid=? and quality=?",
                new String[]{mediaInfo.getVid(), mediaInfo.getQuality()});
    }

    public int update(AliyunDownloadMediaInfo mediaInfo) {
        //没有tvId那么是单集
        if (TextUtils.isEmpty(mediaInfo.getTvId())) {
            return updateByVidAndQuality(mediaInfo);
        } else {
            return updateByVid(mediaInfo);
        }
    }

    /**
     * 根据vid和quality更新清晰度
     */
    private int updateByVidAndQuality(AliyunDownloadMediaInfo mediaInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS, mediaInfo.getProgress());
        contentValues.put(STATUS, mediaInfo.getStatus().ordinal());
        contentValues.put(PATH, mediaInfo.getSavePath());
        contentValues.put(TRACKINDEX, mediaInfo.getQualityIndex());
        contentValues.put(FORMAT, mediaInfo.getFormat());
        contentValues.put(TVNAME, mediaInfo.getTvName());
        contentValues.put(WATCHED, mediaInfo.getWatched());
        /*更新清晰度*/
        return mSqliteDatabase.update(TABLE_NAME, contentValues, " vid=? and quality=?",
                new String[]{mediaInfo.getVid(), mediaInfo.getQuality()});
    }

    /**
     * 根据vid更新数据库
     */
    private int updateByVid(AliyunDownloadMediaInfo mediaInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROGRESS, mediaInfo.getProgress());
        contentValues.put(STATUS, mediaInfo.getStatus().ordinal());
        contentValues.put(PATH, mediaInfo.getSavePath());
        contentValues.put(TRACKINDEX, mediaInfo.getQualityIndex());
        contentValues.put(FORMAT, mediaInfo.getFormat());
        /*更新清晰度*/
        contentValues.put(QUALITY, mediaInfo.getQuality());
        contentValues.put(TVNAME, mediaInfo.getTvName());
        contentValues.put(WATCHED, mediaInfo.getWatched());
        return mSqliteDatabase.update(TABLE_NAME, contentValues, " vid=?",
                new String[]{mediaInfo.getVid()});
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        if (!mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        mSqliteDatabase.delete(TABLE_NAME, "", new String[]{});
    }

    /**
     * 删除指定的数据
     */
    public void deleteItem(AliyunDownloadMediaInfo mediaInfo) {
        if (!mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        mSqliteDatabase.delete(TABLE_NAME, "vid=?,quality=?", new String[]{mediaInfo.getVid(), mediaInfo.getQuality()});
    }

    /**
     * 查询已观看的数据
     */
    public List<AliyunDownloadMediaInfo> selectWatchedList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WATCHED_SQL, new String[]{HAS_WATCHED + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询所有下载中状态的数据
     */
    public List<AliyunDownloadMediaInfo> selectDownloadingList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WITH_STATUS_SQL, new String[]{DOWNLOADING_STATE + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询处于暂停状态的数据
     */
    public List<AliyunDownloadMediaInfo> selectStopedList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WITH_STATUS_SQL, new String[]{STOP_STATE + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询处于暂停状态的数据
     */
    public List<AliyunDownloadMediaInfo> selectWaitList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WITH_STATUS_SQL, new String[]{WAIT_STATE + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询所有完成状态的数据
     */
    public List<AliyunDownloadMediaInfo> selectCompletedList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WITH_STATUS_SQL, new String[]{COMPLETED_STATE + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询所有准备状态的数据
     */
    public List<AliyunDownloadMediaInfo> selectPreparedList() {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WITH_STATUS_SQL, new String[]{PREPARED_STATE + ""});
        queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 根据tvId查找相应的剧集
     */
    public List<AliyunDownloadMediaInfo> selectAllByTvId(String tvId) {
        if (!mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_ALL_BY_TVID, new String[]{tvId});
        List<AliyunDownloadMediaInfo> queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 查询所有
     */
    public List<AliyunDownloadMediaInfo> selectAll() {
        if (!mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_ALL_SQL, new String[]{});
        List<AliyunDownloadMediaInfo> queryLists = selectAllCursorToDownloadMediaInfo(cursor);
        cursor.close();
        return queryLists;
    }

    private List<AliyunDownloadMediaInfo> selectAllCursorToDownloadMediaInfo(Cursor cursor) {
        List<AliyunDownloadMediaInfo> queryLists = new ArrayList<>();
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            return queryLists;
        }
        while (cursor.moveToNext()) {
            AliyunDownloadMediaInfo mediaInfo = new AliyunDownloadMediaInfo();
            mediaInfo.setVid(cursor.getString(cursor.getColumnIndex(VID)));
            mediaInfo.setQuality(cursor.getString(cursor.getColumnIndex(QUALITY)));
            mediaInfo.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            mediaInfo.setCoverUrl(cursor.getString(cursor.getColumnIndex(COVERURL)));
            String duration = cursor.getString(cursor.getColumnIndex(DURATION));
            mediaInfo.setDuration(Long.valueOf(duration));
            String size = cursor.getString(cursor.getColumnIndex(SIZE));
            mediaInfo.setSize(Long.valueOf(size));
            mediaInfo.setProgress(cursor.getInt(cursor.getColumnIndex(PROGRESS)));
            mediaInfo.setSavePath(cursor.getString(cursor.getColumnIndex(PATH)));
            int status = cursor.getInt(cursor.getColumnIndex(STATUS));
            mediaInfo.setFormat(cursor.getString(cursor.getColumnIndex(FORMAT)));
            mediaInfo.setQualityIndex(cursor.getInt(cursor.getColumnIndex(TRACKINDEX)));
            mediaInfo.setTvId(cursor.getString(cursor.getColumnIndex(TVID)));
            mediaInfo.setTvName(cursor.getString(cursor.getColumnIndex(TVNAME)));
            mediaInfo.setTvCoverUrl(cursor.getString(cursor.getColumnIndex(TVCOVERURL)));
            mediaInfo.setWatched(cursor.getInt(cursor.getColumnIndex(WATCHED)));
            mediaInfo.setVidType(cursor.getInt(cursor.getColumnIndex(VIDTYPE)));
            switch (status) {
                case 0:
                    //Idle
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Idle);
                    break;
                case 1:
                    //prepare
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
                    break;
                case 2:
                    //wait
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Wait);
                    break;
                case 3:
                    //start
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Start);
                    break;
                case 4:
                    //stop
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
                    break;
                case 5:
                    //complete
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Complete);
                    break;
                case 6:
                    //error
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Error);
                    break;
                default:
                    mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Idle);
                    break;
            }
            queryLists.add(mediaInfo);
        }
        return queryLists;
    }

    public void close() {
        if (mSqliteDatabase != null) {
            mSqliteDatabase.close();
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
