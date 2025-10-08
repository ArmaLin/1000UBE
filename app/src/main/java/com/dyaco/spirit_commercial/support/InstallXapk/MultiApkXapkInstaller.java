package com.dyaco.spirit_commercial.support.InstallXapk;

import static com.dyaco.spirithome2.support.InstallXapk.FileUtilsKt.getFileName;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MultiApkXapkInstaller extends XapkInstaller{
    PackageInstaller.Session mSession;
    public MultiApkXapkInstaller(Context context,String xapkPath,File xapkUnzipOutputDir){
        super(context,xapkPath,xapkUnzipOutputDir);

    }
    @Override
    public String getUnzipPath() {
        return xapkUnzipOutputDir.getAbsolutePath();
    }

    @Override
    public void onReceiver(Bundle extras) {
        int status = -100;
        String message = "";
        if (extras != null) {
            status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
        }
        switch (status) {
            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                // 此測試應用程式沒有特權，因此使用者必須確認安裝。
//                Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
//                startActivity(confirmIntent);
//
                setErr("安装失败,無安裝權限");
                break;
            case PackageInstaller.STATUS_SUCCESS:
                if (onXapkInstallerListener!=null){
                    onXapkInstallerListener.onInstaller();
                }
                break;
            case PackageInstaller.STATUS_FAILURE:
            case PackageInstaller.STATUS_FAILURE_ABORTED:
            case PackageInstaller.STATUS_FAILURE_BLOCKED:
            case PackageInstaller.STATUS_FAILURE_CONFLICT:
            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
            case PackageInstaller.STATUS_FAILURE_INVALID:
            case PackageInstaller.STATUS_FAILURE_STORAGE:
                setErr("安装失败,请重试"+status);
                break;
            default:
                setErr("安装失败,解压文件可能已丢失或损坏，请重试");
        }
    }

    @Override
    public void install() {
        File[] files = xapkUnzipOutputDir.listFiles();
        ArrayList<String> apkFilePaths=new ArrayList<>();
        for (File file:files){
            if (file.isFile()&&file.getName().endsWith(".apk")){
                apkFilePaths.add(file.getAbsolutePath());
            }
        }
        enterInstall(xapkPath, apkFilePaths);
    }

    private void enterInstall(String xapkPath, ArrayList<String> apkPaths) {
        if (Build.VERSION.SDK_INT < 21) {
            setErr("暂时不支持安装,请更新到Android 5.0及以上版本");
            return;
        }
        if (apkPaths == null || apkPaths.isEmpty()) {
            setErr("解析apk出错或已取消");
            return;
        }

        if (RomUtils.isMeizu() || RomUtils.isVivo()) {
            setErr("魅族或VIVO系统用户如遇安装被中止或者安装失败的情况，请尝试联系手机平台客服，或者更换系统内置包安装器再重试");
            return;
        }
        setLog("即將安裝");
        try {
            mSession = initSession();
            for (String apkPath : apkPaths) {
                addApkToInstallSession(apkPath, mSession);
            }
            commitSession(mSession);
        } catch (IOException e) {
            e.printStackTrace();
            abandonSession();
        }
    }

    private void abandonSession() {
        if (mSession != null) {
            mSession.abandon();
            mSession.close();
        }
        setErr("abandonSession");
    }

    private void commitSession(PackageInstaller.Session session) {

        session.commit(PendingIntent.getBroadcast(
                context,
                0,
                new Intent(Intent.ACTION_VIEW), PendingIntent.FLAG_IMMUTABLE).getIntentSender());
        // Create an install status receiver.
//        Intent intent = new Intent();
//        intent.setAction(PACKAGE_INSTALLED_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,new Intent(Intent.ACTION_VIEW), 0);
//        IntentSender statusReceiver = pendingIntent.getIntentSender();
//        // Commit the session (this will start the installation workflow).
//        session.commit(statusReceiver);
    }

    private void addApkToInstallSession(String filePath, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        setLog("addApkToInstallSession");
        try (OutputStream packageInSession = session.openWrite(getFileName(filePath), 0, new File(filePath).length());
             InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
            setLog("安裝各個APK");
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }

    private PackageInstaller.Session initSession() throws IOException {
        PackageInstaller.Session session;
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        try {
            Method allowDowngrade = PackageInstaller.SessionParams.class.getMethod("setAllowDowngrade", boolean.class);
            allowDowngrade.setAccessible(true);
            allowDowngrade.invoke(params, true);
            setLog("降版安裝正常"+allowDowngrade.getName());
        } catch (Exception e) {
            e.printStackTrace();
            setErr("降版安裝異常"+e);
        }
        int sessionId;
        sessionId = packageInstaller.createSession(params);
        session = packageInstaller.openSession(sessionId);

        return session;
    }


}
