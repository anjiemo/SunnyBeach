package com.aliyun.svideo.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * notch screen (刘海屏) 检测utils
 * 适配了华为、oppo、小米、vivo
 */
public class NotchScreenUtil {

    private static final String TAG = NotchScreenUtil.class.getName();

    /**
     * 设置应用窗口在华为notch手机使用刘海区的flag值, 该值为华为官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_HW = 0x00010000;

    /**
     * vivo手机判断是否是notch, vivo官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_VIVO = 0x00000020;


    public static boolean checkNotchScreen(Context context) {
        if (checkHuaWei(context)) {
            return true;
        } else if (checkVivo(context)) {
            return true;
        } else if (checkMiUI(context)) {
            return true;
        } else if (checkOppo(context)) {
            return true;
        }

        return false;
    }

    /**
     * oppo提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkOppo(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            Log.e(TAG, "checkOppo notchScreen exception");
        }
        return false;
    }

    /**
     * 小米提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkMiUI(Context context) {

        int result = 0;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi")
            @SuppressWarnings("rawtypes")
            Class systemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = systemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = "ro.miui.notch";
            params[1] = 0;
            result = (Integer) getInt.invoke(systemProperties, params);
            return result == 1;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 华为提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：刘海屏；false：非刘海屏
     */
    private static boolean checkHuaWei(Context context) {

        boolean ret = false;

        try {

            ClassLoader cl = context.getClassLoader();

            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");

            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");

            ret = (boolean) get.invoke(hwNotchSizeUtil);

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");

        }
        return ret;
    }

    /**
     * vivo提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：是刘海屏；false：非刘海屏
     */
    private static boolean checkVivo(Context context) {

        boolean ret;
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressLint("PrivateApi")
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method isFeatureSupport = ftFeature.getMethod("isFeatureSupport");
            ret = (boolean) isFeatureSupport.invoke(ftFeature, FLAG_NOTCH_SUPPORT_VIVO);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * 华为提供: 获取刘海尺寸
     *
     * @param context Context
     * @return int[0]值为刘海宽度 int[1]值为刘海高度。
     */
    public static int[] getNotchSize(Context context) {

        int[] ret = new int[]{0, 0};

        try {

            ClassLoader cl = context.getClassLoader();

            Class hwnotchsizeutil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");

            Method get = hwnotchsizeutil.getMethod("getNotchSize");

            ret = (int[]) get.invoke(hwnotchsizeutil);

        } catch (ClassNotFoundException e) {

            Log.e("test", "getNotchSize ClassNotFoundException");

        } catch (NoSuchMethodException e) {

            Log.e("test", "getNotchSize NoSuchMethodException");

        } catch (Exception e) {

            Log.e("test", "getNotchSize Exception");

        }
        return ret;
    }

    /**
     * 华为提供: 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hw add notch screen flag api error");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hw add notch screen flag api error");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "hw add notch screen flag api error");
        } catch (InstantiationException e) {
            Log.e(TAG, "hw add notch screen flag api error");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "hw add notch screen flag api error");
        } catch (Exception e) {
            Log.e(TAG, "other Exception");
        }
    }

    /**
     * 华为提供: 设置应用窗口在华为刘海屏手机不使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW);
            Log.e(TAG, "............clear");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (InstantiationException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (Exception e) {
            Log.e(TAG, "other Exception");
        }
    }
}
