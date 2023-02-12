package com.aliyun.svideo.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 单例Toast工具类
 * <p>
 * 1.解决toast排队的问题
 * 2.修复Toast在android 7.1手机上的BadTokenException
 * 3.兼容位置、时长、stringId
 */
public class ToastUtils {

    private static Toast mToast;
    private static Field mFieldTN;
    private static Field mFieldTNHandler;

    static {
        if (Build.VERSION.SDK_INT == 25) {
            try {
                mFieldTN = Toast.class.getDeclaredField("mTN");
                mFieldTN.setAccessible(true);
                mFieldTNHandler = mFieldTN.getType().getDeclaredField("mHandler");
                mFieldTNHandler.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化/获取mToast对象，适配android 7.1，处理BadTokenException
     *
     * @param context Context
     * @return Toast
     */
    @SuppressLint("ShowToast")
    private static Toast initToast(Context context) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
            if (Build.VERSION.SDK_INT == 25) {
                hook(mToast);
            }
        }
        return mToast;
    }

    /**
     * Toast位置显示在屏幕中间 默认短时长{@link Toast#LENGTH_SHORT}
     *
     * @param context Context
     * @param content 显示内容
     */
    public static void showInCenter(Context context, String content) {

        Toast toast = initToast(context);
        toast.setText(content);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * show Toast 默认短时长 {@link Toast#LENGTH_SHORT}
     *
     * @param context Context
     * @param message 内容
     */
    public static void show(Context context, String message) {

        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * show Toast 默认短时长 {@link Toast#LENGTH_SHORT}
     *
     * @param context  Context
     * @param stringId 内容id
     */
    public static void show(Context context, int stringId) {

        show(context, stringId, Toast.LENGTH_SHORT);
    }

    /**
     * show Toast 可选时长
     *
     * @param context  Context
     * @param message  内容
     * @param duration {@link Toast#LENGTH_SHORT},{@link Toast#LENGTH_LONG}
     */
    public static void show(Context context, String message, int duration) {

        Toast toast = initToast(context);
        toast.setDuration(duration);
        toast.setText(message);
        mToast.show();
    }

    /**
     * show Toast 可选时长
     *
     * @param context  Context
     * @param stringId 内容id
     * @param duration {@link Toast#LENGTH_SHORT},{@link Toast#LENGTH_LONG}
     */
    public static void show(Context context, int stringId, int duration) {

        Toast toast = initToast(context);
        toast.setDuration(duration);
        toast.setText(stringId);
        mToast.show();
    }

    /**
     * show Toast 可选位置
     *
     * @param context  Context
     * @param message  内容
     * @param duration {@link Toast#LENGTH_SHORT},{@link Toast#LENGTH_LONG}
     */
    public static void show(Context context, String message, int gravity, int duration) {

        Toast toast = initToast(context);
        toast.setDuration(duration);
        toast.setText(message);
        toast.setGravity(gravity, 0, 0);
        mToast.show();
    }


    /**
     * 7.1手机上的BadTokenException 相关处理
     *
     * @param toast Toast对象
     */
    private static void hook(Toast toast) {
        try {
            Object tn = mFieldTN.get(toast);
            Handler preHandler = (Handler) mFieldTNHandler.get(tn);
            mFieldTNHandler.set(tn, new FiexHandler(preHandler));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 7.1手机上的BadTokenException 相关处理
     */
    private static class FiexHandler extends Handler {
        private Handler impl;

        FiexHandler(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }
}


