package com.danny_mcoy.simplecommad.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import com.danny_mcoy.simplecommad.log.Logger;

/**
 * Created by Danny_姜新星 on 6/28/2016.
 */
public class DiskMemory {

    public static boolean enoughMemory(final String pathToDir, final Long... sizes) {
        long freeMemory = freeMemory(pathToDir);
        Log.d(Logger.TAG, "Free memory idx: " + freeMemory);
        for (Long size : sizes) {
            freeMemory -= size;
        }
        Log.d(Logger.TAG, "Directory: " + pathToDir);
        Log.d(Logger.TAG, "Free memory: " + freeMemory);
        return freeMemory > 0;
    }

    @SuppressWarnings("WeakerAccess")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long freeMemory(final String path) {
        final boolean modernApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
        StatFs statFs = new StatFs(path);
        if (modernApi) {
            return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        } else {
            return (statFs.getAvailableBlocks() * statFs.getBlockSize());
        }
    }

    public static long busyMemory(final String path){
        StatFs statFs = new StatFs(path);
        long total = (statFs.getBlockCount() * statFs.getBlockSize());
        long free = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        return total - free;
    }


}
