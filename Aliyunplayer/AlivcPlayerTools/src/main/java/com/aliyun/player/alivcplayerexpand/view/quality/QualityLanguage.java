package com.aliyun.player.alivcplayerexpand.view.quality;

import android.content.Context;
import android.text.TextUtils;

import com.aliyun.player.alivcplayerexpand.R;


public class QualityLanguage {

    public QualityLanguage() {

    }

    public static String getSaasLanguage(Context context, String quality) {
        if ("FD".equals(quality)) {
            return context.getString(R.string.alivc_fd_definition);
        } else if ("LD".equals(quality)) {
            return context.getString(R.string.alivc_ld_definition);
        } else if ("SD".equals(quality)) {
            return context.getString(R.string.alivc_sd_definition);
        } else if ("HD".equals(quality)) {
            return context.getString(R.string.alivc_hd_definition);
        } else if ("2K".equals(quality)) {
            return context.getString(R.string.alivc_k2_definition);
        } else if ("4K".equals(quality)) {
            return context.getString(R.string.alivc_k4_definition);
        } else if ("SQ".equals(quality)) {
            return context.getString(R.string.alivc_sq_definition);
        } else if ("HQ".equals(quality)) {
            return context.getString(R.string.alivc_hq_definition);
        } else {
            return "OD".equals(quality) ? context.getString(R.string.alivc_od_definition) : context.getString(R.string.alivc_od_definition);
        }
    }

    public static String getMtsLanguage(Context context, String quality) {
        if (TextUtils.isEmpty(quality)) {
            return null;
        } else {
            String xldStr;
            String item;
            if (quality.toUpperCase().contains("XLD")) {
                xldStr = context.getString(R.string.alivc_mts_xld_definition);
                if (quality.contains("_")) {
                    item = quality.split("_")[1];
                    return xldStr + "_" + item;
                } else {
                    return xldStr;
                }
            } else if (quality.toUpperCase().contains("LD")) {
                xldStr = context.getString(R.string.alivc_mts_ld_definition);
                if (quality.contains("_")) {
                    item = quality.split("_")[1];
                    return xldStr + "_" + item;
                } else {
                    return xldStr;
                }
            } else if (quality.toUpperCase().contains("SD")) {
                xldStr = context.getString(R.string.alivc_mts_sd_definition);
                if (quality.contains("_")) {
                    item = quality.split("_")[1];
                    return xldStr + "_" + item;
                } else {
                    return xldStr;
                }
            } else if (quality.toUpperCase().contains("FHD")) {
                xldStr = context.getString(R.string.alivc_mts_fhd_definition);
                if (quality.contains("_")) {
                    item = quality.split("_")[1];
                    return xldStr + "_" + item;
                } else {
                    return xldStr;
                }
            } else if (quality.toUpperCase().contains("HD")) {
                xldStr = context.getString(R.string.alivc_mts_hd_definition);
                if (quality.contains("_")) {
                    item = quality.split("_")[1];
                    return xldStr + "_" + item;
                } else {
                    return xldStr;
                }
            } else {
                return null;
            }
        }
    }
}
