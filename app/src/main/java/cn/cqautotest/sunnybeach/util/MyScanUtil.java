package cn.cqautotest.sunnybeach.util;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import cn.cqautotest.sunnybeach.ui.activity.ScanCodeActivity;

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 扫码统一入口
 */
public class MyScanUtil {

    public static void startScan(Fragment fragment, ActivityResultLauncher<Intent> launcher, Integer mode) {
        Context context = fragment.getContext();
        if (context == null) return;
        Intent intent = new Intent(context, ScanCodeActivity.class);
        if (mode != null) {
            intent.putExtra("ScanFormatValue", mode);
        }
        launcher.launch(intent);
    }

    public static void startScan(ActivityResultLauncher<Integer> launcher, Integer mode) {
        launcher.launch(mode);
    }
}
