package com.aliyun.player.alivcplayerexpand.util.download;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class StorageUtil {

    public static long MINIST_STORAGE_SIZE = 102400L;
    public static long MIN_STORAGE_SIZE = 204800L;

    public static boolean isExternalMemoryPath(String path) {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path.contains(sdcardPath);
    }

    public static boolean externalMemoryAvailable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize;
            long availableBlocks;
            if (Build.VERSION.SDK_INT >= 18) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = (long) stat.getBlockSize();
                availableBlocks = (long) stat.getAvailableBlocks();
            }

            return blockSize * availableBlocks / 1024L;
        } else {
            return -1L;
        }
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = (long) stat.getBlockSize();
            availableBlocks = (long) stat.getAvailableBlocks();
        }

        return blockSize * availableBlocks / 1024L;
    }

    public static String getTotalExternalMemorySizeStr() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize;
            long totalBlocks;
            if (Build.VERSION.SDK_INT >= 18) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = (long) stat.getBlockSize();
                totalBlocks = (long) stat.getBlockCount();
            }

            return formatSize(totalBlocks * blockSize);
        } else {
            return null;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize;
            long totalBlocks;
            if (Build.VERSION.SDK_INT >= 18) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = (long) stat.getBlockSize();
                totalBlocks = (long) stat.getBlockCount();
            }

            return blockSize * totalBlocks / 1024L;
        } else {
            return -1L;
        }
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024L) {
            suffix = "KB";
            size /= 1024L;
            if (size >= 1024L) {
                suffix = "MB";
                size /= 1024L;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        for (int commaOffset = resultBuffer.length() - 3; commaOffset > 0; commaOffset -= 3) {
            resultBuffer.insert(commaOffset, ',');
        }

        if (suffix != null) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }
}
