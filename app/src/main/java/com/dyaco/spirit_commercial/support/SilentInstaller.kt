package com.dyaco.spirit_commercial.support

import android.content.Context
import android.util.Log

/**
 * 靜默安裝工具（依靠 root 權限，直接使用 pm install 指令）
 */
object SilentInstaller {

    /**
     * 以 root 權限透過 pm install 進行靜默安裝
     *
     * @param context 雖然此處不會用到，但為了保持調用一致性保留
     * @param apkPath APK 檔案完整路徑
     * @param installCallback 安裝成功或失敗後的回調
     */
    @JvmStatic
    fun install(context: Context, apkPath: String, installCallback: InstallCallback) {
        Thread {
            try {
                // 組成 pm 指令，-r 表示替換現有安裝
                // 移除了 --allow-downgrade，因為你的設備不支援此參數
                val command = "pm install -r -d \"$apkPath\""
                // 使用 ProcessBuilder 呼叫 su 指令以獲得 root 權限執行 pm install
                val processBuilder = ProcessBuilder("su", "-c", command)
                // 將錯誤輸出合併到標準輸出，方便讀取全部訊息
                processBuilder.redirectErrorStream(true)
                val process = processBuilder.start()
                val output = process.inputStream.bufferedReader().use { it.readText() }
                val exitCode = process.waitFor()
                Log.d("SilentInstaller", "output: $output, exitCode: $exitCode")
                // 根據 exitCode 與輸出中是否包含 Success 來判斷安裝結果
                if (exitCode == 0 && output.contains("Success", ignoreCase = true)) {
                    installCallback.onSuccess()
                } else {
                    installCallback.onFail("Install failed: exitCode=$exitCode, output: $output")
                }
            } catch (e: Exception) {
                installCallback.onFail(e.localizedMessage ?: "unknown error")
            }
        }.start()
    }
}

/**
 * 安裝回調介面，Java 與 Kotlin 均可調用
 */
interface InstallCallback {
    fun onSuccess()
    fun onFail(reason: String)
}
