package com.dyaco.spirit_commercial.support.interaction

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.WeakHashMap

/**
 * 按鈕長按工具
 */
object LongClickUtil {

    private const val DEFAULT_DEBOUNCE_TIME = 100L     // 單按間隔100ms
    private const val DEFAULT_REPEAT_INTERVAL = 200L     // 長按後間隔200ms執行
    private const val DEFAULT_LONG_PRESS_THRESHOLD = 500L  // 長按500ms開始執行時間

    // 使用 WeakHashMap 管理 View 與 Job，避免記憶體洩漏
    private val jobMap: MutableMap<View, Job> =
        Collections.synchronizedMap(WeakHashMap())

    /**
     * App在背景時不會被調用
     */
    @JvmStatic
    @JvmOverloads
    fun attach(
        view: View,
        repeatInterval: Long = DEFAULT_REPEAT_INTERVAL,
        longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD
    ): LiveData<Unit> {
        return view.longPressFlow(repeatInterval, longPressThreshold)
            .asLiveData(context = Dispatchers.Main.immediate)
    }

    /**
     * 在背景執行，使用 lambda observer
     */
    @JvmStatic
    @JvmOverloads
    fun attachF(
        view: View,
        repeatInterval: Long = DEFAULT_REPEAT_INTERVAL,
        longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD,
        observer: (Unit) -> Unit
    ): Job {
        // 先取消舊的 Job，避免重複綁定
        jobMap[view]?.cancel()

        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        val job = scope.launch {
            view.longPressFlow(repeatInterval, longPressThreshold).collect {
                observer(it)
            }
        }
        jobMap[view] = job

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}

            override fun onViewDetachedFromWindow(v: View) {
                jobMap.remove(view)?.cancel()
                view.removeOnAttachStateChangeListener(this)
                Log.d("ClickUtils", "自動取消: ${view.id}")
            }
        })

        return job
    }

    @JvmStatic
    @JvmOverloads
    fun attachF(
        view: View,
        repeatInterval: Long = DEFAULT_REPEAT_INTERVAL,
        longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD,
        observer: ClickObserver
    ): Job {
        return attachF(view, repeatInterval, longPressThreshold) { observer.onClick() }
    }

    /**
     * Java 呼叫友善的 Callback 介面（SAM 介面）
     */
    @FunctionalInterface
    fun interface ClickObserver {
        fun onClick()
    }

    /** 允許手動取消 Job，防止記憶體洩漏 */
    @JvmStatic
    fun detachF(view: View) {
        jobMap.remove(view)?.cancel()
    }

    @OptIn(FlowPreview::class)
    fun View.longPressFlow(
        repeatInterval: Long = DEFAULT_REPEAT_INTERVAL,
        longPressThreshold: Long = DEFAULT_LONG_PRESS_THRESHOLD
    ): Flow<Unit> = callbackFlow {
        var isLongPressTriggered = false
        var isWaitingForLongPress = false
        val listenerViewRef = WeakReference(this@longPressFlow)

        fun getView(): View? =
            listenerViewRef.get()?.takeIf { it.isAttachedToWindow && it.visibility == View.VISIBLE }

        val repeatAction = object : Runnable {
            override fun run() {
                val view = getView() ?: return
                if (view.isPressed) {
                    trySend(Unit)
                    view.postDelayed(this, repeatInterval)
                }
            }
        }

        val longPressStartAction = object : Runnable {
            override fun run() {
                val view = getView() ?: return
                if (view.isPressed && isWaitingForLongPress) {
                    isLongPressTriggered = true
                    isWaitingForLongPress = false
                    trySend(Unit)
                    view.postDelayed(repeatAction, repeatInterval)
                }
            }
        }

        val listener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.isPressed = true
                    isLongPressTriggered = false
                    isWaitingForLongPress = true

                    trySend(Unit)

                    v.removeCallbacks(repeatAction)
                    v.removeCallbacks(longPressStartAction)

                    // 使用者可以客製化長按起始時間
                    v.postDelayed(longPressStartAction, longPressThreshold)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    v.removeCallbacks(repeatAction)
                    v.removeCallbacks(longPressStartAction)
                    isWaitingForLongPress = false
                    if (!isLongPressTriggered) v.performClick()
                }
            }
            true
        }

        // 注意：此行為會覆蓋原有的 OnTouchListener
        setOnTouchListener(listener)

        awaitClose {
            getView()?.let { v ->
                v.removeCallbacks(longPressStartAction)
                v.removeCallbacks(repeatAction)
                v.setOnTouchListener(null)
            }
            listenerViewRef.clear()
        }
    }
        .debounce(DEFAULT_DEBOUNCE_TIME)
        .flowOn(Dispatchers.Main)
}
