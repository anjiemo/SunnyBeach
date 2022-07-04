package com.aliyun.svideo.common.utils;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

/**
 * @author cross_ly DATE 2019/08/05
 * <p>描述:系统语言工具类
 */
public class LanguageUtils {

    private static final String TAG = "LanguageUtils";

    /**
     * 判断当前语言是否是简体中文
     *
     * @return boolean
     */
    public static boolean isCHEN(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        //语言中文
        boolean isZH = "zh".equals(locale.getLanguage().toLowerCase());
        Log.d(TAG, "当前系统语言 : " + locale.getLanguage() + " ,当前地区 ：" + locale.getCountry());
        return isZH;
    }
}
