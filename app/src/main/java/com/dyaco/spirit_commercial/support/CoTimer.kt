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

    /** 延遲 [delayMillis] 毫秒後呼叫 onFinish() */
    @JvmStatic
    fun after(delayMillis: Long, listener: AfterListener): Job =
        scope.launch {
            delay(delayMillis)
            listener.onFinish()
        }

    /** 每隔 [periodMillis] 毫秒呼叫 onTick(elapsed) */
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

    /** 重複執行 [times] 次，間隔 [periodMillis] 毫秒 */
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

    /** 從 [startCount] 倒數到 0，每隔 [periodMillis] 毫秒 */
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

    @JvmStatic
    fun cancelJob(job: Job) {
        job.cancel()
    }


    /**
     * 一个可暂停／恢复的“every”定时器。
     * Java 侧可通过 CoTimer.pausableEvery(...) 拿到实例并调用 start/pause/resume/cancel。
     */
    class PausableTimer internal constructor(
        private val periodMillis: Long,
        private val listener: EveryListener
    ) {
        private var elapsed = 0L
        private var job: Job? = null

        /** 开始或从头（elapsed=0）启动 */
        @JvmOverloads
        fun start(reset: Boolean = false): PausableTimer {
            if (reset) elapsed = 0L
            // 已在跑的先取消
            job?.cancel()
            job = scope.launch {
                while (isActive) {
                    listener.onTick(elapsed)
                    delay(periodMillis)
                    elapsed += periodMillis
                }
            }
            return this
        }

        fun pause(): PausableTimer {
            job?.cancel()
            job = null
            return this
        }

        fun resume(): PausableTimer {
            if (job == null) {
                job = scope.launch {
                    while (isActive) {
                        listener.onTick(elapsed)
                        delay(periodMillis)
                        elapsed += periodMillis
                    }
                }
            }
            return this
        }

        fun cancel(): PausableTimer {
            job?.cancel()
            job = null
            elapsed = 0L
            return this
        }

        /** 返回当前已过毫秒数 */
        fun getElapsed(): Long = elapsed
    }

    /**
     * 創建一個可暂停／續跑的定時器，
     * @param periodMillis 周期毫秒
     * @param listener callback
     * @return PausableTimer instance，可調用 start/pause/resume/cancel
     */
    @JvmStatic
    fun pausableEvery(
        periodMillis: Long,
        listener: EveryListener
    ): PausableTimer = PausableTimer(periodMillis, listener)




    /**
     * 一個可暫停／恢復的倒數計時器。
     */
    class PausableCountDownTimer internal constructor(
        private val startCount: Int,
        private val periodMillis: Long,
        private val listener: CountDownListener
    ) {
        private var job: Job? = null
        private var remainingCount: Int = startCount
        private var isRunning: Boolean = false

        /**
         * 開始或從頭重新開始倒數。
         * @param reset 如果為 true，則無論當前狀態如何，都從初始的 startCount 重新計時。
         */
        @JvmOverloads
        fun start(reset: Boolean = true): PausableCountDownTimer {
            if (isRunning && !reset) return this // 如果正在運行且不要求重置，則不執行任何操作
            if (reset) {
                remainingCount = startCount
            }
            job?.cancel() // 取消任何正在運行的舊任務
            isRunning = true

            job = scope.launch {
                try {
                    for (i in remainingCount downTo 0) {
                        remainingCount = i // 更新當前剩餘計數
                        listener.onTick(i)
                        if (i > 0) {
                            delay(periodMillis)
                        }
                    }
                    listener.onComplete()
                } finally {
                    // 無論是正常完成還是被取消，都重置狀態
                    resetState()
                }
            }
            return this
        }

        /**
         * 暫停倒數。
         */
        fun pause(): PausableCountDownTimer {
            job?.cancel()
            job = null
            isRunning = false
            return this
        }

        /**
         * 從暫停的地方繼續倒數。
         */
        fun resume(): PausableCountDownTimer {
            if (isRunning || remainingCount <= 0) return this // 如果正在運行或已完成，則不執行任何操作
            // 從當前剩餘的秒數開始，但不重置
            start(reset = false)
            return this
        }

        /**
         * 完全取消並重置計時器。
         */
        fun cancel(): PausableCountDownTimer {
            job?.cancel()
            resetState()
            return this
        }

        private fun resetState() {
            job = null
            isRunning = false
            remainingCount = startCount
        }
    }

    /**
     * 創建一個可暫停／恢復的倒數計時器實例。
     * 這個方法只會創建物件，需要手動呼叫 .start() 來啟動。
     */
    @JvmStatic
    fun pausableCountDown(
        startCount: Int,
        periodMillis: Long,
        listener: CountDownListener
    ): PausableCountDownTimer = PausableCountDownTimer(startCount, periodMillis, listener)

}
