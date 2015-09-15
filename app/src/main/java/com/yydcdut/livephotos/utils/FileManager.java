package com.yydcdut.livephotos.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yuyidong on 15/9/10.
 */
public class FileManager {
    private static final String DIR_ROOT_NAME = "LivePhotos";
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

    public static String[] getNames(String dirName) {
        if (TextUtils.isEmpty(dirName)) {
            return null;
        }
        File dir = new File(dirName);
        String[] fileArray = dir.list();
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(fileArray));
        //移除blur.jpg
        arrayList.remove("blur.jpg");
        final int size = arrayList.size();
        return arrayList.toArray(new String[size]);
    }
}
