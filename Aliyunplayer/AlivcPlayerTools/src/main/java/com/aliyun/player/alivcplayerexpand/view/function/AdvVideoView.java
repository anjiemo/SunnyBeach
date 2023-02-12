package com.aliyun.player.alivcplayerexpand.view.function;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidMps;
import com.aliyun.player.source.VidSts;

import java.lang.ref.WeakReference;

/**
 * 视频广告View
 *
 * @author hanyu
 */
public class AdvVideoView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = AdvVideoView.class.getSimpleName();

    //广告播放器的surfaceView
    private SurfaceView mAdvSurfaceView;
    //用于播放视频广告的播放器
    private AliPlayer mAdvVideoAliyunVodPlayer;

    //对外info改变监听
    private IPlayer.OnInfoListener mOutOnInfoListener;
    //对外错误监听
    private IPlayer.OnErrorListener mOutOnErrorListener;
    //对外播放完成监听
    private IPlayer.OnCompletionListener mOutOnCompletionListener;
    //对外loading状态监听
    private IPlayer.OnLoadingStatusListener mOutOnLoadingStatusListener;
    //对外renderingStart监听
    private IPlayer.OnRenderingStartListener mOutOnRenderingStartListener;
    //状态改变监听
    private IPlayer.OnStateChangedListener mOutOnStateChangedListener;
    //对外准备完成监听
    private IPlayer.OnPreparedListener mOutPreparedListener;
    //返回按钮点击事件
    private OnBackImageViewClickListener mOnBackImageViewClickListener;
    //播放器的状态
    private int mPlayerState = -1;
    //视频广告返回按钮
    private ImageView mBackImageView;
    //视频广告标志View
    private TextView mAdvTipsTextView;


    public AdvVideoView(Context context) {
        super(context);
        init();
    }

    public AdvVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initSurfaceView();
        initBackImagView();
        initAdvTipsView();
        initAdvPlayer();
    }

    private void initSurfaceView() {
        mAdvSurfaceView = new SurfaceView(getContext().getApplicationContext());
        addSubView(mAdvSurfaceView);
    }

    private void initBackImagView() {
        mBackImageView = new ImageView(getContext());
        mBackImageView.setImageResource(R.drawable.ic_back);
        mBackImageView.setPadding(20, 20, 20, 20);
        mBackImageView.setVisibility(View.GONE);
        addSubViewByWrapContent(mBackImageView);

        mBackImageView.setOnClickListener(this);
    }

    private void initAdvTipsView() {
        mAdvTipsTextView = new TextView(getContext());
        mAdvTipsTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.alivc_fillet_bg_shape));
        int paddingLeft = (int) getContext().getResources().getDimension(R.dimen.alivc_common_padding_10);
        int paddingTop = (int) getContext().getResources().getDimension(R.dimen.alivc_common_padding_2);
        int paddingRight = (int) getContext().getResources().getDimension(R.dimen.alivc_common_padding_10);
        int paddingBottom = (int) getContext().getResources().getDimension(R.dimen.alivc_common_padding_2);
        mAdvTipsTextView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        mAdvTipsTextView.setTextSize(14);
        mAdvTipsTextView.setTextColor(getResources().getColor(R.color.alivc_common_bg_white));
        mAdvTipsTextView.setText(getResources().getString(R.string.alivc_adv_video_tips));
        mAdvTipsTextView.setGravity(Gravity.CENTER);
        mAdvTipsTextView.setVisibility(View.GONE);

        addSubViewByGravityRightTop(mAdvTipsTextView);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackImageView) {
            if (mOnBackImageViewClickListener != null) {
                mOnBackImageViewClickListener.onBackImageViewClick();
            }
        }
    }

    /**
     * addSubView 添加子view到布局中
     *
     * @param view 子view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //添加到布局中
        addView(view, params);
    }

    private void addSubViewByWrapContent(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //添加到布局中
        addView(view, params);
    }

    private void addSubViewByGravityRightTop(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = (int) getResources().getDimension(R.dimen.alivc_common_margin_6);
        params.rightMargin = (int) getResources().getDimension(R.dimen.alivc_common_margin_4);
        //添加到布局中
        addView(view, params);
    }

    public SurfaceView getAdvSurfaceView() {
        return mAdvSurfaceView;
    }

    public void setSurfaceViewVisibility(int visibility) {
        mAdvSurfaceView.setVisibility(visibility);
    }

    /**
     * 初始化视频广告
     */
    private void initAdvPlayer() {
        SurfaceHolder holder = mAdvSurfaceView.getHolder();
        //增加surfaceView的监听
        holder.addCallback(new AdvSurfaceHolderCallback(this));

        //该播放器用于播放器视频广告
        mAdvVideoAliyunVodPlayer = AliPlayerFactory.createAliPlayer(getContext().getApplicationContext());
        mAdvVideoAliyunVodPlayer.setAutoPlay(true);
        //设置准备回调
        mAdvVideoAliyunVodPlayer.setOnPreparedListener(new AdvPlayerOnPreparedListener(this));
        //播放器加载回调
        mAdvVideoAliyunVodPlayer.setOnLoadingStatusListener(new AdvPlayerOnLoadingStatusListener(this));
        //播放信息监听
        mAdvVideoAliyunVodPlayer.setOnInfoListener(new AdvPlayerOnInfoListener(this));
        //第一帧显示
        mAdvVideoAliyunVodPlayer.setOnRenderingStartListener(new AdvPlayerOnRenderingStartLitener(this));
        //状态改变监听
        mAdvVideoAliyunVodPlayer.setOnStateChangedListener(new AdvPlayerOnStateChangedListener(this));
        //播放结束
        mAdvVideoAliyunVodPlayer.setOnCompletionListener(new AdvPlayerOnCompletionListener(this));
        //播放器出错监听
        mAdvVideoAliyunVodPlayer.setOnErrorListener(new AdvPlayerOnErrorListener(this));

        mAdvVideoAliyunVodPlayer.setDisplay(mAdvSurfaceView.getHolder());
    }

    /**
     * 设置vidSts
     */
    public void optionSetVidSts(VidSts vidSts) {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.setDataSource(vidSts);
        }
    }

    /**
     * 设置vidSuth
     */
    public void optionSetVidAuth(VidAuth vidAuth) {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.setDataSource(vidAuth);
        }
    }

    /**
     * 设置urlSource
     */
    public void optionSetUrlSource(UrlSource urlSource) {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.setDataSource(urlSource);
        }
    }

    /**
     * 设置vidMps
     */
    public void optionSetVidMps(VidMps vidMps) {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.setDataSource(vidMps);
        }
    }

    /**
     * prepared操作
     */
    public void optionPrepare() {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.prepare();
        }
    }

    /**
     * 开始操作
     */
    public void optionStart() {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.start();
            isShowAdvVideoBackIamgeView(true);
            isShowAdvVideoTipsTextView(true);
        }
    }

    /**
     * 暂停操作
     */
    public void optionPause() {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.pause();
        }
    }

    /**
     * 停止操作
     */
    public void optionStop() {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.stop();
        }
    }

    /**
     * 视频广告的返回按钮是否展示
     */
    public void isShowAdvVideoBackIamgeView(boolean isShow) {
        if (mBackImageView != null) {
            mBackImageView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 视频广告是否展示提示View
     */
    public void isShowAdvVideoTipsTextView(boolean isShow) {
        if (mAdvTipsTextView != null) {
            mAdvTipsTextView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    //获取视频广告的播放器
    public AliPlayer getAdvVideoAliyunVodPlayer() {
        return mAdvVideoAliyunVodPlayer;
    }

    public interface OnBackImageViewClickListener {
        void onBackImageViewClick();
    }

    public void setOnBackImageViewClickListener(OnBackImageViewClickListener listener) {
        this.mOnBackImageViewClickListener = listener;
    }

    /**
     * 设置prepared监听
     */
    public void setOutPreparedListener(IPlayer.OnPreparedListener outPreparedListener) {
        this.mOutPreparedListener = outPreparedListener;
    }

    /**
     * 设置onLoading状态监听
     */
    public void setOutOnLoadingStatusListener(IPlayer.OnLoadingStatusListener onLoadingStatusListener) {
        this.mOutOnLoadingStatusListener = onLoadingStatusListener;
    }

    /**
     * 设置播放完成监听
     */
    public void setOutOnCompletionListener(IPlayer.OnCompletionListener onCompletionListener) {
        this.mOutOnCompletionListener = onCompletionListener;
    }

    /**
     * 设置对外info改变监听
     */
    public void setOutOnInfoListener(IPlayer.OnInfoListener onInfoListener) {
        this.mOutOnInfoListener = onInfoListener;
    }

    /**
     * 设置对外 renderingStart 监听
     */
    public void setOutOnRenderingStartListener(IPlayer.OnRenderingStartListener onRenderingStartListener) {
        this.mOutOnRenderingStartListener = onRenderingStartListener;
    }

    /**
     * 设置对外错误监听
     */
    public void setOutOnErrorListener(IPlayer.OnErrorListener onErrorListener) {
        this.mOutOnErrorListener = onErrorListener;
    }

    //状态改变监听
    public void setOutOnStateChangedListener(IPlayer.OnStateChangedListener listener) {
        this.mOutOnStateChangedListener = listener;
    }

    public void setAutoPlay(boolean autoPlay) {
        if (mAdvVideoAliyunVodPlayer != null) {
            mAdvVideoAliyunVodPlayer.setAutoPlay(autoPlay);
        }
    }

    /**
     * 获取视频广告播放器的状态
     */
    public int getAdvPlayerState() {
        return mPlayerState;
    }

    public enum VideoState {
        /**
         * 广告
         */
        VIDEO_ADV,
        /**
         * 原视频
         */
        VIDEO_SOURCE;
    }

    /**
     * 将要播放的视频
     */
    public enum IntentPlayVideo {
        /**
         * 先播放中间广告,播放完成后再播放最后一条广告
         */
        MIDDLE_END_ADV_SEEK,
        /**
         * 播放中间广告,并且播放完成需要seek
         */
        MIDDLE_ADV_SEEK,
        /**
         * 播放开始广告
         */
        START_ADV,
        /**
         * 播放中间广告
         */
        MIDDLE_ADV,
        /**
         * 播放结束广告
         */
        END_ADV,
        /**
         * 原视频左seek
         */
        REVERSE_SOURCE,
        /**
         * 正常seek
         */
        NORMAL;
    }

    /**
     * ------------------------------------ 视频广告播放器相关回调 ------------------------------------
     */
    //SurfaceView回调监听
    public static class AdvSurfaceHolderCallback implements SurfaceHolder.Callback {

        private WeakReference<AdvVideoView> weakReference;

        public AdvSurfaceHolderCallback(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mAdvVideoAliyunVodPlayer != null) {
                advVideoView.mAdvVideoAliyunVodPlayer.setDisplay(holder);
                //防止黑屏
                advVideoView.mAdvVideoAliyunVodPlayer.redraw();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mAdvVideoAliyunVodPlayer != null) {
                advVideoView.mAdvVideoAliyunVodPlayer.redraw();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mAdvVideoAliyunVodPlayer != null) {
                advVideoView.mAdvVideoAliyunVodPlayer.setDisplay(null);
            }
        }
    }

    //准备完成回调
    public static class AdvPlayerOnPreparedListener implements IPlayer.OnPreparedListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnPreparedListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onPrepared() {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutPreparedListener != null) {
                advVideoView.mOutPreparedListener.onPrepared();
            }
        }
    }

    //loading状态监听
    public static class AdvPlayerOnLoadingStatusListener implements IPlayer.OnLoadingStatusListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnLoadingStatusListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onLoadingBegin() {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnLoadingStatusListener != null) {
                advVideoView.mOutOnLoadingStatusListener.onLoadingBegin();
            }
        }

        @Override
        public void onLoadingProgress(int i, float v) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnLoadingStatusListener != null) {
                advVideoView.mOutOnLoadingStatusListener.onLoadingProgress(i, v);
            }
        }

        @Override
        public void onLoadingEnd() {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnLoadingStatusListener != null) {
                advVideoView.mOutOnLoadingStatusListener.onLoadingEnd();
            }
        }
    }

    //播放信息监听
    public static class AdvPlayerOnInfoListener implements IPlayer.OnInfoListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnInfoListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onInfo(InfoBean infoBean) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnInfoListener != null) {
                advVideoView.mOutOnInfoListener.onInfo(infoBean);
            }
        }
    }

    //首帧显示监听
    public static class AdvPlayerOnRenderingStartLitener implements IPlayer.OnRenderingStartListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnRenderingStartLitener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onRenderingStart() {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnRenderingStartListener != null) {
                advVideoView.mOutOnRenderingStartListener.onRenderingStart();
                advVideoView.isShowAdvVideoBackIamgeView(true);
                advVideoView.isShowAdvVideoTipsTextView(true);
            }
        }
    }

    //播放状态改变监听
    public static class AdvPlayerOnStateChangedListener implements IPlayer.OnStateChangedListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnStateChangedListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onStateChanged(int newState) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnStateChangedListener != null) {
                advVideoView.mPlayerState = newState;
                advVideoView.mOutOnStateChangedListener.onStateChanged(newState);
            }
        }
    }

    //播放完成监听
    public static class AdvPlayerOnCompletionListener implements IPlayer.OnCompletionListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnCompletionListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onCompletion() {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnCompletionListener != null) {
                advVideoView.mOutOnCompletionListener.onCompletion();
                advVideoView.isShowAdvVideoBackIamgeView(false);
                advVideoView.isShowAdvVideoTipsTextView(false);
            }
        }
    }

    //播放出错监听
    public static class AdvPlayerOnErrorListener implements IPlayer.OnErrorListener {

        private WeakReference<AdvVideoView> weakReference;

        public AdvPlayerOnErrorListener(AdvVideoView advVideoView) {
            weakReference = new WeakReference<>(advVideoView);
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            AdvVideoView advVideoView = weakReference.get();
            if (advVideoView != null && advVideoView.mOutOnErrorListener != null) {
                advVideoView.mOutOnErrorListener.onError(errorInfo);
            }
        }
    }
    /**
     * ------------------------------------ 视频广告播放器相关回调 ------------------------------------
     */
}
