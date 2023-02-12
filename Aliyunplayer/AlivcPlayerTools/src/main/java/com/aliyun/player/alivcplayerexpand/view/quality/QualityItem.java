package com.aliyun.player.alivcplayerexpand.view.quality;

import android.content.Context;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 清晰度列表的项
 */
public class QualityItem {
    //原始的清晰度
    private String mQuality;
    //显示的文字
    private String mName;

    private QualityItem(String quality, String name) {
        mQuality = quality;
        mName = name;
    }

    public static QualityItem getItem(Context context, String quality, boolean isMts) {
//mts与其他的清晰度格式不一样，
        if (isMts) {
            //这里是getMtsLanguage
            String name = QualityLanguage.getMtsLanguage(context, quality);
            return new QualityItem(quality, name);
        } else {
            //这里是getSaasLanguage
            String name = QualityLanguage.getSaasLanguage(context, quality);
            return new QualityItem(quality, name);
        }
    }

    /**
     * 获取显示的文字
     *
     * @return 清晰度文字
     */
    public String getName() {
        return mName;
    }

}
