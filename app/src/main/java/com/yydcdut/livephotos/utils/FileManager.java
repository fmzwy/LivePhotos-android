package com.yydcdut.livephotos.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by yuyidong on 15/9/10.
 */
public class FileManager {
    private static final String DIR_ROOT_NAME = "livePhotos";
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String DIR_DATABASE = "database";

    public static void init() {
        File rootDir = new File(getAppDir());
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        File databaseDir = new File(getDatabaseDir());
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }
    }

    public static String getDatabaseDir() {
        return getAppDir() + DIR_DATABASE + File.separator;
    }

    public static boolean isSDCardMounted() {
        return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
    }

    public static String getAppDir() {
        return ROOT_PATH + File.separator + DIR_ROOT_NAME + File.separator;
    }
}
