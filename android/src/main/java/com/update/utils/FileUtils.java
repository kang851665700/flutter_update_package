package com.update.utils;

import android.os.Environment;

public class FileUtils {

    public static String getApkName() {
        String apkUrl = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "UpdateDownloadApk.apk";
        }
        return appName;
    }
}
