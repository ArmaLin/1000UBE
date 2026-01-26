package com.dyaco.spirit_commercial.support

import android.os.SystemClock
import kotlinx.coroutines.*

object CoTimer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    interface AfterListener { fun onFinish() }
    interface EveryListener { fun onTick(elapsedMillis: Long) }
    interface RepeatListener { fun onTick(count: Int); fun onComplete() }
    interface CountDownListener { fun onTick(remaining: Int); fun onComplete() }


    @JvmStatic
    fun after(delayMillis: Long, listener: AfterListener): Job {
        require(delayMillis >= 0) { "delayMillis must be >= 0" }
        return scope.launch {
            delay(delayMillis)
            withContext(Dispatchers.Main.immediate) {
                listener.onFinish()
            }
        }
    }

    @JvmStatic
    fun every(periodMillis: Long, listener: EveryListener): Job {
        require(periodMillis > 0) { "periodMillis must be > 0" }
        return scope.launch {
            val base = SystemClock.elapsedRealtime()
            var emitted = 0L
            var nextAt = base

            while (isActive) {
                withContext(Dispatchers.Main.immediate) {
                    listener.onTick(emitted * periodMillis)
                }
                emitted++
                nextAt += periodMillis

                var now = SystemClock.elapsedRealtime()
                var delayMs = nextAt - now
                if (delayMs <= 0) {
                    val behind = -delayMs
                    val skip = behind / periodMillis + 1
                    emitted += skip
                    nextAt += periodMillis * skip
                    now = SystemClock.elapsedRealtime()
                    delayMs = nextAt - now
                }
                delay(delayMs.coerceAtLeast(0))
            }
        }
    }

    @JvmStatic
    fun repeat(
        times: Int,
        periodMillis: Long,
        listener: RepeatListener
    ): Job {
        require(times >= 0) { "times must be >= 0" }
        require(periodMillis > 0) { "periodMillis must be > 0" }

        return scope.launch {
            val base = SystemClock.elapsedRealtime()
            var nextAt = base

            for (i in 1..times) {
                withContext(Dispatchers.Main.immediate) { listener.onTick(i) }

                if (i < times) {
                    nextAt += periodMillis
                    var now = SystemClock.elapsedRealtime()
                    var delayMs = nextAt - now
                    if (delayMs <= 0) {
                        val behind = -delayMs
                        val skip = behind / periodMillis + 1
                        nextAt += periodMillis * skip
                        now = SystemClock.elapsedRealtime()
                        delayMs = nextAt - now
                    }
                    delay(delayMs.coerceAtLeast(0))
                }
            }

            withContext(Dispatchers.Main.immediate) { listener.onComplete() }
        }
    }

    @JvmStatic
    fun countDown(
        startCount: Int,
        periodMillis: Long,
        listener: CountDownListener
    ): Job {
        require(startCount >= 0) { "startCount must be >= 0" }
        require(periodMillis > 0) { "periodMillis must be > 0" }

        return scope.launch {
            val base = SystemClock.elapsedRealtime()
            var nextAt = base

            for (remaining in startCount downTo 0) {
                withContext(Dispatchers.Main.immediate) { listener.onTick(remaining) }

                if (remaining > 0) {
                    nextAt += periodMillis
                    var now = SystemClock.elapsedRealtime()
                    var delayMs = nextAt - now
                    if (delayMs <= 0) {
                        val behind = -delayMs
                        val skip = behind / periodMillis + 1
                        nextAt += periodMillis * skip
                        now = SystemClock.elapsedRealtime()
                        delayMs = nextAt - now
                    }
                    delay(delayMs.coerceAtLeast(0))
                }
            }

            withContext(Dispatchers.Main.immediate) { listener.onComplete() }
        }
    }

    @JvmStatic
    fun cancelJob(job: Job) {
        job.cancel()
    }


    class PausableTimer internal constructor(
        private val periodMillis: Long,
        private val listener: EveryListener
    ) {
        private var job: Job? = null
        private var base: Long = 0L
        private var cachedElapsed: Long = 0L

        @JvmOverloads
        fun start(reset: Boolean = true): PausableTimer {
            require(periodMillis > 0) { "periodMillis must be > 0" }

            if (reset) cachedElapsed = 0L
            job?.cancel()

            base = SystemClock.elapsedRealtime() - cachedElapsed

            job = scope.launch {
                while (isActive) {
                    val now = SystemClock.elapsedRealtime()
                    val elapsed = now - base
                    withContext(Dispatchers.Main.immediate) {
                        listener.onTick(elapsed)
                    }

                    val nextAt = base + ((elapsed / periodMillis) + 1) * periodMillis
                    val delayMs = (nextAt - SystemClock.elapsedRealtime()).coerceAtLeast(0)
                    delay(delayMs)
                }
            }
            return this
        }

        fun pause(): PausableTimer {
            job?.cancel()
            job = null
            cachedElapsed = SystemClock.elapsedRealtime() - base
            return this
        }

        fun resume(): PausableTimer {
            if (job == null) start(reset = false)
            return this
        }

        fun cancel(): PausableTimer {
            job?.cancel()
            job = null
            cachedElapsed = 0L
            base = 0L
            return this
        }

        fun getElapsed(): Long =
            if (job == null) cachedElapsed else SystemClock.elapsedRealtime() - base
    }

    @JvmStatic
    fun pausableEvery(
        periodMillis: Long,
        listener: EveryListener
    ): PausableTimer = PausableTimer(periodMillis, listener)


    class PausableCountDownTimer internal constructor(
        private val startCount: Int,
        private val periodMillis: Long,
        private val listener: CountDownListener
    ) {
        private var job: Job? = null
        @Volatile private var remainingCount: Int = startCount
        @Volatile private var isRunning: Boolean = false

        @JvmOverloads
        fun start(reset: Boolean = true): PausableCountDownTimer {
            require(startCount >= 0) { "startCount must be >= 0" }
            require(periodMillis > 0) { "periodMillis must be > 0" }

            if (isRunning && !reset) return this
            if (reset) {
                remainingCount = startCount
            }

            job?.cancel()
            isRunning = true

            var nextAt = SystemClock.elapsedRealtime()

            val launched = scope.launch {
                try {
                    for (i in remainingCount downTo 0) {
                        remainingCount = i

                        val delayMs = (nextAt - SystemClock.elapsedRealtime()).coerceAtLeast(0)
                        delay(delayMs)

                        withContext(Dispatchers.Main.immediate) {
                            listener.onTick(i)
                        }

                        if (i > 0) {
                            nextAt += periodMillis
                        }
                    }

                    withContext(Dispatchers.Main.immediate) {
                        listener.onComplete()
                    }
                } finally {
                    isRunning = false
                    val current = coroutineContext[Job]
                    if (job === current) job = null
                }
            }

            job = launched
            return this
        }

        fun pause(): PausableCountDownTimer {
            job?.cancel()
            job = null
            isRunning = false
            return this
        }

        fun resume(): PausableCountDownTimer {
            if (!isRunning && remainingCount > 0) {
                start(reset = false)
            }
            return this
        }

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

        fun getRemainingCount(): Int = remainingCount
    }

    @JvmStatic
    fun pausableCountDown(
        startCount: Int,
        periodMillis: Long,
        listener: CountDownListener
    ): PausableCountDownTimer = PausableCountDownTimer(startCount, periodMillis, listener)
}
