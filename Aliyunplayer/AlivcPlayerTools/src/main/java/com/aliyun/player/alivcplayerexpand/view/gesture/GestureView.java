package com.aliyun.player.alivcplayerexpand.view.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aliyun.player.alivcplayerexpand.view.interfaces.ViewAction;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;
/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的view。用于UI中处理手势的滑动事件，从而去实现手势改变亮度，音量，seek等操作。
 * 此view主要被{@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView} 使用。
 */
public class GestureView extends View implements ViewAction {

    private static final String TAG = GestureView.class.getSimpleName();

    //手势控制
    protected GestureControl mGestureControl;
    //监听器
    private GestureListener mOutGestureListener = null;

    //隐藏原因
    private HideType mHideType = null;
    //是否锁定屏幕
    private boolean mIsFullScreenLocked = false;
    //是否处于分屏模式
    private boolean mIsInMultiWindow;

    public GestureView(Context context) {
        super(context);
        init();
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //创建手势控制
        mGestureControl = new GestureControl(getContext(), this);
        mGestureControl.setMultiWindow(mIsInMultiWindow);
        mGestureControl.setView(this);
        //设置监听
        mGestureControl.setOnGestureControlListener(new GestureListener() {

            @Override
            public void onHorizontalDistance(float downX, float nowX) {
                //其他手势如果锁住了就不回调了。
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onHorizontalDistance(downX, nowX);
                }
            }

            @Override
            public void onLeftVerticalDistance(float downY, float nowY) {
                //其他手势如果锁住了就不回调了。
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onLeftVerticalDistance(downY, nowY);
                }
            }

            @Override
            public void onRightVerticalDistance(float downY, float nowY) {
                //其他手势如果锁住了就不回调了。
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onRightVerticalDistance(downY, nowY);
                }
            }

            @Override
            public void onGestureEnd() {
                //其他手势如果锁住了就不回调了。
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onGestureEnd();
                }
            }

            @Override
            public void onSingleTap() {
                //锁屏的时候，单击还是有用的。。不然没法显示锁的按钮了
                if (mOutGestureListener != null) {
                    mOutGestureListener.onSingleTap();
                }
            }

            @Override
            public void onDoubleTap() {
                //其他手势如果锁住了就不回调了。
                if (mIsFullScreenLocked) {
                    return;
                }

                if (mOutGestureListener != null) {
                    mOutGestureListener.onDoubleTap();
                }
            }

        });
    }

    /**
     * 设置是否锁定全屏了。锁定全屏的话，除了单击手势有响应，其他都不会有响应。
     *
     * @param locked true：锁定。
     */
    public void setScreenLockStatus(boolean locked) {
        mIsFullScreenLocked = locked;
    }

    public void setHideType(HideType hideType) {
        this.mHideType = hideType;
    }

    /**
     * 设置是否处于分屏模式
     *
     * @param isInMultiWindow true,分屏模式,false不是分屏模式
     */
    public void setMultiWindow(boolean isInMultiWindow) {
        this.mIsInMultiWindow = isInMultiWindow;
        if (mGestureControl != null) {
            mGestureControl.setMultiWindow(mIsInMultiWindow);
        }
    }

    public interface GestureListener {
        /**
         * 水平滑动距离
         *
         * @param downX 按下位置
         * @param nowX  当前位置
         */
        void onHorizontalDistance(float downX, float nowX);

        /**
         * 左边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        void onLeftVerticalDistance(float downY, float nowY);

        /**
         * 右边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        void onRightVerticalDistance(float downY, float nowY);

        /**
         * 手势结束
         */
        void onGestureEnd();

        /**
         * 单击事件
         */
        void onSingleTap();

        /**
         * 双击事件
         */
        void onDoubleTap();
    }

    /**
     * 设置手势监听事件
     *
     * @param gestureListener 手势监听事件
     */
    public void setOnGestureListener(GestureListener gestureListener) {
        mOutGestureListener = gestureListener;
    }

    @Override
    public void reset() {
        mHideType = null;
    }

    @Override
    public void show() {
        if (mHideType == HideType.End) {
            //如果是由于错误引起的隐藏，那就不能再展现了
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void hide(HideType hideType) {
        if (mHideType != HideType.End) {
            mHideType = hideType;
        }
        setVisibility(GONE);
    }

    @Override
    public void setScreenModeStatus(AliyunScreenMode mode) {

    }

}
