package cn.cqautotest.sunnybeach.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.scankit.util.a;

import cn.cqautotest.sunnybeach.ui.activity.ScanCodeActivity;

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/21
 * desc   : 复制自 com.huawei.hms.hmsscankit.ScanUtil
 */
public class MyScanUtil {

    @SuppressLint("WrongConstant")
    public static int startScan(Activity var0, int var1, HmsScanAnalyzerOptions var2) {
        byte var3 = 28;
        int var4;
        label40:
        {
            label39:
            {
                label38:
                {
                    label44:
                    {
                        PackageManager var10;
                        String var11;
                        try {
                            var10 = var0.getPackageManager();
                            var11 = var0.getPackageName();
                        } catch (RuntimeException var8) {
                            break label44;
                        }
                        short var10002 = 16384;
                        try {
                            var4 = var10.getPackageInfo(var11, var10002).applicationInfo.targetSdkVersion;
                            break label40;
                        } catch (PackageManager.NameNotFoundException var5) {
                            break label38;
                        } catch (RuntimeException ignored) {
                        }
                    }
                    a.b("exception", "RuntimeException");
                    break label39;
                }
                a.b("exception", "NameNotFoundException");
            }
            var4 = var3;
        }
        if (selfPermissionGranted(var0, var4, "android.permission.CAMERA")) {
            Intent var9;
            // 将需要跳转的扫码界面替换为自定义的扫码界面，以修复弹出选择图片应用对话框导致的无法正常扫码的问题。
            var9 = new Intent(var0, ScanCodeActivity.class);
            if (var2 != null) {
                var9.putExtra("ScanFormatValue", var2.mode);
            }
            var0.startActivityForResult(var9, var1);
            return 0;
        } else {
            return 1;
        }
    }

    public static boolean selfPermissionGranted(Context var0, int var1, String var2) {
        if (Build.VERSION.SDK_INT >= 23) {
            label29:
            {
                if (var1 >= 23) {
                    if (com.huawei.hms.scankit.util.b.a(var2) == null) {
                        return true;
                    }
                    if (var0.checkSelfPermission(var2) == PackageManager.PERMISSION_GRANTED) {
                        break label29;
                    }
                } else if (com.huawei.hms.scankit.util.b.a(var0, var2) == 0) {
                    break label29;
                }
                return false;
            }
        }
        return true;
    }
}
