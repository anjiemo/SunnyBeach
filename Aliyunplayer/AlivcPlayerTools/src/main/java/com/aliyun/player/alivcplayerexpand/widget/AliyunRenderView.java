package com.aliyun.player.alivcplayerexpand.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.LiveSts;
import com.aliyun.player.source.StsInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidMps;
import com.aliyun.player.source.VidSts;

import java.lang.ref.WeakReference;

public class AliyunRenderView extends FrameLayout {

    private Context mContext;
    /**
     * 真正的播放器实例对象
     */
    private AliPlayer mAliPlayer;
    /**
     * Surface
     */
    private IRenderView mIRenderView;

    /**
     * 判断当前解码状态,true:硬解,false:软解
     * 默认是硬解
     */
    private boolean mCurrentEnableHardwareDecoder = true;

    private Surface mSurface;

    public AliyunRenderView(Context context) {
        super(context);
        init(context);
    }

    public AliyunRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AliyunRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        initPlayer();
    }

    private void initPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(mContext.getApplicationContext());

        initPlayerListener();
    }

    private void initPlayerListener() {
        mAliPlayer.setOnInfoListener(new OnAVPInfoListener(this));
        mAliPlayer.setOnTrackReadyListener(new OnAVPTrackReadyListener(this));
        mAliPlayer.setOnErrorListener(new OnAVPErrorListener(this));
        mAliPlayer.setOnSeiDataListener(new OnAVPSeiDataListener(this));
        mAliPlayer.setOnSnapShotListener(new OnAVPSnapShotListener(this));
        mAliPlayer.setOnPreparedListener(new OnAVPPreparedListener(this));
        mAliPlayer.setOnCompletionListener(new OnAVPCompletionListener(this));
        mAliPlayer.setOnTrackChangedListener(new OnAVPTrackChangedListener(this));
        mAliPlayer.setOnSeekCompleteListener(new OnAVPSeekCompleteListener(this));
        mAliPlayer.setOnVideoRenderedListener(new OnAVPVideoRenderedListener(this));
        mAliPlayer.setOnLoadingStatusListener(new OnAVPLoadingStatusListener(this));
        mAliPlayer.setOnRenderingStartListener(new OnAVPRenderingStartListener(this));
        mAliPlayer.setOnVerifyTimeExpireCallback(new OnAVPVerifyStsCallback(this));
        mAliPlayer.setOnStateChangedListener(new OnAVPStateChangedListener(this));
        mAliPlayer.setOnSubtitleDisplayListener(new OnAVPSubtitleDisplayListener(this));
        mAliPlayer.setOnVideoSizeChangedListener(new OnAVPVideoSizeChangedListener(this));
    }

    public enum SurfaceType {
        /**
         * TextureView
         */
        TEXTURE_VIEW,
        /**
         * SurfacView
         */
        SURFACE_VIEW
    }

    /**
     * 获取真正的播放器实例对象
     */
    public AliPlayer getAliPlayer() {
        return mAliPlayer;
    }

    /**
     * 该方法需要在创建播放器完成后,prepare前调用
     *
     * @param surfaceType Surface的类型
     */
    public void setSurfaceType(SurfaceType surfaceType) {
        if (surfaceType == SurfaceType.TEXTURE_VIEW && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            mIRenderView = new TextureRenderView(mContext);
        } else {
            mIRenderView = new SurfaceRenderView(mContext);
        }
        initListener();
        addView(mIRenderView.getView());
    }

    /**
     * 设置播放源
     */
    public void setDataSource(VidSts vidSts) {
        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(vidSts);
        }
    }

    /**
     * 设置播放源
     */
    public void setDataSource(VidAuth vidAuth) {
        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(vidAuth);
        }
    }

    /**
     * 设置播放源
     */
    public void setDataSource(LiveSts liveSts) {
        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(liveSts);
        }
    }

    /**
     * 设置播放源
     */
    public void setDataSource(VidMps vidMps) {
        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(vidMps);
        }
    }

    /**
     * 设置播放源
     */
    public void setDataSource(UrlSource urlSource) {
        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(urlSource);
        }
    }

    /**
     * 刷新sts信息
     */
    public void updateStsInfo(StsInfo stsInfo) {
        if (mAliPlayer != null) {
            mAliPlayer.updateStsInfo(stsInfo);
        }
    }

    /**
     * 刷新Auth信息
     */
    public void updateAuthInfo(VidAuth vidAuth) {
        if (mAliPlayer != null) {
            mAliPlayer.updateVidAuth(vidAuth);
        }
    }

    /**
     * 设置是否静音
     */
    public void setMute(boolean isMute) {
        if (mAliPlayer != null) {
            mAliPlayer.setMute(isMute);
        }
    }

    /**
     * 设置音量
     */
    public void setVolume(float v) {
        if (mAliPlayer != null) {
            mAliPlayer.setVolume(v);
        }
    }

    /**
     * 获取音量
     */
    public float getVolume() {
        if (mAliPlayer != null) {
            return mAliPlayer.getVolume();
        }
        return 0;
    }

    /**
     * 是否开启自动播放
     */
    public void setAutoPlay(boolean isAutoPlay) {
        if (mAliPlayer != null) {
            mAliPlayer.setAutoPlay(isAutoPlay);
        }
    }

    /**
     * 设置播放速率
     */
    public void setSpeed(float speed) {
        if (mAliPlayer != null) {
            mAliPlayer.setSpeed(speed);
        }
    }

    /**
     * 是否循环播放
     */
    public void setLoop(boolean loop) {
        if (mAliPlayer != null) {
            mAliPlayer.setLoop(loop);
        }
    }

    public boolean isLoop() {
        if (mAliPlayer != null) {
            return mAliPlayer.isLoop();
        }
        return false;
    }

    /**
     * 截屏
     */
    public void snapshot() {
        if (mAliPlayer != null) {
            mAliPlayer.snapshot();
        }
    }

    /**
     * 选择 track
     *
     * @param index 索引
     */
    public void selectTrack(int index) {
        if (mAliPlayer != null) {
            mAliPlayer.selectTrack(index);
        }
    }

    public void selectExtSubtitle(int index, boolean selected) {
        if (mAliPlayer != null) {
            mAliPlayer.selectExtSubtitle(index, selected);
        }
    }

    /**
     * 选择 track
     *
     * @param index 索引
     * @param focus 是否强制选择track
     */
    public void selectTrack(int index, boolean focus) {
        if (mAliPlayer != null) {
            mAliPlayer.selectTrack(index, focus);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
        }
    }

    /**
     * prepare
     */
    public void prepare() {
        if (mAliPlayer != null) {
            mAliPlayer.prepare();
        }
    }

    /**
     * 暂停播放,直播流不建议使用
     */
    public void pause() {
        if (mAliPlayer != null) {
            mAliPlayer.pause();
        }
    }

    public void start() {
        if (mAliPlayer != null) {
            mAliPlayer.start();
        }
    }

    public void reload() {
        if (mAliPlayer != null) {
            mAliPlayer.reload();
        }
    }

    /**
     * 获取视频时长
     */
    public long getDuration() {
        if (mAliPlayer != null) {
            return mAliPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前 track
     */
    public TrackInfo currentTrack(TrackInfo.Type typeVideo) {
        if (mAliPlayer != null) {
            return mAliPlayer.currentTrack(typeVideo);
        }
        return null;
    }

    /**
     * 获取当前 track
     */
    @Deprecated
    public TrackInfo currentTrack(int ordinal) {
        if (mAliPlayer != null) {
            return mAliPlayer.currentTrack(ordinal);
        }
        return null;
    }

    /**
     * seek
     *
     * @param position 目标位置
     * @param seekMode 精准/非精准seek
     */
    public void seekTo(long position, IPlayer.SeekMode seekMode) {
        if (mAliPlayer != null) {
            mAliPlayer.seekTo(position, seekMode);
        }
    }

    private void initListener() {
        mIRenderView.addRenderCallback(new MyRenderViewCallback(this));
    }

    /**
     * 缓存配置
     */
    public void setCacheConfig(CacheConfig cacheConfig) {
        if (mAliPlayer != null) {
            mAliPlayer.setCacheConfig(cacheConfig);
        }
    }

    /**
     * 设置PlayerConfig
     */
    public void setPlayerConfig(PlayerConfig playerConfig) {
        if (mAliPlayer != null) {
            mAliPlayer.setConfig(playerConfig);
        }
    }

    /**
     * 获取PlayerConfig
     */
    public PlayerConfig getPlayerConfig() {
        if (mAliPlayer != null) {
            return mAliPlayer.getConfig();
        }
        return null;
    }

    /**
     * 设置缩放模式
     */
    public void setScaleModel(IPlayer.ScaleMode scaleMode) {
        if (mAliPlayer != null) {
            mAliPlayer.setScaleMode(scaleMode);
        }
    }

    /**
     * 获取当前缩放模式
     */
    public IPlayer.ScaleMode getScaleModel() {
        if (mAliPlayer != null) {
            return mAliPlayer.getScaleMode();
        }
        return IPlayer.ScaleMode.SCALE_ASPECT_FIT;
    }

    /**
     * 设置旋转模式
     */
    public void setRotateModel(IPlayer.RotateMode rotateModel) {
        if (mAliPlayer != null) {
            mAliPlayer.setRotateMode(rotateModel);
        }
    }

    /**
     * 获取当前旋转模式
     */
    public IPlayer.RotateMode getRotateModel() {
        if (mAliPlayer != null) {
            return mAliPlayer.getRotateMode();
        }
        return IPlayer.RotateMode.ROTATE_0;
    }

    /**
     * 设置镜像模式
     */
    public void setMirrorMode(IPlayer.MirrorMode mirrorMode) {
        if (mAliPlayer != null) {
            mAliPlayer.setMirrorMode(mirrorMode);
        }
    }

    /**
     * 获取当前镜像模式
     */
    public IPlayer.MirrorMode getMirrorMode() {
        if (mAliPlayer != null) {
            return mAliPlayer.getMirrorMode();
        }
        return IPlayer.MirrorMode.MIRROR_MODE_NONE;
    }

    public MediaInfo getMediaInfo() {
        if (mAliPlayer != null) {
            return mAliPlayer.getMediaInfo();
        }
        return null;
    }

    /**
     * 软硬解开关
     *
     * @param enableHardwareDecoder true:硬解,false:软解
     */
    public void enableHardwareDecoder(boolean enableHardwareDecoder) {
        if (mAliPlayer != null) {
            mCurrentEnableHardwareDecoder = enableHardwareDecoder;
            mAliPlayer.enableHardwareDecoder(enableHardwareDecoder);
        }
    }

    /**
     * 多码率时默认播放码率
     */
    public void setDefaultBandWidth(int bandWidth) {
        if (mAliPlayer != null) {
            Log.e("abc : ", "setDefaultBandWidth: " + bandWidth);
            mAliPlayer.setDefaultBandWidth(bandWidth);
        }
    }

    /**
     * 获取当前解码状态
     *
     * @return true:硬解,false:软解
     */
    public boolean isHardwareDecoder() {
        return mCurrentEnableHardwareDecoder;
    }

    public void release() {
        if (mAliPlayer != null) {
            stop();
            mAliPlayer.setSurface(null);
            mAliPlayer.release();
            mAliPlayer = null;
        }
        mSurface = null;
    }

    private static class MyRenderViewCallback implements IRenderView.IRenderCallback {

        private WeakReference<AliyunRenderView> weakReference;

        private MyRenderViewCallback(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onSurfaceCreate(Surface surface) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null && aliyunRenderView.mAliPlayer != null) {
                aliyunRenderView.mSurface = surface;
                aliyunRenderView.mAliPlayer.setSurface(surface);
            }
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null && aliyunRenderView.mAliPlayer != null) {
                aliyunRenderView.mAliPlayer.surfaceChanged();
            }
        }

        @Override
        public void onSurfaceDestroyed() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null && aliyunRenderView.mAliPlayer != null) {
                aliyunRenderView.mAliPlayer.setSurface(null);
            }
        }
    }

    /**
     * OnPrepared
     */
    private static class OnAVPPreparedListener implements IPlayer.OnPreparedListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPPreparedListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }


        @Override
        public void onPrepared() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onPrepared();
            }
        }
    }


    /**
     * 纯音频、纯视频流监听
     */
    public interface OnVideoStreamTrackTypeListener {
        //纯视频
        void onVideoOnlyType();

        //纯音频
        void onAudioOnlyType();
    }

    private OnVideoStreamTrackTypeListener mOnVideoStreamTrackTypeListener;

    public void setOnVideoStreamTrackType(OnVideoStreamTrackTypeListener listener) {
        this.mOnVideoStreamTrackTypeListener = listener;
    }

    private IPlayer.OnPreparedListener mOnPreparedListener;

    public void setOnPreparedListener(IPlayer.OnPreparedListener listener) {
        this.mOnPreparedListener = listener;
    }

    private void onPrepared() {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared();
        }
        if (mOnVideoStreamTrackTypeListener != null) {
            TrackInfo trackVideo = mAliPlayer.currentTrack(TrackInfo.Type.TYPE_VIDEO);
            TrackInfo trackAudio = mAliPlayer.currentTrack(TrackInfo.Type.TYPE_AUDIO);
            if (trackVideo == null && trackAudio != null) {
                mOnVideoStreamTrackTypeListener.onAudioOnlyType();
            } else if (trackVideo != null && trackAudio == null) {
                mOnVideoStreamTrackTypeListener.onVideoOnlyType();
            }
        }

    }


    /**
     * OnVideoRenderedListener
     */
    private static class OnAVPVideoRenderedListener implements IPlayer.OnVideoRenderedListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPVideoRenderedListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onVideoRendered(long timeMs, long pts) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onVideoRendered(timeMs, pts);
            }
        }
    }

    private IPlayer.OnVideoRenderedListener mOnVideoRenderedListener;

    public void setOnVideoRenderedListener(IPlayer.OnVideoRenderedListener listener) {
        this.mOnVideoRenderedListener = listener;
    }

    private void onVideoRendered(long timeMs, long pts) {
        if (mOnVideoRenderedListener != null) {
            mOnVideoRenderedListener.onVideoRendered(timeMs, pts);
        }
    }

    /**
     * OnRenderingStartListener
     */
    private static class OnAVPRenderingStartListener implements IPlayer.OnRenderingStartListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPRenderingStartListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onRenderingStart() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onRenderingStart();
            }
        }
    }

    private IPlayer.OnRenderingStartListener mOnRenderingStartListener;

    public void setOnRenderingStartListener(IPlayer.OnRenderingStartListener listener) {
        this.mOnRenderingStartListener = listener;
    }

    private void onRenderingStart() {
        if (mOnRenderingStartListener != null) {
            mOnRenderingStartListener.onRenderingStart();
        }
    }

    /**
     * OnStateChangedListner
     */
    private static class OnAVPStateChangedListener implements IPlayer.OnStateChangedListener {

        private WeakReference<AliyunRenderView> weakReference;

        public OnAVPStateChangedListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onStateChanged(int i) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onStateChangedListener(i);
            }
        }
    }

    private IPlayer.OnStateChangedListener mOnStateChangedListener;

    public void setOnStateChangedListener(IPlayer.OnStateChangedListener listener) {
        this.mOnStateChangedListener = listener;
    }

    private void onStateChangedListener(int newState) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(newState);
        }
    }

    /**
     * OnVideoSizeChangedListener
     */
    private static class OnAVPVideoSizeChangedListener implements IPlayer.OnVideoSizeChangedListener {

        private WeakReference<AliyunRenderView> weakReference;

        public OnAVPVideoSizeChangedListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onVideoSizeChanged(width, height);
            }
        }
    }

    private IPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public void setOnVideoSizeChangedListener(IPlayer.OnVideoSizeChangedListener listener) {
        this.mOnVideoSizeChangedListener = listener;
    }

    private void onVideoSizeChanged(int width, int height) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(width, height);
        }
    }

    /**
     * OnInfoListener
     */
    private static class OnAVPInfoListener implements IPlayer.OnInfoListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPInfoListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onInfo(InfoBean infoBean) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onInfo(infoBean);
            }
        }
    }

    private IPlayer.OnInfoListener mOnInfoListener;

    public void setOnInfoListener(IPlayer.OnInfoListener listener) {
        this.mOnInfoListener = listener;
    }

    private void onInfo(InfoBean infoBean) {
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(infoBean);
        }
    }

    private static class OnAVPTrackReadyListener implements IPlayer.OnTrackReadyListener {

        private WeakReference<AliyunRenderView> mRenderViewWeakReference;

        public OnAVPTrackReadyListener(AliyunRenderView renderView) {
            mRenderViewWeakReference = new WeakReference<>(renderView);
        }

        @Override
        public void onTrackReady(MediaInfo mediaInfo) {
            AliyunRenderView aliyunRenderView = mRenderViewWeakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onTrackReady(mediaInfo);
            }
        }
    }

    private IPlayer.OnTrackReadyListener mOnTrackReadyListener;

    public void setOnTrackReadyListenenr(IPlayer.OnTrackReadyListener listener) {
        this.mOnTrackReadyListener = listener;
    }

    private void onTrackReady(MediaInfo mediaInfo) {
        if (mOnTrackReadyListener != null) {
            mOnTrackReadyListener.onTrackReady(mediaInfo);
        }
    }

    /**
     * OnLoadingStatusListener
     */
    private static class OnAVPLoadingStatusListener implements IPlayer.OnLoadingStatusListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPLoadingStatusListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onLoadingBegin() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onLoadingBegin();
            }
        }

        @Override
        public void onLoadingProgress(int percent, float netSpeed) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onLoadingProgress(percent, netSpeed);
            }
        }

        @Override
        public void onLoadingEnd() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onLoadingEnd();
            }
        }
    }

    private IPlayer.OnLoadingStatusListener mOnLoadingStatusListener;

    public void setOnLoadingStatusListener(IPlayer.OnLoadingStatusListener listener) {
        this.mOnLoadingStatusListener = listener;
    }

    private void onLoadingBegin() {
        if (mOnLoadingStatusListener != null) {
            mOnLoadingStatusListener.onLoadingBegin();
        }
    }

    private void onLoadingProgress(int percent, float netSpeed) {
        if (mOnLoadingStatusListener != null) {
            mOnLoadingStatusListener.onLoadingProgress(percent, netSpeed);
        }
    }

    private void onLoadingEnd() {
        if (mOnLoadingStatusListener != null) {
            mOnLoadingStatusListener.onLoadingEnd();
        }
    }


    /**
     * OnSnapShotListener
     */
    private static class OnAVPSnapShotListener implements IPlayer.OnSnapShotListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPSnapShotListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onSnapShot(Bitmap bitmap, int with, int height) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSnapShot(bitmap, with, height);
            }
        }
    }

    private IPlayer.OnSnapShotListener mOnSnapShotListener;

    public void setOnSnapShotListener(IPlayer.OnSnapShotListener listener) {
        this.mOnSnapShotListener = listener;
    }

    private void onSnapShot(Bitmap bitmap, int with, int height) {
        if (mOnSnapShotListener != null) {
            mOnSnapShotListener.onSnapShot(bitmap, with, height);
        }
    }

    /**
     * OnCompletionListener
     */
    private static class OnAVPCompletionListener implements IPlayer.OnCompletionListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPCompletionListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onCompletion() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onCompletion();
            }
        }
    }

    private IPlayer.OnCompletionListener mOnCompletionListener;

    public void setOnCompletionListener(IPlayer.OnCompletionListener listener) {
        this.mOnCompletionListener = listener;
    }

    private void onCompletion() {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion();
        }
    }

    /**
     * OnSeekCompleteListener
     */
    private static class OnAVPSeekCompleteListener implements IPlayer.OnSeekCompleteListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPSeekCompleteListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onSeekComplete() {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSeekComplete();
            }
        }
    }

    private IPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

    public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener listener) {
        this.mOnSeekCompleteListener = listener;
    }

    private void onSeekComplete() {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete();
        }
    }

    /**
     * OnTrackChangedListener
     */
    private static class OnAVPTrackChangedListener implements IPlayer.OnTrackChangedListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPTrackChangedListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onChangedSuccess(TrackInfo trackInfo) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onChangedSuccess(trackInfo);
            }
        }

        @Override
        public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onChangedFail(trackInfo, errorInfo);
            }
        }
    }

    private IPlayer.OnTrackChangedListener mOnTrackChangedListener;

    public void setOnTrackChangedListener(IPlayer.OnTrackChangedListener listener) {
        this.mOnTrackChangedListener = listener;
    }

    private void onChangedSuccess(TrackInfo trackInfo) {
        if (mOnTrackChangedListener != null) {
            mOnTrackChangedListener.onChangedSuccess(trackInfo);
        }
    }

    private void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
        if (mOnTrackChangedListener != null) {
            mOnTrackChangedListener.onChangedFail(trackInfo, errorInfo);
        }
    }

    /**
     * OnErrorListener
     */
    private static class OnAVPErrorListener implements IPlayer.OnErrorListener {

        private WeakReference<AliyunRenderView> weakReference;

        private OnAVPErrorListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onError(errorInfo);
            }
        }
    }

    private IPlayer.OnErrorListener mOnErrorListener;

    public void setOnErrorListener(IPlayer.OnErrorListener listener) {
        this.mOnErrorListener = listener;
    }

    private void onError(ErrorInfo errorInfo) {
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(errorInfo);
        }
    }

    /**
     * onSubtitleDisplayListener
     */
    private static class OnAVPSubtitleDisplayListener implements IPlayer.OnSubtitleDisplayListener {

        private WeakReference<AliyunRenderView> weakReference;

        public OnAVPSubtitleDisplayListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onSubtitleExtAdded(int trackIndex, String url) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSubtitleExtAdded(trackIndex, url);
            }
        }

        @Override
        public void onSubtitleShow(int trackIndex, long id, String data) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSubtitleShow(trackIndex, id, data);
            }
        }

        @Override
        public void onSubtitleHide(int trackIndex, long id) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSubtitleHide(trackIndex, id);
            }
        }

        @Override
        public void onSubtitleHeader(int index, String data) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.oSubtitleHeader(index, data);
            }
        }
    }

    private void oSubtitleHeader(int index, String data) {
        if (mOnSubtitleDisplayListener != null) {
            mOnSubtitleDisplayListener.onSubtitleHeader(index, data);
        }
    }

    private void onSubtitleHide(int trackIndex, long id) {
        if (mOnSubtitleDisplayListener != null) {
            mOnSubtitleDisplayListener.onSubtitleHide(trackIndex, id);
        }
    }

    private void onSubtitleShow(int trackIndex, long id, String data) {
        if (mOnSubtitleDisplayListener != null) {
            mOnSubtitleDisplayListener.onSubtitleShow(trackIndex, id, data);
        }
    }

    private void onSubtitleExtAdded(int trackIndex, String url) {
        if (mOnSubtitleDisplayListener != null) {
            mOnSubtitleDisplayListener.onSubtitleExtAdded(trackIndex, url);
        }
    }

    private IPlayer.OnSubtitleDisplayListener mOnSubtitleDisplayListener;

    public void setOnSubtitleDisplayListener(IPlayer.OnSubtitleDisplayListener listener) {
        this.mOnSubtitleDisplayListener = listener;
    }

    /**
     * onSeiDataListener
     */
    private static class OnAVPSeiDataListener implements IPlayer.OnSeiDataListener {

        private WeakReference<AliyunRenderView> weakReference;

        public OnAVPSeiDataListener(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public void onSeiData(int type, byte[] bytes) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                aliyunRenderView.onSeiData(type, bytes);
            }
        }
    }

    private void onSeiData(int type, byte[] bytes) {
        if (mOnSeiDataListener != null) {
            mOnSeiDataListener.onSeiData(type, bytes);
        }
    }

    private IPlayer.OnSeiDataListener mOnSeiDataListener;

    public void setOnSeiDataListener(IPlayer.OnSeiDataListener listener) {
        this.mOnSeiDataListener = listener;
    }

    private static class OnAVPVerifyStsCallback implements AliPlayer.OnVerifyTimeExpireCallback {

        private WeakReference<AliyunRenderView> weakReference;

        public OnAVPVerifyStsCallback(AliyunRenderView aliyunRenderView) {
            weakReference = new WeakReference<>(aliyunRenderView);
        }

        @Override
        public AliPlayer.Status onVerifySts(StsInfo stsInfo) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                return aliyunRenderView.onVerifySts(stsInfo);
            }
            return AliPlayer.Status.Valid;
        }

        @Override
        public AliPlayer.Status onVerifyAuth(VidAuth vidAuth) {
            AliyunRenderView aliyunRenderView = weakReference.get();
            if (aliyunRenderView != null) {
                return aliyunRenderView.onVerifyAuth(vidAuth);
            }
            return AliPlayer.Status.Valid;
        }
    }

    private AliPlayer.OnVerifyTimeExpireCallback mOnVerifyTimeExpireCallback;

    public void setOnVerifyTimeExpireCallback(AliPlayer.OnVerifyTimeExpireCallback listener) {
        this.mOnVerifyTimeExpireCallback = listener;
    }

    private AliPlayer.Status onVerifyAuth(VidAuth vidAuth) {
        if (mOnVerifyTimeExpireCallback != null) {
            return mOnVerifyTimeExpireCallback.onVerifyAuth(vidAuth);
        }
        return AliPlayer.Status.Valid;
    }

    private AliPlayer.Status onVerifySts(StsInfo stsInfo) {
        if (mOnVerifyTimeExpireCallback != null) {
            return mOnVerifyTimeExpireCallback.onVerifySts(stsInfo);
        }
        return AliPlayer.Status.Valid;
    }
}
