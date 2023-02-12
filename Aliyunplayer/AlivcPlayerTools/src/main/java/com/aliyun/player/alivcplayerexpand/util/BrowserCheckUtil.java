package com.aliyun.player.alivcplayerexpand.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class BrowserCheckUtil {

    /**
     * 检测手机安装的浏览器
     */
    @SuppressLint("WrongConstant")
    public static List<ResolveInfo> checkBrowserList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://"));
        return packageManager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
    }
}
