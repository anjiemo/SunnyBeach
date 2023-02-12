package com.aliyun.player.aliyunlistplayer.listener;

/**
 * 刷新数据
 */
public interface OnRefreshDataListener {
    /**
     * 下拉刷新
     */
    void onRefresh();

    /**
     * 上拉加载
     */
    void onLoadMore();
}
