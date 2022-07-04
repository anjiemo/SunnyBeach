package com.aliyun.svideo.common.utils.upgrade;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.aliyun.svideo.common.R;
import com.aliyun.svideo.common.utils.ThreadUtils;
import com.aliyun.svideo.common.widget.CustomProgressDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author cross_ly
 * @date 2018/11/07
 * <p>描述:自动升级相关 -> allmodule
 * **更新策略为强制更新**
 */
public class AutoUpgradeClient {

    /**
     * 请求读写权限的request code
     */
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 10001;
    private static String TAG = AutoUpgradeClient.class.getName();
    /**
     * oss-upgrade文件的URL
     * cdn加速域名
     */
    private static String UPGRADE_JSON_BASE_URL = "https://alivc-demo-cms.alicdn.com";

    /**
     * 临时apk文件的文件名头
     */
    private static String sOutputBasePath = Environment.getExternalStorageDirectory() + "/Download/" + File.separator + "aliyunVideoAllmodule";

    private static Context sContext;

    private static String outPath;

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_ERROR = 3;
    private static CustomProgressDialog progressDialog;

    private static int progress;// 当前进度


    private static Handler mHandler = null;

    private static void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWN_UPDATE:
                        progressDialog.setProgress(progress);
                        break;
                    case DOWN_OVER:
                        progressDialog.dismiss();
                        installApk(outPath);
                        break;
                    case DOWN_ERROR:
                        progressDialog.dismiss();
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public static void setUpgradeJsonBaseUrl(String upgradeJsonBaseUrl) {
        UPGRADE_JSON_BASE_URL = upgradeJsonBaseUrl;
    }

    public static String getUpgradeJsonBaseUrl() {
        return UPGRADE_JSON_BASE_URL;
    }

    /**
     * 检查升级
     */
    public static void checkUpgrade(Context context, final String jsonName, final int currentVersion) {
        sContext = context;
        final String urlPath = UPGRADE_JSON_BASE_URL + jsonName;
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                String line;
                BufferedReader reader = null;
                URL url;
                try {
                    url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        // 通过HttpURLConnection对象，得到InputStream
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        String info = sb.toString();
                        Log.d("text", "版本升级信息:" + info);//--------
                        // 对升级的信息进行封装
                        JSONObject object = new JSONObject(info);
                        final UpgradeBean upgradeBean = new UpgradeBean();
                        upgradeBean.setVersionName(object.optString("versionName"));
                        upgradeBean.setVersionCode(object.optInt("versionCode"));
                        upgradeBean.setDescribe(object.optString("describe"));
                        upgradeBean.setUrl(object.optString("url"));
                        Log.i(TAG, "当前版本code = " + currentVersion + " ,最新版本code = " + upgradeBean.getVersionCode());
                        if (upgradeBean.getVersionCode() > currentVersion) {
                            //提示有升级
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showHintDialog(upgradeBean);
                                }
                            });

                        } else {
                            release();
                        }

                    }
                } catch (Exception e) {
                    release();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 提示有更新
     *
     * @param upgradeBean UpgradeBean
     */
    private static void showHintDialog(final UpgradeBean upgradeBean) {
        if (sContext == null) {
            return;
        }
        Dialog dialog = new AlertDialog.Builder(sContext)
                .setPositiveButton(sContext.getResources().getString(R.string.aliyun_common_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startDownload(upgradeBean);
                    }
                })
                .setNegativeButton(sContext.getResources().getString(R.string.aliyun_common_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage(upgradeBean.getDescribe())
                .setTitle(sContext.getResources().getString(R.string.aliyun_common_update_app))
                .setCancelable(false)
                .create();
        dialog.show();
    }

    /**
     * 开始下载apk
     *
     * @param upgradeBean UpgradeBean
     */
    private static void startDownload(final UpgradeBean upgradeBean) {
        if (sContext == null) {
            return;
        }
        //android 23 权限适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ContextCompat.checkSelfPermission(sContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //进入到这里代表没有权限.
                ActivityCompat.requestPermissions((Activity) sContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                Log.e(TAG, "自动升级 Failure : Permission Not WRITE_EXTERNAL_STORAGE  ");
                return;
            }
        }
        Log.i(TAG, "自动升级,----------------- start ----------------");
        outPath = sOutputBasePath + upgradeBean.getVersionCode() + ".apk";
        progressDialog = new CustomProgressDialog(sContext);
        progressDialog.setMaxProgress(100);
        progressDialog.setMessage(upgradeBean.getDescribe());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(sContext.getResources().getString(R.string.aliyun_common_updating));
        progressDialog.show();
        if (mHandler == null) {
            initHandler();
        }

        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(upgradeBean.getUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream ins = conn.getInputStream();
                    FileOutputStream outStream = new FileOutputStream(outPath);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    int numread;
                    while ((numread = ins.read(buf)) != -1) {
                        outStream.write(buf, 0, numread);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        // 下载进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (progress == 100) {
                            mHandler.sendEmptyMessage(DOWN_OVER);
                            break;
                        }
                    }
                    outStream.close();
                    ins.close();
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(DOWN_ERROR);
                    release();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 安装应用
     */
    private static void installApk(final String outPath) {

        if (sContext != null) {
            //安装应用
            File apkFile = new File(outPath);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(getUriForFile(sContext, apkFile), "application/vnd.android.package-archive");
            Log.e(TAG, "installApk: ");
            sContext.startActivity(install);
        }
    }

    /**
     * 兼容7.0
     *
     * @param context
     * @param file
     * @return
     */
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 释放context对象
     */
    private static void release() {
        sContext = null;
        mHandler = null;
    }

}
