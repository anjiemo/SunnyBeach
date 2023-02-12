package com.aliyun.player.alivcplayerexpand.util.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.downloader.AliDownloaderFactory;
import com.aliyun.downloader.AliMediaDownloader;
import com.aliyun.downloader.DownloaderConfig;
import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.listener.RefreshStsCallback;
import com.aliyun.player.alivcplayerexpand.util.VidStsUtil;
import com.aliyun.player.alivcplayerexpand.util.database.DatabaseManager;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbDatasListener;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbTvListDatasListenerr;
import com.aliyun.player.alivcplayerexpand.util.database.LongVideoDatabaseManager;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.svideo.common.utils.ThreadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 下载管理
 */
public class AliyunDownloadManager {

    public static final String TAG = "AliyunDownloadManager";

    public static final String MEMORY_LESS_MSG = "memory_less";

    public static final int INTENT_STATE_START = 0;
    public static final int INTENT_STATE_STOP = 1;
    public static final int INTENT_STATE_ADD = 2;

    private static final int MAX_NUM = 5;
    private static final int MIN_NUM = 1;

    public static final int VID_STS = 0;
    public static final int VID_AUTH = 1;
    /**
     * 并行下载最大数量,默认3
     */
    private int mMaxNum = 3;
    /**
     * 下载路径
     */
    private String downloadDir;
    /**
     * 加密文件路径
     */
    private String encryptFilePath;

    /**
     * AliyunDownloadManager 单例
     */
    private static volatile AliyunDownloadManager mInstance = null;

    /**
     * AliyunDownloadMediaInfo和AliMediaDownloader 一一 对应
     */
    private LinkedHashMap<AliyunDownloadMediaInfo, AliMediaDownloader> downloadInfos = new LinkedHashMap<>();

    /**
     * 用于保存处于准备状态的数据
     */
    private ConcurrentLinkedQueue<AliyunDownloadMediaInfo> preparedList = new ConcurrentLinkedQueue<>();

    /**
     * 用于保存处于下载中的状态的数据
     */
    private ConcurrentLinkedQueue<AliyunDownloadMediaInfo> downloadingList = new ConcurrentLinkedQueue<>();

    /**
     * 用于保存下载完成状态的数据
     */
    private ConcurrentLinkedQueue<AliyunDownloadMediaInfo> completedList = new ConcurrentLinkedQueue<>();

    /**
     * 用于保存暂停状态的数据
     */
    private ConcurrentLinkedQueue<AliyunDownloadMediaInfo> waitedList = new ConcurrentLinkedQueue<>();

    /**
     * 用于保存停止状态的数据
     */
    private ConcurrentLinkedQueue<AliyunDownloadMediaInfo> stopedList = new ConcurrentLinkedQueue<>();

    /**
     * 对外接口回调
     */
    private List<AliyunDownloadInfoListener> outListenerList = new ArrayList<>();

    /**
     * 数据库管理类
     */
    private DatabaseManager mDatabaseManager;

    private LongVideoDatabaseManager mLongVideoDatabaseManager;
    /**
     * 剩余内存
     */
    private long freshStorageSizeTime = 0;
    /**
     * 保存Downloader防止循环创建时,导致内存不足被回收,无法回调的问题
     */
    private List<AliMediaDownloader> mJniDownloadLists = new ArrayList<>();

    private Context mContext;

    /**
     * vidAuth方式下载
     */
    private VidAuth mVidAuth;

    /**
     * vidSts方式下载
     */
    private VidSts mVidSts;

    /**
     * 下载配置
     */
    private DownloaderConfig mDownloaderConfig = new DownloaderConfig();

