package com.aliyun.player.aliyunplayerbase.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 屏幕相关的操作类
 */
public class ScreenUtils {
    /**
     * 获取宽度
     *
     * @param mContext 上下文
     * @return 宽度值，px
     */
    public static int getWidth(Context mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取高度
     *
     * @param mContext 上下文
     * @return 高度值，px
     */
    public static int getHeight(Context mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 是否在屏幕右侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    public static boolean isInRight(Context mContext, int xPos) {
        return (xPos > getWidth(mContext) / 2);
    }

    /**
     * 是否在屏幕左侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    public static boolean isInLeft(Context mContext, int xPos) {
        return (xPos < getWidth(mContext) / 2);
    }

    /**
     * 是否在View的右侧
     *
     * @param view 要判断的View
     * @param xPos 位置x坐标
     * @return true:是 false:不是
     */
    public static boolean isInRight(View view, int xPos) {
        return (xPos > view.getMeasuredWidth() / 2);
    }

    /**
     * 是否在View的左侧
     *
     * @param view 要判断的View
     * @param xPos 位置x坐标
     * @return true:是 false:不是
     */
    public static boolean isInLeft(View view, int xPos) {
        return (xPos < view.getMeasuredWidth() / 2);
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}
