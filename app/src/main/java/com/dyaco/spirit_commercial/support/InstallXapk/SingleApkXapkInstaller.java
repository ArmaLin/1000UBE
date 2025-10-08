package com.dyaco.spirit_commercial.support.InstallXapk;

import static com.dyaco.spirithome2.support.InstallXapk.FileUtilsKt.getFileByPath;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import java.io.File;

public class SingleApkXapkInstaller extends XapkInstaller{

    public SingleApkXapkInstaller(Context context, String xapkPath, File xapkUnzipOutputDir){
        super(context,xapkPath,xapkUnzipOutputDir);
    }
    @Override
    public String getUnzipPath() {
        return xapkUnzipOutputDir.getAbsolutePath();
    }
    @Override
    public void onReceiver(Bundle extras) {

    }
    @Override
    public void install() {
        File[] files = xapkUnzipOutputDir.listFiles();
        for (File file:files){
            if ((file.isFile() && file.getName().endsWith(".apk"))) {
               String filePath = file.getAbsolutePath();
               if (XapkManager.checkStr(filePath)){
                   installApp(getFileByPath(filePath));
               }
            }
        }

    }

    private void installApp(File file) {
        try {
            if(file != null && file.exists()){
                context.startActivity(getInstallAppIntent(file, true));
            }
        } catch (Exception e) {
            e.printStackTrace();
            setErr("安裝異常:"+e.toString());
        }
    }

    private Intent getInstallAppIntent(File file, boolean isNewTask) {

        Intent intent =new  Intent(Intent.ACTION_VIEW);
        Uri data;
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            data = Uri.fromFile(file);
        } else {
//            String authority = context.getPackageName() + ".utilcode.provider";
            String authority="com.example.solehomeiis";
            data = FileProvider.getUriForFile(context,authority , file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) ;
        }
        context.grantUriPermission(
                context.getPackageName(),
                data,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );
        intent.setDataAndType(data, type);
        if (isNewTask){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }
}
