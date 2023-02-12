package com.aliyun.player.aliyunlistplayer.listener;

import android.view.View;

/**
 * View滑动监听
 */
public interface OnViewPagerListener {

    /**
     * 初始化完成
     */
    void onInitComplete();

    /**
     * 页面不可见, 释放
     *
     * @param isNext   是否有下一个
     * @param position 下标
     */
    void onPageRelease(boolean isNext, int position, View view);

    /**
     * 选中的index
     *
     * @param position 下标
     * @param bottom   是否到底部
     */
    void onPageSelected(int position, boolean bottom, View view);
}
