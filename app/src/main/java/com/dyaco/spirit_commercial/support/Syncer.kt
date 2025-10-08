package com.dyaco.spirit_commercial.support

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object SyncerUtil {
    private const val TAG = "EgymUtil"

    /**
     * 執行指定的 shell 命令，並回傳執行結果的 exit code。
     *
     * @param commands     欲執行的命令陣列，每個元素代表一行命令
     * @param commandModel 呼叫的 shell，例如 "sh"
     * @return 回傳 exit code，若失敗則回傳 -1
     */
    @JvmStatic
    fun execCommand(commands: Array<String>, commandModel: String): Int {
        if (commands.isEmpty()) {
            Log.v(TAG, "請求命令錯誤")
            return -1
        }

        var process: Process? = null
        var result = -1
        try {
            // 建立 process 執行指定的 shell
            process = Runtime.getRuntime().exec(commandModel)
            // 利用 use 自動關閉 DataOutputStream
            process.outputStream.use { os ->
                val commandStr = StringBuilder().apply {
                    for (command in commands) {
                        append(command).append("\n")
                    }
                }
                Log.i(TAG, "execCommand: 請求執行的命令：$commandStr")
                os.write(commandStr.toString().toByteArray())
                os.flush()
                os.write("exit\n".toByteArray())
                os.flush()
            }
            // 等待命令執行完畢
            result = process.waitFor()
            // 讀取錯誤輸出 stream 方便除錯
            BufferedReader(InputStreamReader(process.errorStream)).use { errorReader ->
                var line: String?
                while (errorReader.readLine().also { line = it } != null) {
                    Log.e(TAG, line!!)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "命令執行失敗: ${e.message}", e)
        } catch (e: InterruptedException) {
            Log.e(TAG, "命令執行失敗: ${e.message}", e)
        } finally {
            process?.destroy()
        }
        return result
    }

    /**
     * 執行 sync 指令，強制 Linux 將快取資料寫入磁碟，
     * 以降低因斷電導致資料遺失的風險。
     * 雖然此做法能降低風險，但無法百分之百保證資料不會遺失。
     */
    @JvmStatic
    fun callSync() {
        val sync = arrayOf("sync")
        val result = execCommand(sync, "sh")
        if (result != -1) {
            Log.v(TAG, "sync 請求成功")
        } else {
            Log.e(TAG, "sync 指令執行失敗")
        }
    }
}
