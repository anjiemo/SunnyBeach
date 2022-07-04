package com.aliyun.player.aliyunplayerbase.view.tipsview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.aliyunplayerbase.R;


/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 重播提示对话框。播放结束的时候会显示这个界面
 */
public class ReplayView extends RelativeLayout {
    //重播按钮
    private TextView mReplayBtn;
    //重播事件监听
    private OnReplayClickListener mOnReplayClickListener = null;
    private OnTipsViewBackClickListener mOnTipsViewBackClickListener = null;
    private ImageView mBackImageView;

    public ReplayView(Context context) {
        super(context);
        init();
    }

    public ReplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alivc_dialog_replay, this);

        //设置监听
        mReplayBtn = (TextView) view.findViewById(R.id.replay);
        mBackImageView = view.findViewById(R.id.iv_back);
        mReplayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnReplayClickListener != null) {
                    mOnReplayClickListener.onReplay();
                }
            }
        });

        mBackImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnTipsViewBackClickListener != null) {
                    mOnTipsViewBackClickListener.onBackClick();
                }
            }
        });

    }

//    @Override
//    public void setTheme(Theme theme) {
//        //更新主题
//        if (theme == Theme.Blue) {
//            mReplayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_blue);
//            mReplayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_blue));
//        } else if (theme == Theme.Green) {
//            mReplayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_green);
//            mReplayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_green));
//        } else if (theme == Theme.Orange) {
//            mReplayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_orange);
//            mReplayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_orange));
//        } else if (theme == Theme.Red) {
//            mReplayBtn.setBackgroundResource(R.drawable.alivc_rr_bg_red);
//            mReplayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.alivc_red));
//        }
//    }

    /**
     * 重播点击事件
     */
    public interface OnReplayClickListener {
        /**
         * 重播事件
         */
        void onReplay();
    }

    /**
     * 设置重播事件监听
     *
     * @param l 重播事件
     */
    public void setOnReplayClickListener(OnReplayClickListener l) {
        mOnReplayClickListener = l;
    }

    /**
     * 设置返回按钮监听
     */
    public void setOnBackClickListener(OnTipsViewBackClickListener listener) {
        this.mOnTipsViewBackClickListener = listener;
    }
}
