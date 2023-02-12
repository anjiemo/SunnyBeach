package com.aliyun.svideo.common.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author cross_ly DATE 2019/01/24
 * <p>描述:
 */
public class FileUtils {

    /**
     * 获取数据库存储路径
     *
     * @param context
     * @return
     */
    public static File getDbDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AliPlayerDemoDownload/");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }

    /**
     * 获取sdcard剩余内存
     *
     * @return 单位b
     */
    public static long getSdcardAvailableSize() {

        File directory = Environment.getExternalStorageDirectory();

        StatFs statFs = new StatFs(directory.getPath());
        //获取可供程序使用的Block数量
        long blockAvailable = statFs.getAvailableBlocks();
        //获得Sdcard上每个block的size
        long blockSize = statFs.getBlockSize();

        return blockAvailable * blockSize;
    }

    /**
     * 获取sdcard总内存大小
     *
     * @return 单位b
     */
    public static long getSdcardTotalSize() {

        File directory = Environment.getExternalStorageDirectory();

        StatFs statFs = new StatFs(directory.getPath());
        //获得sdcard上 block的总数
        long blockCount = statFs.getBlockCount();
        //获得sdcard上每个block 的大小
        long blockSize = statFs.getBlockSize();

        return blockCount * blockSize;
    }

    public static File getApplicationSdcardPath(Context context) {
        File var1 = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (var1 == null) {
            var1 = context.getFilesDir();
        }

        return var1;
    }

    public static boolean deleteFD(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File var1 = new File(path);
            File var2 = new File(var1.getAbsolutePath() + System.currentTimeMillis());
            var1.renameTo(var2);
            return deleteFD(var2);
        }
    }

    public static boolean deleteFD(File fd) {
        if (!fd.exists()) {
            return false;
        } else {
            return fd.isDirectory() ? deleteDirectory(fd) : fd.delete();
        }
    }

    public static boolean deleteDirectory(File dir) {
        clearDirectory(dir);
        return dir.delete();
    }

    public static void clearDirectory(File dir) {
        File[] var1 = dir.listFiles();
        if (var1 != null) {
            File[] var2 = var1;
            int var3 = var1.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                File var5 = var2[var4];
                if (var5.isDirectory()) {
                    deleteDirectory(var5);
                } else {
                    var5.delete();
                }
            }

        }
    }

    /**
     * 保存图片到本地
     */
    public static String saveBitmap(Bitmap bitmap, String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File f = new File(path, "snapShot_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = null;
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return f.getAbsolutePath();
    }

    /**
     * android Q 版本默认路径
     * /storage/emulated/0/Android/data/包名/files/Media/
     * android Q 以下版本默认"/sdcard/DCIM/Camera/"
     */
    public static String getDir(Context context) {
        String dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = context.getExternalFilesDir("") + File.separator + "Media" + File.separator;
        } else {
            dir = Environment.getExternalStorageDirectory() + File.separator + "DCIM"
                    + File.separator + "Camera" + File.separator;
        }
        File file = new File(dir);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return dir;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveImgToMediaStore(Context context, String fileName, String mimeType) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
            // Write data into the pending image.
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
            ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            BufferedOutputStream bot = new BufferedOutputStream(outputStream);
            byte[] bt = new byte[2048];
            int len;
            while ((len = bin.read(bt)) >= 0) {
                bot.write(bt, 0, len);
                bot.flush();
            }
            bin.close();
            bot.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now that we're finished, release the "pending" status, and allow other apps
        // to view the image.
        values.clear();
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);
        //打印写入时间
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveVideoToMediaStore(Context context, String fileName) {

        long startTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        String name = startTime + "-video.mp4";
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
            // Write data into the pending video.
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
            ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            BufferedOutputStream bot = new BufferedOutputStream(outputStream);
            byte[] bt = new byte[2048];
            int len;
            while ((len = bin.read(bt)) >= 0) {
                bot.write(bt, 0, len);
                bot.flush();
            }
            bin.close();
            bot.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now that we're finished, release the "pending" status, and allow other apps
        // to view the video.
        values.clear();
        values.put(MediaStore.Video.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);
        //打印写入时间
    }
}
