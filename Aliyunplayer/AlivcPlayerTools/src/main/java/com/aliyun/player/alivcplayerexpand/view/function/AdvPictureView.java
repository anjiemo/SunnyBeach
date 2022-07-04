package com.aliyun.player.alivcplayerexpand.view.function;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.lang.ref.WeakReference;

/**
 * 图片广告功能
 */
public class AdvPictureView extends RelativeLayout {

    /**
     * 广告图片
     */
    private ImageView mAdvImageView;
    /**
     * 倒计时按钮
     */
    private TextView mCountDownTextView;
    /**
     * root View
     */
    private RelativeLayout mAdvPictureRootRelativeLayout;

    /**
     * 图片广告监听
     */
    private OnAdvPictureListener mOnAdvPictureListener;
    /**
     * 是否展示暂停时的图片广告
     */
    private boolean isShowCenterAdv = false;
    /**
     * 回退
     */
    private ImageView mBackImageView;
    /**
     * 倒计时时间
     */
    private int mCountDownTime = 5;
    /**
     * 当前倒计时的时间
     */
    private int mCurrentCountDownTime = 5;
    /**
     * 图片广告链接
     */
    private String mAdvPictureUrl;
    /**
     * 是否处于倒计时状态中
     */
    private boolean mIsCountDown;
    /**
     * 倒计时handler
     */
    private CountDownHandler mCountDownHandler;

    private int mWidth;
    private int mHeight;

    public AdvPictureView(Context context) {
        super(context);
        init(context);
    }

    public AdvPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdvPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.alivc_view_adv_picture, this, true);
        findAllView();
        initCountDown();
        initListener();
    }

    private void findAllView() {
        mAdvImageView = findViewById(R.id.iv_adv);
        mBackImageView = findViewById(R.id.alivc_back);
        mCountDownTextView = findViewById(R.id.tv_count_down);
        mAdvPictureRootRelativeLayout = findViewById(R.id.rl_adv_picture_root);
    }

    private void initPicture() {
        if (mAdvImageView != null) {
            new ImageLoaderImpl().loadImage(getContext(), mAdvPictureUrl, new ImageLoaderOptions.Builder()
                            .crossFade()
                            .centerCrop()
                            .error(R.drawable.alivc_player_adv_picture)
                            .build())
                    .into(mAdvImageView);
        }
    }

    /**
     * 初始化倒计时功能
     */
    private void initCountDown() {
        mCountDownHandler = new CountDownHandler(this);
        mCountDownHandler.sendEmptyMessage(mCountDownTime);
    }

    /**
     * 设置监听
     */
    private void initListener() {
        mCountDownTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAdvPictureListener != null) {
                    if (mCountDownHandler != null) {
                        mCountDownHandler.removeCallbacksAndMessages(null);
                    }
                    mIsCountDown = false;
                    mOnAdvPictureListener.close();
                }
            }
        });

        mAdvImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAdvPictureListener != null) {
                    mOnAdvPictureListener.onClick();
                }
            }
        });

        mBackImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAdvPictureListener != null) {
                    mOnAdvPictureListener.finish();
                }
            }
        });
    }

    /**
     * 隐藏全部
     */
    public void hideAll() {
        if (mAdvPictureRootRelativeLayout != null) {
            mAdvPictureRootRelativeLayout.setVisibility(View.GONE);
            isShowCenterAdv = false;
        }
    }

    /**
     * 展示全部
     */
    public void showAll() {
        recovery();
        if (mAdvPictureRootRelativeLayout != null && !mAdvPictureRootRelativeLayout.isShown()) {
            mAdvPictureRootRelativeLayout.setVisibility(View.VISIBLE);
        }
        if (mBackImageView != null) {
            mBackImageView.setVisibility(View.VISIBLE);
        }
    }

    public void cancel() {
        if (mCountDownHandler != null) {
            mCountDownHandler.removeCallbacksAndMessages(null);
        }
    }

    public void reStart() {
        if (mCountDownHandler != null) {
            mCountDownHandler.sendEmptyMessage(mCurrentCountDownTime);
        }
    }

    public void stop() {
        cancel();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && isShowCenterAdv) {
            showCenterAdv();
        }
    }

    /**
     * 恢复初始状态
     */
    public void recovery() {
        if (mAdvImageView != null) {
            ViewGroup.LayoutParams layoutParams = mAdvImageView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mAdvImageView.setLayoutParams(layoutParams);
        }

        if (mCountDownTextView != null) {
            mCountDownTextView.setVisibility(View.VISIBLE);
        }
        if (mCountDownHandler != null) {
            mCountDownHandler.removeCallbacksAndMessages(null);
        }
        mCountDownTime = 5;
        initCountDown();
        isShowCenterAdv = false;
    }

    /**
     * 暂停时候,图片广告显示在中间
     */
    public void showCenterAdv() {
        if (mAdvImageView != null) {
            LayoutParams layoutParams = (LayoutParams) mAdvImageView.getLayoutParams();
            layoutParams.height = mHeight / 2;
            layoutParams.width = mWidth / 2;
            layoutParams.addRule(CENTER_IN_PARENT);
            mAdvImageView.setLayoutParams(layoutParams);
            if (mAdvPictureRootRelativeLayout != null) {
                mAdvPictureRootRelativeLayout.setVisibility(View.VISIBLE);
            }
            if (mCountDownTextView != null) {
                mCountDownTextView.setVisibility(View.GONE);
            }
            mAdvImageView.invalidate();

            isShowCenterAdv = true;
        }
        if (mBackImageView != null) {
            mBackImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCountDownHandler != null) {
            mCountDownHandler.removeCallbacksAndMessages(null);
        }
    }


    public void setOnAdvPictureListener(OnAdvPictureListener listener) {
        this.mOnAdvPictureListener = listener;
    }

    /**
     * 设置图片广告链接
     */
    public void setAdvPictureUrl(String url) {
        this.mAdvPictureUrl = url;
        initPicture();
    }

    /**
     * 是否是在倒计时状态下
     */
    public boolean isInCountDown() {
        return mIsCountDown;
    }

    /**
     * 图片广告监听
     */
    public interface OnAdvPictureListener {
        void finish();

        void close();

        void onClick();
    }

    /**
     * 实现倒计时Handler
     */
    private static class CountDownHandler extends Handler {

        private WeakReference<AdvPictureView> weakreference;

        public CountDownHandler(AdvPictureView mAdvPictureView) {
            weakreference = new WeakReference<>(mAdvPictureView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AdvPictureView advPictureView = weakreference.get();
            if (advPictureView != null) {
                int countDownTime = msg.what;
                advPictureView.mCurrentCountDownTime = countDownTime;
                if (countDownTime <= 0) {
                    //当倒计时到0的时候,移除所有消息
                    advPictureView.mCountDownHandler.removeCallbacksAndMessages(null);
                    if (advPictureView.mOnAdvPictureListener != null) {
                        advPictureView.mIsCountDown = false;
                        advPictureView.mOnAdvPictureListener.close();
                    }
                    return;
                }
                advPictureView.mIsCountDown = true;
                String content = "<font color='#00c1de'>" + (countDownTime) + "&nbsp;&nbsp;" + "</font>"
                        + "<font color='#FFFFFF'>" + advPictureView.getContext().getString(R.string.alivc_check_list_close) + "</font>";
                advPictureView.mCountDownTextView.setText(Html.fromHtml(content));
                countDownTime--;
                Message mMsg = Message.obtain();
                mMsg.what = countDownTime;
                advPictureView.mCountDownHandler.sendMessageDelayed(mMsg, 1000);
            }
        }
    }
}
