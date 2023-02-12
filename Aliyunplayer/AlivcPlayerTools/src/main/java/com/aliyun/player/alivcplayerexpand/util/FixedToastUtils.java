package com.aliyun.player.alivcplayerexpand.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Toast工具类, 主要修复Toast在android 7.1手机上的BadTokenExceptiion
 *
 * @author xlx
 */
public class FixedToastUtils {

    private static Field mFieldTN;
    private static Field mFieldTNHandler;
    private static Toast mToast;

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

    public static Toast show(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
            if (Build.VERSION.SDK_INT == 25) {
                hook(mToast);
            }
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(message);
        }
        mToast.show();
        return mToast;
    }

    public static Toast show(Context context, int resId) {
        return show(context, context.getResources().getString(resId));
    }

    private static void hook(Toast toast) {
        try {
            Object tn = mFieldTN.get(toast);
            Handler preHandler = (Handler) mFieldTNHandler.get(tn);
            mFieldTNHandler.set(tn, new FiexHandler(preHandler));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


