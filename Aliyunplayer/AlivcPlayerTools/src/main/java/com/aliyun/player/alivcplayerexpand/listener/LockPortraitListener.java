package com.aliyun.player.alivcplayerexpand.listener;

/**
 * 屏幕状态监听
 */
public interface LockPortraitListener {

    int FIX_MODE_SMALL = 1;
    int FIX_MODE_FULL = 2;

    void onLockScreenMode(int type);
}
