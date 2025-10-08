package com.dyaco.spirit_commercial.support.InstallXapk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public abstract class XapkInstaller {
    public static String TAG="XapkInstaller";
    public static  String PACKAGE_INSTALLED_ACTION = "com.dyaco.spiritbike.InstallXapk.XapkInstaller";
    public Context context;
    public String xapkPath;
    public File xapkUnzipOutputDir;
    public OnXapkInstallerListener onXapkInstallerListener;
    public XapkInstaller(Context context, String xapkPath, File xapkUnzipOutputDir){
        this.context=context;
        this.xapkPath=xapkPath;
        this.xapkUnzipOutputDir=xapkUnzipOutputDir;
    }
    public  void setErr(String log) {
        setLog(log);
        if (onXapkInstallerListener!=null){
            onXapkInstallerListener.onErr(log);
        }
    }
    public void setLog(String log){
        Log.d(TAG,log);
    }

    public void setOnXapkInstallerListener(OnXapkInstallerListener onXapkInstallerListener) {
        this.onXapkInstallerListener = onXapkInstallerListener;
    }

    public static void setPackageInstalledAction(String packageInstalledAction) {
        PACKAGE_INSTALLED_ACTION = packageInstalledAction;
    }

    public static String getPackageInstalledAction() {
        return PACKAGE_INSTALLED_ACTION;
    }

    public abstract void install();
    public abstract String getUnzipPath();
    public abstract  void onReceiver(Bundle extras);
    public interface OnXapkInstallerListener {

        void onInstaller();

        void onErr(String err);
    }
}
