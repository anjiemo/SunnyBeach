package com.aliyun.svideo.common.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时长、时间转换工具类
 */
public class DateTimeUtils {

    /**
     * 格式化毫秒数为 xx:xx:xx这样的时间格式。
     *
     * @param ms 毫秒数
     * @return 格式化后的字符串
     */
    public static String formatMs(long ms) {
        int seconds = (int) (ms / 1000);
        int finalSec = seconds % 60;
        int finalMin = seconds / 60 % 60;
        int finalHour = seconds / 3600;

        StringBuilder msBuilder = new StringBuilder("");
        if (finalHour > 9) {
            msBuilder.append(finalHour).append(":");
        } else if (finalHour > 0) {
            msBuilder.append("0").append(finalHour).append(":");
        }

        if (finalMin > 9) {
            msBuilder.append(finalMin).append(":");
        } else if (finalMin > 0) {
            msBuilder.append("0").append(finalMin).append(":");
        } else {
            msBuilder.append("00").append(":");
        }

        if (finalSec > 9) {
            msBuilder.append(finalSec);
        } else if (finalSec > 0) {
            msBuilder.append("0").append(finalSec);
        } else {
            msBuilder.append("00");
        }

        return msBuilder.toString();
    }

    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd-HHmmssSSS
     *
     * @param millisecond 时间毫秒
     * @return 时间格式: yyyy-MM-dd-HHmmssSSS
     */
    public static String getDateTimeFromMillisecond(Long millisecond) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSS");
        Date date = new Date(millisecond);
        return simpleDateFormat.format(date);
    }


}
