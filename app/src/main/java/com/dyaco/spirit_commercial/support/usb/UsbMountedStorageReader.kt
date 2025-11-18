package com.dyaco.spirit_commercial.support.usb

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


object UsbMountedStorageReader {

    private const val TAG = "USB_MOUNT_READER"

    @JvmStatic
    fun debugDumpMounts() {
        try {
            BufferedReader(FileReader("/proc/mounts")).use { br ->
                Log.d(TAG, "===== /proc/mounts BEGIN =====")
                br.lineSequence().forEach { line ->
                    Log.d(TAG, line)
                }
                Log.d(TAG, "===== /proc/mounts END =====")
            }
        } catch (e: Exception) {
            Log.e(TAG, "讀取 /proc/mounts 失敗：${e.localizedMessage}", e)
        }
    }

    @JvmStatic
    fun findUsbMountPoint(): File? {
        val candidates = mutableListOf<File>()

        try {
            BufferedReader(FileReader("/proc/mounts")).use { br ->
                br.lineSequence().forEach { line ->
                    val parts = line.split(" ")
                    if (parts.size < 3) return@forEach

                    val dev = parts[0]
                    val mountPoint = parts[1]
                    val fsType = parts[2]

                    // 只關心 storage / mnt/media_rw 相關掛載
                    if (!(mountPoint.startsWith("/storage") ||
                                mountPoint.startsWith("/mnt/media_rw"))
                    ) {
                        return@forEach
                    }

                    // 排除內建 emulated / self
                    if (mountPoint.startsWith("/storage/emulated") ||
                        mountPoint.startsWith("/storage/self")
                    ) {
                        return@forEach
                    }

                    // 檔案系統類型：FAT32 / exFAT / 其他外接盤常見
                    val isPossibleFs =
                        fsType.equals("vfat", true) ||
                                fsType.equals("exfat", true) ||
                                fsType.equals("texfat", true) ||
                                fsType.equals("fuse", true) ||
                                fsType.equals("fuseblk", true) ||
                                fsType.equals("ntfs", true)

                    if (!isPossibleFs) return@forEach

                    // dev 端：通常是 /dev/block/vold/public:* 或 /dev/fuse
                    val isPossibleDev =
                        dev.startsWith("/dev/block/vold") ||
                                dev.startsWith("/dev/fuse") ||
                                dev.startsWith("/dev/block/sd")

                    if (!isPossibleDev) return@forEach

                    val f = File(mountPoint)
                    if (f.exists() && f.isDirectory && f.canRead()) {
                        Log.d(
                            TAG,
                            "找到 USB 掛載候選：dev=$dev, mp=$mountPoint, fs=$fsType"
                        )
                        candidates.add(f)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析 /proc/mounts 發生錯誤：${e.localizedMessage}", e)
        }

        // 若 /proc/mounts 沒抓到，再用目錄掃描做最後嘗試
        if (candidates.isEmpty()) {
            Log.w(TAG, "findUsbMountPoint() /proc/mounts 沒有候選，改用目錄掃描")

            scanDirForUsbMount(File("/storage"), candidates)
            scanDirForUsbMount(File("/mnt/media_rw"), candidates)
        }

        if (candidates.isEmpty()) {
            Log.w(TAG, "findUsbMountPoint() 最終找不到任何 USB 掛載點")
            return null
        }

        // 排序：優先 /storage/* 其次 /mnt/media_rw/*
        val sorted = candidates.sortedBy { file ->
            when {
                file.absolutePath.startsWith("/storage/") -> 0
                file.absolutePath.startsWith("/mnt/media_rw/") -> 1
                else -> 2
            }
        }

        val mount = sorted.first()
        Log.d(TAG, "findUsbMountPoint() 選擇掛載點：${mount.absolutePath}")
        return mount
    }

    /**
     * 掃描某個目錄底下「可能是外接儲存」的目錄
     */
    private fun scanDirForUsbMount(root: File, out: MutableList<File>) {
        if (!root.exists() || !root.isDirectory) return

        val children = root.listFiles() ?: return
        children.forEach { f ->
            if (!f.isDirectory || !f.canRead()) return@forEach

            val path = f.absolutePath
            val name = f.name

            if (name.equals("emulated", true) ||
                name.equals("self", true) ||
                name.equals("obb", true) ||
                name.equals("asec", true) ||
                name.equals("secure", true)
            ) {
                Log.d(TAG, "掃描時忽略目錄：$path")
                return@forEach
            }

            Log.d(TAG, "掃描候選掛載目錄：$path")
            out.add(f)
        }
    }

    // --------------------------------------------------------
    // 2. 遞迴找檔案
    // --------------------------------------------------------
    /**
     * 遞迴尋找指定檔名（預設深度 6）
     */
    @JvmStatic
    fun findFileOnUsbRecursive(root: File, fileName: String, maxDepth: Int = 6): File? {
        Log.d(TAG, "開始在 ${root.absolutePath} 遞迴尋找檔案：$fileName, maxDepth=$maxDepth")
        return findFileInternal(root, fileName, 0, maxDepth)
    }

    private fun findFileInternal(dir: File, fileName: String, depth: Int, maxDepth: Int): File? {
        if (depth > maxDepth) return null
        val files = dir.listFiles() ?: return null

        // 先找當前目錄的檔案
        files.forEach { f ->
            if (f.isFile && f.name.equals(fileName, ignoreCase = true)) {
                Log.d(TAG, "在 ${f.parent} 找到檔案：${f.name}")
                return f
            }
        }

        // 再往子目錄遞迴
        files.forEach { f ->
            if (f.isDirectory && f.canRead()) {
                val found = findFileInternal(f, fileName, depth + 1, maxDepth)
                if (found != null) return found
            }
        }
        return null
    }

    // --------------------------------------------------------
    // 3. 複製 / 讀取
    // --------------------------------------------------------
    /**
     * 複製檔案到 app cacheDir，並回傳路徑（給 APK / MP4 / Image / JSON）
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyFileToCache(context: Context, src: File): String {
        val target = File(context.cacheDir, src.name)
        Log.d(TAG, "開始複製檔案到 cache：${src.absolutePath} -> ${target.absolutePath}")
        src.inputStream().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return target.absolutePath
    }

    /**
     * 複製檔案並回傳 ByteArray（給 BIN / JSON）
     */
    @JvmStatic
    @Throws(IOException::class)
    fun readFileAsBytes(src: File): ByteArray {
        Log.d(TAG, "讀取檔案為 ByteArray：${src.absolutePath}")
        return src.readBytes()
    }

    // --------------------------------------------------------
    // 4. 一次做完：尋找 + 讀取
    // --------------------------------------------------------
    /**
     * @param returnBytes = true → 回傳 ByteArray
     * @param returnBytes = false → 回傳複製後檔案路徑
     *
     * 回傳：
     *   - ByteArray 或 String
     *   - 失敗時為 null
     */
    @JvmStatic
    @JvmOverloads
    fun readUsbFile(
        context: Context,
        fileName: String,
        returnBytes: Boolean = false
    ): Any? {
        Log.d(TAG, "readUsbFile() 開始，fileName=$fileName, returnBytes=$returnBytes")

        val mount = findUsbMountPoint()
        if (mount == null) {
            Log.w(TAG, "readUsbFile() 找不到掛載點，直接回傳 null")
            return null
        }

        val target = findFileOnUsbRecursive(mount, fileName, maxDepth = 6)
        if (target == null) {
            Log.w(TAG, "readUsbFile() 在 ${mount.absolutePath} 下找不到檔案：$fileName")
            return null
        }

        return try {
            if (returnBytes) {
                val data = readFileAsBytes(target)
                Log.d(TAG, "readUsbFile() 完成，回傳 ByteArray，大小=${data.size}")
                data
            } else {
                val path = copyFileToCache(context, target)
                Log.d(TAG, "readUsbFile() 完成，回傳檔案路徑=$path")
                path
            }
        } catch (e: Exception) {
            Log.e(TAG, "readUsbFile() 讀取失敗：${e.localizedMessage}", e)
            null
        }
    }

    // --------------------------------------------------------
    // 5. 直接讀 JSON 字串
    // --------------------------------------------------------
    /**
     * 直接從 USB 讀取 JSON 檔並回傳字串
     * Java 呼叫：
     *   String json = UsbMountedStorageReader.readUsbJson(getApp(), "update.json");
     */
    @JvmStatic
    fun readUsbJson(context: Context, fileName: String = "update.json"): String? {
        Log.d(TAG, "readUsbJson() 開始，fileName=$fileName")

        val any = readUsbFile(context, fileName, true) ?: run {
            Log.w(TAG, "readUsbJson() 無法從 USB 讀到檔案：$fileName")
            return null
        }

        val bytes = any as? ByteArray ?: run {
            Log.w(TAG, "readUsbJson() 回傳型別不是 ByteArray：${any::class.java}")
            return null
        }

        return try {
            val json = String(bytes, Charsets.UTF_8)
            Log.d(TAG, "readUsbJson() 成功讀到 JSON，長度=${json.length}")
            Log.d(TAG, "JSON文本：\n$json")
            json
        } catch (e: Exception) {
            Log.e(TAG, "readUsbJson() 轉字串失敗：${e.localizedMessage}", e)
            null
        }
    }
}
