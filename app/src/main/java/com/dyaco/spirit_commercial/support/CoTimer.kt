// 檔案：CoTimer.kt
package com.dyaco.spirit_commercial.support

import kotlinx.coroutines.*

/**
 * 全域單例 CoTimer，底層使用 MainDispatcher（Dispatchers.Main）
 */
object CoTimer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /** 延遲執行一次的回呼介面 */
    interface AfterListener {
        fun onFinish()
    }

    /** 定期執行的回呼介面 */
    interface EveryListener {
        fun onTick(elapsedMillis: Long)
    }

    /** 重複次數執行的回呼介面 */
    interface RepeatListener {
        fun onTick(count: Int)
        fun onComplete()
    }

    /** 倒數執行的回呼介面 */
    interface CountDownListener {
        fun onTick(remaining: Int)
        fun onComplete()
    }

    /**
     * 延遲 [delayMillis] 毫秒後呼叫 onFinish()
     * 回傳 Job，可用 job.cancel() 取消
     */
    @JvmStatic
    fun after(delayMillis: Long, listener: AfterListener): Job =
        scope.launch {
            delay(delayMillis)
            listener.onFinish()
        }

    /**
     * 每隔 [periodMillis] 毫秒呼叫 onTick(elapsed)
     * 回傳 Job，可用 job.cancel() 取消
     */
    @JvmStatic
    fun every(periodMillis: Long, listener: EveryListener): Job =
        scope.launch {
            var elapsed = 0L
            while (isActive) {
                listener.onTick(elapsed)
                delay(periodMillis)
                elapsed += periodMillis
            }
        }

    /**
     * 重複執行 [times] 次，間隔 [periodMillis] 毫秒，
     * 每次呼 onTick(count)，全部執行完再呼 onComplete()
     * 回傳 Job，可用 job.cancel() 取消
     */
    @JvmStatic
    fun repeat(
        times: Int,
        periodMillis: Long,
        listener: RepeatListener
    ): Job = scope.launch {
        repeat(times) { index ->
            listener.onTick(index + 1)
            if (index < times - 1) delay(periodMillis)
        }
        listener.onComplete()
    }

    /**
     * 從 [startCount] 倒數到 0，每隔 [periodMillis] 毫秒，
     * 每次呼 onTick(剩餘秒數)、最後呼 onComplete()
     * 回傳 Job，可用 job.cancel() 取消
     */
    @JvmStatic
    fun countDown(
        startCount: Int,
        periodMillis: Long,
        listener: CountDownListener
    ): Job = scope.launch {
        for (i in startCount downTo 0) {
            listener.onTick(i)
            if (i > 0) delay(periodMillis)
        }
        listener.onComplete()
    }
}
/**
 * // 1. 延遲 2 秒執行一次
 *         job = CoTimer.after(2000L, new CoTimer.AfterListener() {
 *             @Override
 *             public void onFinish() {
 *                 Log.d("CoTimer", "延遲 2 秒執行");
 *             }
 *         });
 *
 *         // 2. 每 500ms 執行一次，回傳已過時間
 *         job = CoTimer.every(500L, new CoTimer.EveryListener() {
 *             @Override
 *             public void onTick(long elapsedMillis) {
 *                 Log.d("CoTimer", "已過 " + elapsedMillis + " ms");
 *             }
 *         });
 *
 *         // 3. 重複執行 5 次，每秒一次，最後 onComplete
 *         job = CoTimer.repeat(5, 1000L, new CoTimer.RepeatListener() {
 *             @Override
 *             public void onTick(int count) {
 *                 Log.d("CoTimer", "第 " + count + " 次執行");
 *             }
 *             @Override
 *             public void onComplete() {
 *                 Log.d("CoTimer", "全部 5 次執行完畢");
 *             }
 *         });
 *
 *         // 4. 倒數 10 到 0，每秒一次
 *         job = CoTimer.countDown(10, 1000L, new CoTimer.CountDownListener() {
 *             @Override
 *             public void onTick(int remaining) {
 *                 Log.d("CoTimer", "剩餘 " + remaining + " 秒");
 *             }
 *             @Override
 *             public void onComplete() {
 *                 Log.d("CoTimer", "倒數結束");
 *             }
 *         });
 *
 *         // 若要取消目前正在執行的計時，可呼：
 *         // job.cancel();
 *     }
 */