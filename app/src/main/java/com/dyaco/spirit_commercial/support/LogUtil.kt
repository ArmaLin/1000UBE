package com.dyaco.spirit_commercial.support

import android.util.Log

/**
 * 輕量日誌工具，Java 與 Kotlin 可共用。
 * Kotlin 支援 lambda 延遲評估，Java 則使用傳統呼叫。
 */
object LogUtil {

    @JvmStatic
    var enableLog: Boolean = true

    /** Kotlin 專用延遲 Log，用於效能敏感的地方 */
    fun log(type: String = "d", tag: String, messageProvider: () -> String) {
        if (!enableLog) return
        val message = messageProvider()
        when (type.lowercase()) {
            "d" -> Log.d(tag, message)
            "i" -> Log.i(tag, message)
            "w" -> Log.w(tag, message)
            "e" -> Log.e(tag, message)
            else -> Log.v(tag, message)
        }
    }

    /** Java 呼叫專用的簡單版本 */
    @JvmStatic fun d(tag: String, message: String) { if (enableLog) Log.d(tag, message) }
    @JvmStatic fun i(tag: String, message: String) { if (enableLog) Log.i(tag, message) }
    @JvmStatic fun w(tag: String, message: String) { if (enableLog) Log.w(tag, message) }
    @JvmStatic fun e(tag: String, message: String) { if (enableLog) Log.e(tag, message) }
}
