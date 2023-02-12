package com.aliyun.player.aliyunplayerbase.bean;

public enum VideoSourceType {
    /**
     * 选用url播放
     */
    TYPE_URL,
    /**
     * 选用sts方式播放
     */
    TYPE_STS,
    /**
     * 选用直播的方式播放
     */
    TYPE_LIVE,
    /**
     * 错误的视频，不在列表中显示
     */
    TYPE_ERROR_NOT_SHOW
}
