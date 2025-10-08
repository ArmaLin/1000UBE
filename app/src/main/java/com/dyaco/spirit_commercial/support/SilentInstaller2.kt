package com.dyaco.spirit_commercial.support

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Method

/**
 * SilentInstaller2 – 利用 PackageInstaller Session 進行靜默安裝 APK，
 * 適用於 system app（非 root）環境，並透過 INSTALL_ACTION 廣播接收安裝結果。
 *
 * 注意：此實作需要系統應用權限（如 INSTALL_PACKAGES）。
 */
object SilentInstaller2 {

    private const val TAG = "UPDATE_APP"
    private const val WAIT_TIMEOUT_MS = 5000L
    private const val CHECK_INTERVAL_MS = 500L
    private const val INSTALL_ACTION = "com.dyaco.spirit_commercial.InstallAction"

    /**
     * 安裝結果回呼介面
     */
    interface InstallCallback {
        fun onSuccess()
        fun onFail(reason: String)
    }

    /**
     * 進行 APK 的靜默安裝。
     *
     * 流程：
     *  1. 檢查 APK 檔案是否存在並解析預期 package name
     *  2. 建立 PackageInstaller Session 並設定允許降版（若支援），設定 appPackageName
     *  3. 將 APK 寫入 Session，commit 安裝
     *  4. 註冊廣播接收器取得安裝結果，commit 完成後利用 waitForPackage 檢查是否安裝成功，
     *     狀態為 SUCCESS 或 -1 時直接回傳成功（不再額外查 package）。
     *
     * @param context  上下文
     * @param apkPath  APK 檔案完整路徑
     * @param callback 安裝結果回呼
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @JvmStatic
    fun install(context: Context, apkPath: String, callback: InstallCallback) {
        Thread {
            try {
                val apkFile = File(apkPath)
                if (!apkFile.exists()) {
                    runOnUiThread { callback.onFail("APK 檔案不存在: $apkPath") }
                    return@Thread
                }

                val expectedPackageName = getPackageNameFromApk(context, apkFile)
                if (expectedPackageName.isNullOrEmpty()) {
                    runOnUiThread { callback.onFail("無法從 APK 中讀取套件名稱: $apkPath") }
                    return@Thread
                }
                Log.d(TAG, "從 APK 取得的套件名稱: $expectedPackageName")

                val packageInstaller = context.packageManager.packageInstaller
                val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                // 嘗試設定允許降版安裝（若系統支援）
                try {
                    val method: Method = PackageInstaller.SessionParams::class.java.getMethod(
                        "setAllowDowngrade", Boolean::class.javaPrimitiveType
                    )
                    method.isAccessible = true
                    method.invoke(params, true)
                    Log.d(TAG, "允許降版安裝已啟用")
                } catch (e: Exception) {
                    Log.d(TAG, "setAllowDowngrade 不支援: ${e.message}")
                }
                // 設定預期的套件名稱
                params.setAppPackageName(expectedPackageName)

                val sessionId = packageInstaller.createSession(params)
                val session = packageInstaller.openSession(sessionId)
                addApkToSession(apkFile, session)

                // 註冊廣播接收器以接收安裝結果
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        Log.d(TAG, "收到安裝完成廣播: $intent")
                        val extras = intent.extras
                        val status = extras?.getInt(PackageInstaller.EXTRA_STATUS) ?: -1
                        val message = extras?.getString(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "沒有訊息"
                        Log.d(TAG, "安裝狀態: $status, 訊息: $message")
                        context.unregisterReceiver(this)

                        // 狀態 SUCCESS 或 -1（不檢查 package 資訊）時直接認定安裝成功
                        if (status == PackageInstaller.STATUS_SUCCESS || status == -1) {
                            waitForPackage(context, expectedPackageName) { success ->
                                if (success) {
                                    runOnUiThread { callback.onSuccess() }
                                } else {
                                    runOnUiThread { callback.onFail("安裝完成但未確認成功: status=$status, message=$message") }
                                }
                            }
                        } else {
                            runOnUiThread { callback.onFail("安裝失敗: status=$status, message=$message") }
                        }
                    }
                }
                val filter = IntentFilter(INSTALL_ACTION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
                } else {
                    context.registerReceiver(receiver, filter)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    sessionId,
                    Intent(INSTALL_ACTION),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                Log.d(TAG, "提交安裝任務 (commit)")
                session.commit(pendingIntent.intentSender)
                session.close()
            } catch (e: Exception) {
                Log.e(TAG, "例外發生: ${e.message}")
                runOnUiThread { callback.onFail("例外發生: ${e.message}") }
            }
        }.start()
    }

    @Throws(IOException::class)
    private fun addApkToSession(apkFile: File, session: PackageInstaller.Session) {
        session.openWrite("base.apk", 0, apkFile.length()).use { outStream ->
            BufferedInputStream(FileInputStream(apkFile)).use { inStream ->
                val buffer = ByteArray(8192)
                var count: Int
                while (inStream.read(buffer).also { count = it } != -1) {
                    outStream.write(buffer, 0, count)
                }
            }
            session.fsync(outStream)
        }
        Log.d(TAG, "APK 寫入並同步完成")
    }

    /**
     * 等待 PackageManager 更新資訊，檢查指定 package 是否安裝成功。
     */
    private fun waitForPackage(
        context: Context,
        packageName: String,
        onDone: (Boolean) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "啟動等待 package: $packageName")

        fun check() {
            if (isPackageInstalled(context, packageName)) {
                Log.d(TAG, "確認安裝成功: $packageName")
                onDone(true)
            } else if (System.currentTimeMillis() - startTime >= WAIT_TIMEOUT_MS) {
                Log.d(TAG, "等待超時: $packageName 未安裝")
                onDone(false)
            } else {
                Log.d(TAG, "等待中... $packageName")
                handler.postDelayed({ check() }, CHECK_INTERVAL_MS)
            }
        }
        check()
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 從 APK 檔中取得 package name。
     */
    private fun getPackageNameFromApk(context: Context, apkFile: File): String? {
        return try {
            val info = context.packageManager.getPackageArchiveInfo(apkFile.path, PackageManager.GET_ACTIVITIES)
            info?.applicationInfo?.let { appInfo ->
                appInfo.sourceDir = apkFile.absolutePath
                appInfo.publicSourceDir = apkFile.absolutePath
            }
            val pkg = info?.packageName
            pkg
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 將 action 切換至 UI 執行緒執行。
     */
    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post { action() }
    }
}
