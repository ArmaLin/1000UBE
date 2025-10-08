package com.dyaco.spirit_commercial.support.interaction

import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast

// 回調介面，Java 呼叫時只要實作這個介面即可
interface OnTenClickListener {
    fun onTenClick()
}

/**
 * 按鈕連點觸發功能
 */
object TenTapClick {

    /**
     * 設定快速點擊監聽
     *
     * @param view 要設定監聽的 View
     * @param count 需要快速點擊的次數，預設 10 次
     * @param interval 每次點擊間的間隔時間 (毫秒)，預設 300 毫秒
     * @param showToast 是否顯示 Toast，預設 false 不顯示
     * @param listener 點擊達標時觸發的回調
     *
     * Java 可透過 @JvmOverloads 呼叫各種重載版本，但如果只想指定 showToast 參數，就需要另外新增一個重載方法。
     */
    @JvmStatic
    @JvmOverloads
    fun setTenClickListener(
        view: View,
        count: Int = 10,
        interval: Long = 300,
        showToast: Boolean = false,
        listener: OnTenClickListener
    ) {
        var clickCount = 0
        var lastClickTime: Long = 0
        var currentToast: Toast? = null

        view.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()
            // 若兩次點擊間隔超過設定值，則重置計數
            if (currentTime - lastClickTime > interval) {
                clickCount = 0
            }
            lastClickTime = currentTime
            clickCount++

            Log.d("RapidClickUtil", "目前點擊次數：$clickCount")

            if (showToast && clickCount in 8..10) {
                currentToast?.cancel()  // 取消前一個 Toast
                currentToast = Toast.makeText(view.context, "Click $clickCount times", Toast.LENGTH_SHORT)
                currentToast?.show()
            }

            if (clickCount >= count) {
                clickCount = 0
                listener.onTenClick()
            }
        }
    }

    /**
     * 重載方法：只需要輸入 view、showToast 與 listener，其餘使用預設值
     *
     * @param view 要設定監聽的 View
     * @param showToast 是否顯示 Toast
     * @param listener 點擊達標時觸發的回調
     */
    @JvmStatic
    fun setTenClickListener(
        view: View,
        showToast: Boolean,
        listener: OnTenClickListener
    ) {
        setTenClickListener(view, 10, 300, showToast, listener)
    }
}
