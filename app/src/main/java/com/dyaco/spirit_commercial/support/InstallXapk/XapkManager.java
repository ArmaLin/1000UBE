package com.dyaco.spirit_commercial.support.InstallXapk;


import static com.dyaco.spirithome2.support.InstallXapk.FileUtilsKt.createOrExistsDir;
import static com.dyaco.spirithome2.support.InstallXapk.FileUtilsKt.getFileNameNoExtension;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

public class XapkManager {

    public static XapkInstaller createXapkInstaller(Context context, String xapkPath) {
        if (!checkStr(xapkPath)) {
            return null;
        }
        File xapkFile = new File(xapkPath);
        String unzipOutputDirPath = createUnzipOutputDir(xapkFile);
        if (!checkStr(unzipOutputDirPath)) {
            return null;
        }
        File unzipOutputDir = new File(unzipOutputDirPath);
        try {
            ZipUtil.unpack(xapkFile, unzipOutputDir, new NameMapper() {
                @Override
                public String map(String name) {
                    if (name.endsWith(".apk")) {
                        return name;
                    } else {
                        return null;
                    }
                }
            });
        } catch (Exception e) {
            setLog("解壓縮異常" + e.toString());
        }
        File[] files = unzipOutputDir.listFiles();
        int apkSize = 0;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".apk")) {
                apkSize++;
            }
        }
        if (!unzipObbToAndroidObbDir(xapkFile, new File(getMobileAndroidObbDir()))) {
            return null;
        }
        if (apkSize > 1) {
            setLog("多筆APK");
            return new MultiApkXapkInstaller(context, xapkPath, unzipOutputDir);
        } else {
            setLog("單筆APK");
            return new SingleApkXapkInstaller(context, xapkPath, unzipOutputDir);
        }
    }

    private static String createUnzipOutputDir(File file) {
        String filePathPex = file.getParent() + File.separator;
        String unzipOutputDir = filePathPex + getFileNameNoExtension(file);
        boolean result = createOrExistsDir(unzipOutputDir);
        return result ? unzipOutputDir : null;


    }

    private static String getMobileAndroidObbDir() {
        String path;
        if (isSDCardEnableByEnvironment()) {
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "obb";
        } else {
            path = Environment.getDataDirectory().getParentFile().toString() + File.separator + "Android" + File.separator + "obb";
        }

        createOrExistsDir(path);
        return path;
    }

    private static boolean unzipObbToAndroidObbDir(File xapkFile, File unzipOutputDir) {
        String prefix = "Android/obb";
        try {
            //只保留apk文件和Android/obb下的文件,以及json文件用于获取主包（当有多个apk时）
            ZipUtil.unpack(xapkFile, unzipOutputDir, new NameMapper() {
                @Override
                public String map(String name) {
                    if (name.startsWith(prefix)) {
                        return name.substring(prefix.length());
                    } else {
                        return null;
                    }

                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isSDCardEnableByEnvironment() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }


    public static boolean checkStr(String str) {
        return !"".equals(str) && str != null;
    }

    private static void setLog(String log) {
        Log.d(XapkInstaller.TAG, log);
    }
}
