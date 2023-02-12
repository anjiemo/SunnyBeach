package com.aliyun.player.alivcplayerexpand.listener;

import com.aliyun.player.source.VidSts;

/**
 * sts刷新回调
 */
public interface RefreshStsCallback {

    VidSts refreshSts(String vid, String quality, String format, String title, boolean encript);
}
