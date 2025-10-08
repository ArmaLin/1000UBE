package com.dyaco.spirit_commercial.maintenance_mode.usbx

import android.util.Log
import kotlinx.coroutines.*
import java.io.IOException

/**
 * 判斷 ExFAT or FAT32
 */
object UsbFsDetector {
    private const val TAG = "UsbFsDetector"
    private const val MOUNT_POINT      = "/mnt/usb_detect"
    private const val EXFAT_MOUNT_TOOL = "/data/local/tmp/mount.exfat-fuse"

    // 最大嘗試尋找裝置次數
    private const val MAX_FIND_ATTEMPTS = 3
    private const val FIND_RETRY_DELAY_MS = 200L

    enum class FsType { FAT32, EXFAT, UNKNOWN, NONE }
    interface Callback { fun onResult(type: FsType) }

    private val detectionScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @JvmStatic
    @JvmOverloads
    fun detectUsbFsAsync(callback: Callback, delayMs: Long = 1500L) {
        detectionScope.launch {
            Log.d(TAG, "delay ${delayMs}ms 後開始偵測 USB 檔案系統")
            delay(delayMs)
            val type = detectUsbFsSync()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "偵測結束，結果：$type")
                callback.onResult(type)
            }
        }
    }

    private fun detectUsbFsSync(): FsType {
        Log.d(TAG, "開始同步偵測 USB 檔案系統")
        val device = findUsbBlockDevice() ?: run {
            Log.d(TAG, "找不到 USB 裝置")
            return FsType.NONE
        }
        Log.d(TAG, "找到裝置: $device")

        // 1. 先用 blkid 快速判別
        val fs = probeFsByBlkid(device)
        if (fs == "vfat" || fs == "fat32") {
            Log.d(TAG, "blkid 判斷為 FAT32")
            return FsType.FAT32
        }
        if (fs == "exfat") {
            Log.d(TAG, "blkid 判斷為 exFAT")
            return FsType.EXFAT
        }
        Log.d(TAG, "blkid 無法判別 (回傳 `$fs`)，進入掛載檢測流程")

        // 2. 清理掛載點
        cleanupMountPoint()

        // 3. 嘗試 FAT32 掛載
        if (mountFs(device, useExfatTool = false)) {
            Log.d(TAG, "掛載 FAT32 成功")
            umountCleanup()
            return FsType.FAT32
        }
        Log.d(TAG, "掛載 FAT32 失敗")

        // 4. 嘗試 exFAT-FUSE 掛載
        cleanupMountPoint()
        if (mountFs(device, useExfatTool = true)) {
            Log.d(TAG, "掛載 exFAT 成功")
            umountCleanup()
            return FsType.EXFAT
        }
        Log.d(TAG, "掛載 exFAT 失敗")

        umountCleanup()
        Log.d(TAG, "所有方式都失敗，回傳 UNKNOWN")
        return FsType.UNKNOWN
    }

    /** 加入重試機制：最多嘗試 ${MAX_FIND_ATTEMPTS} 次 */
    private fun findUsbBlockDevice(): String? {
        for (attempt in 1..MAX_FIND_ATTEMPTS) {
            try {
                Log.d(TAG, "第${attempt}次嘗試執行 find /dev/block -type b")
                val proc = Runtime.getRuntime().exec(arrayOf("su", "-c", "find /dev/block -type b"))
                val lines = proc.inputStream.bufferedReader().readLines().also { proc.waitFor() }
                val dev = lines.firstOrNull { it.matches(Regex("^/dev/block/sd[a-z][0-9]*$")) }
                Log.d(TAG, "find 第${attempt}次結果 ${lines.size} 列, 選中 $dev")
                if (dev != null) return dev
            } catch (e: IOException) {
                Log.e(TAG, "第${attempt}次嘗試找裝置失敗", e)
            }
            if (attempt < MAX_FIND_ATTEMPTS) {
                Log.e(TAG, "找不到裝置，等待 ${FIND_RETRY_DELAY_MS}ms 後重試")
                Thread.sleep(FIND_RETRY_DELAY_MS)
            }
        }
        Log.e(TAG, "超過 $MAX_FIND_ATTEMPTS 次嘗試，仍找不到裝置")
        return null
    }

    /** 用 blkid -o value -s TYPE 來探測 fs type */
    private fun probeFsByBlkid(device: String): String {
        Log.d(TAG, "執行 blkid 探測 FS: blkid -o value -s TYPE $device")
        return try {
            val cmd = arrayOf("su","-c","blkid -o value -s TYPE $device")
            val p = Runtime.getRuntime().exec(cmd)
            val out = p.inputStream.bufferedReader().readText().trim()
            p.waitFor()
            Log.d(TAG, "blkid 回傳: `$out`")
            out
        } catch (e: Exception) {
            Log.e(TAG, "blkid 探測失敗", e)
            ""
        }
    }

    /** 清理掛載點 */
    private fun cleanupMountPoint() {
        execCmd("su","-c","umount $MOUNT_POINT")
        Thread.sleep(100)
        execCmd("su","-c","rmdir $MOUNT_POINT")
        execCmd("su","-c","mkdir -p $MOUNT_POINT")
    }

    /** 嘗試掛載 FAT32 或 exFAT */
    private fun mountFs(device: String, useExfatTool: Boolean): Boolean {
        // 先 unmount device
        unmountDevice(device)

        return if (!useExfatTool) {
            val cmd = "mount -t vfat -o ro $device $MOUNT_POINT"
            Log.d(TAG, "vfat 掛載: $cmd")
            execCmd("su","-c",cmd) == 0
        } else {
            val cmd = "$EXFAT_MOUNT_TOOL -o ro,nonempty $device $MOUNT_POINT"
            Log.d(TAG, "exfat-fuse 掛載: $cmd")
            execCmd("su","-c",cmd) == 0
        }
    }

    /** 解除 device 既有掛載 */
    private fun unmountDevice(device: String) {
        try {
            Log.d(TAG, "檢查並 umount 裝置原本掛載位置")
            val lines = Runtime.getRuntime().exec(arrayOf("su","-c","mount"))
                .inputStream.bufferedReader().readLines()
            lines.filter { it.startsWith("$device on ") }
                .forEach {
                    val mnt = it.split(" ")[2]
                    Log.d(TAG, "umount 已掛載: $mnt")
                    execCmd("su","-c","umount $mnt")
                    Thread.sleep(100)
                }
        } catch (e: Exception) {
            Log.e(TAG, "解除既有掛載失敗", e)
        }
    }

    private fun umountCleanup() {
        Log.d(TAG, "umountCleanup")
        execCmd("su","-c","umount $MOUNT_POINT")
        Thread.sleep(100)
    }

    /** 執行指令並印出 stdout/stderr */
    private fun execCmd(vararg cmd: String): Int {
        val full = cmd.joinToString(" ")
        return try {
            Log.d(TAG, "執行: $full")
            val p = Runtime.getRuntime().exec(cmd)

            val out = p.inputStream.bufferedReader().readText()
            if (out.isNotBlank()) Log.d(TAG, "stdout: $out")

            val err = p.errorStream.bufferedReader().readText()
            if (err.isNotBlank()) Log.e(TAG, "stderr: $err")

            val code = p.waitFor()
            Log.d(TAG, "exit code=$code")
            code
        } catch (e: Exception) {
            Log.e(TAG, "execCmd 失敗: $full", e)
            -1
        }
    }
}
