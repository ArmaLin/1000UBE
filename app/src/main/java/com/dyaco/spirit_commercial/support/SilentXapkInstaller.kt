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
import org.zeroturnaround.zip.ZipUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.reflect.Method

object SilentXapkInstaller {
    private const val TAG = "UPDATE_APP"
    private val INSTALL_ACTION = SilentXapkInstaller::class.java.name + ".InstallAction"
    private const val WAIT_TIMEOUT_MS = 5000L
    private const val CHECK_INTERVAL_MS = 500L

    interface OnInstallerListener {
        fun onSuccess()
        fun onError(error: String)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @JvmStatic
    fun install(context: Context, xapkPath: String, listener: OnInstallerListener) {
        Thread {
            try {
                val xapkFile = File(xapkPath)
                if (!xapkFile.exists()) {
                    runOnUiThread { listener.onError("xAPK file not found: $xapkPath") }
                    return@Thread
                }

                // 建立解壓目錄：使用 xAPK 檔案名稱（不含副檔名）作為目錄名稱
                val unzipOutputDir = File(xapkFile.parent, getFileNameNoExtension(xapkFile))
                if (!createOrExistsDir(unzipOutputDir)) {
                    runOnUiThread { listener.onError("Failed to create unzip directory") }
                    return@Thread
                }

                // 解壓 xAPK，僅提取副檔名為 .apk 的檔案
                try {
                    ZipUtil.unpack(xapkFile, unzipOutputDir) { name ->
                        if (name.endsWith(".apk", ignoreCase = true)) name else null
                    }
                } catch (e: Exception) {
                    runOnUiThread { listener.onError("Unzip failed: ${e.message}") }
                    return@Thread
                }

                // 搜尋解壓目錄內的 APK 檔案
                val apkFiles = unzipOutputDir.listFiles { file ->
                    file.isFile && file.name.endsWith(".apk", ignoreCase = true)
                }
                if (apkFiles.isNullOrEmpty()) {
                    runOnUiThread { listener.onError("No APK files found after unzip") }
                    return@Thread
                }
                Log.d(TAG, "Found ${apkFiles.size} APK files")

                // 選取檔案大小最大的 APK 作為主要 APK
                val mainApk = apkFiles.maxByOrNull { it.length() } ?: apkFiles[0]
                Log.d(TAG, "Selected main APK: ${mainApk.name}")

                // 從主要 APK 取得預期的 package name
                val expectedPackageName = getPackageNameFromApk(context, mainApk)
                Log.d(TAG, "Extracted package name: $expectedPackageName")
                // 如果無法取得 package name，直接假定安裝成功
                if (expectedPackageName.isNullOrEmpty()) {
                    Log.w(TAG, "Cannot extract package name; assuming installation is successful")
                    runOnUiThread { listener.onSuccess() }
                    return@Thread
                }

                // 建立 PackageInstaller Session
                val packageInstaller = context.packageManager.packageInstaller
                val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                try {
                    val method: Method =
                        PackageInstaller.SessionParams::class.java.getMethod("setAllowDowngrade", Boolean::class.javaPrimitiveType)
                    method.isAccessible = true
                    method.invoke(params, true)
                    Log.d(TAG, "Allow downgrade enabled successfully")
                } catch (e: Exception) {
                    Log.d(TAG, "setAllowDowngrade not supported: ${e.message}")
                }
                params.setAppPackageName(expectedPackageName)

                val sessionId = packageInstaller.createSession(params)
                val session = packageInstaller.openSession(sessionId)
                for (apk in apkFiles) {
                    addApkToSession(apk, session)
                }

                // 註冊廣播接收器以取得安裝結果
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        Log.d(TAG, "Installation broadcast received: $intent")
                        val extras = intent.extras
                        val status = extras?.getInt(PackageInstaller.EXTRA_STATUS) ?: -1
                        val message = extras?.getString(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "No message"
                        Log.d(TAG, "Installation status: $status, message: $message")
                        context.unregisterReceiver(this)
                        // 當狀態 SUCCESS 或 -1 時，等待 PackageManager 更新資訊後再回呼 onSuccess
                        if (status == PackageInstaller.STATUS_SUCCESS || status == -1) {
                            waitForPackage(context, expectedPackageName) { success ->
                                if (success) {
                                    runOnUiThread { listener.onSuccess() }
                                } else {
                                    runOnUiThread { listener.onError("Installation completed but package not detected: status=$status, message=$message") }
                                }
                            }
                        } else {
                            runOnUiThread { listener.onError("Installation failed: status=$status, message=$message") }
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
                Log.d(TAG, "Submitting installation session (commit)")
                session.commit(pendingIntent.intentSender)
                session.close()
            } catch (e: Exception) {
                runOnUiThread { listener.onError("Exception occurred: ${e.message}") }
            }
        }.start()
    }

    @Throws(IOException::class)
    private fun addApkToSession(apkFile: File, session: PackageInstaller.Session) {
        session.openWrite(apkFile.name, 0, apkFile.length()).use { outStream ->
            BufferedInputStream(FileInputStream(apkFile)).use { inStream ->
                val buffer = ByteArray(4096)
                var count: Int
                while (inStream.read(buffer).also { count = it } != -1) {
                    outStream.write(buffer, 0, count)
                }
            }
            session.fsync(outStream)
        }
        Log.d(TAG, "APK written and synced: ${apkFile.name}")
    }

    /**
     * 等待 PackageManager 更新資訊，檢查指定 packageName 是否已成功安裝，
     * 最多等待 WAIT_TIMEOUT_MS 毫秒，每 CHECK_INTERVAL_MS 毫秒檢查一次。
     */
    private fun waitForPackage(
        context: Context,
        packageName: String,
        onDone: (Boolean) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "Waiting for PackageManager update, target package: $packageName")
        fun check() {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                Log.d(TAG, "PackageManager updated: $packageName installed")
                onDone(true)
            } catch (e: PackageManager.NameNotFoundException) {
                if (System.currentTimeMillis() - startTime >= WAIT_TIMEOUT_MS) {
                    Log.d(TAG, "Timeout waiting for package: $packageName")
                    onDone(false)
                } else {
                    Log.d(TAG, "Waiting... $packageName not yet updated")
                    handler.postDelayed({ check() }, CHECK_INTERVAL_MS)
                }
            }
        }
        check()
    }

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

    private fun createOrExistsDir(file: File): Boolean =
        if (file.exists()) file.isDirectory else file.mkdirs()

    private fun getFileNameNoExtension(file: File): String {
        val name = file.name
        val lastDot = name.lastIndexOf('.')
        return if (lastDot == -1) name else name.substring(0, lastDot)
    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post { action() }
    }
}
