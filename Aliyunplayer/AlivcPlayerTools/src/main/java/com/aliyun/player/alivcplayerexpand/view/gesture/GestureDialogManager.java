package com.aliyun.player.alivcplayerexpand.view.gesture;

import android.app.Activity;
import android.view.View;

import com.aliyun.player.alivcplayerexpand.view.gesturedialog.BrightnessDialog;
import com.aliyun.player.alivcplayerexpand.view.gesturedialog.SeekDialog;
import com.aliyun.player.alivcplayerexpand.view.gesturedialog.VolumeDialog;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势对话框的管理器。
 * 用于管理亮度{@link com.aliyun.player.alivcplayerexpand.view.gesturedialog.BrightnessDialog} ，
 * seek{@link com.aliyun.player.alivcplayerexpand.view.gesturedialog.SeekDialog} ，
 * 音量{@link com.aliyun.player.alivcplayerexpand.view.gesturedialog.VolumeDialog}等dialog的显示/隐藏等。
 */

public class GestureDialogManager {

    //用于构建手势用的dialog
    private Activity mActivity;
    //seek手势对话框
    private SeekDialog mSeekDialog = null;
    //亮度对话框
    private BrightnessDialog mBrightnessDialog = null;
    //音量对话框
    private VolumeDialog mVolumeDialog = null;
    //当前屏幕默认为小屏
    private AliyunScreenMode mCurrentScreenMode = AliyunScreenMode.Small;

    public GestureDialogManager(Activity activity) {
        mActivity = activity;
    }

    /**
     * 显示seek对话框
     *
     * @param parent         显示在哪个view的中间
     * @param targetPosition seek的位置
     */
    public void showSeekDialog(View parent, int targetPosition) {
        if (mSeekDialog == null) {
            mSeekDialog = new SeekDialog(mActivity, targetPosition);
        }
        if (!mSeekDialog.isShowing()) {
            mSeekDialog.show(parent);
            mSeekDialog.updatePosition(targetPosition);
        }

    }

    /**
     * 更新seek进度。 在手势滑动的过程中调用。
     *
     * @param duration        时长
     * @param currentPosition 当前位置
     * @param deltaPosition   滑动位置
     */
    public void updateSeekDialog(long duration, long currentPosition, long deltaPosition) {
        int targetPosition = mSeekDialog.getTargetPosition(duration, currentPosition, deltaPosition);
        mSeekDialog.updatePosition(targetPosition);
    }

    /**
     * 隐藏seek对话框
     *
     * @return 最终的seek位置，用于实际的seek操作
     */
    public int dismissSeekDialog() {
        int seekPosition = -1;
        if (mSeekDialog != null && mSeekDialog.isShowing()) {
            seekPosition = mSeekDialog.getFinalPosition();
            mSeekDialog.dismiss();
        }
        mSeekDialog = null;
        //返回最终的seek位置，用于实际的seek操作
        return seekPosition;
    }


    /**
     * 显示亮度对话框
     *
     * @param parent 显示在哪个view中间
     */
    public void showBrightnessDialog(View parent, int currentBrightness) {

        if (mBrightnessDialog == null) {
            mBrightnessDialog = new BrightnessDialog(mActivity, currentBrightness);
        }

        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.setScreenMode(mCurrentScreenMode);
            mBrightnessDialog.show(parent);
            mBrightnessDialog.updateBrightness(currentBrightness);
        }
    }

    /**
     * 更新亮度值
     *
     * @param changePercent 亮度变化百分比
     * @return 最终的亮度百分比
     */
    public int updateBrightnessDialog(int changePercent) {
        int targetBrightnessPercent = mBrightnessDialog.getTargetBrightnessPercent(changePercent);
        mBrightnessDialog.updateBrightness(targetBrightnessPercent);
        return targetBrightnessPercent;
    }

    /**
     * 隐藏亮度对话框
     */
    public void dismissBrightnessDialog() {
        if (mBrightnessDialog != null && mBrightnessDialog.isShowing()) {
            mBrightnessDialog.dismiss();
        }
        mBrightnessDialog = null;
    }

    public void initDialog(Activity activity, float currentPercent) {
        this.mActivity = activity;
        if (mVolumeDialog == null) {
            mVolumeDialog = new VolumeDialog(activity, currentPercent);
        }
    }

    /**
     * 显示音量对话框
     *
     * @param parent         显示在哪个view中间
     * @param currentPercent 当前音量百分比
     */
    public void showVolumeDialog(View parent, float currentPercent) {
        if (mVolumeDialog == null) {
            mVolumeDialog = new VolumeDialog(mActivity, currentPercent);
        }

        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.setScreenMode(mCurrentScreenMode);
            mVolumeDialog.show(parent);
            mVolumeDialog.updateVolume(currentPercent);
        }
    }

    public boolean isVolumeDialogIsShow() {
        if (mVolumeDialog == null) {
            return false;
        }
        return mVolumeDialog.isShowing();
    }

    /**
     * 更新音量
     *
     * @param changePercent 变化的百分比
     * @return 最终的音量百分比
     */
    public float updateVolumeDialog(int changePercent) {
        float targetVolume = mVolumeDialog.getTargetVolume(changePercent);
        mVolumeDialog.updateVolume(targetVolume);
        return targetVolume;
    }

    /**
     * 关闭音量对话框
     */
    public void dismissVolumeDialog() {
        if (mVolumeDialog != null && mVolumeDialog.isShowing()) {
            mVolumeDialog.dismiss();
        }
        mVolumeDialog = null;
    }

    public void setCurrentScreenMode(AliyunScreenMode currentScreenMode) {
        this.mCurrentScreenMode = currentScreenMode;
    }
}
