package com.dyaco.spirit_commercial.support.interaction

import android.view.MotionEvent
import android.view.View

object TouchClickUtil {
    /**
     *  ACTION_DOWN 時 觸發 view 的 press, 和 製造 alpha
     *  ACTION_UP 時觸發 view 的 release 和 Click  和 解除 alpha
     *  手指移開view時，不會觸發 Click
     * Java 呼叫範例：
     * 1. 無需 alpha 處理:
     *    view.setOnTouchListener(TouchClickUtil.createTouchListener());
     *
     * 2. 需要 alpha 處理 (例如點擊時透明度 0.7f):
     *    view.setOnTouchListener(TouchClickUtil.createTouchListener(0.7f));
     */
    @JvmOverloads
    @JvmStatic
    fun createTouchListener(pressAlpha: Float? = null): View.OnTouchListener {
        var isFingerClick = false
        return View.OnTouchListener { view, motionEvent ->
            val width = view.width
            val height = view.height
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isFingerClick = true
                    if (pressAlpha != null) {
                        view.alpha = pressAlpha
                    }
                    view.isPressed = true // 手動設定 pressed 狀態
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = motionEvent.x
                    val y = motionEvent.y
                    // 當手指超出 view 邊界時取消 pressed 狀態
                    if (x < 0 || x > width || y < 0 || y > height) {
                        if (pressAlpha != null) {
                            view.alpha = 1.0f
                        }
                        view.isPressed = false
                        isFingerClick = false
                    } else {
                        view.isPressed = true
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (pressAlpha != null) {
                        view.alpha = 1.0f
                    }
                    view.isPressed = false
                    if (isFingerClick) {
                        view.performClick() // 執行點擊動作
                    }
                    isFingerClick = false
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (pressAlpha != null) {
                        view.alpha = 1.0f
                    }
                    view.isPressed = false
                    isFingerClick = false
                    true
                }
                else -> false
            }
        }
    }
}