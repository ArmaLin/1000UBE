package com.dyaco.spirit_commercial

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import android.util.Log

class GlobalTouchIdleWatcher() {

    interface Callback {
        /** 螢幕有觸控事件時呼叫 */
        fun onScreenTouched()
        /** 空閒時間到時呼叫 */
        fun onIdleTimeout()
    }

    companion object {
        private const val TAG = "TouchIdleWatcher"
        /** 最小觸發間隔，內建 500ms */
        private const val MIN_TOUCH_INTERVAL_MS = 500L
        /** 預設空閒逾時：10 分鐘 */
        private const val DEFAULT_IDLE_TIMEOUT_MS = 10 * 60 * 1000L
    }

    private var callback: Callback? = null
    private var idleTimeoutMs: Long = DEFAULT_IDLE_TIMEOUT_MS

    @Volatile private var isRunning = false
    private var lastTouchTime: Long = 0L
    private var lastCallbackTime: Long = 0L

    private val mainHandler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable {
        val now = System.currentTimeMillis()
        if (now - lastTouchTime >= idleTimeoutMs) {
            Log.d(TAG, "已超過 $idleTimeoutMs ms無觸控 → 執行空閒任務")
            callback?.onIdleTimeout()
        } else {
            scheduleIdleCheck()
        }
    }

    // 以背景優先權啟動 I/O 讀取執行緒
    private val ioThread = HandlerThread(
        "IdleWatcherIO",
        Process.THREAD_PRIORITY_BACKGROUND
    ).apply { start() }
    private val ioHandler = Handler(ioThread.looper)

    // 用 java.lang.Process 來執行 getevent
    private var eventProcess: java.lang.Process? = null

    /** 設定回呼物件 */
    fun setCallback(cb: Callback): GlobalTouchIdleWatcher {
        this.callback = cb
        return this
    }

    /** 動態修改空閒逾時時間（ms），並重置倒數 */
    fun setIdleTimeoutMs(timeoutMs: Long): GlobalTouchIdleWatcher {
        idleTimeoutMs = timeoutMs
        lastTouchTime = System.currentTimeMillis()
        scheduleIdleCheck()
        Log.d(TAG, "idleTimeoutMs 已更新為 $idleTimeoutMs ms")
        return this
    }

    /** 啟動監聽 */
    fun start() {
        if (isRunning) return
        isRunning = true
        lastTouchTime = System.currentTimeMillis()
        scheduleIdleCheck()

        ioHandler.post {
            // 再次保險設定 thread 優先度
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            try {
                eventProcess = Runtime.getRuntime().exec(arrayOf("getevent", "-lt"))
                eventProcess!!.inputStream
                    .bufferedReader()
                    .forEachLine { line ->
                        if (!isRunning) return@forEachLine
                        if (line.contains("SYN_REPORT")) {
                            onTouchDetected()
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "TouchIdleWatcher 監聽失敗", e)
            }
        }

        Log.d(TAG, "TouchIdleWatcher 已啟動")
    }

    /** 停止監聽 */
    fun stop() {
        isRunning = false
        eventProcess?.destroy()
        mainHandler.removeCallbacks(idleRunnable)
        ioThread.quitSafely()
        Log.d(TAG, "TouchIdleWatcher 已停止")
    }

    private fun onTouchDetected() {
        val now = System.currentTimeMillis()
        if (now - lastCallbackTime < MIN_TOUCH_INTERVAL_MS) return

        lastCallbackTime = now
        lastTouchTime = now
        callback?.onScreenTouched()
        scheduleIdleCheck()
    }

    private fun scheduleIdleCheck() {
        mainHandler.removeCallbacks(idleRunnable)
        val delay = idleTimeoutMs - (System.currentTimeMillis() - lastTouchTime)
        if (delay <= 0) mainHandler.post(idleRunnable)
        else mainHandler.postDelayed(idleRunnable, delay)
    }
}
