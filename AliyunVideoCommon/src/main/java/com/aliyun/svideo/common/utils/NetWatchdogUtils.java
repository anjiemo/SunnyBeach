package com.aliyun.svideo.common.utils;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接状态的监听器。通过注册broadcast实现的
 */
public class NetWatchdogUtils {

    private Context mContext;
    //网络变化监听
    private NetChangeListener mNetChangeListener;

    //广播过滤器，监听网络变化
    private IntentFilter mNetIntentFilter = new IntentFilter();

    /**
     * 网络变化监听事件
     */
    public interface NetChangeListener {
        /**
         * wifi变为4G
         */
        void onWifiTo4G();

        /**
         * 4G变为wifi
         */
        void on4GToWifi();

        /**
         * 网络已连接
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * 网络未连接
         */
        void onNetUnConnected();
    }

    private boolean isReconnect;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取手机的连接服务管理器，这里是连接管理器类
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null) {
                return;
            }
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
            NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

            if (wifiNetworkInfo != null) {
                wifiState = wifiNetworkInfo.getState();
            }
            if (mobileNetworkInfo != null) {
                mobileState = mobileNetworkInfo.getState();
            }

            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                if (mNetChangeListener != null) {
                    mNetChangeListener.onReNetConnected(isReconnect);
                    isReconnect = false;
                }
            } else if (activeNetworkInfo == null) {
                if (mNetChangeListener != null) {
                    isReconnect = true;
                    mNetChangeListener.onNetUnConnected();
                }
            }

            if (NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
                if (mNetChangeListener != null) {
                    mNetChangeListener.onWifiTo4G();
                }
            } else if (NetworkInfo.State.CONNECTED == wifiState && NetworkInfo.State.CONNECTED != mobileState) {
                if (mNetChangeListener != null) {
                    mNetChangeListener.on4GToWifi();
                }
            } else if (NetworkInfo.State.CONNECTED != wifiState) {
                if (mNetChangeListener != null) {
                    mNetChangeListener.onNetUnConnected();
                }
            }

        }
    };


    public NetWatchdogUtils(Context context) {
        mContext = context.getApplicationContext();
        mNetIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    /**
     * 设置网络变化监听
     *
     * @param l 监听事件
     */
    public void setNetChangeListener(NetChangeListener l) {
        mNetChangeListener = l;
    }


    /**
     * 开始监听
     */
    public void startWatch() {
        try {
            mContext.registerReceiver(mReceiver, mNetIntentFilter);
        } catch (Exception ignored) {
        }
    }

    /**
     * 结束监听
     */
    public void stopWatch() {
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception ignored) {
        }
    }


    /**
     * 静态方法获取是否有网络连接
     *
     * @param context 上下文
     * @return 是否连接
     */
    public static boolean hasNet(Context context) {
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }
        NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        NetworkInfo.State wifiState = NetworkInfo.State.UNKNOWN;
        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

        if (wifiNetworkInfo != null) {
            wifiState = wifiNetworkInfo.getState();
        }
        if (mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        if (NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED != mobileState) {
            return false;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * 静态判断是不是4G网络
     *
     * @param context 上下文
     * @return 是否是4G
     */
    public static boolean is4GConnected(Context context) {
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        NetworkInfo.State mobileState = NetworkInfo.State.UNKNOWN;

        if (mobileNetworkInfo != null) {
            mobileState = mobileNetworkInfo.getState();
        }

        return NetworkInfo.State.CONNECTED == mobileState;
    }

}