    /**
     * 内部接口回调
     */
    private AliyunDownloadInfoListener innerDownloadInfoListener = new AliyunDownloadInfoListener() {

        @Override
        public void onPrepared(final List<AliyunDownloadMediaInfo> infos) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onPrepared(infos);
                    }
                }
            });

        }

        @Override
        public void onAdd(final AliyunDownloadMediaInfo info) {
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    prepareMediaInfo(info);
                    List<AliyunDownloadMediaInfo> downloadMediaInfos = mDatabaseManager.selectAll();
                    if (downloadMediaInfos.contains(info)) {
                        int update = mDatabaseManager.update(info);
                    } else {
                        mDatabaseManager.insert(info);
                    }

                }
            });
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onAdd(info);
                    }
                }
            });

        }

        @Override
        public void onStart(final AliyunDownloadMediaInfo info) {
            startMediaInfo(info);
            //在子线程中更新数据库
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    List<AliyunDownloadMediaInfo> downloadMediaInfos = mDatabaseManager.selectAll();

                    boolean hasContains = false;
                    //单集
                    if (TextUtils.isEmpty(info.getTvId())) {
                        for (AliyunDownloadMediaInfo downloadMediaInfo : downloadMediaInfos) {
                            hasContains = judgeEquals(downloadMediaInfo, info);
                            if (hasContains) {
                                break;
                            }
                        }
                    } else {
                        hasContains = downloadMediaInfos.contains(info);
                    }
                    if (hasContains) {
                        int update = mDatabaseManager.update(info);
                        //如果更新失败的话，代表数据库里面有这个Vid，但是没有这个清晰度的视频,
                    } else {
                        mDatabaseManager.insert(info);
                    }

                }
            });
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onStart(info);
                    }
                }
            });

        }

        @Override
        public void onProgress(final AliyunDownloadMediaInfo info, final int percent) {
            //在子线程中更新数据库
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    if (freshStorageSizeTime == 0 || ((new Date()).getTime() - freshStorageSizeTime) > 2 * 1000) {

                        int update = mDatabaseManager.update(info);

                        if (DownloadUtils.isStorageAlarm(mContext)) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopDownloads(downloadingList);
                                    stopDownloads(waitedList);
                                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                                        aliyunDownloadInfoListener.onError(info, ErrorCode.ERROR_UNKNOWN_ERROR, MEMORY_LESS_MSG, null);
                                    }
                                }
                            });
                        }
                        freshStorageSizeTime = (new Date()).getTime();
                    }
                }
            });

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        info.setStatus(AliyunDownloadMediaInfo.Status.Start);
                        aliyunDownloadInfoListener.onProgress(info, percent);
                    }
                }
            });

        }

        @Override
        public void onWait(final AliyunDownloadMediaInfo outMediaInfo) {
            waitMediaInfo(outMediaInfo);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onWait(outMediaInfo);
                    }
                }
            });

        }

        @Override
        public void onDelete(final AliyunDownloadMediaInfo info) {
            deleteMediaInfo(info);
            mDatabaseManager.delete(info);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onDelete(info);
                    }
                }
            });

        }

        @Override
        public void onDeleteAll() {
            deleteAllMediaInfo();
            mDatabaseManager.deleteAll();
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onDeleteAll();
                    }
                }
            });

        }

        @Override
        public void onFileProgress(final AliyunDownloadMediaInfo mediaInfo) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.File);
                        aliyunDownloadInfoListener.onFileProgress(mediaInfo);
                    }
                }
            });
        }

        @Override
        public void onStop(final AliyunDownloadMediaInfo info) {
            stopMediaInfo(info);
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    int update = mDatabaseManager.update(info);

                }
            });
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onStop(info);
                    }
                }
            });

        }

        @Override
        public void onCompletion(final AliyunDownloadMediaInfo info) {
            completedMediaInfo(info);
            AliMediaDownloader jniDownloader = downloadInfos.get(info);
            if (jniDownloader == null) {
                return;
            }
            info.setSavePath(jniDownloader.getFilePath());

            int update = mDatabaseManager.update(info);

            for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                aliyunDownloadInfoListener.onCompletion(info);
            }
        }

        @Override
        public void onError(final AliyunDownloadMediaInfo info, final ErrorCode code, final String msg, final String requestId) {
            errorMediaInfo(info, code, msg);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (AliyunDownloadInfoListener aliyunDownloadInfoListener : outListenerList) {
                        aliyunDownloadInfoListener.onError(info, code, msg, requestId);
                    }
                }
            });
        }

    };

    private AliyunDownloadManager(Context context) {
        this.mContext = context;
        mDatabaseManager = DatabaseManager.getInstance();
        mLongVideoDatabaseManager = LongVideoDatabaseManager.getInstance();
        if (!TextUtils.isEmpty(downloadDir)) {
            File file = new File(downloadDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public static AliyunDownloadManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AliyunDownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new AliyunDownloadManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置DownloaderConfig
     */
    public void setDownloaderConfig(DownloaderConfig downloaderConfig) {
        this.mDownloaderConfig = downloaderConfig;
    }

    /**
     * 获取DownloaderConfig
     */
    public DownloaderConfig getDownloaderConfig() {
        return mDownloaderConfig;
    }

    /**
     * 设置下载对外监听
     */
    public void setDownloadInfoListener(AliyunDownloadInfoListener listener) {
        this.outListenerList.clear();
        if (listener != null) {
            this.outListenerList.add(listener);
        }
    }

    /**
     * 添加下载对外监听
     */
    public void addDownloadInfoListener(AliyunDownloadInfoListener listener) {
        if (this.outListenerList == null) {
            this.outListenerList = new ArrayList<>();
        }
        if (listener != null && !this.outListenerList.contains(listener)) {
            this.outListenerList.add(listener);
        }
    }

    /**
     * 判断两个MediaInfo是否属于同一资源
     */
    private boolean judgeEquals(AliyunDownloadMediaInfo mediaInfo1, AliyunDownloadMediaInfo mediaInfo2) {
        if (mediaInfo1 == null || mediaInfo2 == null) {
            return false;
        }

        if (!TextUtils.isEmpty(mediaInfo1.getVid()) && mediaInfo1.getVid().equals(mediaInfo2.getVid())
                && !TextUtils.isEmpty(mediaInfo1.getQuality()) && mediaInfo1.getQuality().equals(mediaInfo2.getQuality())
                && !TextUtils.isEmpty(mediaInfo1.getFormat()) && mediaInfo1.getFormat().equals(mediaInfo2.getFormat())) {

            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置最大并行下载行数
     */
    public int getMaxNum() {
        return mMaxNum;
    }

    /**
     * 设置并行下载行数
     * 最小为0,最大为5
     */
    public void setMaxNum(int mMaxNum) {
        if (mMaxNum <= MIN_NUM) {
            mMaxNum = MIN_NUM;
        }
        if (mMaxNum > MAX_NUM) {
            mMaxNum = MAX_NUM;
        }
        this.mMaxNum = mMaxNum;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public String getEncryptFilePath() {
        return encryptFilePath;
    }

    public void setEncryptFilePath(String encryptFilePath) {
        if (TextUtils.isEmpty(encryptFilePath)) {
            return;
        }
        this.encryptFilePath = encryptFilePath;
    }

    /**
     * 准备下载项
     * 用于从数据库查询出数据后，恢复数据展示
     */
    private void prepareDownload(VidSts vidSts, final List<AliyunDownloadMediaInfo> mediaInfos) {
        if (vidSts == null || mediaInfos == null) {
            return;
        }
        for (final AliyunDownloadMediaInfo aliyunDownloadMediaInfo : mediaInfos) {
            vidSts.setVid(aliyunDownloadMediaInfo.getVid());
            aliyunDownloadMediaInfo.setVidSts(vidSts);
            //修改成wait状态
            if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Start ||
                    aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Prepare) {
                aliyunDownloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
            }
            final AliMediaDownloader jniDownloader = AliDownloaderFactory.create(mContext);
            jniDownloader.setSaveDir(downloadDir);
            jniDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
                @Override
                public void onPrepared(MediaInfo mediaInfo) {
                    if (downloadInfos != null && mediaInfo.getVideoId().equals(aliyunDownloadMediaInfo.getVid())) {
                        List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                        for (TrackInfo trackInfo : trackInfos) {
                            if (trackInfo != null && trackInfo.getVodDefinition().equals(aliyunDownloadMediaInfo.getQuality())) {
                                //AliyunDownloadMediaInfo 与 AliMediaDownloader 相对应
                                aliyunDownloadMediaInfo.setTrackInfo(trackInfo);
                                downloadInfos.put(aliyunDownloadMediaInfo, jniDownloader);
                            }
                        }
                    }
                }
            });

            setErrorListener(jniDownloader, aliyunDownloadMediaInfo);

            jniDownloader.setDownloaderConfig(mDownloaderConfig);
            jniDownloader.prepare(vidSts);
        }
    }

    /**
     * 添加下载项
     */
    public void addDownload(VidSts vidSts, final AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
        if (vidSts == null || aliyunDownloadMediaInfo == null) {
            return;
        }
        if (preparedList.contains(aliyunDownloadMediaInfo) || stopedList.contains(aliyunDownloadMediaInfo)
                || waitedList.contains(aliyunDownloadMediaInfo) || downloadingList.contains(aliyunDownloadMediaInfo)
                || completedList.contains(aliyunDownloadMediaInfo)) {
            return;
        }
        vidSts.setVid(aliyunDownloadMediaInfo.getVid());
        aliyunDownloadMediaInfo.setVidSts(vidSts);
        AliMediaDownloader jniDownloader = downloadInfos.get(aliyunDownloadMediaInfo);
        if (jniDownloader == null || aliyunDownloadMediaInfo.getTrackInfo() == null) {
            prepareDownloadByQuality(aliyunDownloadMediaInfo, INTENT_STATE_ADD);
        } else {
            jniDownloader.setSaveDir(downloadDir);
            jniDownloader.selectItem(aliyunDownloadMediaInfo.getTrackInfo().getIndex());
            if (innerDownloadInfoListener != null) {
                innerDownloadInfoListener.onAdd(aliyunDownloadMediaInfo);
            }

            setErrorListener(jniDownloader, aliyunDownloadMediaInfo);
        }
    }

    /**
     * 准备下载项目
     */
    public void prepareDownloadByLongVideoBean(final VidSts vidSts, final LongVideoBean longVideoBean) {
        if (vidSts == null || TextUtils.isEmpty(vidSts.getVid())) {
            return;
        }
        final List<AliyunDownloadMediaInfo> downloadMediaInfos = new ArrayList<>();
        final AliMediaDownloader jniDownloader = AliDownloaderFactory.create(mContext);
        //调用prepared监听,获取该vid下所有的清晰度
        jniDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                onPreparedCallback(vidSts, mediaInfo, downloadMediaInfos, longVideoBean);
                if (innerDownloadInfoListener != null) {
                    //这里回调只为了展示可下载的选项
                    innerDownloadInfoListener.onPrepared(downloadMediaInfos);
                }
                mJniDownloadLists.remove(jniDownloader);
            }
        });
        jniDownloader.setOnErrorListener(new AliMediaDownloader.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                if (innerDownloadInfoListener != null) {
                    AliyunDownloadMediaInfo mediaInfo = new AliyunDownloadMediaInfo();
                    mediaInfo.setVidSts(vidSts);
                    innerDownloadInfoListener.onError(mediaInfo, errorInfo.getCode(), errorInfo.getMsg(), null);
                    mJniDownloadLists.remove(jniDownloader);
                    jniDownloader.release();
                }
            }
        });

        jniDownloader.setDownloaderConfig(mDownloaderConfig);
        jniDownloader.prepare(vidSts);
        mJniDownloadLists.add(jniDownloader);
    }


    /**
     * 准备下载项目
     */
    public void prepareDownloadLists(final VidSts vidSts, AliyunDownloadMediaInfo mediaInfo) {
        if (vidSts == null || TextUtils.isEmpty(vidSts.getVid())) {
            return;
        }

        final List<AliyunDownloadMediaInfo> downloadMediaInfos = new ArrayList<>();
        AliMediaDownloader jniDownloader = downloadInfos.get(mediaInfo);
        if (jniDownloader != null) {
            return;
        } else {
            jniDownloader = AliDownloaderFactory.create(mContext);
        }

        //调用prepared监听,获取该vid下所有的清晰度
        jniDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                onPreparedCallback(vidSts, mediaInfo, downloadMediaInfos, null);

                if (innerDownloadInfoListener != null) {
                    //这里回调只为了展示可下载的选项
                    innerDownloadInfoListener.onPrepared(downloadMediaInfos);
                }
            }
        });

        mediaInfo.setVidSts(vidSts);
        setErrorListener(jniDownloader, mediaInfo);

        jniDownloader.setDownloaderConfig(mDownloaderConfig);
        jniDownloader.prepare(vidSts);
    }

    /**
     * 准备下载项目
     */
    public void prepareDownload(final VidSts vidSts) {
        if (vidSts == null || TextUtils.isEmpty(vidSts.getVid())) {
            return;
        }
        this.mVidSts = vidSts;

        final List<AliyunDownloadMediaInfo> downloadMediaInfos = new ArrayList<>();
        final AliMediaDownloader jniDownloader = AliDownloaderFactory.create(mContext);

        //调用prepared监听,获取该vid下所有的清晰度
        jniDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                onPreparedCallback(vidSts, mediaInfo, downloadMediaInfos, null);

                if (innerDownloadInfoListener != null) {
                    //这里回调只为了展示可下载的选项
                    innerDownloadInfoListener.onPrepared(downloadMediaInfos);
                }
                mJniDownloadLists.remove(jniDownloader);
            }
        });
        AliyunDownloadMediaInfo mediaInfo = new AliyunDownloadMediaInfo();
        mediaInfo.setVidSts(vidSts);
        setErrorListener(jniDownloader, mediaInfo);

        jniDownloader.setDownloaderConfig(mDownloaderConfig);
        jniDownloader.prepare(vidSts);
        mJniDownloadLists.add(jniDownloader);
    }

    /**
     * 准备下载项目
     */
    public void prepareDownload(final VidAuth vidAuth) {
        if (vidAuth == null || TextUtils.isEmpty(vidAuth.getVid())) {
            return;
        }
        this.mVidAuth = vidAuth;
        final List<AliyunDownloadMediaInfo> downloadMediaInfos = new ArrayList<>();
        final AliMediaDownloader downloader = AliDownloaderFactory.create(mContext);
        //调用prepared监听,获取该vid下所有的清晰度
        downloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                for (TrackInfo trackInfo : trackInfos) {
                    TrackInfo.Type type = trackInfo.getType();
                    if (type == TrackInfo.Type.TYPE_VOD) {
//                        //一个AliMediaDownloader 对应多个 AliyunDownloaderMediaInfo(同一Vid,不同清晰度)
                        final AliyunDownloadMediaInfo downloadMediaInfo = new AliyunDownloadMediaInfo();
                        downloadMediaInfo.setVid(vidAuth.getVid());
                        downloadMediaInfo.setQuality(trackInfo.getVodDefinition());
                        downloadMediaInfo.setTitle(mediaInfo.getTitle());
                        downloadMediaInfo.setCoverUrl(mediaInfo.getCoverUrl());
                        downloadMediaInfo.setDuration(mediaInfo.getDuration());
                        downloadMediaInfo.setTrackInfo(trackInfo);
                        downloadMediaInfo.setQualityIndex(trackInfo.getIndex());
                        downloadMediaInfo.setFormat(trackInfo.getVodFormat());
                        downloadMediaInfo.setSize(trackInfo.getVodFileSize());
                        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
                        downloadMediaInfo.setVidAuth(vidAuth);
                        downloadMediaInfo.setVidType(VID_AUTH);
                        downloadMediaInfos.add(downloadMediaInfo);

                        AliMediaDownloader itemAliMediaDownloader = AliDownloaderFactory.create(mContext);
                        itemAliMediaDownloader.setSaveDir(downloadDir);

                        downloadInfos.put(downloadMediaInfo, itemAliMediaDownloader);
                    }
                }
                if (innerDownloadInfoListener != null) {
                    //这里回调只为了展示可下载的选项
                    innerDownloadInfoListener.onPrepared(downloadMediaInfos);
                }
            }
        });
        setErrorListener(downloader, null);

        downloader.setDownloaderConfig(mDownloaderConfig);
        downloader.prepare(vidAuth);
    }

    private void onPreparedCallback(VidSts vidSts, MediaInfo mediaInfo, List<AliyunDownloadMediaInfo> downloadMediaInfos, LongVideoBean longVideoBean) {
        List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
        for (TrackInfo trackInfo : trackInfos) {
            TrackInfo.Type type = trackInfo.getType();
            if (type == TrackInfo.Type.TYPE_VOD) {
//                        //一个AliMediaDownloader 对应多个 AliyunDownloaderMediaInfo(同一Vid,不同清晰度)
                VidSts mVidSts = new VidSts();

                mVidSts.setVid(mediaInfo.getVideoId());
                mVidSts.setAccessKeyId(vidSts.getAccessKeyId());
                mVidSts.setSecurityToken(vidSts.getSecurityToken());
                mVidSts.setAccessKeySecret(vidSts.getAccessKeySecret());

                final AliyunDownloadMediaInfo downloadMediaInfo = new AliyunDownloadMediaInfo();
                downloadMediaInfo.setVid(mediaInfo.getVideoId());
                downloadMediaInfo.setQuality(trackInfo.getVodDefinition());
                downloadMediaInfo.setTitle(mediaInfo.getTitle());
                downloadMediaInfo.setCoverUrl(mediaInfo.getCoverUrl());
                downloadMediaInfo.setDuration(mediaInfo.getDuration());
                downloadMediaInfo.setTrackInfo(trackInfo);
                downloadMediaInfo.setQualityIndex(trackInfo.getIndex());
                downloadMediaInfo.setFormat(trackInfo.getVodFormat());
                downloadMediaInfo.setSize(trackInfo.getVodFileSize());
                downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
                downloadMediaInfo.setVidType(VID_STS);
                downloadMediaInfo.setVidSts(mVidSts);
                if (longVideoBean != null) {
                    downloadMediaInfo.setTvId(longVideoBean.getTvId());
                    downloadMediaInfo.setTvName(longVideoBean.getTvName());
                    downloadMediaInfo.setTvCoverUrl(longVideoBean.getTvCoverUrl());
                }

                downloadMediaInfos.add(downloadMediaInfo);

                AliMediaDownloader aliMediaDownloader = downloadInfos.get(downloadMediaInfo);
                if (aliMediaDownloader == null) {
                    aliMediaDownloader = AliDownloaderFactory.create(mContext);
                }
                aliMediaDownloader.setSaveDir(downloadDir);

                downloadInfos.put(downloadMediaInfo, aliMediaDownloader);
            }
        }
    }

    /**
     * 准备下载项(指定清晰度)
     */
    public void prepareDownloadByQuality(final AliyunDownloadMediaInfo downloadMediaInfo, final int intentState) {
        if (downloadMediaInfo == null) {
            return;
        }
        final List<AliyunDownloadMediaInfo> downloadMediaInfos = new ArrayList<>();
        final AliMediaDownloader jniDownloader = AliDownloaderFactory.create(mContext);
        jniDownloader.setSaveDir(downloadDir);
        //调用prepared监听,获取该vid下所有的清晰度
        jniDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                for (TrackInfo trackInfo : trackInfos) {
                    TrackInfo.Type type = trackInfo.getType();
                    if (type == TrackInfo.Type.TYPE_VOD && trackInfo.getVodDefinition().equals(downloadMediaInfo.getQuality())) {
//                        //一个AliMediaDownloader 对应多个 AliyunDownloaderMediaInfo(同一Vid,不同清晰度)
                        downloadMediaInfo.setQuality(trackInfo.getVodDefinition());
                        downloadMediaInfo.setTitle(mediaInfo.getTitle());
                        downloadMediaInfo.setCoverUrl(mediaInfo.getCoverUrl());
                        downloadMediaInfo.setDuration(mediaInfo.getDuration());
                        downloadMediaInfo.setTrackInfo(trackInfo);
                        downloadMediaInfo.setQualityIndex(trackInfo.getIndex());
                        downloadMediaInfo.setFormat(trackInfo.getVodFormat());
                        downloadMediaInfo.setSize(trackInfo.getVodFileSize());
                        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
                        downloadMediaInfos.add(downloadMediaInfo);

                        downloadInfos.put(downloadMediaInfo, jniDownloader);

                        jniDownloader.selectItem(trackInfo.getIndex());
                        if (intentState == INTENT_STATE_START) {
                            if (downloadingList.size() <= mMaxNum) {
                                //开始下载
                                setListener(downloadMediaInfo, jniDownloader);
                                jniDownloader.start();
                                if (innerDownloadInfoListener != null) {
                                    innerDownloadInfoListener.onStart(downloadMediaInfo);
                                }
                            } else {
                                if (innerDownloadInfoListener != null) {
                                    innerDownloadInfoListener.onWait(downloadMediaInfo);
                                }
                            }

                        } else if (intentState == INTENT_STATE_STOP) {
                            //删除下载
                            executeDelete(downloadMediaInfo);
                        } else {
                            //添加下载项
                            jniDownloader.setSaveDir(downloadDir);
                            jniDownloader.selectItem(downloadMediaInfo.getTrackInfo().getIndex());
                            if (innerDownloadInfoListener != null) {
                                innerDownloadInfoListener.onAdd(downloadMediaInfo);
                            }

                            setErrorListener(jniDownloader, downloadMediaInfo);
                        }

                    }
                }
            }
        });
        setErrorListener(jniDownloader, null);

        jniDownloader.setDownloaderConfig(mDownloaderConfig);
        if (downloadMediaInfo.getVidType() == VID_STS) {
            if (downloadMediaInfo.getVidSts() == null) {
                mVidSts.setVid(downloadMediaInfo.getVid());
                jniDownloader.prepare(mVidSts);
            } else {
                jniDownloader.prepare(downloadMediaInfo.getVidSts());
            }
        } else {
            if (downloadMediaInfo.getVidAuth() == null) {
                mVidAuth.setVid(downloadMediaInfo.getVid());
                jniDownloader.prepare(mVidAuth);
            } else {
                jniDownloader.prepare(downloadMediaInfo.getVidAuth());
            }
        }

    }

    /**
     * 开始下载
     */
    public void startDownload(final AliyunDownloadMediaInfo downloadMediaInfo) {
        if (downloadMediaInfo == null) {
            return;
        }
        AliyunDownloadMediaInfo.Status status = downloadMediaInfo.getStatus();
        if (status == AliyunDownloadMediaInfo.Status.Start
                || downloadingList.contains(downloadMediaInfo)) {
            return;
        }
        if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete) {
            String savePath = downloadMediaInfo.getSavePath();
            File file = new File(savePath);
            if (file.exists()) {
                Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.alivc_video_download_finish_tips), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //直接开始下载
        //判断磁盘空间是否足够
        if (DownloadUtils.isStorageAlarm(mContext, downloadMediaInfo)) {
            //判断要下载的mediaInfo的当前状态

            if (downloadingList.size() < mMaxNum) {
                AliMediaDownloader jniDownloader = downloadInfos.get(downloadMediaInfo);
                if (jniDownloader == null) {
                    jniDownloader = AliDownloaderFactory.create(mContext);
                    jniDownloader.setSaveDir(downloadDir);
                    downloadInfos.put(downloadMediaInfo, jniDownloader);
                }
                jniDownloader.selectItem(downloadMediaInfo.getQualityIndex());
                setListener(downloadMediaInfo, jniDownloader);
                if (downloadMediaInfo.getVidType() == VID_AUTH) {
                    if (downloadMediaInfo.getVidAuth() == null) {
                        if (innerDownloadInfoListener != null) {
                            innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN, mContext.getString(R.string.alivc_player_video_download_sts_and_auth_is_empty), "");
                        }
                        return;
                    }
                    jniDownloader.updateSource(downloadMediaInfo.getVidAuth());
                } else {
                    if (mVidSts == null) {
                        if (innerDownloadInfoListener != null) {
                            innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN, mContext.getString(R.string.alivc_player_video_download_sts_and_auth_is_empty), "");
                        }
                        return;
                    }
                    mVidSts.setVid(downloadMediaInfo.getVid());
                    jniDownloader.updateSource(mVidSts);
                }
                jniDownloader.start();
                if (innerDownloadInfoListener != null) {
                    innerDownloadInfoListener.onStart(downloadMediaInfo);
                }
            } else {
//                    防止重复添加
                if (!waitedList.contains(downloadMediaInfo) && innerDownloadInfoListener != null) {
                    innerDownloadInfoListener.onWait(downloadMediaInfo);
                }
            }
        } else {
            if (innerDownloadInfoListener != null) {
                innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN_ERROR, MEMORY_LESS_MSG, null);
            }
        }
    }

    /**
     * 暂停下载
     * 和 stopDownload 类似，只是不调用release
     */
    public void pauseDownload(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (downloadMediaInfo == null || downloadInfos == null) {
            return;
        }
        if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete ||
                downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Error
                || downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {
            return;

        }
        AliMediaDownloader jniDownloader = downloadInfos.get(downloadMediaInfo);
        if (jniDownloader == null) {
            return;
        }
        jniDownloader.stop();
        if (innerDownloadInfoListener != null) {
            innerDownloadInfoListener.onStop(downloadMediaInfo);
        }
        autoDownload();
    }

    /**
     * 停止下载
     */
    public void stopDownload(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (downloadMediaInfo == null || downloadInfos == null) {
            return;
        }
        if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete ||
                downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Error
                || downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {
            return;

        }
        AliMediaDownloader jniDownloader = downloadInfos.get(downloadMediaInfo);
        if (jniDownloader == null) {
            return;
        }
        jniDownloader.stop();
        releaseJniDownloader(downloadMediaInfo);
        if (innerDownloadInfoListener != null) {
            innerDownloadInfoListener.onStop(downloadMediaInfo);
        }
        autoDownload();
    }

    /**
     * 停止多个下载
     */
    public void stopDownloads(ConcurrentLinkedQueue<AliyunDownloadMediaInfo> downloadMediaInfos) {
        if (downloadMediaInfos == null || downloadMediaInfos.size() == 0 || downloadInfos == null) {
            return;
        }
        for (AliyunDownloadMediaInfo downloadMediaInfo : downloadMediaInfos) {

            if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Start ||
                    downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Wait) {
                AliMediaDownloader jniDownloader = downloadInfos.get(downloadMediaInfo);
                if (jniDownloader == null || downloadMediaInfo.getStatus() != AliyunDownloadMediaInfo.Status.Start) {
                    continue;
                }
                jniDownloader.stop();
                releaseJniDownloader(downloadMediaInfo);
                if (innerDownloadInfoListener != null) {
                    innerDownloadInfoListener.onStop(downloadMediaInfo);
                }
            }
        }
    }


    /**
     * 删除下载文件
     */
    public void deleteFile(final AliyunDownloadMediaInfo downloadMediaInfo) {
        if (downloadMediaInfo == null || downloadInfos == null) {
            return;
        }

        if (downloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Delete) {
            return;

        }
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Delete);

        executeDelete(downloadMediaInfo);
    }

    /**
     * 执行删除操作
     */
    private void executeDelete(AliyunDownloadMediaInfo downloadMediaInfo) {
        AliMediaDownloader jniDownloader = downloadInfos.get(downloadMediaInfo);

        if (downloadMediaInfo == null) {
            if (innerDownloadInfoListener != null) {
                innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN_ERROR, mContext.getResources().getString(R.string.alivc_player_delete_failed), null);
            }
            return;
        }
        String saveDir = getDownloadDir();
        String vid = downloadMediaInfo.getVid();
        String format = downloadMediaInfo.getFormat();
        int index = downloadMediaInfo.getQualityIndex();

        if (jniDownloader != null) {
            jniDownloader.stop();
            //释放 jniDownloader
            releaseJniDownloader(downloadMediaInfo);
        }
        int ret = AliDownloaderFactory.deleteFile(saveDir, vid, format, index);
        if (ret == 12 || ret == 11) { //删除失败
            Log.e(TAG, "deleteFile warning  ret = " + ret);
            //删除下载需要选择哪个清晰度,否则无法删除本地文件
//            jniDownloader.selectItem(index);
//            jniDownloader.deleteFile();
            if (innerDownloadInfoListener != null) {
                innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN_ERROR, mContext.getResources().getString(R.string.alivc_player_delete_failed), null);
            }
        }

        if (innerDownloadInfoListener != null) {
            innerDownloadInfoListener.onDelete(downloadMediaInfo);
        }
        autoDownload();
    }


    /**
     * 获取sts信息
     */
    private void getVidSts(final AliyunDownloadMediaInfo downloadMediaInfo, final int intentState) {
        VidStsUtil.getVidSts(GlobalPlayerConfig.mVid, new VidStsUtil.OnStsResultListener() {
            @Override
            public void onSuccess(String vid, String akid, String akSecret, String token) {

                VidSts vidSts = new VidSts();
                vidSts.setVid(downloadMediaInfo.getVid());
                vidSts.setRegion("cn-shanghai");
                vidSts.setAccessKeyId(akid);
                vidSts.setSecurityToken(token);
                vidSts.setAccessKeySecret(akSecret);
                vidSts.setQuality(downloadMediaInfo.getQuality(), false);
                downloadMediaInfo.setVidSts(vidSts);
                prepareDownloadByQuality(downloadMediaInfo, intentState);
            }

            @Override
            public void onFail() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.alivc_player_get_sts_failed), Toast.LENGTH_SHORT).show();
                        if (innerDownloadInfoListener != null) {
                            innerDownloadInfoListener.onError(downloadMediaInfo, ErrorCode.ERROR_UNKNOWN_ERROR, mContext.getResources().getString(R.string.alivc_player_get_sts_failed), null);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除所有文件
     */
    public void deleteAllFile() {
        for (AliyunDownloadMediaInfo mediaInfo : preparedList) {
            deleteFile(mediaInfo);
        }

        for (AliyunDownloadMediaInfo mediaInfo : downloadingList) {
            deleteFile(mediaInfo);
        }

        for (AliyunDownloadMediaInfo mediaInfo : completedList) {
            deleteFile(mediaInfo);
        }

        for (AliyunDownloadMediaInfo mediaInfo : waitedList) {
            deleteFile(mediaInfo);
        }

        for (AliyunDownloadMediaInfo mediaInfo : stopedList) {
            deleteFile(mediaInfo);
        }
    }

    /**
     * 设置监听
     */
    private void setListener(final AliyunDownloadMediaInfo downloadMediaInfo, final AliMediaDownloader jniDownloader) {
        jniDownloader.setOnProgressListener(new AliMediaDownloader.OnProgressListener() {

            @Override
            public void onDownloadingProgress(int percent) {
                Log.e(TAG, "onDownloadingProgress内部下载 : " + percent);
                if (innerDownloadInfoListener != null) {
                    downloadMediaInfo.setProgress(percent);
                    innerDownloadInfoListener.onProgress(downloadMediaInfo, percent);
                }
            }

            @Override
            public void onProcessingProgress(int percent) {
                if (innerDownloadInfoListener != null) {
                    downloadMediaInfo.setmFileHandleProgress(percent);
                    innerDownloadInfoListener.onFileProgress(downloadMediaInfo);
                }
            }
        });

        jniDownloader.setOnCompletionListener(new AliMediaDownloader.OnCompletionListener() {
            @Override
            public void onCompletion() {
                if (innerDownloadInfoListener != null) {
                    innerDownloadInfoListener.onCompletion(downloadMediaInfo);
                }
                releaseJniDownloader(downloadMediaInfo);
            }
        });

        setErrorListener(jniDownloader, downloadMediaInfo);
    }

    /**
     * 初始化正在下载的缓存
     */
    public void initDownloading(LinkedList<AliyunDownloadMediaInfo> list) {
        if (downloadingList.size() != 0) {
            downloadingList.clear();
        }
        downloadingList.addAll(list);
    }

    /**
     * 初始化下载完成的缓存
     */
    public void initCompleted(LinkedList<AliyunDownloadMediaInfo> list) {
        if (completedList.size() != 0) {
            completedList.clear();
        }
        completedList.addAll(list);
    }

    /**
     * 自动开始等待中的下载任务
     */
    private void autoDownload() {
        //当前下载数小于设置的最大值,并且还有在等待中的下载任务
        if (downloadingList.size() < mMaxNum && waitedList.size() > 0) {
            AliyunDownloadMediaInfo aliyunDownloadMediaInfo = waitedList.peek();
            if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Wait) {
                startDownload(aliyunDownloadMediaInfo);
            }
        }
    }

    private void deleteAllMediaInfo() {
        preparedList.clear();
        waitedList.clear();
        downloadingList.clear();
        stopedList.clear();
        completedList.clear();
        downloadInfos.clear();
    }

    private void deleteMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        Iterator<AliyunDownloadMediaInfo> preparedIterator = preparedList.iterator();
        while (preparedIterator.hasNext()) {
            if (preparedIterator.next().equals(downloadMediaInfo)) {
                preparedIterator.remove();
            }
        }

        Iterator<AliyunDownloadMediaInfo> waitedIterator = waitedList.iterator();
        while (waitedIterator.hasNext()) {
            if (waitedIterator.next().equals(downloadMediaInfo)) {
                waitedIterator.remove();
            }
        }

        Iterator<AliyunDownloadMediaInfo> downloadingIterator = downloadingList.iterator();
        while (downloadingIterator.hasNext()) {
            if (downloadingIterator.next().equals(downloadMediaInfo)) {
                downloadingIterator.remove();
            }
        }

        Iterator<AliyunDownloadMediaInfo> stopedIterator = stopedList.iterator();
        while (stopedIterator.hasNext()) {
            if (stopedIterator.next().equals(downloadMediaInfo)) {
                stopedIterator.remove();
            }
        }

        Iterator<AliyunDownloadMediaInfo> completedIterator = completedList.iterator();
        while (completedIterator.hasNext()) {
            if (completedIterator.next().equals(downloadMediaInfo)) {
                completedIterator.remove();
            }
        }
        downloadInfos.remove(downloadMediaInfo);
    }

    private void waitMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (!waitedList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            waitedList.add(downloadMediaInfo);
        }
        preparedList.remove(downloadMediaInfo);
        downloadingList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Wait);
    }

    private void stopMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (!stopedList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            stopedList.add(downloadMediaInfo);
        }
        downloadingList.remove(downloadMediaInfo);
        preparedList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
    }

    private void prepareMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (!preparedList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            preparedList.add(downloadMediaInfo);
        }
        downloadingList.remove(downloadMediaInfo);
        completedList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
    }

    private void startMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {

        if (!downloadingList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            downloadingList.add(downloadMediaInfo);
        }
        preparedList.remove(downloadMediaInfo);
        stopedList.remove(downloadMediaInfo);
        completedList.remove(downloadMediaInfo);
        waitedList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Start);
    }

    private void completedMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (!completedList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            completedList.add(downloadMediaInfo);
        }
        downloadingList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Complete);
        autoDownload();
    }

    private void errorMediaInfo(AliyunDownloadMediaInfo downloadMediaInfo, ErrorCode code, String msg) {
        //在prepare的时候,如果获取不到MediaInfo,downloadMediaInfo会作为空值传递,所以会导致空指针异常
        if (downloadMediaInfo == null) {
            return;
        }
        if (!stopedList.contains(downloadMediaInfo) && downloadMediaInfo != null) {
            stopedList.add(downloadMediaInfo);
        }
        preparedList.remove(downloadMediaInfo);
        downloadingList.remove(downloadMediaInfo);
        completedList.remove(downloadMediaInfo);
        waitedList.remove(downloadMediaInfo);
        downloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Error);
        downloadMediaInfo.setErrorCode(code);
        downloadMediaInfo.setErrorMsg(msg);
        autoDownload();
    }

    public void removeDownloadInfoListener(AliyunDownloadInfoListener listener) {
        if (listener != null && outListenerList != null) {
            this.outListenerList.remove(listener);
        }
    }

    /**
     * 从数据库查询数据
     */
    public void findDatasByDb(final VidSts vidSts, final LoadDbDatasListener listener) {
        if (mDatabaseManager != null) {
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    //查询所有准备完成状态的数据,用于展示
                    List<AliyunDownloadMediaInfo> selectPreparedList = mDatabaseManager.selectPreparedList();
                    //查询所有等待状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectStopedList = mDatabaseManager.selectStopedList();
                    //查询所有完成状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectCompletedList = mDatabaseManager.selectCompletedList();
                    //查询所有下载状态中的数据
                    final List<AliyunDownloadMediaInfo> selectDownloadingList = mDatabaseManager.selectDownloadingList();
                    final List<AliyunDownloadMediaInfo> dataList = new ArrayList<>();
                    dataList.addAll(selectCompletedList);
                    dataList.addAll(selectStopedList);
                    dataList.addAll(selectPreparedList);
                    for (AliyunDownloadMediaInfo mediaInfo : selectPreparedList) {
                        mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
                    }
                    for (AliyunDownloadMediaInfo mediaInfo : selectDownloadingList) {
                        mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
                    }
                    dataList.addAll(selectDownloadingList);
                    if (stopedList != null) {
                        stopedList.addAll(selectDownloadingList);
                        stopedList.addAll(selectStopedList);
                        stopedList.addAll(selectPreparedList);
                    }
                    if (completedList != null) {
                        completedList.addAll(selectCompletedList);
                    }
                    /*
                     * 这里不需要将从数据库查询的下载中状态的数据进行内存缓存,在prepareDownload这些数据的时候,
                     * 会全部置为等待状态,需要手动点击开始下载
                     */
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prepareDownload(vidSts, dataList);
                            if (listener != null) {
                                listener.onLoadSuccess(dataList);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 从数据库查询数据
     */
    public void findDatasByDb(final LoadDbDatasListener listener) {
        if (mDatabaseManager != null) {
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    //查询所有准备完成状态的数据,用于展示
                    List<AliyunDownloadMediaInfo> selectPreparedList = mDatabaseManager.selectPreparedList();
                    //查询所有等待状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectStopedList = mDatabaseManager.selectStopedList();
                    //查询所有完成状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectCompletedList = mDatabaseManager.selectCompletedList();
                    //查询所有下载状态中的数据
                    final List<AliyunDownloadMediaInfo> selectDownloadingList = mDatabaseManager.selectDownloadingList();
                    //查询所有等待状态
                    final List<AliyunDownloadMediaInfo> selectWaitList = mDatabaseManager.selectWaitList();

                    final List<AliyunDownloadMediaInfo> dataList = new ArrayList<>();
                    if (selectPreparedList != null) {
                        for (AliyunDownloadMediaInfo mediaInfo : selectPreparedList) {
                            mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
                        }
                        dataList.addAll(selectPreparedList);
                    }
                    if (selectStopedList != null) {
                        dataList.addAll(selectStopedList);
                    }
                    if (selectCompletedList != null) {
                        dataList.addAll(selectCompletedList);
                    }

                    if (selectDownloadingList != null) {
                        for (AliyunDownloadMediaInfo mediaInfo : selectDownloadingList) {
                            mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Stop);
                        }
                        dataList.addAll(selectDownloadingList);
                    }
                    if (selectWaitList != null) {
                        dataList.addAll(selectWaitList);
                    }

                    if (stopedList != null) {
                        if (selectDownloadingList != null) {
                            stopedList.addAll(selectDownloadingList);
                        }
                        if (selectStopedList != null) {
                            stopedList.addAll(selectStopedList);
                        }
                        if (selectPreparedList != null) {
                            stopedList.addAll(selectPreparedList);
                        }
                    }
                    if (completedList != null) {
                        if (selectCompletedList != null) {
                            completedList.addAll(selectCompletedList);
                        }
                    }
                    /*
                     * 这里不需要将从数据库查询的下载中状态的数据进行内存缓存,在prepareDownload这些数据的时候,
                     * 会全部置为等待状态,需要手动点击开始下载
                     */
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onLoadSuccess(dataList);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 从数据库查询数据
     */
    public void findDatasByDbTv(final LoadDbDatasListener listener) {
        if (mDatabaseManager != null) {
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    //查询所有准备完成状态的数据,用于展示
                    List<AliyunDownloadMediaInfo> selectPreparedList = mDatabaseManager.selectPreparedList();
                    //查询所有等待状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectStopedList = mDatabaseManager.selectStopedList();
                    //查询所有完成状态的数据,用于展示
                    final List<AliyunDownloadMediaInfo> selectCompletedList = mDatabaseManager.selectCompletedList();
                    //查询所有下载状态中的数据
                    final List<AliyunDownloadMediaInfo> selectDownloadingList = mDatabaseManager.selectDownloadingList();
                    //查询所有等待状态
                    final List<AliyunDownloadMediaInfo> selectWaitList = mDatabaseManager.selectWaitList();
                    //观看历史
                    List<AliyunDownloadMediaInfo> selectWatchedList = mDatabaseManager.selectWatchedList();

                    final List<AliyunDownloadMediaInfo> dataList = new ArrayList<>();
                    if (selectCompletedList != null) {
                        dataList.addAll(selectCompletedList);
                    }
                    if (selectStopedList != null) {
                        dataList.addAll(selectStopedList);
                    }
                    if (selectPreparedList != null) {
                        dataList.addAll(selectPreparedList);
                    }

                    if (selectDownloadingList != null) {
                        dataList.addAll(selectDownloadingList);
                    }
                    if (selectWaitList != null) {
                        dataList.addAll(selectWaitList);
                    }

                    if (stopedList != null) {
                        if (selectStopedList != null) {
                            stopedList.addAll(selectStopedList);
                        }
                    }
                    if (completedList != null) {
                        completedList.addAll(selectCompletedList);

                    }
                    /*
                     * 这里不需要将从数据库查询的下载中状态的数据进行内存缓存,在prepareDownload这些数据的时候,
                     * 会全部置为等待状态,需要手动点击开始下载
                     */
                    final List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos = removeDuplicate(selectWatchedList, dataList);

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onLoadSuccess(aliyunDownloadMediaInfos);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 从数据库查询出来处理
     * 只保留一个tvTd，计算列表包含的元素个数
     */
    private List<AliyunDownloadMediaInfo> removeDuplicate(List<AliyunDownloadMediaInfo> selectWatchedList, List<AliyunDownloadMediaInfo> dataList) {
        //设置观看数量
        for (AliyunDownloadMediaInfo mediaInfo : dataList) {
            for (AliyunDownloadMediaInfo watchedMediaInfo : selectWatchedList) {
                if (watchedMediaInfo.getVid().equals(mediaInfo.getVid())) {
                    mediaInfo.setWatchNumber(1);
                }
            }
        }
        //设置大小
        long size = 0;
        //设置观看数量
        int watchNum = 0;
        //设置视频个数
        int num = 0;
        for (int i = 0; i < dataList.size(); i++) {
            size = dataList.get(i).getSize();
            watchNum = dataList.get(i).getWatchNumber();
            num = dataList.get(i).getNumber();
            for (int j = dataList.size() - 1; j > i; j--) {
                if (!TextUtils.isEmpty(dataList.get(j).getTvId()) && !TextUtils.isEmpty(dataList.get(i).getTvId())) {
                    if (dataList.get(j).getTvId().equals(dataList.get(i).getTvId())) {
                        size += dataList.get(j).getSize();
                        watchNum += dataList.get(j).getWatchNumber();
                        num += dataList.get(j).getNumber();
                        dataList.remove(j);

                    }
                }
            }
            //设置封面size大小
            dataList.get(i).setSize(size);
            dataList.get(i).setWatchNumber(watchNum);
            //更新数量
            dataList.get(i).setNumber(num);
        }

        return dataList;
    }


    /**
     * 根据tvId获取对应的所有电视剧
     * 还要判断是否观看过
     */
    public void getDownloadMediaInfoWithTvId(final String tvId, final LoadDbTvListDatasListenerr loadDbDatasListener) {

        if (mDatabaseManager != null) {

            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {

                    //观看历史
                    final List<LongVideoBean> longVideoBeans = mLongVideoDatabaseManager.selectAllWatchHistory();
                    //查询所有是此tvid的视频
                    final List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos = mDatabaseManager.selectAllByTvId(tvId);

                    if (aliyunDownloadMediaInfos != null) {
                        //判断视频是否观看
                        for (AliyunDownloadMediaInfo mediaInfo : aliyunDownloadMediaInfos) {
                            for (LongVideoBean longVideoBean : longVideoBeans) {
                                if (longVideoBean.getVideoId().equals(mediaInfo.getVid())) {
                                    mediaInfo.setWatchNumber(1);
                                }
                            }
                        }
                    }
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loadDbDatasListener != null) {
                                loadDbDatasListener.onLoadTvListSuccess(aliyunDownloadMediaInfos);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取准备完成的数据
     */
    public ConcurrentLinkedQueue<AliyunDownloadMediaInfo> getPreparedList() {
        return preparedList;
    }

    /**
     * 获取下载完成的数据
     */
    public ConcurrentLinkedQueue<AliyunDownloadMediaInfo> getCompletedList() {
        return completedList;
    }

    /**
     * 获取下载中的数据
     */
    public ConcurrentLinkedQueue<AliyunDownloadMediaInfo> getDownloadingList() {
        return downloadingList;
    }

    /**
     * 获取等待中的数据
     */
    public ConcurrentLinkedQueue<AliyunDownloadMediaInfo> getWaitedList() {
        return waitedList;
    }

    /**
     * 获取暂停中的数据
     */
    public ConcurrentLinkedQueue<AliyunDownloadMediaInfo> getStopedList() {
        return stopedList;
    }

    public void release() {
        if (mDatabaseManager != null) {
            mDatabaseManager.close();
        }
        if (preparedList != null) {
            preparedList.clear();
        }
        if (downloadingList != null) {
            downloadingList.clear();
        }
        if (completedList != null) {
            completedList.clear();
        }
        if (waitedList != null) {
            waitedList.clear();
        }
        if (outListenerList != null) {
            outListenerList.clear();
        }
        if (mJniDownloadLists != null) {
            mJniDownloadLists.clear();
        }
    }

    public void clearList() {
        if (preparedList != null) {
            preparedList.clear();
        }
        if (!downloadInfos.isEmpty()) {
            Iterator<Map.Entry<AliyunDownloadMediaInfo, AliMediaDownloader>> iterator = downloadInfos.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<AliyunDownloadMediaInfo, AliMediaDownloader> next = iterator.next();
                next.getValue().stop();
            }
            downloadInfos.clear();
        }
        if (downloadingList != null) {
            downloadingList.clear();
        }
        if (completedList != null) {
            completedList.clear();
        }
        if (waitedList != null) {
            waitedList.clear();
        }
        if (outListenerList != null) {
            outListenerList.clear();
        }
        if (stopedList != null) {
            stopedList.clear();
        }
    }


    /**
     * sts 刷新回调
     */
    public void setRefreshStsCallback(final RefreshStsCallback refreshStsCallback) {

    }

    /**
     * 插入数据库
     */
    public void insertDb(AliyunDownloadMediaInfo mediaInfo) {
        if (mediaInfo == null || mDatabaseManager == null) {
            return;
        }
        mediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
        int itemCount = mDatabaseManager.selectItemExist(mediaInfo);
        if (itemCount <= 0) {
            mDatabaseManager.insert(mediaInfo);
        }
    }

    /**
     * 更新数据库
     */
    public void updateDb(AliyunDownloadMediaInfo mediaInfo) {
        if (mediaInfo == null || mDatabaseManager == null) {
            return;
        }
        mDatabaseManager.update(mediaInfo);
    }

    public VidAuth getmVidAuth() {
        return mVidAuth;
    }

    public void setmVidAuth(VidAuth mVidAuth) {
        this.mVidAuth = mVidAuth;
    }

    public VidSts getmVidSts() {
        return mVidSts;
    }

    public void setmVidSts(VidSts mVidSts) {
        this.mVidSts = mVidSts;
    }

    /**
     * 设置错误监听
     *
     * @param jniDownloader           下载类
     * @param aliyunDownloadMediaInfo javaBean
     */
    private void setErrorListener(final AliMediaDownloader jniDownloader, final AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
        if (jniDownloader == null) {
            return;
        }
        jniDownloader.setOnErrorListener(new AliMediaDownloader.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                if (innerDownloadInfoListener != null) {
                    innerDownloadInfoListener.onError(aliyunDownloadMediaInfo, errorInfo.getCode(), errorInfo.getMsg(), errorInfo.getExtra());
                }
                if (aliyunDownloadMediaInfo == null) {
                    jniDownloader.release();
                } else {
                    releaseJniDownloader(aliyunDownloadMediaInfo);
                }
            }
        });
    }

    /**
     * 释放 jniDownloader
     */
    private void releaseJniDownloader(AliyunDownloadMediaInfo downloadMediaInfo) {
        if (downloadInfos != null && downloadInfos.containsKey(downloadMediaInfo)) {
            AliMediaDownloader aliMediaDownloader = downloadInfos.get(downloadMediaInfo);
            if (aliMediaDownloader != null) {
                aliMediaDownloader.release();
            }
            downloadInfos.remove(downloadMediaInfo);
        }
    }
}
