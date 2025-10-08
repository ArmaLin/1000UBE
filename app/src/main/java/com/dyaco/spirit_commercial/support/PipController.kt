package com.dyaco.spirit_commercial.support

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader

object PipController {
    // AppOpsService 中 PICTURE_IN_PICTURE 的整數 & 字串常數
    private const val OP_PIP_INT = 67
    private const val OP_PIP_STR = AppOpsManager.OPSTR_PICTURE_IN_PICTURE

    /** 檢查目標 App 是否允許 PiP */
    @SuppressLint("ObsoleteSdkInt")
    @JvmStatic
    fun isPipAllowed(context: Context, pkg: String): Boolean {
        val uid = try {
            context.packageManager.getApplicationInfo(pkg, 0).uid
        } catch (_: PackageManager.NameNotFoundException) {
            return false
        }
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mode = appOps.checkOpNoThrow(OP_PIP_STR, uid, pkg)
            mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_DEFAULT
        } else {
            val mode = appOps.checkOpNoThrow(OP_PIP_INT.toString(), uid, pkg)
            mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_DEFAULT
        }
    }

    /**
     * 設定目標 App 的 PiP 權限
     * 1) 先試反射隱藏 API（Android P+ 多半不會生效，但不報錯）
     * 2) 再用最可靠的 shell 指令：cmd appops set … PICTURE_IN_PICTURE allow|ignore
     *
     * @param pkg   目標 App package name
     * @param allow true=允許；false=禁止
     * @return      true=指令執行回傳 0 (至少下令成功)；false=執行失敗或例外
     */
    @JvmStatic
    fun setPipPermission(context: Context, pkg: String, allow: Boolean): Boolean {
        val uid = try {
            context.packageManager.getApplicationInfo(pkg, 0).uid
        } catch (_: PackageManager.NameNotFoundException) {
            return false
        }

        // 1️⃣ 反射呼叫 setUidMode(...)
        runCatching {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val method = AppOpsManager::class.java.getDeclaredMethod(
                "setUidMode",
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            method.isAccessible = true
            method.invoke(appOps, OP_PIP_STR, uid, if (allow) AppOpsManager.MODE_ALLOWED else AppOpsManager.MODE_IGNORED)
        }

        // 2️⃣ Shell 指令路徑：cmd appops set --user 0 <pkg> PICTURE_IN_PICTURE allow|ignore
        val action = if (allow) "allow" else "ignore"
        val cmd = arrayOf("cmd", "appops", "set", "--user", "0", pkg, "PICTURE_IN_PICTURE", action)

        return runCatching {
            val proc = Runtime.getRuntime().exec(cmd)
            val exitCode = proc.waitFor()
            if (exitCode != 0) {
                BufferedReader(InputStreamReader(proc.errorStream)).use { it.forEachLine { /* 忽略錯誤輸出 */ } }
            }
            exitCode == 0
        }.getOrDefault(false)
    }
}
