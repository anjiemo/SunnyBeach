package com.aliyun.player.alivcplayerexpand.constants;

import com.aliyun.player.IPlayer;

import java.io.File;

/**
 * 播放器配置
 */
public class GlobalPlayerConfig {

    /**
     * 截图成功图片保存地址
     */
    public static final String SNAP_SHOT_PATH = "snapShot" + File.separator;
    /**
     * 边播边缓存保存地址
     */
    public static final String CACHE_DIR_PATH = "cache" + File.separator;
    /**
     * 视频下载默认路径
     */
    public static final String DOWNLOAD_DIR_PATH = "download" + File.separator;
    /**
     * 加密文件保存路径
     */
    public static final String ENCRYPT_DIR_PATH = "encrypt" + File.separator;

    /**
     * Intent的key
     */
    public static class Intent_Key {
        /**
         * 本地视频地址
         */
        public static final String LOCAL_VIDEO_PATH = "local_video_path";

        /**
         * URL和本地视频播放方式下，是否只允许全屏播放
         */
        public static final String NEED_ONLY_FULL_SCREEN = "need_only_full_screen";
    }


    /**
     * 播放相关的配置
     */
    public static class PlayConfig {

        //artc判断
        private static final boolean IS_ARTC_URL = (mCurrentPlayType == PLAYTYPE.URL && mUrlPath.startsWith("artc"));
        //artp判断
        private static final boolean IS_ARTP_URL = (mCurrentPlayType == PLAYTYPE.URL && mUrlPath.startsWith("artp"));
        private static int MAX_DELAY_TIME_RESULT;

        static {
            if (IS_ARTC_URL) {
                MAX_DELAY_TIME_RESULT = 0;
            } else if (IS_ARTP_URL) {
                MAX_DELAY_TIME_RESULT = 100;
            } else {
                MAX_DELAY_TIME_RESULT = 5000;
            }
        }

        /**
         * 默认值
         */
        public static final int DEFAULT_START_BUFFER_DURATION = IS_ARTC_URL ? 10 : 500;
        public static final int DEFAULT_HIGH_BUFFER_DURATION = IS_ARTC_URL ? 10 : 3000;
        public static final int DEFAULT_MAX_BUFFER_DURATION = IS_ARTC_URL ? 150 : 50000;
        public static final int DEFAULT_NETWORK_RETRY_COUNT = 2;
        public static final int DEFAULT_MAX_DELAY_TIME = MAX_DELAY_TIME_RESULT;
        public static final int DEFAULT_PROBE_SIZE = -1;
        public static final int DEFAULT_NETWORK_TIMEOUT = 15000;
        public static final boolean DEFAULT_ENABLE_SEI = false;
        public static final boolean DEFAULT_ENABLE_CLEAR_WHEN_STOP = false;


        /**
         * 起播缓冲区时长,单位ms,默认500ms,值越小,起播越快,但可能会导致播放之后很快进入loading
         */
        public static int mStartBufferDuration = DEFAULT_START_BUFFER_DURATION;

        /**
         * 高缓冲时长,卡顿恢复,单位ms,默认3000ms,loading的缓冲区达到该值后,结束loading
         */
        public static int mHighBufferDuration = DEFAULT_HIGH_BUFFER_DURATION;
        /**
         * 最大缓冲区时长,单位ms,默认值50000ms,播放器每次最多加载这么长时间的缓冲数据
         */
        public static int mMaxBufferDuration = DEFAULT_MAX_BUFFER_DURATION;
        /**
         * 直播最大延时,单位ms,默认值5000ms,注意:直播有效。当延时比较大时,播放器sdk内部会追帧等,保证播放器的延时在这个范围内
         */
        public static int mMaxDelayTime = DEFAULT_MAX_DELAY_TIME;
        /**
         * 最大probe大小
         */
        public static int mMaxProbeSize = DEFAULT_PROBE_SIZE;
        /**
         * 请求refer
         */
        public static String mReferrer;
        /**
         * http代理
         */
        public static String mHttpProxy;
        /**
         * 网络超时时间,单位ms,默认值5000ms
         */
        public static int mNetworkTimeout = DEFAULT_NETWORK_TIMEOUT;
        /**
         * 网络重试次数,网络重试次数，每次间隔networkTimeout，networkRetryCount=0则表示不重试，重试策略app决定，默认值为2
         */
        public static int mNetworkRetryCount = DEFAULT_NETWORK_RETRY_COUNT;
        /**
         * 是否允许sei
         */
        public static boolean mEnableSei = DEFAULT_ENABLE_SEI;
        /**
         * 是否打开停止显示最后帧
         */
        public static boolean mEnableClearWhenStop = DEFAULT_ENABLE_CLEAR_WHEN_STOP;
        /**
         * auto开关是否打开
         */
        public static boolean mAutoSwitchOpen = false;
        /**
         * 是否开启精准seek
         */
        public static boolean mEnableAccurateSeekModule = false;
        /**
         * 是否允许后台播放
         */
        public static boolean mEnablePlayBackground = false;
    }

    /**
     * 缓存策略
     */
    public static class PlayCacheConfig {
        /**
         * 默认值
         */
        public static final boolean DEFAULT_ENABLE_CACHE = false;
        public static final int DEFAULT_MAX_DURATION_S = 100;
        public static final int DEFAULT_MAX_SIZE_MB = 200;

