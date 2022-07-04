package com.aliyun.player.alivcplayerexpand.view.gesturedialog;

import android.app.Activity;

import com.aliyun.player.alivcplayerexpand.R;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的音量提示框。
 */
public class VolumeDialog extends BaseGestureDialog {

    private static final String TAG = VolumeDialog.class.getSimpleName();
    private float initVolume = 0;

    public VolumeDialog(Activity context, float percent) {
        super(context);
        initVolume = percent;
        mImageView.setImageResource(R.drawable.alivc_volume_img);
        updateVolume(percent);
    }

    /**
     * 更新音量值
     *
     * @param percent 音量百分比
     */
    public void updateVolume(float percent) {
        mTextView.setText((int) percent + "%");
        mImageView.setImageLevel((int) percent);
    }

    /**
     * 获取最后的音量
     *
     * @param changePercent 变化的百分比
     * @return 最后的音量
     */
    public float getTargetVolume(int changePercent) {
        float newVolume = initVolume - changePercent;
        if (newVolume > 100) {
            newVolume = 100;
        } else if (newVolume < 0) {
            newVolume = 0;
        }
        return newVolume;
    }
}
