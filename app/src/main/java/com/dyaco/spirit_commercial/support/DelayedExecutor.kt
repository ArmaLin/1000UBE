package com.dyaco.spirit_commercial.support

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object DelayedExecutor {
    private val handler = Handler(Looper.getMainLooper())
    private val scheduledRunnables = mutableMapOf<String, Runnable>()

    /**
     * 供 Java 呼叫：在延遲指定毫秒後於主執行緒執行任務，並提供 taskId 方便取消
     *
     * 範例：
     * DelayedExecutor.runOnMainThread(1000L, "myTask", new Runnable() { ... });
     */
    @JvmStatic
    fun runOnMainThread(delayMs: Long, taskId: String, action: Runnable) {
        // 若已有相同 taskId 任務，先取消先前任務
        cancel(taskId)
        val runnable = Runnable {
            try {
                action.run()
            } finally {
                scheduledRunnables.remove(taskId)
            }
        }
        scheduledRunnables[taskId] = runnable
        handler.postDelayed(runnable, delayMs)
    }

    /**
     * 供 Kotlin 呼叫：直接傳入 Lambda，並提供 taskId 以便取消
     *
     * 範例：
     * DelayedExecutor.runOnMainThread(1000L, "myTask") { ... }
     */
    @JvmStatic
    fun runOnMainThread(delayMs: Long, taskId: String, block: () -> Unit) {
        runOnMainThread(delayMs, taskId, Runnable { block() })
    }

    /**
     * 供 Java 呼叫：在延遲指定毫秒後於主執行緒執行任務，無需提供 taskId（無法取消）
     *
     * 範例：
     * DelayedExecutor.runOnMainThread(1000L, new Runnable() { ... });
     */
    @JvmStatic
    fun timer(delayMs: Long, action: Runnable) {
        handler.postDelayed(action, delayMs)
    }

    /**
     * 供 Kotlin 呼叫：直接傳入 Lambda，不需要提供 taskId（無法取消）
     *
     * 範例：
     * DelayedExecutor.runOnMainThread(1000L) { ... }
     */
    @JvmStatic
    fun runOnMainThread(delayMs: Long, block: () -> Unit) {
        timer(delayMs, Runnable { block() })
    }

    /**
     * 取消已排程的任務，僅適用於提供 taskId 的任務
     *
     * 範例：
     * DelayedExecutor.cancel("myTask");
     */
    @JvmStatic
    fun cancel(taskId: String) {
        scheduledRunnables.remove(taskId)?.let {
            handler.removeCallbacks(it)
        }
    }

    /**
     * Kotlin 協程用法：延遲指定毫秒後於主執行緒執行區塊
     *
     * 範例：
     * delayAndRun(1000L) { ... }
     */
    suspend fun delayAndRun(delayMs: Long, block: () -> Unit) {
        delay(delayMs)
        withContext(Dispatchers.Main) {
            block()
        }
    }
}
