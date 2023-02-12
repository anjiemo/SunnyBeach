package com.aliyun.vodplayerview.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.oss.common.utils.DateUtil;
import com.aliyun.downloader.DownloaderConfig;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.VidPlayerConfigGen;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.dialogfragment.AlivcShareDialogFragment;
import com.aliyun.player.alivcplayerexpand.listener.OnScreenCostingSingleTagListener;
import com.aliyun.player.alivcplayerexpand.listener.OnStoppedListener;
import com.aliyun.player.alivcplayerexpand.listener.RefreshStsCallback;
import com.aliyun.player.alivcplayerexpand.playlist.AlivcVideoInfo;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.player.alivcplayerexpand.util.VidStsUtil;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadInfoListener;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.alivcplayerexpand.view.choice.AlivcShowMoreDialog;
import com.aliyun.player.alivcplayerexpand.view.control.ControlView;
import com.aliyun.player.alivcplayerexpand.view.gesturedialog.BrightnessDialog;
import com.aliyun.player.alivcplayerexpand.view.more.AliyunShowMoreValue;
import com.aliyun.player.alivcplayerexpand.view.more.DanmakuSettingView;
import com.aliyun.player.alivcplayerexpand.view.more.ShowMoreView;
import com.aliyun.player.alivcplayerexpand.view.more.SpeedValue;
import com.aliyun.player.alivcplayerexpand.view.more.TrackInfoView;
import com.aliyun.player.alivcplayerexpand.view.quality.QualityItem;
import com.aliyun.player.alivcplayerexpand.view.softinput.SoftInputDialogFragment;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.aliyunplayerbase.activity.BaseActivity;
import com.aliyun.player.aliyunplayerbase.bean.AliyunMps;
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth;
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts;
import com.aliyun.player.aliyunplayerbase.net.GetAuthInformation;
import com.aliyun.player.aliyunplayerbase.net.ServiceCommon;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;
import com.aliyun.player.aliyunplayerbase.util.NetWatchdog;
import com.aliyun.player.aliyunplayerbase.util.ScreenUtils;
import com.aliyun.player.aliyunplayerbase.view.tipsview.ErrorInfo;
import com.aliyun.player.aliyunplayerbase.view.tipsview.OnTipsViewBackClickListener;
import com.aliyun.player.aliyunplayerbase.view.tipsview.TipsView;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.Definition;
import com.aliyun.player.source.LiveSts;
import com.aliyun.player.source.StsInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidMps;
import com.aliyun.player.source.VidSts;
import com.aliyun.svideo.common.base.AlivcListSelectorDialogFragment;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.FastClickUtil;
import com.aliyun.svideo.common.utils.FileUtils;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.aliyun.vodplayer.R;
import com.aliyun.vodplayerview.adapter.AliyunPlayerVideoListAdapter;
import com.aliyun.vodplayerview.global.Global;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Request;

/**
 * 播放器和播放列表界面 Created by Mulberry on 2018/4/9.
 */
public class AliyunPlayerSkinActivity extends BaseActivity {

    private static final String TAG = "AliyunPlayerSkinActivit";

    static {
        System.loadLibrary("RtsSDK");
    }

    private AliyunScreenMode currentScreenMode = AliyunScreenMode.Small;

    private AliyunVodPlayerView mAliyunVodPlayerView = null;
    private ErrorInfo currentError = ErrorInfo.Normal;

    /**
     * get StsToken stats
     */
    private boolean inRequest;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * 当前点击的视频列表的下标
     */
    private int currentVidItemPosition;
    /**
     * 弹幕设置Dialog
     */
    private AlivcShowMoreDialog danmakuShowMoreDialog;
    /**
     * 更多Dialog
     */
    private AlivcShowMoreDialog showMoreDialog;
    /**
     * 投屏选择Dialog
     */
    private AlivcShowMoreDialog screenShowMoreDialog;
    /**
     * 弹幕设置View
     */
    private DanmakuSettingView mDanmakuSettingView;
    /**
     * 下载管理类
     */
    private AliyunDownloadManager mAliyunDownloadManager;
    //弹幕透明度、显示区域、速率progress
    private int mAlphProgress = 0, mRegionProgress = 0, mSpeedProgress = 30;

    /**
     * 是否鉴权过期
     */
    private boolean mIsTimeExpired = false;
    /**
     * 是否正在加载下载信息
     */
    private boolean mIsLoadDownloadInfo = false;
    /**
     * 播放列表RecyclerView
     */
    private RecyclerView mPlayerListRecyclerView;
    /**
     * 播放列表Adapter
     */
    private AliyunPlayerVideoListAdapter mAliyunPlayerVideoListAdapter;

    /**
     * 播放列表资源
     */
    private ArrayList<AlivcVideoInfo.DataBean.VideoListBean> mVideoListBean;
    /**
     * 下载ImageView
     */
    private ImageView mDownloadImageView;
    /**
     * 分享ImageView
     */
    private ImageView mShareImageView;
    /**
     * 下载监听类
     */
    private MyDownloadInfoListener myDownloadInfoListener;
    /**
     * 下载清晰度Dialog
     */
    private AlivcListSelectorDialogFragment mAlivcListSelectorDialogFragment;
    /**
     * 点击发送弹幕的画笔弹出的dialog
     */
    private SoftInputDialogFragment mSoftInputDialogFragment;
    /**
     * 下载列表
     */
    private TextView mDownloadListTextView;
    /**
     * 本地视频播放地址
     */
    private String mLocalVideoPath;

    /**
     * 用于恢复原本的播放方式，如果跳转到下载界面，播放本地视频，会切换到url播放方式
     */
    private GlobalPlayerConfig.PLAYTYPE mCurrentPlayType = GlobalPlayerConfig.mCurrentPlayType;
    /**
     * 下载Progress
     */
    private ProgressBar mDownloadProgressBar;
    /**
     * 当前正在播放的videoId
     */
    private String mCurrentVideoId;
    private boolean mNeedOnlyFullScreen;
    /**
     * 当前系统屏幕亮度
     */
    private int mCurrentBrightValue;
    /**
     * 判断是否是从下载界面进入到播放界面的
     */
    private boolean mIsFromDownloadActivity;

    public static void startAliyunPlayerSkinActivityWithLocalVideo(Context context, String path) {
        Intent intent = new Intent(context, AliyunPlayerSkinActivity.class);
        intent.putExtra(GlobalPlayerConfig.Intent_Key.LOCAL_VIDEO_PATH, path);
        intent.putExtra(GlobalPlayerConfig.Intent_Key.NEED_ONLY_FULL_SCREEN, true);
        GlobalPlayerConfig.mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (isStrangePhone()) {
            //            setTheme(R.style.ActTheme);
        } else {
            setTheme(R.style.NoActionTheme);
        }

        super.onCreate(savedInstanceState);
        restoreSaveInstance(savedInstanceState);
        setManualBright();
        mCurrentBrightValue = getCurrentBrightValue();
        setContentView(R.layout.alivc_player_layout_skin);
        mLocalVideoPath = getIntent().getStringExtra(GlobalPlayerConfig.Intent_Key.LOCAL_VIDEO_PATH);
        mNeedOnlyFullScreen = getIntent().getBooleanExtra(GlobalPlayerConfig.Intent_Key.NEED_ONLY_FULL_SCREEN, false);

        initDownloadManager();
        initView();
        initAliyunPlayerView();
        initPlayerConfig();
        initDataSource();
        initVideoList();
        initListener();
    }

