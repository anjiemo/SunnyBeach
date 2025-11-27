package cn.cqautotest.sunnybeach.util;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import cn.cqautotest.sunnybeach.ui.activity.ScanCodeActivity;

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 扫码统一入口
 */
public class MyScanUtil {

    public static void startScan(Fragment fragment, ActivityResultLauncher<Intent> launcher, HmsScanAnalyzerOptions options) {
        Context context = fragment.getContext();
        if (context == null) return;
        Intent intent = new Intent(context, ScanCodeActivity.class);
        if (options != null) {
            intent.putExtra("ScanFormatValue", options.mode);
        }
        launcher.launch(intent);
    }

    public static void startScan(ActivityResultLauncher<HmsScanAnalyzerOptions> launcher, HmsScanAnalyzerOptions options) {
        launcher.launch(options);
    }
}
