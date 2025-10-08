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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.zeroturnaround.zip.ZipUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale

/**
 * SilentAllInstaller 用於靜默安裝 xAPK、APK 與 APKM，
 * 根據傳入的 localPath（例如：
 * /storage/emulated/0/Android/data/com.dyaco.spirit_commercial/cache/Spirit_xxx.xapk）
 * 若副檔名為 xapk 則執行 xAPK 的安裝流程，
 * 若副檔名為 apk 則執行 APK 的安裝流程，
 * 若副檔名為 apkm 則執行 APKM 的安裝流程。
 *
 * 廣播接收安裝結果，App安裝完成後並不能馬上取得PackageName，
 * 透過 waitForPackage 方法重複確認取得PackageName資訊後回傳 onSuccess ，逾時則回 onError。
 *
 * 此實作需系統權限（如 INSTALL_PACKAGES）。
 *
 * 可降版安裝
 */
object SilentAppInstaller {
    private const val TAG = "UPDATE_APP"
    // INSTALL_ACTION 與硬碼參數
    private val INSTALL_ACTION = SilentAppInstaller::class.java.name + ".InstallAction"
    private const val WAIT_TIMEOUT_MS = 5000L
    private const val CHECK_INTERVAL_MS = 500L

    // 支援的副檔名參數
    private const val EXT_XAPK = "xapk"
    private const val EXT_APK = "apk"
    private const val EXT_APKM = "apkm"

    /**
     * 安裝結果回呼介面
     */
    interface OnInstallerListener {
        fun onSuccess()
        fun onError(error: String)
    }