    private void initDownloadManager() {
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());
        DownloaderConfig downloaderConfig = new DownloaderConfig();
        downloaderConfig.mConnectTimeoutS = 3;
        downloaderConfig.mNetworkTimeoutMs = 5000;
        mAliyunDownloadManager.setDownloaderConfig(downloaderConfig);
    }

    private void initView() {
        mShareImageView = findViewById(R.id.iv_share);
        mDownloadImageView = findViewById(R.id.iv_download);
        mAliyunVodPlayerView = findViewById(R.id.video_view);
        mDownloadProgressBar = findViewById(R.id.download_progress);
        mDownloadListTextView = findViewById(R.id.tv_download_list);
        mPlayerListRecyclerView = findViewById(R.id.recyclerview_player_list);

        initSoftDialogFragment();

    }

    private void initSoftDialogFragment() {
        mSoftInputDialogFragment = SoftInputDialogFragment.newInstance();
        mSoftInputDialogFragment.setOnBarrageSendClickListener(new SoftInputDialogFragment.OnBarrageSendClickListener() {
            @Override
            public void onBarrageSendClick(String danmu) {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setmDanmaku(danmu);
                    mSoftInputDialogFragment.dismiss();
                }
            }
        });
    }

    private void initListener() {
        //下载监听
        myDownloadInfoListener = new MyDownloadInfoListener(this);
        mAliyunDownloadManager.addDownloadInfoListener(myDownloadInfoListener);
        mDownloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean is4gConnected = NetWatchdog.is4GConnected(getApplicationContext());
                if (is4gConnected) {
                    Toast.makeText(AliyunPlayerSkinActivity.this, getString(R.string.alivc_player_doawload_operator), Toast.LENGTH_SHORT).show();
                }
                if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL
                        || GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
                    Toast.makeText(AliyunPlayerSkinActivity.this, R.string.alivc_not_support_download, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mIsLoadDownloadInfo) {
                    downloadVideo();
                }
                mIsLoadDownloadInfo = true;
            }
        });

        mShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlivcShareDialogFragment alivcVideoShareDialogFragment = AlivcShareDialogFragment.newInstance();
                alivcVideoShareDialogFragment.show(getSupportFragmentManager(), "AlivcShareDialogFragment");
            }
        });

        mAliyunPlayerVideoListAdapter.setOnItemClickListener(new AliyunPlayerVideoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changePlaySource(position);
            }
        });

        //下载列表
        mDownloadListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                String downloadVid = "";
                if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {
                    downloadVid = mCurrentVideoId;
                } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS
                        || GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
                    downloadVid = GlobalPlayerConfig.mVid;
                }
                if (TextUtils.isEmpty(GlobalPlayerConfig.mVid) && TextUtils.isEmpty(mCurrentVideoId)) {
                    return;
                }
                if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS
                        || GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {

                    VidSts vidSts = getVidSts(downloadVid);
                    mAliyunDownloadManager.setmVidSts(vidSts);
                } else {
                    VidAuth vidAuth = getVidAuth(downloadVid);
                    mAliyunDownloadManager.setmVidAuth(vidAuth);
                }
                //同步iOS，无论是否开启后台播放，进入下载界面后，都进行pause(点播)/stop(直播)
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.onStop();
                }
                Intent intent = new Intent(AliyunPlayerSkinActivity.this, AliyunPlayerDownloadListActivity.class);
                startActivityForResult(intent, AliyunPlayerDownloadListActivity.DOWNLOAD_ACTIVITY_FOR_REQUEST_CODE);
            }
        });
    }

    /**
     * 下载视频
     */
    private void downloadVideo() {
        if (mAliyunDownloadManager == null) {
            return;
        }
        if (mDownloadProgressBar != null) {
            mDownloadProgressBar.setVisibility(View.VISIBLE);
        }
        String downloadVid = "";
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {
            downloadVid = mCurrentVideoId;
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS
                || GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            downloadVid = GlobalPlayerConfig.mVid;
        }
        if (TextUtils.isEmpty(GlobalPlayerConfig.mVid) && TextUtils.isEmpty(mCurrentVideoId)) {
            return;
        }
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS
                || GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {

            VidSts vidSts = getVidSts(downloadVid);
            mAliyunDownloadManager.setmVidSts(vidSts);
            mAliyunDownloadManager.prepareDownload(vidSts);
        } else {
            VidAuth vidAuth = getVidAuth(downloadVid);
            mAliyunDownloadManager.setmVidAuth(vidAuth);
            mAliyunDownloadManager.prepareDownload(vidAuth);
        }

    }

    private void initAliyunPlayerView() {
        //保持屏幕常亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setTheme(Theme.Blue);
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.needOnlyFullScreenPlay(mNeedOnlyFullScreen);

        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        mAliyunVodPlayerView.setOnTrackReadyListener(new MyOnTrackReadyListener(this));
        mAliyunVodPlayerView.setNetConnectedListener(new MyNetConnectedListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new MyFrameInfoListener(this));
        mAliyunVodPlayerView.setOnTrackChangedListener(new MyOnTrackChangedListener(this));
        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));
        mAliyunVodPlayerView.setOrientationChangeListener(new MyOrientationChangeListener(this));
        mAliyunVodPlayerView.setOnTimeExpiredErrorListener(new MyOnTimeExpiredErrorListener(this));
        mAliyunVodPlayerView.setOnShowMoreClickListener(new MyShowMoreClickLisener(this));
        mAliyunVodPlayerView.setOnPlayStateBtnClickListener(new MyPlayStateBtnClickListener(this));
        mAliyunVodPlayerView.setOnSeekCompleteListener(new MySeekCompleteListener(this));
        mAliyunVodPlayerView.setOnSeekStartListener(new MySeekStartListener(this));
        mAliyunVodPlayerView.setOnFinishListener(new MyOnFinishListener(this));
        mAliyunVodPlayerView.setOnScreenCostingSingleTagListener(new MyOnScreenCostingSingleTagListener(this));
        mAliyunVodPlayerView.setOnScreenBrightness(new MyOnScreenBrightnessListener(this));
        mAliyunVodPlayerView.setSoftKeyHideListener(new MyOnSoftKeyHideListener(this));
        mAliyunVodPlayerView.setOnErrorListener(new MyOnErrorListener(this));
        mAliyunVodPlayerView.setScreenBrightness(BrightnessDialog.getActivityBrightness(AliyunPlayerSkinActivity.this));
        mAliyunVodPlayerView.setOnTrackInfoClickListener(new MyOnTrackInfoClickListener(this));
        mAliyunVodPlayerView.setOnInfoListener(new MyOnInfoListener(this));
        mAliyunVodPlayerView.setOutOnSeiDataListener(new MyOnSeiDataListener(this));
        mAliyunVodPlayerView.setOnTipClickListener(new MyOnTipClickListener(this));
        mAliyunVodPlayerView.setOnTipsViewBackClickListener(new MyOnTipsViewBackClickListener(this));
        mAliyunVodPlayerView.setOutOnVerifyTimeExpireCallback(new MyOnVerifyStsCallback(this));
        mAliyunVodPlayerView.enableNativeLog();
        mAliyunVodPlayerView.setScreenBrightness(mCurrentBrightValue);
        mAliyunVodPlayerView.startNetWatch();

    }

    private void initVideoList() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mPlayerListRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAliyunPlayerVideoListAdapter = new AliyunPlayerVideoListAdapter(this);
        mPlayerListRecyclerView.setAdapter(mAliyunPlayerVideoListAdapter);
    }


    /**
     * 获取播放列表数据
     */
    private void loadPlayList() {
        AlivcOkHttpClient.getInstance().get(ServiceCommon.GET_VIDEO_DEFAULT_LIST, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Toast.makeText(AliyunPlayerSkinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                AlivcVideoInfo alivcVideoInfo = gson.fromJson(result, AlivcVideoInfo.class);
                if (alivcVideoInfo != null && alivcVideoInfo.getData() != null) {
                    mVideoListBean = (ArrayList<AlivcVideoInfo.DataBean.VideoListBean>) alivcVideoInfo.getData().getVideoList();
                    if (mAliyunPlayerVideoListAdapter != null) {
                        mAliyunPlayerVideoListAdapter.setData(mVideoListBean);
                        mAliyunPlayerVideoListAdapter.notifyDataSetChanged();
                    }
                    if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {
                        if (mAliyunVodPlayerView != null) {
                            mCurrentVideoId = mVideoListBean.get(currentVidItemPosition).getVideoId();
                            VidSts vidSts = getVidSts(mCurrentVideoId);
                            mAliyunVodPlayerView.setVidSts(vidSts);
                        }
                    }
                }
            }
        });
    }

    /**
     * 播放方式
     */
    private void initDataSource() {
        GlobalPlayerConfig.PLAYTYPE mCurrentPlayType = GlobalPlayerConfig.mCurrentPlayType;
        if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            VidAuth vidAuth = getVidAuth(GlobalPlayerConfig.mVid);
            mCurrentVideoId = GlobalPlayerConfig.mVid;
            mAliyunVodPlayerView.setAuthInfo(vidAuth);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS) {
            VidSts vidSts = getVidSts(GlobalPlayerConfig.mVid);
            mCurrentVideoId = GlobalPlayerConfig.mVid;
            mAliyunVodPlayerView.setVidSts(vidSts);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL) {
            UrlSource urlSource = new UrlSource();
            mCurrentVideoId = "";
            if (TextUtils.isEmpty(mLocalVideoPath)) {
                urlSource.setUri(GlobalPlayerConfig.mUrlPath);
            } else {
                urlSource.setUri(mLocalVideoPath);
            }
            mAliyunVodPlayerView.setLocalSource(urlSource);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
            VidMps vidMps = getVidMps(GlobalPlayerConfig.mVid);
            mCurrentVideoId = GlobalPlayerConfig.mVid;
            mAliyunVodPlayerView.setVidMps(vidMps);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.LIVE_STS) {
            LiveSts liveSts = getLiveSts(GlobalPlayerConfig.mLiveStsUrl);
            mAliyunVodPlayerView.setLiveStsDataSource(liveSts);
        } else {
            //default
            currentVidItemPosition = 0;
            loadPlayList();
        }

        if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS
                || mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH
                || mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.DEFAULT) {
            mDownloadImageView.setVisibility(View.VISIBLE);
        } else {
            mDownloadImageView.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化播放配置
     */
    private void initPlayerConfig() {
        if (mAliyunVodPlayerView != null) {
            //界面设置
            mAliyunVodPlayerView.setEnableHardwareDecoder(GlobalPlayerConfig.mEnableHardDecodeType);
            mAliyunVodPlayerView.setRenderMirrorMode(GlobalPlayerConfig.mMirrorMode);
            mAliyunVodPlayerView.setRenderRotate(GlobalPlayerConfig.mRotateMode);
            mAliyunVodPlayerView.setDefaultBandWidth(GlobalPlayerConfig.mCurrentMutiRate.getValue());
            //播放配置设置
            PlayerConfig playerConfig = mAliyunVodPlayerView.getPlayerConfig();
            playerConfig.mStartBufferDuration = GlobalPlayerConfig.PlayConfig.mStartBufferDuration;
            playerConfig.mHighBufferDuration = GlobalPlayerConfig.PlayConfig.mHighBufferDuration;
            playerConfig.mMaxBufferDuration = GlobalPlayerConfig.PlayConfig.mMaxBufferDuration;
            playerConfig.mMaxDelayTime = GlobalPlayerConfig.PlayConfig.mMaxDelayTime;
            playerConfig.mNetworkTimeout = GlobalPlayerConfig.PlayConfig.mNetworkTimeout;
            playerConfig.mMaxProbeSize = GlobalPlayerConfig.PlayConfig.mMaxProbeSize;
            playerConfig.mReferrer = GlobalPlayerConfig.PlayConfig.mReferrer;
            playerConfig.mHttpProxy = GlobalPlayerConfig.PlayConfig.mHttpProxy;
            playerConfig.mNetworkRetryCount = GlobalPlayerConfig.PlayConfig.mNetworkRetryCount;
            playerConfig.mEnableSEI = GlobalPlayerConfig.PlayConfig.mEnableSei;
            playerConfig.mClearFrameWhenStop = GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop;
            mAliyunVodPlayerView.setPlayerConfig(playerConfig);
            //缓存设置
            initCacheConfig();
            Log.e(TAG, "cache dir : " + GlobalPlayerConfig.PlayCacheConfig.mDir
                    + " startBufferDuration = " + GlobalPlayerConfig.PlayConfig.mStartBufferDuration
                    + " highBufferDuration = " + GlobalPlayerConfig.PlayConfig.mHighBufferDuration
                    + " maxBufferDuration = " + GlobalPlayerConfig.PlayConfig.mMaxBufferDuration
                    + " maxDelayTime = " + GlobalPlayerConfig.PlayConfig.mMaxDelayTime
                    + " enableCache = " + GlobalPlayerConfig.PlayCacheConfig.mEnableCache
                    + " --- mMaxDurationS = " + GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS
                    + " --- mMaxSizeMB = " + GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB);
        }
    }

    private void initCacheConfig() {
        CacheConfig cacheConfig = new CacheConfig();
        GlobalPlayerConfig.PlayCacheConfig.mDir = FileUtils.getDir(this) + GlobalPlayerConfig.CACHE_DIR_PATH;
        cacheConfig.mEnable = GlobalPlayerConfig.PlayCacheConfig.mEnableCache;
        cacheConfig.mDir = GlobalPlayerConfig.PlayCacheConfig.mDir;
        cacheConfig.mMaxDurationS = GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS;
        cacheConfig.mMaxSizeMB = GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB;

        mAliyunVodPlayerView.setCacheConfig(cacheConfig);
    }


    /**
     * 切换播放资源
     *
     * @param position 需要播放的数据在集合中的下标
     */
    private void changePlaySource(int position) {

        currentVidItemPosition = position;

        AlivcVideoInfo.DataBean.VideoListBean videoListBean = mVideoListBean.get(position);

        changePlayVidSource(videoListBean);
    }

    /**
     * 播放本地资源
     */
    private void changePlayLocalSource(String url, String title) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        urlSource.setTitle(title);
        mAliyunVodPlayerView.setLocalSource(urlSource);
    }

    /**
     * 切换播放资源
     */
    private void changePlayVidSource(AlivcVideoInfo.DataBean.VideoListBean videoListItem) {
        if (mAliyunVodPlayerView != null) {
            initCacheConfig();
            mCurrentVideoId = videoListItem.getVideoId();
            VidSts vidSts = getVidSts(mCurrentVideoId);
            mAliyunVodPlayerView.setVidSts(vidSts);
        }
    }

    /**
     * 获取VidSts
     *
     * @param vid videoId
     */
    private VidSts getVidSts(String vid) {
        VidSts vidSts = new VidSts();
        vidSts.setVid(vid);
        vidSts.setRegion(GlobalPlayerConfig.mRegion);
        vidSts.setAccessKeyId(GlobalPlayerConfig.mStsAccessKeyId);
        vidSts.setSecurityToken(GlobalPlayerConfig.mStsSecurityToken);
        vidSts.setAccessKeySecret(GlobalPlayerConfig.mStsAccessKeySecret);
        //试看
        if (GlobalPlayerConfig.mPreviewTime > 0) {
            VidPlayerConfigGen configGen = new VidPlayerConfigGen();
            configGen.setPreviewTime(GlobalPlayerConfig.mPreviewTime);
            vidSts.setPlayConfig(configGen);
        }

        if (GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen) {
            List<Definition> list = new ArrayList<>();
            list.add(Definition.DEFINITION_AUTO);
            vidSts.setDefinition(list);
        }
        return vidSts;
    }

    /**
     * 获取VidAuth
     *
     * @param vid videoId
     */
    private VidAuth getVidAuth(String vid) {
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(vid);
        vidAuth.setRegion(GlobalPlayerConfig.mRegion);
        vidAuth.setPlayAuth(GlobalPlayerConfig.mPlayAuth);
        //试看
        if (GlobalPlayerConfig.mPreviewTime > 0) {
            VidPlayerConfigGen configGen = new VidPlayerConfigGen();
            configGen.setPreviewTime(GlobalPlayerConfig.mPreviewTime);
            vidAuth.setPlayConfig(configGen);
        }

        if (GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen) {
            List<Definition> list = new ArrayList<>();
            list.add(Definition.DEFINITION_AUTO);
            vidAuth.setDefinition(list);
        }
        return vidAuth;
    }

    /**
     * 获取LiveSts
     *
     * @param mUrlPath url地址
     */
    private LiveSts getLiveSts(String mUrlPath) {
        LiveSts liveSts = new LiveSts();
        liveSts.setUrl(mUrlPath);
        liveSts.setRegion(GlobalPlayerConfig.mRegion);
        liveSts.setAccessKeyId(GlobalPlayerConfig.mLiveStsAccessKeyId);
        liveSts.setAccessKeySecret(GlobalPlayerConfig.mLiveStsAccessKeySecret);
        liveSts.setSecurityToken(GlobalPlayerConfig.mLiveStsSecurityToken);
        liveSts.setDomain(GlobalPlayerConfig.mLiveStsDomain);
        liveSts.setApp(GlobalPlayerConfig.mLiveStsApp);
        liveSts.setStream(GlobalPlayerConfig.mLiveStsStream);
        return liveSts;
    }

    /**
     * 获取VidMps
     *
     * @param vid videoId
     */
    private VidMps getVidMps(String vid) {
        VidMps vidMps = new VidMps();
        vidMps.setMediaId(vid);
        vidMps.setRegion(GlobalPlayerConfig.mMpsRegion);
        vidMps.setAccessKeyId(GlobalPlayerConfig.mMpsAccessKeyId);
        vidMps.setAccessKeySecret(GlobalPlayerConfig.mMpsAccessKeySecret);
        vidMps.setSecurityToken(GlobalPlayerConfig.mMpsSecurityToken);
        vidMps.setAuthInfo(GlobalPlayerConfig.mMpsAuthInfo);
        vidMps.setHlsUriToken(GlobalPlayerConfig.mMpsHlsUriToken);
        //试看
        if (GlobalPlayerConfig.mPreviewTime > 0) {
            VidPlayerConfigGen configGen = new VidPlayerConfigGen();
            configGen.setPreviewTime(GlobalPlayerConfig.mPreviewTime);
            vidMps.setPlayConfig(configGen);
        }

        if (GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen) {
            List<Definition> list = new ArrayList<>();
            list.add(Definition.DEFINITION_AUTO);
            vidMps.setDefinition(list);
        }
        return vidMps;
    }

    private static class MyPrepareListener implements IPlayer.OnPreparedListener {

        private final WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyPrepareListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<>(skinActivity);
        }

        @Override
        public void onPrepared() {
            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }
    }

    private void onPrepared() {
        Toast.makeText(this, R.string.toast_prepare_success, Toast.LENGTH_SHORT).show();
        if (mAliyunVodPlayerView != null) {
            MediaInfo mediaInfo = mAliyunVodPlayerView.getMediaInfo();
            if (mediaInfo != null) {
                mCurrentVideoId = mediaInfo.getVideoId();
            }
        }
    }

    private static class MyOnTrackReadyListener implements IPlayer.OnTrackReadyListener {

        public WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTrackReadyListener(AliyunPlayerSkinActivity weakReference) {
            this.weakReference = new WeakReference<>(weakReference);
        }

        @Override
        public void onTrackReady(MediaInfo mediaInfo) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onTrackReady(mediaInfo);
            }
        }
    }

    private void onTrackReady(MediaInfo mediaInfo) {
        if (mediaInfo != null) {
            long bitrate = mediaInfo.getTotalBitrate();
            if (bitrate == 0) {
                List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
                if (trackInfos.size() > 0) {
                    bitrate = trackInfos.get(0).videoBitrate;
                }
            }
        }
    }

    private static class MyCompletionListener implements IPlayer.OnCompletionListener {

        private final WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyCompletionListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {

            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private void onCompletion() {
        Toast.makeText(this, R.string.toast_play_compleion, Toast.LENGTH_SHORT).show();

        hideAllDialog();

        // 当前视频播放结束, 播放下一个视频
        if (GlobalPlayerConfig.mCurrentPlayType.equals(GlobalPlayerConfig.PLAYTYPE.DEFAULT)) {
            onNext();
        } else {
            if (mAliyunVodPlayerView != null) {
                mAliyunVodPlayerView.showReplay();
            }
        }
    }

    /**
     * 隐藏所有Dialog
     */
    private void hideAllDialog() {
        if (danmakuShowMoreDialog != null && danmakuShowMoreDialog.isShowing()) {
            danmakuShowMoreDialog.dismiss();
        }
        if (showMoreDialog != null && showMoreDialog.isShowing()) {
            showMoreDialog.dismiss();
        }
        if (screenShowMoreDialog != null && screenShowMoreDialog.isShowing()) {
            screenShowMoreDialog.dismiss();
        }
    }

    /**
     * 播放下一个视频
     */
    private void onNext() {
        if (currentError == ErrorInfo.UnConnectInternet) {
            // 此处需要判断网络和播放类型
            // 网络资源, 播放完自动波下一个, 无网状态提示ErrorTipsView
            // 本地资源, 播放完需要重播, 显示Replay, 此处不需要处理
            if (GlobalPlayerConfig.mCurrentPlayType.equals(GlobalPlayerConfig.PLAYTYPE.STS)) {
                mAliyunVodPlayerView.showErrorTipView(4014, "-1", getResources().getString(R.string.alivc_net_disable));
            }
            return;
        }

        currentVidItemPosition++;
        if (currentVidItemPosition > mVideoListBean.size() - 1) {
            //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
            currentVidItemPosition = 0;
        }

        if (mVideoListBean.size() > 0) {
            AlivcVideoInfo.DataBean.VideoListBean videoListBean = mVideoListBean.get(currentVidItemPosition);
            if (videoListBean != null) {
                changePlayVidSource(videoListBean);
            }
        }

    }

    private static class MyFrameInfoListener implements IPlayer.OnRenderingStartListener {

        private final WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyFrameInfoListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<>(skinActivity);
        }

        @Override
        public void onRenderingStart() {
            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFirstFrameStart();
            }
        }
    }

    private void onFirstFrameStart() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(this, getResources().getString(R.string.alivc_sd_card_permission) + "", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 下载监听
     */
    private static class MyDownloadInfoListener implements AliyunDownloadInfoListener {

        private final WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyDownloadInfoListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
            Collections.sort(infos, new Comparator<AliyunDownloadMediaInfo>() {
                @Override
                public int compare(AliyunDownloadMediaInfo mediaInfo1, AliyunDownloadMediaInfo mediaInfo2) {
                    if (mediaInfo1.getSize() > mediaInfo2.getSize()) {
                        return 1;
                    }
                    if (mediaInfo1.getSize() < mediaInfo2.getSize()) {
                        return -1;
                    }

                    if (mediaInfo1.getSize() == mediaInfo2.getSize()) {
                        return 0;
                    }
                    return 0;
                }
            });
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onDownloadPrepared(infos);
            }
        }

        @Override
        public void onAdd(AliyunDownloadMediaInfo info) {
//            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
//            if (aliyunPlayerSkinActivity != null) {
//                if (aliyunPlayerSkinActivity.downloadDataProvider != null) {
//                    aliyunPlayerSkinActivity.downloadDataProvider.addDownloadMediaInfo(info);
//                }
//            }
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo info) {
//            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
//            if (aliyunPlayerSkinActivity != null) {
//                if (aliyunPlayerSkinActivity.dialogDownloadView != null) {
//                    aliyunPlayerSkinActivity.dialogDownloadView.updateInfo(info);
//                }
//                if (aliyunPlayerSkinActivity.downloadView != null) {
//                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
//                }
//
//            }
        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo info, int percent) {
//            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
//            if (aliyunPlayerSkinActivity != null) {
//                if (aliyunPlayerSkinActivity.dialogDownloadView != null) {
//                    aliyunPlayerSkinActivity.dialogDownloadView.updateInfo(info);
//                }
//                if (aliyunPlayerSkinActivity.downloadView != null) {
//                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
//                }
//            }
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo info) {
//            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
//            if (aliyunPlayerSkinActivity != null) {
//                if (aliyunPlayerSkinActivity.dialogDownloadView != null) {
//                    aliyunPlayerSkinActivity.dialogDownloadView.updateInfo(info);
//                }
//                if (aliyunPlayerSkinActivity.downloadView != null) {
//                    aliyunPlayerSkinActivity.downloadView.updateInfo(info);
//                }
//            }
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo info) {
//            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
//            if (aliyunPlayerSkinActivity != null) {
//                synchronized (aliyunPlayerSkinActivity){
//                    if (aliyunPlayerSkinActivity.downloadView != null) {
//                        aliyunPlayerSkinActivity.downloadView.updateInfoByComplete(info);
//                    }
//
//                    if (aliyunPlayerSkinActivity.dialogDownloadView != null) {
//                        aliyunPlayerSkinActivity.dialogDownloadView.updateInfoByComplete(info);
//                    }
//
//                    if (aliyunPlayerSkinActivity.downloadDataProvider != null) {
//                        aliyunPlayerSkinActivity.downloadDataProvider.addDownloadMediaInfo(info);
//                    }
//                }
//            }
        }

        @Override
        public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (code.getValue() == ErrorCode.ERROR_SERVER_POP_TOKEN_EXPIRED.getValue()
                        || code.getValue() == ErrorCode.ERROR_SERVER_VOD_INVALIDAUTHINFO_EXPIRETIME.getValue()) {
                    //鉴权过期
                    aliyunPlayerSkinActivity.refresh(true);
                } else {
                    if (aliyunPlayerSkinActivity.mDownloadProgressBar != null) {
                        aliyunPlayerSkinActivity.mDownloadProgressBar.setVisibility(View.GONE);
                    }
                    aliyunPlayerSkinActivity.mIsLoadDownloadInfo = false;
                    Toast.makeText(aliyunPlayerSkinActivity, code.getValue() + " --- " + msg, Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo info) {
        }

        @Override
        public void onDelete(AliyunDownloadMediaInfo info) {
            if (info != null && Global.mDownloadMediaLists.size() > 0) {
                Global.mDownloadMediaLists.remove(info);
            }
        }

        @Override
        public void onDeleteAll() {
            if (Global.mDownloadMediaLists.size() > 0) {
                Global.mDownloadMediaLists.clear();
            }
        }

        @Override
        public void onFileProgress(AliyunDownloadMediaInfo info) {

        }
    }

    private void onDownloadPrepared(final List<AliyunDownloadMediaInfo> infos) {
        //数据源
        ArrayList<String> selectors = new ArrayList<>();
        for (AliyunDownloadMediaInfo info : infos) {
            selectors.add(QualityItem.getItem(this, info.getQuality(), false).getName());
        }

//        清晰度DialogFragment
        mAlivcListSelectorDialogFragment = new AlivcListSelectorDialogFragment.Builder(getSupportFragmentManager())
                .setGravity(Gravity.BOTTOM)
                .setCancelableOutside(true)
                .setItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_red_wine))
                .setUnItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_black))
                .setNewData(selectors)
                .setDialogAnimationRes(R.style.Dialog_Animation)
                .setOnListItemSelectedListener(new AlivcListSelectorDialogFragment.OnListItemSelectedListener() {
                    private File mFile;
                    private String mPath;

                    @Override
                    public void onClick(String position) {
                        for (AliyunDownloadMediaInfo info : infos) {
                            if (QualityItem.getItem(AliyunPlayerSkinActivity.this, info.getQuality(), false).getName().equals(position)) {
                                if (Global.mDownloadMediaLists.contains(info)) {
                                    AliyunDownloadMediaInfo aliyunDownloadMediaInfo = Global.mDownloadMediaLists.get(Global.mDownloadMediaLists.indexOf(info));
                                    String savePath = aliyunDownloadMediaInfo.getSavePath();
                                    mPath = TextUtils.isEmpty(savePath) ? "" : savePath;
                                    mFile = new File(mPath);
                                    //手动删除本地文件后,无法再次下载问题
                                    if (mFile.exists()) {
                                        Toast.makeText(AliyunPlayerSkinActivity.this, getString(R.string.alivc_player_download_repeat_add), Toast.LENGTH_SHORT).show();
                                    } else {
                                        mAliyunDownloadManager.startDownload(info);
                                    }
                                } else {
                                    mAliyunDownloadManager.startDownload(info);
                                    if (!Global.mDownloadMediaLists.contains(info)) {
                                        Global.mDownloadMediaLists.add(0, info);
                                    }
                                }
                                break;
                            }
                        }
                    }
                })
                .create()
                .show();

        if (mDownloadProgressBar != null) {
            mDownloadProgressBar.setVisibility(View.GONE);
        }

        if (mAlivcListSelectorDialogFragment != null) {
            mAlivcListSelectorDialogFragment.setPosition(QualityItem.getItem(this, "", false).getName());
        }
        mIsLoadDownloadInfo = false;
    }

    private static class MyOnTrackChangedListener implements IPlayer.OnTrackChangedListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTrackChangedListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onChangedSuccess(TrackInfo trackInfo) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.changeTrackSuccess(trackInfo);
            }
        }

        @Override
        public void onChangedFail(TrackInfo trackInfo, com.aliyun.player.bean.ErrorInfo errorInfo) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.changeTrackFail(trackInfo, errorInfo);
            }
        }
    }

    private void changeTrackFail(TrackInfo trackInfo, com.aliyun.player.bean.ErrorInfo errorInfo) {
        if (showMoreDialog != null && showMoreDialog.isShowing()) {
            showMoreDialog.dismiss();
        }
        Toast.makeText(this, getString(R.string.alivc_player_track_change_error, errorInfo.getCode(), errorInfo.getMsg()), Toast.LENGTH_SHORT).show();
    }

    private void changeTrackSuccess(TrackInfo trackInfo) {
        if (trackInfo == null) {
            return;
        }
        if (showMoreDialog != null && showMoreDialog.isShowing()) {
            showMoreDialog.dismiss();
        }
        if (trackInfo.getType() == TrackInfo.Type.TYPE_VIDEO) {
            //码率
            Toast.makeText(this, getString(R.string.alivc_player_track_bitrate_change_success, trackInfo.getVideoBitrate() + ""), Toast.LENGTH_SHORT).show();
        } else if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
            Toast.makeText(this, getString(R.string.alivc_player_track_definition_change_success, trackInfo.getVodDefinition()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.alivc_player_track_change_success, trackInfo.getDescription()), Toast.LENGTH_SHORT).show();
        }
    }


    private static class MyStoppedListener implements OnStoppedListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyStoppedListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<>(skinActivity);
        }

        @Override
        public void onStop() {
            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    private static class MyRefreshStsCallback implements RefreshStsCallback {

        @Override
        public VidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            //NOTE: 注意：这个不能启动线程去请求。因为这个方法已经在线程中调用了。
            VidSts vidSts = VidStsUtil.getVidSts(vid);
            if (vidSts == null) {
                return null;
            } else {
                vidSts.setVid(vid);
                vidSts.setQuality(quality, true);
                vidSts.setTitle(title);
                return vidSts;
            }
        }
    }

    private void onStopped() {
        Toast.makeText(this, R.string.log_play_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (!GlobalPlayerConfig.PlayConfig.mEnablePlayBackground || mIsFromDownloadActivity) {
            if (mAliyunVodPlayerView != null) {
                mAliyunVodPlayerView.setAutoPlay(true);
                mAliyunVodPlayerView.onResume();
            }
            GlobalPlayerConfig.mCurrentPlayType = mCurrentPlayType;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsFromDownloadActivity = false;
        if (!GlobalPlayerConfig.PlayConfig.mEnablePlayBackground) {
            if (mAliyunVodPlayerView != null) {
                mAliyunVodPlayerView.setAutoPlay(false);
                mAliyunVodPlayerView.onStop();
            }
            mCurrentPlayType = GlobalPlayerConfig.mCurrentPlayType;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (mDownloadListTextView != null) {
                // 不显示下载按钮
                // mDownloadListTextView.setVisibility(orientation == Configuration.ORIENTATION_PORTRAIT ? View.VISIBLE : View.GONE);
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }

        if (mAliyunDownloadManager != null) {
            mAliyunDownloadManager.removeDownloadInfoListener(myDownloadInfoListener);
            myDownloadInfoListener = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode();
    }

    private static class MyOnScreenCostingSingleTagListener implements OnScreenCostingSingleTagListener {

        private final WeakReference<AliyunPlayerSkinActivity> weakReference;

        private MyOnScreenCostingSingleTagListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onScreenCostingSingleTag() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.screenCostingSingleTag();
            }
        }
    }

    private void screenCostingSingleTag() {
        if (screenShowMoreDialog != null && screenShowMoreDialog.isShowing()) {
            screenShowMoreDialog.dismiss();
        }
    }

    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOrientationChangeListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            AliyunPlayerSkinActivity activity = weakReference.get();

            if (activity != null) {
                if (currentMode == AliyunScreenMode.Small
                        && GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL
                        && !TextUtils.isEmpty(activity.mLocalVideoPath)) {
                    //如果播放本地视频，切换到小屏后，直接关闭
                    activity.finish();
                } else {
                    activity.hideDownloadDialog(from, currentMode);
                    activity.hideShowMoreDialog(from, currentMode);
                    activity.hideDanmakuSettingDialog(from, currentMode);
                    activity.hideScreenSostDialog(from, currentMode);
                }
            }
        }
    }

    private void hideShowMoreDialog(boolean from, AliyunScreenMode currentMode) {
        if (showMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                showMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    private void hideDanmakuSettingDialog(boolean fromUser, AliyunScreenMode currentMode) {
        if (danmakuShowMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                danmakuShowMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    private void hideScreenSostDialog(boolean fromUser, AliyunScreenMode currentMode) {
        if (screenShowMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                screenShowMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    private void hideDownloadDialog(boolean from, AliyunScreenMode currentMode) {

        if (mAlivcListSelectorDialogFragment != null) {
            if (currentScreenMode != currentMode) {
                mAlivcListSelectorDialogFragment.dismiss();
                currentScreenMode = currentMode;
            }
        }
        // 不显示下载按钮
        // mDownloadListTextView.setVisibility(currentMode == AliyunScreenMode.Small ? View.VISIBLE : View.GONE);
    }

    /**
     * 判断是否有网络的监听
     */
    private class MyNetConnectedListener implements AliyunVodPlayerView.NetConnectedListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyNetConnectedListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onReNetConnected(isReconnect);
            }
        }

        @Override
        public void onNetUnConnected() {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onNetUnConnected();
            }
        }
    }

    private void onNetUnConnected() {
        currentError = ErrorInfo.UnConnectInternet;
//        if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
//            ConcurrentLinkedQueue<AliyunDownloadMediaInfo> allDownloadMediaInfo = new ConcurrentLinkedQueue<>();
//            List<AliyunDownloadMediaInfo> mediaInfos = downloadDataProvider.getAllDownloadMediaInfo();
//            allDownloadMediaInfo.addAll(mediaInfos);
//            downloadManager.stopDownloads(allDownloadMediaInfo);
//        }
    }

    private void onReNetConnected(boolean isReconnect) {
        currentError = ErrorInfo.Normal;
    }

    /**
     * 因为鉴权过期,而去重新鉴权
     */
    private static class RetryExpiredSts implements VidStsUtil.OnStsResultListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public RetryExpiredSts(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(String vid, String akid, String akSecret, String token) {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onStsRetrySuccess(vid, akid, akSecret, token);
            }
        }

        @Override
        public void onFail() {

        }
    }

    private void onStsRetrySuccess(String mVid, String akid, String akSecret, String token) {
        GlobalPlayerConfig.mVid = mVid;
        GlobalPlayerConfig.mStsAccessKeyId = akid;
        GlobalPlayerConfig.mStsAccessKeySecret = akSecret;
        GlobalPlayerConfig.mStsSecurityToken = token;

        inRequest = false;
        mIsTimeExpired = false;

        VidSts vidSts = getVidSts(mVid);
        mAliyunVodPlayerView.setVidSts(vidSts);
    }

    public static class MyOnTimeExpiredErrorListener implements AliyunVodPlayerView.OnTimeExpiredErrorListener {

        WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTimeExpiredErrorListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onTimeExpiredError() {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onTimExpiredError();
            }
        }
    }

    /**
     * 鉴权过期
     */
    private void onTimExpiredError() {
        VidStsUtil.getVidSts(GlobalPlayerConfig.mVid, new RetryExpiredSts(this));
    }

    private static class MyShowMoreClickLisener implements ControlView.OnShowMoreClickListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        MyShowMoreClickLisener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void showMore() {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                activity.showMore(activity);
            }
        }
    }

    private void showMore(final AliyunPlayerSkinActivity activity) {
        showMoreDialog = new AlivcShowMoreDialog(activity);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume((int) mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScaleMode(mAliyunVodPlayerView.getScaleMode());
        moreValue.setLoop(mAliyunVodPlayerView.isLoop());

        ShowMoreView showMoreView = new ShowMoreView(activity, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();

        showMoreView.setOnScreenCastButtonClickListener(new ShowMoreView.OnScreenCastButtonClickListener() {
            @Override
            public void onScreenCastClick() {
            }
        });

        showMoreView.setOnBarrageButtonClickListener(new ShowMoreView.OnBarrageButtonClickListener() {
            @Override
            public void onBarrageClick() {
                if (showMoreDialog != null && showMoreDialog.isShowing()) {
                    showMoreDialog.dismiss();
                }
                showDanmakuSettingView();
            }
        });

        showMoreView.setOnSpeedCheckedChangedListener(new ShowMoreView.OnSpeedCheckedChangedListener() {
            @Override
            public void onSpeedChanged(RadioGroup group, int checkedId) {
                // 点击速度切换
                if (checkedId == R.id.rb_speed_normal) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
                } else if (checkedId == R.id.rb_speed_onequartern) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneQuartern);
                } else if (checkedId == R.id.rb_speed_onehalf) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneHalf);
                } else if (checkedId == R.id.rb_speed_twice) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
                }

            }
        });

        showMoreView.setOnScaleModeCheckedChangedListener(new ShowMoreView.OnScaleModeCheckedChangedListener() {
            @Override
            public void onScaleModeChanged(RadioGroup group, int checkedId) {
                //切换画面比例
                IPlayer.ScaleMode mScaleMode;
                if (checkedId == R.id.rb_scale_aspect_fit) {
                    mScaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FIT;
                } else if (checkedId == R.id.rb_scale_aspect_fill) {
                    mScaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FILL;
                } else if (checkedId == R.id.rb_scale_to_fill) {
                    mScaleMode = IPlayer.ScaleMode.SCALE_TO_FILL;
                } else {
                    mScaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FIT;
                }
                mAliyunVodPlayerView.setScaleMode(mScaleMode);
            }
        });

        showMoreView.setOnLoopCheckedChangedListener(new ShowMoreView.OnLoopCheckedChangedListener() {
            @Override
            public void onLoopChanged(RadioGroup group, int checkedId) {
                boolean isLoop;
                if (checkedId == R.id.rb_loop_open) {
                    isLoop = true;
                } else {
                    isLoop = false;
                }
                mAliyunVodPlayerView.setLoop(isLoop);
            }
        });

        /**
         * 初始化亮度
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setBrightness(mAliyunVodPlayerView.getScreenBrightness());
        }
        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                setWindowBrightness(progress);
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setScreenBrightness(progress);
                }
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

        /**
         * 初始化音量
         */
        if (mAliyunVodPlayerView != null) {
            showMoreView.setVoiceVolume(mAliyunVodPlayerView.getCurrentVolume());
        }
        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentVolume(progress / 100.00f);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

    }

    /**
     * 显示弹幕设置对话框
     */
    private void showDanmakuSettingView() {
        danmakuShowMoreDialog = new AlivcShowMoreDialog(this);
        mDanmakuSettingView = new DanmakuSettingView(this);
        mDanmakuSettingView.setAlphaProgress(mAlphProgress);
        mDanmakuSettingView.setSpeedProgress(mSpeedProgress);
        mDanmakuSettingView.setRegionProgress(mRegionProgress);
        danmakuShowMoreDialog.setContentView(mDanmakuSettingView);
        danmakuShowMoreDialog.show();

        //透明度
        mDanmakuSettingView.setOnAlphaSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAlphProgress = progress;
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setDanmakuAlpha(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //显示区域
        mDanmakuSettingView.setOnRegionSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRegionProgress = progress;
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setDanmakuRegion(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //速率
        mDanmakuSettingView.setOnSpeedSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpeedProgress = progress;
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setDanmakuSpeed(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //恢复默认
        mDanmakuSettingView.setOnDefaultListener(new DanmakuSettingView.OnDefaultClickListener() {
            @Override
            public void onDefaultClick() {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setDanmakuDefault();
                }
            }
        });

    }

    /**
     * 获取url的scheme
     *
     * @param url
     * @return
     */
    private String getUrlScheme(String url) {
        return Uri.parse(url).getScheme();
    }

    private static class MyPlayStateBtnClickListener implements AliyunVodPlayerView.OnPlayStateBtnClickListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        MyPlayStateBtnClickListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onPlayBtnClick(int playerState) {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onPlayStateSwitch(playerState);
            }
        }
    }

    /**
     * 播放状态切换
     */
    private void onPlayStateSwitch(int playerState) {
        if (playerState == IPlayer.started) {

        } else if (playerState == IPlayer.paused) {

        }

    }

    private static class MySeekCompleteListener implements IPlayer.OnSeekCompleteListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        MySeekCompleteListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeekComplete() {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onSeekComplete();
            }
        }
    }

    private void onSeekComplete() {
    }

    private static class MySeekStartListener implements AliyunVodPlayerView.OnSeekStartListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        MySeekStartListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeekStart(int position) {
            AliyunPlayerSkinActivity activity = weakReference.get();
            if (activity != null) {
                activity.onSeekStart(position);
            }
        }
    }

    private static class MyOnFinishListener implements AliyunVodPlayerView.OnFinishListener {

        WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnFinishListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onFinishClick() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.finish();
            }
        }
    }

    private static class MyOnScreenBrightnessListener implements AliyunVodPlayerView.OnScreenBrightnessListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnScreenBrightnessListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onScreenBrightness(int brightness) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.setWindowBrightness(brightness);
                if (aliyunPlayerSkinActivity.mAliyunVodPlayerView != null) {
                    aliyunPlayerSkinActivity.mAliyunVodPlayerView.setScreenBrightness(brightness);
                }
            }
        }
    }

    /**
     * 软键盘隐藏监听
     */
    private static class MyOnSoftKeyHideListener implements AliyunVodPlayerView.OnSoftKeyHideListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnSoftKeyHideListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void softKeyHide() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.hideSoftKeyBoard(aliyunPlayerSkinActivity);
            }
        }

        @Override
        public void onClickPaint() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onSoftKeyShow();

            }
        }
    }

    private void onSoftKeyShow() {
        if (mSoftInputDialogFragment != null) {
            mSoftInputDialogFragment.show(getSupportFragmentManager(), "SoftInputDialogFragment");
        }
    }

    /**
     * 播放器出错监听
     */
    private static class MyOnErrorListener implements IPlayer.OnErrorListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnErrorListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(com.aliyun.player.bean.ErrorInfo errorInfo) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onError(errorInfo);
            }
        }
    }

    private void onError(com.aliyun.player.bean.ErrorInfo errorInfo) {
        //鉴权过期
        if (errorInfo.getCode().getValue() == ErrorCode.ERROR_SERVER_POP_UNKNOWN.getValue()) {
            mIsTimeExpired = true;
        }
    }

    /**
     * 字幕、清晰度、码率、音轨点击事件
     */
    private static class MyOnTrackInfoClickListener implements ControlView.OnTrackInfoClickListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTrackInfoClickListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        //字幕
        @Override
        public void onSubtitleClick(List<TrackInfo> subtitleTrackInfoList) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onSubtitleClick(subtitleTrackInfoList);
            }
        }

        //音轨
        @Override
        public void onAudioClick(List<TrackInfo> audioTrackInfoList) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onAudioClick(audioTrackInfoList);
            }
        }

        //码率
        @Override
        public void onBitrateClick(List<TrackInfo> bitrateTrackInfoList) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onBitrateClick(bitrateTrackInfoList);
            }
        }

        //清晰度
        @Override
        public void onDefinitionClick(List<TrackInfo> definitionTrackInfoList) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onDefinitionClick(definitionTrackInfoList);
            }
        }
    }

    /**
     * 字幕改变事件
     */
    private void onSubtitleClick(List<TrackInfo> subtitleTrackInfoList) {
        showMoreDialog = new AlivcShowMoreDialog(this);
        final TrackInfoView mTrackInfoView = new TrackInfoView(this);
        mTrackInfoView.setTrackInfoLists(subtitleTrackInfoList);
        mTrackInfoView.setCurrentTrackInfo(mAliyunVodPlayerView.currentTrack(TrackInfo.Type.TYPE_SUBTITLE));
        showMoreDialog.setContentView(mTrackInfoView);
        showMoreDialog.show();

        mTrackInfoView.setOnSubtitleChangedListener(new TrackInfoView.OnSubtitleChangedListener() {
            @Override
            public void onSubtitleChanged(TrackInfo selectTrackInfo) {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.selectTrack(selectTrackInfo);
                }
            }

            @Override
            public void onSubtitleCancel() {
                Toast.makeText(AliyunPlayerSkinActivity.this, R.string.alivc_player_cancel_subtitle, Toast.LENGTH_SHORT).show();
                if (mAliyunVodPlayerView != null) {
//                    mAliyunVodPlayerView.cancelSubtitle();
                }
            }
        });
    }

    /**
     * 音轨改变事件
     */
    private void onAudioClick(List<TrackInfo> audioTrackInfoList) {
        showMoreDialog = new AlivcShowMoreDialog(this);
        final TrackInfoView mTrackInfoView = new TrackInfoView(this);
        mTrackInfoView.setTrackInfoLists(audioTrackInfoList);
        mTrackInfoView.setCurrentTrackInfo(mAliyunVodPlayerView.currentTrack(TrackInfo.Type.TYPE_AUDIO));
        showMoreDialog.setContentView(mTrackInfoView);
        showMoreDialog.show();

        mTrackInfoView.setOnAudioChangedListener(new TrackInfoView.OnAudioChangedListener() {
            @Override
            public void onAudioChanged(TrackInfo selectTrackInfo) {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.selectTrack(selectTrackInfo);
                }
            }
        });
    }

    /**
     * 码率改变事件
     */
    private void onBitrateClick(List<TrackInfo> bitrateTrackInfoList) {
        showMoreDialog = new AlivcShowMoreDialog(this);
        final TrackInfoView mTrackInfoView = new TrackInfoView(this);
        mTrackInfoView.setTrackInfoLists(bitrateTrackInfoList);
        mTrackInfoView.setCurrentTrackInfo(mAliyunVodPlayerView.currentTrack(TrackInfo.Type.TYPE_VIDEO));
        showMoreDialog.setContentView(mTrackInfoView);
        showMoreDialog.show();

        mTrackInfoView.setOnBitrateChangedListener(new TrackInfoView.OnBitrateChangedListener() {
            @Override
            public void onBitrateChanged(TrackInfo selectTrackInfo, int checkedId) {
                if (mAliyunVodPlayerView != null) {
                    if (checkedId == R.id.auto_bitrate) {
                        mAliyunVodPlayerView.selectAutoBitrateTrack();
                    } else {
                        mAliyunVodPlayerView.selectTrack(selectTrackInfo);
                    }
                }
            }
        });
    }

    /**
     * 清晰度改变事件
     */
    private void onDefinitionClick(List<TrackInfo> definitionTrackInfoList) {
        showMoreDialog = new AlivcShowMoreDialog(this);
        final TrackInfoView mTrackInfoView = new TrackInfoView(this);
        mTrackInfoView.setTrackInfoLists(definitionTrackInfoList);
        mTrackInfoView.setCurrentTrackInfo(mAliyunVodPlayerView.currentTrack(TrackInfo.Type.TYPE_VOD));
        showMoreDialog.setContentView(mTrackInfoView);
        showMoreDialog.show();

        mTrackInfoView.setOnDefinitionChangedListener(new TrackInfoView.OnDefinitionChangedListrener() {
            @Override
            public void onDefinitionChanged(TrackInfo selectTrackInfo) {
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.selectTrack(selectTrackInfo);
                }
            }
        });
    }

    private static class MyOnInfoListener implements IPlayer.OnInfoListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnInfoListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onInfo(InfoBean infoBean) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onInfo(infoBean);
            }
        }
    }

    private void onInfo(InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.CacheSuccess) {
            Toast.makeText(this, R.string.alivc_player_cache_success, Toast.LENGTH_SHORT).show();
        } else if (infoBean.getCode() == InfoCode.CacheError) {
            Toast.makeText(this, infoBean.getExtraMsg(), Toast.LENGTH_SHORT).show();
        } else if (infoBean.getCode() == InfoCode.SwitchToSoftwareVideoDecoder) {
            Toast.makeText(this, R.string.alivc_player_switch_to_software_video_decoder, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * sei监听事件
     */
    private static class MyOnSeiDataListener implements IPlayer.OnSeiDataListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnSeiDataListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onSeiData(int type, byte[] bytes) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onSeiData(type, bytes);
            }
        }
    }

    private void onSeiData(int type, byte[] bytes) {
        Log.e(TAG, "onSeiData: type = " + type + " data = " + new String(bytes));
    }

    /**
     * TipsView点击监听事件
     */
    private static class MyOnTipClickListener implements TipsView.OnTipClickListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTipClickListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onContinuePlay() {

        }

        @Override
        public void onStopPlay() {

        }

        @Override
        public void onRetryPlay(int errorCode) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                if (errorCode == ErrorCode.ERROR_LOADING_TIMEOUT.getValue()) {
                    aliyunPlayerSkinActivity.mAliyunVodPlayerView.reTry();
                } else {
                    aliyunPlayerSkinActivity.refresh(false);
                }
            }
        }

        @Override
        public void onReplay() {

        }

        @Override
        public void onRefreshSts() {

        }

        @Override
        public void onWait() {

        }

        @Override
        public void onExit() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.finish();
            }
        }
    }

    /**
     * 重试
     */
    private void refresh(final boolean isDownload) {
        if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String errorMsg) {
                    ToastUtils.show(AliyunPlayerSkinActivity.this, errorMsg);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mStsAccessKeyId = dataBean.getAccessKeyId();
                        GlobalPlayerConfig.mStsSecurityToken = dataBean.getSecurityToken();
                        GlobalPlayerConfig.mStsAccessKeySecret = dataBean.getAccessKeySecret();

                        VidSts vidSts = getVidSts(mCurrentVideoId);
                        if (isDownload) {
                            mAliyunDownloadManager.setmVidSts(vidSts);
                            mAliyunDownloadManager.prepareDownload(vidSts);
                        } else {
                            if (mAliyunVodPlayerView != null) {
                                mAliyunVodPlayerView.setVidSts(vidSts);
                            }
                        }

                    }
                }
            });
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayAuthInfo(new GetAuthInformation.OnGetPlayAuthInfoListener() {
                @Override
                public void onGetPlayAuthError(String msg) {
                    ToastUtils.show(AliyunPlayerSkinActivity.this, msg);
                }

                @Override
                public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mPlayAuth = dataBean.getPlayAuth();

                        VidAuth vidAuth = getVidAuth(mCurrentVideoId);
                        if (isDownload) {
                            mAliyunDownloadManager.setmVidAuth(vidAuth);
                            mAliyunDownloadManager.prepareDownload(vidAuth);
                        } else {
                            if (mAliyunVodPlayerView != null) {
                                mAliyunVodPlayerView.setAuthInfo(vidAuth);
                            }
                        }

                    }
                }
            });
        } else if (GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayMpsInfo(new GetAuthInformation.OnGetMpsInfoListener() {
                @Override
                public void onGetMpsError(String msg) {
                    ToastUtils.show(AliyunPlayerSkinActivity.this, msg);
                }

                @Override
                public void onGetMpsSuccess(AliyunMps.MpsBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mMpsRegion = dataBean.getRegionId();
                        GlobalPlayerConfig.mMpsAuthInfo = dataBean.getAuthInfo();
                        GlobalPlayerConfig.mMpsHlsUriToken = dataBean.getHlsUriToken();
                        GlobalPlayerConfig.mMpsAccessKeyId = dataBean.getAkInfo().getAccessKeyId();
                        GlobalPlayerConfig.mMpsSecurityToken = dataBean.getAkInfo().getSecurityToken();
                        GlobalPlayerConfig.mMpsAccessKeySecret = dataBean.getAkInfo().getAccessKeySecret();

                        VidMps vidMps = getVidMps(mCurrentVideoId);
                        if (mAliyunVodPlayerView != null) {
                            mAliyunVodPlayerView.setVidMps(vidMps);
                        }
                    }
                }
            });
        } else {
            if (mAliyunVodPlayerView != null) {
                mAliyunVodPlayerView.reTry();
            }
        }
    }

    /**
     * TipsView返回按钮点击事件
     */
    private static class MyOnTipsViewBackClickListener implements OnTipsViewBackClickListener {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnTipsViewBackClickListener(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public void onBackClick() {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                aliyunPlayerSkinActivity.onTipsViewClick();
            }
        }
    }

    private void onTipsViewClick() {
        finish();
    }

    private static class MyOnVerifyStsCallback implements AliPlayer.OnVerifyTimeExpireCallback {

        private WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnVerifyStsCallback(AliyunPlayerSkinActivity aliyunPlayerSkinActivity) {
            weakReference = new WeakReference<>(aliyunPlayerSkinActivity);
        }

        @Override
        public AliPlayer.Status onVerifySts(StsInfo stsInfo) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                return aliyunPlayerSkinActivity.onVerifySts(stsInfo);
            }
            return AliPlayer.Status.Valid;
        }

        @Override
        public AliPlayer.Status onVerifyAuth(VidAuth vidAuth) {
            AliyunPlayerSkinActivity aliyunPlayerSkinActivity = weakReference.get();
            if (aliyunPlayerSkinActivity != null) {
                return aliyunPlayerSkinActivity.onVerifyAuth(vidAuth);
            }
            return AliPlayer.Status.Valid;
        }
    }

    private AliPlayer.Status onVerifyAuth(final VidAuth vidAuth) {
        Log.e(TAG, "onVerifyAuth: ");
        String mLiveExpiration = GlobalPlayerConfig.mLiveExpiration;
        long expirationInGMTFormat = TimeFormater.getExpirationInGMTFormat(mLiveExpiration);
        //判断鉴权信息是否过期
        if (TextUtils.isEmpty(mLiveExpiration) || DateUtil.getFixedSkewedTimeMillis() / 1000 > expirationInGMTFormat - 5 * 60) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayAuthInfo(new GetAuthInformation.OnGetPlayAuthInfoListener() {
                @Override
                public void onGetPlayAuthError(String msg) {
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                    }
                    ToastUtils.show(AliyunPlayerSkinActivity.this, "Get Auth Info error : " + msg);
                }

                @Override
                public void onGetPlayAuthSuccess(AliyunPlayAuth.PlayAuthBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mLivePlayAuth = dataBean.getPlayAuth();
                        if (mAliyunVodPlayerView != null) {
                            vidAuth.setPlayAuth(GlobalPlayerConfig.mLivePlayAuth);
                            mAliyunVodPlayerView.updateAuthInfo(vidAuth);
                        }
                    }
                }
            });
            Log.e(TAG, "refreshAuth: ");
            return AliPlayer.Status.Pending;
        } else {
            Log.e(TAG, "IPlayer.AuthStatus.Valid: ");
            return AliPlayer.Status.Valid;
        }
    }

    private AliPlayer.Status onVerifySts(final StsInfo stsInfo) {
        Log.e(TAG, "onVerifySts: ");
        String mLiveExpiration = GlobalPlayerConfig.mLiveExpiration;
        long expirationInGMTFormat = TimeFormater.getExpirationInGMTFormat(mLiveExpiration);
        //判断鉴权信息是否过期
        if ((TextUtils.isEmpty(mLiveExpiration) || DateUtil.getFixedSkewedTimeMillis() / 1000 > expirationInGMTFormat - 5 * 60) && GlobalPlayerConfig.mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.LIVE_STS) {
            GetAuthInformation getAuthInformation = new GetAuthInformation();
            getAuthInformation.getVideoPlayLiveStsInfo(new GetAuthInformation.OnGetStsInfoListener() {
                @Override
                public void onGetStsError(String msg) {
                    if (mAliyunVodPlayerView != null) {
                        mAliyunVodPlayerView.onStop();
                    }
                    ToastUtils.show(AliyunPlayerSkinActivity.this, "Get Sts Info error : " + msg);
                }

                @Override
                public void onGetStsSuccess(AliyunSts.StsBean dataBean) {
                    if (dataBean != null) {
                        GlobalPlayerConfig.mLiveStsAccessKeyId = dataBean.getAccessKeyId();
                        GlobalPlayerConfig.mLiveStsSecurityToken = dataBean.getSecurityToken();
                        GlobalPlayerConfig.mLiveStsAccessKeySecret = dataBean.getAccessKeySecret();
                        GlobalPlayerConfig.mLiveExpiration = dataBean.getExpiration();

                        if (mAliyunVodPlayerView != null) {
                            stsInfo.setAccessKeyId(GlobalPlayerConfig.mLiveStsAccessKeyId);
                            stsInfo.setAccessKeySecret(GlobalPlayerConfig.mLiveStsAccessKeySecret);
                            stsInfo.setSecurityToken(GlobalPlayerConfig.mLiveStsSecurityToken);
                            mAliyunVodPlayerView.updateStsInfo(stsInfo);
                        }
                    }
                }
            });
            Log.e(TAG, "refreshSts: ");
            return AliPlayer.Status.Pending;
        } else {
            Log.e(TAG, "IPlayer.StsStatus.Valid: ");
            return AliPlayer.Status.Valid;
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void onSeekStart(int position) {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && event.getKeyCode() == 67) {
//            if (mAliyunVodPlayerView != null && mAliyunVodPlayerView.getEditText() != null && mAliyunVodPlayerView.getEditText().getText().length() == 0) {
//                //删除按键监听,部分手机在EditText没有内容时,点击删除按钮会隐藏软键盘
//                return false;
//            }
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 设置屏幕亮度
     */
    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 100.00f;
        window.setAttributes(lp);
    }

    /**
     * 仅当系统的亮度模式是非自动模式的情况下，获取当前屏幕亮度值[0, 255].
     * 如果是自动模式，那么该方法获得的值不正确。
     */
    private int getCurrentBrightValue() {
        int nowBrightnessValue = 0;
        ContentResolver resolver = getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS, 255);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    public void setManualBright() {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AliyunPlayerDownloadListActivity.DOWNLOAD_ACTIVITY_FOR_REQUEST_CODE) {
            //当从下载界面回到播放界面时，改回调优先于onResume调用
            mIsFromDownloadActivity = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mCurrentPlayType", mCurrentPlayType.ordinal());
        if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.AUTH) {
            outState.putString("mVid", GlobalPlayerConfig.mVid);
            outState.putString("mRegion", GlobalPlayerConfig.mRegion);
            outState.putString("mPlayAuth", GlobalPlayerConfig.mPlayAuth);
            outState.putInt("mPreviewTime", GlobalPlayerConfig.mPreviewTime);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.STS) {
            outState.putString("mVid", GlobalPlayerConfig.mVid);
            outState.putString("mRegion", GlobalPlayerConfig.mRegion);
            outState.putString("mStsAccessKeyId", GlobalPlayerConfig.mStsAccessKeyId);
            outState.putString("mStsAccessKeySecret", GlobalPlayerConfig.mStsAccessKeySecret);
            outState.putString("mStsSecurityToken", GlobalPlayerConfig.mStsSecurityToken);
            outState.putInt("mPreviewTime", GlobalPlayerConfig.mPreviewTime);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.MPS) {
            outState.putString("mVid", GlobalPlayerConfig.mVid);
            outState.putString("mRegion", GlobalPlayerConfig.mRegion);
            outState.putString("mMpsAccessKeyId", GlobalPlayerConfig.mMpsAccessKeyId);
            outState.putString("mMpsAccessKeySecret", GlobalPlayerConfig.mMpsAccessKeySecret);
            outState.putString("mMpsSecurityToken", GlobalPlayerConfig.mMpsSecurityToken);
            outState.putString("mMpsHlsUriToken", GlobalPlayerConfig.mMpsHlsUriToken);
            outState.putString("mMpsAuthInfo", GlobalPlayerConfig.mMpsAuthInfo);
            outState.putInt("mPreviewTime", GlobalPlayerConfig.mPreviewTime);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.LIVE_STS) {
            outState.putString("mLiveStsUrl", GlobalPlayerConfig.mLiveStsUrl);
            outState.putString("mRegion", GlobalPlayerConfig.mRegion);
            outState.putString("mLiveStsAccessKeyId", GlobalPlayerConfig.mLiveStsAccessKeyId);
            outState.putString("mLiveStsAccessKeySecret", GlobalPlayerConfig.mLiveStsAccessKeySecret);
            outState.putString("mLiveStsSecurityToken", GlobalPlayerConfig.mLiveStsSecurityToken);
            outState.putString("mLiveStsDomain", GlobalPlayerConfig.mLiveStsDomain);
            outState.putString("mLiveStsApp", GlobalPlayerConfig.mLiveStsApp);
            outState.putString("mLiveStsStream", GlobalPlayerConfig.mLiveStsStream);
        } else if (mCurrentPlayType == GlobalPlayerConfig.PLAYTYPE.URL) {
            outState.putString("mUrlPath", GlobalPlayerConfig.mUrlPath);
        } else {
            //default
            outState.putString("mVid", GlobalPlayerConfig.mVid);
            outState.putString("mRegion", GlobalPlayerConfig.mRegion);
            outState.putString("mStsAccessKeyId", GlobalPlayerConfig.mStsAccessKeyId);
            outState.putString("mStsAccessKeySecret", GlobalPlayerConfig.mStsAccessKeySecret);
            outState.putString("mStsSecurityToken", GlobalPlayerConfig.mStsSecurityToken);
        }

        //PlayerConfig
        outState.putInt("mStartBufferDuration", GlobalPlayerConfig.PlayConfig.mStartBufferDuration);
        outState.putInt("mHighBufferDuration", GlobalPlayerConfig.PlayConfig.mHighBufferDuration);
        outState.putInt("mMaxBufferDuration", GlobalPlayerConfig.PlayConfig.mMaxBufferDuration);
        outState.putInt("mMaxDelayTime", GlobalPlayerConfig.PlayConfig.mMaxDelayTime);
        outState.putInt("mMaxProbeSize", GlobalPlayerConfig.PlayConfig.mMaxProbeSize);
        outState.putString("mReferrer", GlobalPlayerConfig.PlayConfig.mReferrer);
        outState.putString("mHttpProxy", GlobalPlayerConfig.PlayConfig.mHttpProxy);
        outState.putInt("mNetworkTimeout", GlobalPlayerConfig.PlayConfig.mNetworkTimeout);
        outState.putInt("mNetworkRetryCount", GlobalPlayerConfig.PlayConfig.mNetworkRetryCount);
        outState.putBoolean("mEnableSei", GlobalPlayerConfig.PlayConfig.mEnableSei);
        outState.putBoolean("mEnableClearWhenStop", GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop);
        outState.putBoolean("mAutoSwitchOpen", GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen);
        outState.putBoolean("mEnableAccurateSeekModule", GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule);
        outState.putBoolean("mEnablePlayBackground", GlobalPlayerConfig.PlayConfig.mEnablePlayBackground);
        outState.putBoolean("mEnableHardDecodeType", GlobalPlayerConfig.mEnableHardDecodeType);

        //CacheConfig
        outState.putBoolean("mEnableCache", GlobalPlayerConfig.PlayCacheConfig.mEnableCache);
        outState.putString("mDir", GlobalPlayerConfig.PlayCacheConfig.mDir);
        outState.putInt("mMaxDurationS", GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS);
        outState.putInt("mMaxSizeMB", GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB);
    }

    private void restoreSaveInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int mPlayType = savedInstanceState.getInt("mCurrentPlayType");
            int authType = GlobalPlayerConfig.PLAYTYPE.AUTH.ordinal();
            int stsType = GlobalPlayerConfig.PLAYTYPE.STS.ordinal();
            int mpsType = GlobalPlayerConfig.PLAYTYPE.MPS.ordinal();
            int urlType = GlobalPlayerConfig.PLAYTYPE.URL.ordinal();
            int liveStsType = GlobalPlayerConfig.PLAYTYPE.LIVE_STS.ordinal();
            if (mPlayType == authType) {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.AUTH;
                GlobalPlayerConfig.mVid = savedInstanceState.getString("mVid");
                GlobalPlayerConfig.mRegion = savedInstanceState.getString("mRegion");
                GlobalPlayerConfig.mPlayAuth = savedInstanceState.getString("mPlayAuth");
                GlobalPlayerConfig.mPreviewTime = savedInstanceState.getInt("mPreviewTime");
            } else if (mPlayType == stsType) {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.STS;
                GlobalPlayerConfig.mVid = savedInstanceState.getString("mVid");
                GlobalPlayerConfig.mRegion = savedInstanceState.getString("mRegion");
                GlobalPlayerConfig.mStsAccessKeyId = savedInstanceState.getString("mStsAccessKeyId");
                GlobalPlayerConfig.mStsAccessKeySecret = savedInstanceState.getString("mStsAccessKeySecret");
                GlobalPlayerConfig.mStsSecurityToken = savedInstanceState.getString("mStsSecurityToken");
                GlobalPlayerConfig.mPreviewTime = savedInstanceState.getInt("mPreviewTime");
            } else if (mPlayType == mpsType) {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.MPS;
                GlobalPlayerConfig.mVid = savedInstanceState.getString("mVid");
                GlobalPlayerConfig.mRegion = savedInstanceState.getString("mRegion");
                GlobalPlayerConfig.mMpsAccessKeyId = savedInstanceState.getString("mMpsAccessKeyId");
                GlobalPlayerConfig.mMpsAccessKeySecret = savedInstanceState.getString("mMpsAccessKeySecret");
                GlobalPlayerConfig.mMpsSecurityToken = savedInstanceState.getString("mMpsSecurityToken");
                GlobalPlayerConfig.mMpsHlsUriToken = savedInstanceState.getString("mMpsHlsUriToken");
                GlobalPlayerConfig.mMpsAuthInfo = savedInstanceState.getString("mMpsAuthInfo");
                GlobalPlayerConfig.mPreviewTime = savedInstanceState.getInt("mPreviewTime");
            } else if (mPlayType == urlType) {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.URL;
                GlobalPlayerConfig.mUrlPath = savedInstanceState.getString("mUrlPath");
            } else if (mPlayType == liveStsType) {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.LIVE_STS;
                GlobalPlayerConfig.mLiveStsUrl = savedInstanceState.getString("mLiveStsUrl");
                GlobalPlayerConfig.mRegion = savedInstanceState.getString("mRegion");
                GlobalPlayerConfig.mLiveStsAccessKeyId = savedInstanceState.getString("mLiveStsAccessKeyId");
                GlobalPlayerConfig.mLiveStsAccessKeySecret = savedInstanceState.getString("mLiveStsAccessKeySecret");
                GlobalPlayerConfig.mLiveStsSecurityToken = savedInstanceState.getString("mLiveStsSecurityToken");
                GlobalPlayerConfig.mLiveStsDomain = savedInstanceState.getString("mLiveStsDomain");
                GlobalPlayerConfig.mLiveStsApp = savedInstanceState.getString("mLiveStsApp");
                GlobalPlayerConfig.mLiveStsStream = savedInstanceState.getString("mLiveStsStream");
            } else {
                mCurrentPlayType = GlobalPlayerConfig.PLAYTYPE.DEFAULT;
                GlobalPlayerConfig.mVid = savedInstanceState.getString("mVid");
                GlobalPlayerConfig.mRegion = savedInstanceState.getString("mRegion");
                GlobalPlayerConfig.mStsAccessKeyId = savedInstanceState.getString("mStsAccessKeyId");
                GlobalPlayerConfig.mStsAccessKeySecret = savedInstanceState.getString("mStsAccessKeySecret");
                GlobalPlayerConfig.mStsSecurityToken = savedInstanceState.getString("mStsSecurityToken");
            }
            GlobalPlayerConfig.mCurrentPlayType = mCurrentPlayType;

            //PlayerConfig
            GlobalPlayerConfig.PlayConfig.mStartBufferDuration = savedInstanceState.getInt("mStartBufferDuration");
            GlobalPlayerConfig.PlayConfig.mHighBufferDuration = savedInstanceState.getInt("mHighBufferDuration");
            GlobalPlayerConfig.PlayConfig.mMaxBufferDuration = savedInstanceState.getInt("mMaxBufferDuration");
            GlobalPlayerConfig.PlayConfig.mMaxDelayTime = savedInstanceState.getInt("mMaxDelayTime");
            GlobalPlayerConfig.PlayConfig.mMaxProbeSize = savedInstanceState.getInt("mMaxProbeSize");
            GlobalPlayerConfig.PlayConfig.mReferrer = savedInstanceState.getString("mReferrer");
            GlobalPlayerConfig.PlayConfig.mHttpProxy = savedInstanceState.getString("mHttpProxy");
            GlobalPlayerConfig.PlayConfig.mNetworkTimeout = savedInstanceState.getInt("mNetworkTimeout");
            GlobalPlayerConfig.PlayConfig.mNetworkRetryCount = savedInstanceState.getInt("mNetworkRetryCount");
            GlobalPlayerConfig.PlayConfig.mEnableSei = savedInstanceState.getBoolean("mEnableSei");
            GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop = savedInstanceState.getBoolean("mEnableClearWhenStop");
            GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen = savedInstanceState.getBoolean("mAutoSwitchOpen");
            GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule = savedInstanceState.getBoolean("mEnableAccurateSeekModule");
            GlobalPlayerConfig.PlayConfig.mEnablePlayBackground = savedInstanceState.getBoolean("mEnablePlayBackground");

            //CacheConfig
            GlobalPlayerConfig.PlayCacheConfig.mEnableCache = savedInstanceState.getBoolean("mEnableCache");
            GlobalPlayerConfig.PlayCacheConfig.mDir = savedInstanceState.getString("mDir");
            GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS = savedInstanceState.getInt("mMaxDurationS");
            GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB = savedInstanceState.getInt("mMaxSizeMB");

            GlobalPlayerConfig.mEnableHardDecodeType = savedInstanceState.getBoolean("mEnableHardDecodeType");

        }
    }
}
