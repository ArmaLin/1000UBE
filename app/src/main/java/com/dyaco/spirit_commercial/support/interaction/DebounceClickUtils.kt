package com.dyaco.spirit_commercial.support.interaction

import android.util.Log
import android.view.View
import java.util.Collections
import java.util.WeakHashMap

/**
 * 防止按鈕在短時間內重複點擊的工具類 (自動管理，無需手動解除)
 */
object DebounceClick {

    private const val DEFAULT_DEBOUNCE_TIME = 500L // ✅ 預設 500ms 避免快速點擊
    private val lastClickMap = Collections.synchronizedMap(WeakHashMap<View, Long>())

    /**
     * 設定防止快速點擊的監聽器
     *
     * @param view 目標按鈕
     * @param debounceTime 防抖時間 (預設 500ms)
     * @param listener 點擊監聽 (Java 也可使用)
     */
    @JvmStatic
    @JvmOverloads
    fun attach(
        view: View,
        debounceTime: Long = DEFAULT_DEBOUNCE_TIME,
        listener: ClickListener
    ) {
        // ✅ 如果 debounceTime <= 0，直接使用普通點擊事件
        if (debounceTime <= 0) {
            view.setOnClickListener { listener.onDebouncedClick(view) }
            return
        }

        val debouncedOnClickListener = View.OnClickListener { v ->
            val currentTime = System.currentTimeMillis()
            val lastClickTime = lastClickMap[v] ?: 0L

            if (currentTime - lastClickTime >= debounceTime) {
                lastClickMap[v] = currentTime
                listener.onDebouncedClick(v)
            }
        }

        // ✅ 記錄監聽器到 WeakHashMap，確保不會被 `tag` 影響
        lastClickMap[view] = 0L

        // ✅ 自動管理監聽器生命週期，避免記憶體洩漏
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.setOnClickListener(debouncedOnClickListener) // ✅ View 重新 attach 時恢復監聽
            }

            override fun onViewDetachedFromWindow(v: View) {
                v.setOnClickListener(null) // ✅ View 被銷毀時移除監聽
                lastClickMap.remove(v) // ✅ 確保回收時清除記錄
                Log.d("ClickUtils", "SAFE 自動取消: ${view.id}")
            }
        })

        // ✅ 初始設定監聽器
        view.setOnClickListener(debouncedOnClickListener)
    }
}

/**
 * **Java 也可以使用的 Debounce 點擊監聽介面**
 */
fun interface ClickListener {
    fun onDebouncedClick(v: View)
}
