package com.aliyun.player.alivcplayerexpand.view.dot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.aliyun.player.alivcplayerexpand.R;

/**
 * 打点View
 */
public class DotView extends RelativeLayout {

    /**
     * 需要打点的时间点(单位秒)
     */
    private String mTime;
    /**
     * 精彩剧集信息
     */
    private String mDotMsg;
    /**
     * 打点View的跟布局
     */
    private RelativeLayout mDotViewRoot;
    /**
     * 打点点击事件
     */
    private OnDotViewClickListener mOnDotViewClickListener;

    public DotView(Context context) {
        super(context);
        init();
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_player_dot_view_layout, this, true);
        initView();
        initListener();
    }

    private void initListener() {
        mDotViewRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDotViewClickListener != null) {
                    mOnDotViewClickListener.onDotViewClick();
                }
            }
        });
    }

    public void setOnDotViewClickListener(OnDotViewClickListener listener) {
        this.mOnDotViewClickListener = listener;
    }

    private void initView() {
        mDotViewRoot = findViewById(R.id.dot_view_root);
    }

    public int getRootWidth() {
        return mDotViewRoot.getMeasuredWidth();
    }

    public void setDotTime(String time) {
        this.mTime = time;
    }

    public void setDotMsg(String content) {
        this.mDotMsg = content;
    }

    public String getDotTime() {
        return this.mTime;
    }

    public String getDotMsg() {
        return this.mDotMsg;
    }

    public interface OnDotViewClickListener {
        void onDotViewClick();
    }
}
