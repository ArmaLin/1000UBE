// ProcessUtils.kt
package com.dyaco.spirit_commercial.support

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process

object ProcessUtils {
    /** 主进程判断，Java 里可以直接调用 ProcessUtils.isMainProcess(context) */
    @JvmStatic
    fun isMainProcess(context: Context): Boolean {
        val pkg = context.packageName
        val proc = getProcessName(context) ?: return false
        return pkg == proc
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getProcessName(context: Context): String? {
        // Android P 及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        val pid = Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return null
        for (info in am.runningAppProcesses) {
            if (info.pid == pid) {
                return info.processName
            }
        }
        return null
    }
}
