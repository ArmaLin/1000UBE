package com.dyaco.spirit_commercial.support

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue

object IdleRunner {

    private val handler = Handler(Looper.getMainLooper())

    /**
     * 執行任務於主線程 idle 時，或 timeout 後 fallback 執行，
     * 並確保任務只執行一次。
     *
     * @param task 任務
     * @param timeoutMs 等待 idle 的最大時間（預設 500ms）
     */
    @JvmStatic
    @JvmOverloads
    fun run(task: Runnable, timeoutMs: Long = 500) {
        // 確保在主線程執行
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post { run(task, timeoutMs) }
            return
        }

        var executed = false
        // 區域函式：確保任務只執行一次
        fun executeOnce() {
            if (!executed) {
                executed = true
                task.run()
            }
        }

        var fallback: Runnable? = null
        // IdleHandler：當 Looper 空閒時執行
        val idleHandler = MessageQueue.IdleHandler {
            // 移除 fallback callback 避免重複執行
            fallback?.let { handler.removeCallbacks(it) }
            executeOnce()
            false // 執行一次後移除 idle handler
        }

        fallback = Runnable {
            // fallback 被觸發時，先移除 idle handler
            Looper.myQueue().removeIdleHandler(idleHandler)
            executeOnce()
        }

        // 註冊 idle handler 與 fallback
        Looper.myQueue().addIdleHandler(idleHandler)
        handler.postDelayed(fallback, timeoutMs)
    }
}
