@file:JvmName("CommandExecutor")

package com.dyaco.spirit_commercial.support

import timber.log.Timber
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

private const val TAG = "CommandExecutor"

/**
 * 代表一次命令執行的結果。
 * 使用 data class 可以自動獲得 equals(), hashCode(), toString() 等實用方法。
 * @param exitCode 結束碼，通常 0 代表成功。
 * @param output 標準輸出 (stdout)。
 * @param error 錯誤輸出 (stderr)。
 */
data class CommandResult(val exitCode: Int, val output: String, val error: String) {
    /**
     * 檢查指令是否成功執行。
     */
    fun isSuccessful(): Boolean = exitCode == 0
}

/**
 * ROOT
 * 以 root 權限異步執行一或多個 Shell 命令。
 *
 * @param commands 要執行的 Shell 命令序列 (vararg)。
 * @return 一個 CommandResult 物件，包含了執行的詳細結果。
 */
fun executeAsRoot(vararg commands: String): CommandResult {
    if (commands.isEmpty()) {
        Timber.tag(TAG).w("No commands to execute.")
        return CommandResult(-1, "", "No commands provided.")
    }

    val commandString = commands.joinToString("\n") + "\n"
    Timber.tag(TAG).d("Executing commands:\n$commandString")

    var process: Process? = null
    return try {
        process = Runtime.getRuntime().exec("su")

        DataOutputStream(process.outputStream).use { os ->
            os.write(commandString.toByteArray(StandardCharsets.UTF_8))
            os.writeBytes("exit\n")
            os.flush()
        }

        // 增加超時等待
        val processExited = process.waitFor(15, TimeUnit.SECONDS)

        if (!processExited) {
            Timber.tag(TAG).e("Command execution timed out.")
            return CommandResult(-1, "", "Execution timed out.")
        }

        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()
        val exitCode = process.exitValue()

        Timber.tag(TAG).d("Execution finished with exit code: $exitCode")
        if (error.isNotEmpty()) {
            Timber.tag(TAG).w("Error stream output:\n$error")
        }

        CommandResult(exitCode, output, error)

    } catch (e: Exception) {
        Timber.tag(TAG).e(e, "An error occurred while executing root commands.")
        CommandResult(-1, "", e.message ?: "Unknown error")
    } finally {
        process?.destroy()
    }
}