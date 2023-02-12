package com.aliyun.player.alivcplayerexpand.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliyun.player.alivcplayerexpand.bean.DotBean;
import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LongVideoDatabaseManager {

    /**
     * 查询所有观看历史的视频
     */
    private static final String SELECT_ALL_WATCH_HISTORY = "select * from " + DatabaseManager.WATCH_HISTORY_TABLE_NAME;

    /**
     * 限制查看数量
     */
    private static final String SELECT_ALL_WATCH_HISTORY_LIMIT = "select * from " + DatabaseManager.WATCH_HISTORY_TABLE_NAME + " order by updatetime desc limit 20";

    /**
     * 根据vid查询观看历史
     */
    private static final String SELECT_WATCH_HISTORY_WITH_VID = "select * from " + DatabaseManager.WATCH_HISTORY_TABLE_NAME + " where vid = ? ";


    private static LongVideoDatabaseManager mInstance = null;

    private DatabaseHistoryHelper databaseHelper;
    private SQLiteDatabase mSqliteDatabase;
    private Gson mGson;

    private LongVideoDatabaseManager() {
        mGson = new Gson();
    }

    public static LongVideoDatabaseManager getInstance() {
        if (mInstance == null) {
            synchronized (DatabaseManager.class) {
                if (mInstance == null) {
                    mInstance = new LongVideoDatabaseManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 创建数据库
     */
    public void createDataBase(Context context) {
        databaseHelper = DatabaseHistoryHelper.getInstance(context);
        if (mSqliteDatabase == null) {
            synchronized (DatabaseManager.class) {
                if (mSqliteDatabase == null) {
                    mSqliteDatabase = databaseHelper.getWritableDatabase();
                }
            }
        }
    }

    public void close() {
        if (mSqliteDatabase != null) {
            mSqliteDatabase.close();
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private ContentValues longVideoBeanToContentValues(LongVideoBean longVideoBean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseManager.TITLE, longVideoBean.getTitle());
        contentValues.put(DatabaseManager.VID, longVideoBean.getVideoId());
        contentValues.put(DatabaseManager.DESCRIPTION, longVideoBean.getDescription());
        contentValues.put(DatabaseManager.DURATION, longVideoBean.getDuration());
        contentValues.put(DatabaseManager.COVERURL, longVideoBean.getCoverUrl());
        contentValues.put(DatabaseManager.STATUS, longVideoBean.getStatus());
        contentValues.put(DatabaseManager.FIRSTFRAMEURL, longVideoBean.getFirstFrameUrl());
        contentValues.put(DatabaseManager.SIZE, longVideoBean.getSize());
        contentValues.put(DatabaseManager.TAGS, longVideoBean.getTags());
        contentValues.put(DatabaseManager.TVID, longVideoBean.getTvId());
        contentValues.put(DatabaseManager.TVNAME, longVideoBean.getTvName());
        List<DotBean> dotList = longVideoBean.getDot();
        contentValues.put(DatabaseManager.DOT, mGson.toJson(dotList));
        contentValues.put(DatabaseManager.SORT, longVideoBean.getSort());
        contentValues.put(DatabaseManager.ISVIP, longVideoBean.getIsVip());
        contentValues.put(DatabaseManager.DOWNLOADING, longVideoBean.isDownloading());
        contentValues.put(DatabaseManager.DOWNLOADED, longVideoBean.isDownloaded());
        contentValues.put(DatabaseManager.WATCHPERCENT, longVideoBean.getWatchPercent());
        contentValues.put(DatabaseManager.WATCHDURATION, longVideoBean.getWatchDuration());
        contentValues.put(DatabaseManager.UPDATETIME, System.currentTimeMillis());
        return contentValues;
    }

    /**
     * 更新观看历史数据
     */
    public void updateWatchHistory(LongVideoBean longVideoBean) {
        List<LongVideoBean> longVideoBeans = selectWatchHistoryByVid(longVideoBean);
        ContentValues contentValues = longVideoBeanToContentValues(longVideoBean);
        if (longVideoBeans == null || longVideoBeans.size() <= 0) {
            insertWatchHistory(longVideoBean);
        } else {
            mSqliteDatabase.update(DatabaseManager.WATCH_HISTORY_TABLE_NAME, contentValues, "vid = ?", new String[]{longVideoBean.getVideoId()});
        }
    }

    /**
     * 插入观看历史数据
     *
     * @param longVideoBean
     */
    private void insertWatchHistory(LongVideoBean longVideoBean) {
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        ContentValues contentValues = longVideoBeanToContentValues(longVideoBean);
        mSqliteDatabase.insert(DatabaseManager.WATCH_HISTORY_TABLE_NAME, null, contentValues);
    }

    /**
     * 查询所有观看历史视频
     */
    public List<LongVideoBean> selectAllWatchHistory() {
        List<LongVideoBean> queryLists = new ArrayList<>();
        if (databaseHelper == null) {
            return queryLists;
        }
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        Cursor cursor = mSqliteDatabase.rawQuery(SELECT_ALL_WATCH_HISTORY_LIMIT, new String[]{});
        queryLists = selectAllWatchHitoryCusrorToLongVideoBean(cursor);
        cursor.close();
        return queryLists;
    }

    /**
     * 根据vid查询观看历史
     */
    public List<LongVideoBean> selectWatchHistoryByVid(LongVideoBean longVideoBean) {
        List<LongVideoBean> longVideoBeans = new ArrayList<>();
        if (mSqliteDatabase == null || !mSqliteDatabase.isOpen()) {
            mSqliteDatabase = databaseHelper.getWritableDatabase();
        }
        if (longVideoBean != null && longVideoBean.getVideoId() != null) {
            Cursor cursor = mSqliteDatabase.rawQuery(SELECT_WATCH_HISTORY_WITH_VID, new String[]{longVideoBean.getVideoId()});
            longVideoBeans = selectAllWatchHitoryCusrorToLongVideoBean(cursor);
            cursor.close();
        }
        return longVideoBeans;
    }

    private List<LongVideoBean> selectAllWatchHitoryCusrorToLongVideoBean(Cursor cursor) {
        List<LongVideoBean> queryLists = new ArrayList<>();
        if (cursor == null || cursor.getCount() <= 0) {
            if (cursor != null) {
                cursor.close();
            }
            return queryLists;
        }
        while (cursor.moveToNext()) {
            LongVideoBean longVideoBean = new LongVideoBean();
            longVideoBean.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseManager.TITLE)));
            longVideoBean.setVideoId(cursor.getString(cursor.getColumnIndex(DatabaseManager.VID)));
            longVideoBean.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseManager.DESCRIPTION)));
            longVideoBean.setCoverUrl(cursor.getString(cursor.getColumnIndex(DatabaseManager.COVERURL)));
            String duration = cursor.getString(cursor.getColumnIndex(DatabaseManager.DURATION));
            longVideoBean.setDuration(duration);
            String size = cursor.getString(cursor.getColumnIndex(DatabaseManager.SIZE));
            longVideoBean.setSize(size);
            longVideoBean.setTvId(cursor.getString(cursor.getColumnIndex(DatabaseManager.TVID)));
            longVideoBean.setFirstFrameUrl(cursor.getString(cursor.getColumnIndex(DatabaseManager.FIRSTFRAMEURL)));
            longVideoBean.setTags(cursor.getString(cursor.getColumnIndex(DatabaseManager.TAGS)));
            longVideoBean.setTvName(cursor.getString(cursor.getColumnIndex(DatabaseManager.TVNAME)));

            String jsonDotList = cursor.getString(cursor.getColumnIndex(DatabaseManager.DOT));
            List<DotBean> dotBeans = mGson.fromJson(jsonDotList, new TypeToken<List<DotBean>>() {
            }.getType());
            longVideoBean.setDot(dotBeans);

            longVideoBean.setSort(cursor.getString(cursor.getColumnIndex(DatabaseManager.SORT)));
            String isVip = cursor.getString(cursor.getColumnIndex(DatabaseManager.ISVIP));
            boolean videoIsVip;
            /*
                这里存储到数据库的值为0,1或者null
                但是Boolean不遵循0为false,非0为true的规则
             */
            if (TextUtils.isEmpty(isVip)) {
                videoIsVip = false;
            } else if ("0".equals(isVip)) {
                videoIsVip = false;
            } else {
                videoIsVip = true;
            }
            longVideoBean.setIsVip(videoIsVip);
            String isDownloading = cursor.getString(cursor.getColumnIndex(DatabaseManager.DOWNLOADING));
            longVideoBean.setDownloading(Boolean.parseBoolean(isDownloading));
            String isDownloaded = cursor.getString(cursor.getColumnIndex(DatabaseManager.DOWNLOADED));
            longVideoBean.setDownloading(Boolean.parseBoolean(isDownloaded));
            longVideoBean.setWatchDuration(cursor.getString(cursor.getColumnIndex(DatabaseManager.WATCHDURATION)));
            longVideoBean.setWatchPercent(cursor.getInt(cursor.getColumnIndex(DatabaseManager.WATCHPERCENT)));
            queryLists.add(longVideoBean);
        }
        return queryLists;
    }
}
