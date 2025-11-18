package com.dyaco.spirit_commercial.support.usb

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

object UsbMountedStorageReader {

    private const val TAG = "USB_MOUNT_READER"

    // --------------------------------------------------------
    // Debug：dump /proc/mounts
    // --------------------------------------------------------
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

    // --------------------------------------------------------
    // 1. 找掛載點（優先順序：StorageManager → /proc/mounts → 目錄掃描）
    // --------------------------------------------------------
    /**
     * 尋找 USB 掛載點
     * 優先順序：
     * 1. StorageManager 可移除 volume（Android 7+）
     * 2. /proc/mounts 解析
     * 3. 掃描 /storage、/mnt/media_rw、/mnt/user/0、/mnt/pass_through/0、/mnt/usb
     */
    @JvmStatic
    fun findUsbMountPoint(context: Context): File? {
        // 1) StorageManager（最可靠，支援 exFAT）
        getUsbMountFromStorageManager(context)?.let {
            Log.d(TAG, "findUsbMountPoint() 由 StorageManager 取得掛載點：${it.absolutePath}")
            return it
        }

        // 2) /proc/mounts
        val fromProc = getUsbMountFromProcMounts()
        if (fromProc != null) {
            Log.d(TAG, "findUsbMountPoint() 由 /proc/mounts 取得掛載點：${fromProc.absolutePath}")
            return fromProc
        }

        // 3) 目錄掃描（包含 /mnt/usb，給 exfat-fuse 用）
        val candidates = mutableListOf<File>()
        Log.w(TAG, "findUsbMountPoint() 前兩步皆失敗，改用目錄掃描")

        scanDirForUsbMount(File("/storage"), candidates)
        scanDirForUsbMount(File("/mnt/media_rw"), candidates)
        scanDirForUsbMount(File("/mnt/user/0"), candidates)
        scanDirForUsbMount(File("/mnt/pass_through/0"), candidates)

        // 也把 /mnt/usb 當成候選（你 root 掛 exFAT 時會用到）
        val custom = File("/mnt/usb")
        if (custom.exists() && custom.isDirectory && custom.canRead()) {
            Log.d(TAG, "掃描到自訂掛載點：${custom.absolutePath}")
            candidates.add(custom)
        }

        if (candidates.isEmpty()) {
            Log.w(TAG, "findUsbMountPoint() 最終找不到任何 USB 掛載點")
            return null
        }

        val mount = pickBestCandidate(candidates)
        Log.d(TAG, "findUsbMountPoint() 由目錄掃描選擇掛載點：${mount.absolutePath}")
        return mount
    }

    /**
     * 使用 StorageManager 取得 USB 掛載點（Android 7.0+）
     */
    private fun getUsbMountFromStorageManager(context: Context): File? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return null

        return try {
            val sm = context.getSystemService(StorageManager::class.java)
            val vols = sm.storageVolumes

            Log.d(TAG, "StorageManager 回傳 ${vols.size} 個 volume")
            val candidates = mutableListOf<File>()

            for (vol in vols) {
                val desc = try {
                    vol.getDescription(context)
                } catch (_: Throwable) {
                    ""
                }
                Log.d(
                    TAG,
                    "volume: desc=$desc, removable=${vol.isRemovable}, state=${vol.state}, uuid=${vol.uuid}, isPrimary=${vol.isPrimary}"
                )

                // 只挑可移除且已掛載的（外接 USB / SD）
                if (!vol.isRemovable) continue
                if (vol.state != Environment.MEDIA_MOUNTED) continue

                val dir: File? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    vol.directory
                } else {
                    // Android 7~10 這裡可以用反射拿 path（可選），先略過
                    null
                }

                if (dir != null && dir.exists() && dir.isDirectory && dir.canRead()) {
                    Log.d(TAG, "StorageManager 找到候選掛載點：${dir.absolutePath}")
                    candidates.add(dir)
                }
            }

            if (candidates.isEmpty()) {
                null
            } else {
                pickBestCandidate(candidates)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUsbMountFromStorageManager() 發生錯誤：${e.localizedMessage}", e)
            null
        }
    }

    /**
     * 從 /proc/mounts 找候選掛載點（放寬條件，支援 exFAT / 其他 fs）
     */
    private fun getUsbMountFromProcMounts(): File? {
        val candidates = mutableListOf<File>()

        try {
            BufferedReader(FileReader("/proc/mounts")).use { br ->
                br.lineSequence().forEach { line ->
                    val parts = line.split(" ")
                    if (parts.size < 3) return@forEach

                    val dev = parts[0]
                    val mountPoint = parts[1]
                    val fsType = parts[2]

                    // 只看 storage / mnt/media_rw / mnt/user/0 / mnt/pass_through/0
                    if (!(mountPoint.startsWith("/storage") ||
                                mountPoint.startsWith("/mnt/media_rw") ||
                                mountPoint.startsWith("/mnt/user/0") ||
                                mountPoint.startsWith("/mnt/pass_through/0"))
                    ) {
                        return@forEach
                    }

                    // 排除內建 emulated / self / runtime
                    if (mountPoint.startsWith("/storage/emulated") ||
                        mountPoint.startsWith("/storage/self") ||
                        mountPoint.contains("/emulated/0") ||
                        mountPoint.startsWith("/mnt/runtime")
                    ) {
                        return@forEach
                    }

                    // 排除明顯不是外接儲存
                    if (mountPoint.contains("/obb") ||
                        mountPoint.contains("/asec") ||
                        mountPoint.contains("/secure")
                    ) {
                        return@forEach
                    }

                    val f = File(mountPoint)
                    if (f.exists() && f.isDirectory && f.canRead()) {
                        Log.d(
                            TAG,
                            "從 /proc/mounts 找到外接候選：dev=$dev, mp=$mountPoint, fs=$fsType"
                        )
                        candidates.add(f)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUsbMountFromProcMounts() 解析 /proc/mounts 發生錯誤：${e.localizedMessage}", e)
        }

        if (candidates.isEmpty()) return null
        return pickBestCandidate(candidates)
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
                name.equals("secure", true) ||
                name.equals("tmp", true)
            ) {
                Log.d(TAG, "掃描時忽略目錄：$path")
                return@forEach
            }

            Log.d(TAG, "掃描候選掛載目錄：$path")
            out.add(f)
        }
    }

    /**
     * 根據路徑優先順序選出最適合的掛載點
     */
    private fun pickBestCandidate(candidates: List<File>): File {
        return candidates.sortedBy { file ->
            val p = file.absolutePath
            when {
                p.startsWith("/storage/") -> 0
                p.startsWith("/mnt/media_rw/") -> 1
                p.startsWith("/mnt/user/0/") -> 2
                p.startsWith("/mnt/pass_through/0/") -> 3
                p.startsWith("/mnt/usb") -> 4
                else -> 5
            }
        }.first()
    }

    // --------------------------------------------------------
    // 2. 遞迴找檔案
    // --------------------------------------------------------
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
     */
    @JvmStatic
    @JvmOverloads
    fun readUsbFile(
        context: Context,
        fileName: String,
        returnBytes: Boolean = false
    ): Any? {
        Log.d(TAG, "readUsbFile() 開始，fileName=$fileName, returnBytes=$returnBytes")

        val mount = findUsbMountPoint(context)
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