    /**
     * 根據傳入 localPath 的副檔名，決定走 xAPK、APK 或 APKM 的安裝流程。
     *
     * @param context 上下文
     * @param localPath xAPK、APK 或 APKM 的完整路徑
     * @param listener 回呼介面
     */
    @JvmStatic
    fun install(context: Context, localPath: String, listener: OnInstallerListener) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(localPath)
                if (!file.exists()) {
                    runOnUiThread { listener.onError("File not found: $localPath") }
                    return@launch
                }
                when (val ext = getFileExtension(file.name).lowercase(Locale.getDefault())) {
                    EXT_XAPK -> installXapk(context, file, listener)
                    EXT_APK  -> installApk(context, file, listener)
                    EXT_APKM -> installApkm(context, file, listener)
                    else -> runOnUiThread { listener.onError("Unsupported file format: $ext") }
                }
            } catch (e: Exception) {
                runOnUiThread { listener.onError("Exception occurred: ${e.message}") }
            }
        }
    }

    /**
     * 安裝 xAPK 流程：
     * 1. 檢查 xAPK 檔案是否存在
     * 2. 以 xAPK 檔名（不含副檔名）建立解壓目錄
     * 3. 解壓 xAPK，僅提取副檔名為 .apk 的檔案
     * 4. 從解壓目錄內尋找所有 APK，選取檔案大小最大的 APK 當作主要 APK，
     *    並從該 APK 取得預期的 package name
     * 5. 進行 PackageInstaller Session 安裝
     */
    private fun installXapk(context: Context, xapkFile: File, listener: OnInstallerListener) {
        // 建立解壓目錄：以 xAPK 檔案名稱（不含副檔名）作為目錄名稱
        val unzipOutputDir = File(xapkFile.parent, getFileNameNoExtension(xapkFile))
        if (!createOrExistsDir(unzipOutputDir)) {
            runOnUiThread { listener.onError("Failed to create unzip directory") }
            return
        }

        // 解壓 xAPK，僅提取副檔名為 .apk 的檔案
        try {
            ZipUtil.unpack(xapkFile, unzipOutputDir) { name ->
                if (name.endsWith(".apk", ignoreCase = true)) name else null
            }
        } catch (e: Exception) {
            runOnUiThread { listener.onError("Unzip failed: ${e.message}") }
            return
        }

        // 搜尋解壓目錄內的 APK 檔案
        val apkFiles = unzipOutputDir.listFiles { file ->
            file.isFile && file.name.endsWith(".apk", ignoreCase = true)
        }
        if (apkFiles.isNullOrEmpty()) {
            runOnUiThread { listener.onError("No APK files found after unzip") }
            return
        }
        Log.d(TAG, "找到 ${apkFiles.size} 個 APK 檔案")
        // 選取檔案大小最大的 APK 作為主要 APK
        val mainApk = apkFiles.maxByOrNull { it.length() } ?: apkFiles[0]
        Log.d(TAG, "選取主要 APK: ${mainApk.name}")

        // 從主要 APK 取得預期的 package name
        val expectedPackageName = getPackageNameFromApk(context, mainApk)
        Log.d(TAG, "擷取的 package name: $expectedPackageName")
        if (expectedPackageName.isNullOrEmpty()) {
            Log.w(TAG, "無法擷取 package name，假定安裝成功")
            runOnUiThread { listener.onSuccess() }
            return
        }
        performInstallation(context, apkFiles.toList(), expectedPackageName, listener)
    }

    /**
     * 安裝 APK 流程：
     * 1. 檢查 APK 是否存在
     * 2. 從 APK 中取得預期的 package name
     * 3. 進行 PackageInstaller Session 安裝
     */
    private fun installApk(context: Context, apkFile: File, listener: OnInstallerListener) {
        val expectedPackageName = getPackageNameFromApk(context, apkFile)
        if (expectedPackageName.isNullOrEmpty()) {
            runOnUiThread { listener.onError("Failed to extract package name from APK") }
            return
        }
        Log.d(TAG, "擷取的 package name: $expectedPackageName")
        performInstallation(context, listOf(apkFile), expectedPackageName, listener)
    }

    /**
     * 安裝 APKM 流程：
     * 1. 假設 APKM 為壓縮檔，建立解壓目錄（以 APKM 檔案名稱不含副檔名）
     * 2. 解壓 APKM，僅提取副檔名為 .apk 的檔案
     * 3. 從解壓目錄內尋找所有 APK，選取檔案大小最大的 APK 當作主要 APK，
     *    並從該 APK 取得預期的 package name
     * 4. 進行 PackageInstaller Session 安裝
     */
    private fun installApkm(context: Context, apkmFile: File, listener: OnInstallerListener) {
        // 建立解壓目錄：以 APKM 檔案名稱（不含副檔名）作為目錄名稱
        val unzipOutputDir = File(apkmFile.parent, getFileNameNoExtension(apkmFile))
        if (!createOrExistsDir(unzipOutputDir)) {
            runOnUiThread { listener.onError("Failed to create unzip directory for APKM") }
            return
        }

        // 解壓 APKM，僅提取副檔名為 .apk 的檔案
        try {
            ZipUtil.unpack(apkmFile, unzipOutputDir) { name ->
                if (name.endsWith(".apk", ignoreCase = true)) name else null
            }
        } catch (e: Exception) {
            runOnUiThread { listener.onError("Unzip failed for APKM: ${e.message}") }
            return
        }

        // 搜尋解壓目錄內的 APK 檔案
        val apkFiles = unzipOutputDir.listFiles { file ->
            file.isFile && file.name.endsWith(".apk", ignoreCase = true)
        }
        if (apkFiles.isNullOrEmpty()) {
            runOnUiThread { listener.onError("No APK files found in APKM after unzip") }
            return
        }
        Log.d(TAG, "在 APKM 中找到 ${apkFiles.size} 個 APK 檔案")
        // 選取檔案大小最大的 APK 作為主要 APK
        val mainApk = apkFiles.maxByOrNull { it.length() } ?: apkFiles[0]
        Log.d(TAG, "在 APKM 中選取主要 APK: ${mainApk.name}")

        // 從主要 APK 取得預期的 package name
        val expectedPackageName = getPackageNameFromApk(context, mainApk)
        Log.d(TAG, "從 APKM 擷取的 package name: $expectedPackageName")
        if (expectedPackageName.isNullOrEmpty()) {
            runOnUiThread { listener.onError("Failed to extract package name from APKM") }
            return
        }
        performInstallation(context, apkFiles.toList(), expectedPackageName, listener)
    }

    /**
     * 進行 PackageInstaller Session 安裝：
     * 1. 建立 Session 並設定允許降版（若支援）
     * 2. 將所有 APK 檔寫入 Session
     * 3. 註冊廣播接收器，等待安裝結果並用 waitForPackage 檢查更新
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun performInstallation(
        context: Context,
        apkFiles: List<File>,
        expectedPackageName: String,
        listener: OnInstallerListener
    ) {
        try {
            val packageInstaller = context.packageManager.packageInstaller
            // 利用 apply 簡化 SessionParams 的設定
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
                try {
                    val method = PackageInstaller.SessionParams::class.java.getMethod(
                        "setAllowDowngrade", Boolean::class.javaPrimitiveType
                    )
                    method.isAccessible = true
                    method.invoke(this, true)
                    Log.d(TAG, "允許降版已啟用")
                } catch (e: Exception) {
                    Log.d(TAG, "setAllowDowngrade 不支援: ${e.message}")
                }
                setAppPackageName(expectedPackageName)
            }

            val sessionId = packageInstaller.createSession(params)
            val session = packageInstaller.openSession(sessionId)
            // 將每個 APK 檔案加入 Session
            for (apk in apkFiles) {
                addApkToSession(apk, session)
            }

            // 註冊廣播接收器以取得安裝結果
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Log.d(TAG, "收到安裝廣播: $intent")
                    val extras = intent.extras
                    val status = extras?.getInt(PackageInstaller.EXTRA_STATUS) ?: -1
                    val message = extras?.getString(PackageInstaller.EXTRA_STATUS_MESSAGE) ?: "No message"
                    Log.d(TAG, "安裝狀態: $status, 訊息: $message")
                    context.unregisterReceiver(this)
                    // 剛安裝完，系統可能尚未立即更新 package 資訊
                    if (status == PackageInstaller.STATUS_SUCCESS || status == -1) {
                        waitForPackage(context, expectedPackageName) { success ->
                            if (success) {
                                runOnUiThread { listener.onSuccess() }
                            } else {
                                runOnUiThread {
                                    listener.onError("Installation completed but confirmation failed: status=$status, message=$message")
                                }
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
            Log.d(TAG, "提交安裝 Session (commit)")
            session.commit(pendingIntent.intentSender)
            session.close()
        } catch (e: Exception) {
            runOnUiThread { listener.onError("Installation exception: ${e.message}") }
        }
    }

    /**
     * 將 APK 檔案寫入 PackageInstaller Session 並同步
     */
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
        Log.d(TAG, "APK 寫入並同步完成: ${apkFile.name}")
    }

    /**
     * 等待 PackageManager 更新資訊，檢查指定 package 是否安裝成功，
     * 最多等待 WAIT_TIMEOUT_MS 毫秒，每 CHECK_INTERVAL_MS 毫秒檢查一次。
     */
    private fun waitForPackage(
        context: Context,
        packageName: String,
        onDone: (Boolean) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "等待 PackageManager 更新，目標 package: $packageName")
        fun check() {
            try {
                context.packageManager.getPackageInfo(packageName, 0)
                Log.d(TAG, "確認 package 已安裝: $packageName")
                onDone(true)
            } catch (e: PackageManager.NameNotFoundException) {
                if (System.currentTimeMillis() - startTime >= WAIT_TIMEOUT_MS) {
                    Log.d(TAG, "等待逾時：$packageName 未安裝")
                    onDone(false)
                } else {
                    Log.d(TAG, "等待中... $packageName")
                    handler.postDelayed({ check() }, CHECK_INTERVAL_MS)
                }
            }
        }
        check()
    }

    /**
     * 取得檔案副檔名。
     */
    private fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex != -1 && dotIndex < fileName.length - 1) {
            fileName.substring(dotIndex + 1)
        } else {
            ""
        }
    }

    /**
     * 取得檔名（不包含副檔名）。
     */
    private fun getFileNameNoExtension(file: File): String {
        val name = file.name
        val lastDot = name.lastIndexOf('.')
        return if (lastDot == -1) name else name.substring(0, lastDot)
    }

    /**
     * 若目錄存在則回傳 true，否則嘗試建立目錄
     */
    private fun createOrExistsDir(file: File): Boolean =
        if (file.exists()) file.isDirectory else file.mkdirs()

    /**
     * 從 APK 檔案中取得 package name。
     */
    private fun getPackageNameFromApk(context: Context, apkFile: File): String? {
        return try {
            val info = context.packageManager.getPackageArchiveInfo(apkFile.path, PackageManager.GET_ACTIVITIES)
            info?.applicationInfo?.let { appInfo ->
                appInfo.sourceDir = apkFile.absolutePath
                appInfo.publicSourceDir = apkFile.absolutePath
            }
            val pkg = info?.packageName
            Log.d(TAG, "從 APK 擷取的 package name: $pkg")
            pkg
        } catch (e: Exception) {
            Log.e(TAG, "擷取 package name 失敗: ${e.message}")
            null
        }
    }

    /**
     * 將指定動作切換至 UI 執行緒執行。
     */
    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post { action() }
    }
}
