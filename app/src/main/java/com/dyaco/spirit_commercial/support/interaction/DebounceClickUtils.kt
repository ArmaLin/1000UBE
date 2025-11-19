package com.dyaco.spirit_commercial.support.interaction

import android.os.SystemClock
import android.view.View


object DebounceClick {
    private const val DEFAULT_DEBOUNCE_TIME = 500L // 預設 500ms

    /**
     * 設定防止快速點擊的監聽器 (Java / Kotlin 通用)
     *
     * @param view 目標按鈕
     * @param debounceTime 防抖時間 (預設 500ms)。若 <= 0，則不做任何防抖
     * @param listener 點擊監聽
     */
    @JvmStatic
    @JvmOverloads
    fun attach(
        view: View,
        debounceTime: Long = DEFAULT_DEBOUNCE_TIME,
        listener: ClickListener
    ) {
        if (debounceTime <= 0) {
            // 不做防抖，直接呼叫
            view.setOnClickListener { listener.onDebouncedClick(it) }
        } else {
            // 每個 View 用一個新的 DebouncedOnClickListener，覆蓋掉舊的 listener
            view.setOnClickListener(DebouncedOnClickListener(debounceTime, listener))
        }
    }


    private class DebouncedOnClickListener(
        private val debounceTime: Long,
        private val listener: ClickListener
    ) : View.OnClickListener {

        // 紀錄上一次點擊的「開機後經過時間」
        private var lastClickTime = 0L

        override fun onClick(v: View) {
            // 獲取系統開機後的毫秒數 (包含睡眠時間)，此時間保證只增不減
            val now = SystemClock.elapsedRealtime()

            if (now - lastClickTime >= debounceTime) {
                lastClickTime = now
                listener.onDebouncedClick(v)
            }
        }
    }
}

/**
 * **Java 也可以使用的 Debounce 點擊監聽介面**
 */
fun interface ClickListener {
    fun onDebouncedClick(v: View)
}


/**
 * Kotlin Extension: 讓 View 可以直接呼叫 setOnDebouncedClickListener
 *
 * 用法:
 * button.setOnDebouncedClickListener {
 * // 點擊邏輯
 * }
 */
fun View.setOnDebouncedClickListener(
    debounceTime: Long = 500L,
    action: (View) -> Unit
) {
    DebounceClick.attach(this, debounceTime) { action(it) }
}