        /**
         * 缓存目录
         */
        public static String mDir = "";
        /**
         * 是否允许边播边缓存
         */
        public static boolean mEnableCache = DEFAULT_ENABLE_CACHE;
        /**
         * 设置能够缓存的单个文件的最大时长。如果文件的时长超过此时长，则不会缓存。单位：秒。
         */
        public static int mMaxDurationS = DEFAULT_MAX_DURATION_S;
        /**
         * 缓存目录的最大占用空间。如果超过，则删除最旧的文件。单位：MB
         */
        public static int mMaxSizeMB = DEFAULT_MAX_SIZE_MB;
    }

    /**
     * 镜像模式，默认无
     */
    public static IPlayer.MirrorMode mMirrorMode = IPlayer.MirrorMode.MIRROR_MODE_NONE;

    /**
     * 解码方式，默认硬解
     */
    public static boolean mEnableHardDecodeType = true;

    /**
     * 旋转角度，默认0
     */
    public static IPlayer.RotateMode mRotateMode = IPlayer.RotateMode.ROTATE_0;

    /**
     * vid
     */
    public static String mVid = "";

    /**
     * region 默认 cn-shanghai
     */
    public static String mRegion = "cn-shanghai";

    /**
     * 试看时间
     */
    public static int mPreviewTime = -1;

    /**
     * AccessKeyId
     */
    public static String mStsAccessKeyId = "";

    /**
     * SecurityToken
     */
    public static String mStsSecurityToken = "";

    /**
     * AccessKeySecret
     */
    public static String mStsAccessKeySecret = "";

    /**
     * LiveAccessKeyId
     */
    public static String mLiveStsAccessKeyId = "";

    /**
     * LiveSecurityToken
     */
    public static String mLiveStsSecurityToken = "";

    /**
     * LiveAccessKeySecret
     */
    public static String mLiveStsAccessKeySecret = "";

    /**
     * LiveExpiration
     */
    public static String mLiveExpiration = "";

    /**
     * LivePlayAuth
     */
    public static String mLivePlayAuth = "";

    /**
     * Domain
     */
    public static String mLiveStsDomain = "";

    /**
     * App
     */
    public static String mLiveStsApp = "";

    /**
     * Stream
     */
    public static String mLiveStsStream = "";

    /**
     * PlayAuth信息
     */
    public static String mPlayAuth = "";

    /**
     * url播放地址
     */
    public static String mUrlPath = "";

    /**
     * 直播加密 URL 播放地址
     */
    public static String mLiveStsUrl = "";

    /**
     * MPS Region
     */
    public static String mMpsRegion = "";

    /**
     * MPS AuthInfo
     */
    public static String mMpsAuthInfo = "";

    /**
     * MPS SecurityToken
     */
    public static String mMpsSecurityToken = "";

    /**
     * MPS AccessKeyId
     */
    public static String mMpsAccessKeyId = "";

    /**
     * MPS AccessKeySecret
     */
    public static String mMpsAccessKeySecret = "";

    /**
     * MPS HlsUriToken
     */
    public static String mMpsHlsUriToken = "";
    /**
     * 播放方式，默认URL方式
     */
    public static PLAYTYPE mCurrentPlayType = PLAYTYPE.DEFAULT;

    /**
     * 起播码率,默认 3000 档位
     */
    public static MUTIRATE mCurrentMutiRate = MUTIRATE.RATE_3000;

    /**
     * 非vip用户观看非vip视频，如果视频支持视频广告就放视频广告，否则默认用图片广告
     */
    public static boolean IS_VIDEO = false;

    /**
     * 非vip用户观看vip视频，先展示视频广告，展示之后展示试看功能，暂停的时候展示图片广告
     */
    public static boolean IS_PICTRUE = false;
    /**
     * 非vip用户观看vip视频，先展示视频广告，展示之后展示试看功能，暂停的时候展示图片广告
     */
    public static boolean IS_TRAILER = false;
    /**
     * 是否开启弹幕
     * 弹幕默认开启
     */
    public static boolean IS_BARRAGE = true;

    /**
     * 是否开启水印
     */
    public static boolean IS_WATERMARK = false;

    /**
     * 是否开启跑马灯
     */
    public static boolean IS_MARQUEE = false;


    /**
     * 各种播放方式是否选中，如果未选中，会有默认源直接播放，
     * 如果在编辑页面点击了使用此配置button，则为选中状态，
     * 不会有默认源直接播放
     */
    public static boolean URL_TYPE_CHECKED = false;
    public static boolean STS_TYPE_CHECKED = false;
    public static boolean MPS_TYPE_CHECKED = false;
    public static boolean AUTH_TYPE_CHECKED = false;
    public static boolean LIVE_STS_TYPE_CHECKED = false;

    public enum PLAYTYPE {
        /**
         * 默认播放方式
         */
        DEFAULT,
        /**
         * URL播放方式
         */
        URL,
        /**
         * STS播放方式
         */
        STS,
        /**
         * MPS播放方式
         */
        MPS,
        /**
         * AUTH播放方式
         */
        AUTH,
        /**
         * LiveSts播放方式
         */
        LIVE_STS
    }

    public enum MUTIRATE {
        RATE_400(400),
        RATE_900(900),
        RATE_1500(1500),
        RATE_3000(3000),
        RATE_3500(3500),
        RATE_6000(6000);

        private int value;

        MUTIRATE(int i) {
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }
}
