package com.aliyun.svideo.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author cross_ly
 * @date 2018/11/28
 * <p>描述:获取当前设备的屏幕参数
 */
public class ScreenUtils {

    /**
     * 获取屏幕的宽高 单位px
     * x = width
     * y = height
     *
     * @param context Context
     * @return Point
     */
    public static Point getScreenPoint(Context context) {

        Point screenPoint = new Point();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenPoint.x = displayMetrics.widthPixels;
        screenPoint.y = displayMetrics.heightPixels;
        return screenPoint;
    }

    /**
     * 获取屏幕宽
     *
     * @param context 上下文
     * @return int ，单位px
     */
    public static int getWidth(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高
     *
     * @param context 上下文
     * @return int ，单位px
     */
    public static int getHeight(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 获取屏幕的真实高度，包含导航栏、状态栏
     *
     * @param context 上下文
     * @return int, 单位px
     */
    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        return dm.heightPixels;
    }

    /**
     * 获取屏幕的真实高度，包含导航栏、状态栏
     *
     * @param context 上下文
     * @return int, 单位px
     */
    public static int getRealWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        return dm.widthPixels;
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
     * 获取虚拟导航栏高度
     *
     * @param activity 上下文
     * @return int, 单位px
     */
    public static int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
