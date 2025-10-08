package com.dyaco.spirit_commercial

import android.media.MediaDrm
import android.util.Log
import java.util.*

// 注意：WIDEVINE_UUID 一定要是這個字串
private val WIDEVINE_UUID = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")

object WidevineUtil {
    /**
     * 取得 Widevine DRM Security Level（L1/L3）
     * Java 呼叫方式：String lvl = WidevineUtil.getWidevineSecurityLevel();
     */
    @JvmStatic
    fun getWidevineSecurityLevel(): String {
        var drm: MediaDrm? = null
        return try {
            drm = MediaDrm(WIDEVINE_UUID)
            val level = drm.getPropertyString("securityLevel")
            Log.d("WidevineUtil", "Widevine Security Level = $level")
            level
        } catch (e: Exception) {
            Log.e("WidevineUtil", "查不到 Widevine Level", e)
            "Unknown"
        } finally {
            drm?.release()
        }
    }
}
