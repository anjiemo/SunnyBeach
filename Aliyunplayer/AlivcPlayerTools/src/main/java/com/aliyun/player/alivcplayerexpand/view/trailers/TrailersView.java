package com.aliyun.player.alivcplayerexpand.view.trailers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;

/**
 * 试看
 */
public class TrailersView extends RelativeLayout {

    private View view;
    /**
     * 开通vip
     */
    private TextView mTrailerOpenTextView;
    /**
     * 重新播放
     */
    private LinearLayout mTrailerPlayAgainRootView;

    /**
     * 重播
     */
    public OnTrailerViewClickListener mListener;
    /**
     * 视频播放试看tips
     */
    private LinearLayout mTrailerPlayTipsRoot;
    /**
     * 视频试看播放完成tips
     */
    private FrameLayout mTrailerTipsRootView;

    public TrailersView(Context context) {
        super(context);
        init();
    }

    public TrailersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrailersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.alivc_trailers_view_layout, this, true);

        initView();
        initListener();
    }

    private void initView() {
        mTrailerOpenTextView = view.findViewById(R.id.tv_trailer_open);
        mTrailerTipsRootView = view.findViewById(R.id.ll_trailer_tips_root);
        mTrailerPlayTipsRoot = view.findViewById(R.id.ll_trailer_play_tips_root);
        mTrailerPlayAgainRootView = view.findViewById(R.id.ll_trailer_play_again);
    }

    private void initListener() {
        mTrailerOpenTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOpenVipClick();
                }
            }
        });

        mTrailerPlayAgainRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTrailerPlayAgainClick();
                }
            }
        });
    }

    public void hideAll() {
        if (mTrailerPlayTipsRoot != null) {
            mTrailerPlayTipsRoot.setVisibility(View.GONE);
        }
        if (mTrailerTipsRootView != null) {
            mTrailerTipsRootView.setVisibility(View.GONE);
        }
    }

    public void trailerPlayTipsIsShow(boolean isShow) {
        if (mTrailerPlayTipsRoot != null) {
            mTrailerPlayTipsRoot.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        if (mTrailerTipsRootView != null) {
            mTrailerTipsRootView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        }
    }

    public interface OnTrailerViewClickListener {
        void onTrailerPlayAgainClick();

        void onOpenVipClick();
    }

    public void setOnTrailerViewClickListener(OnTrailerViewClickListener listener) {
        this.mListener = listener;
    }
}
