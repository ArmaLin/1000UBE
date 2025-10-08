package com.dyaco.spirit_commercial.egym;

import android.os.Handler;
import android.os.Looper;

public class HandlerTimer {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TimerListener listener;
    private long delayMillis;
    private boolean isRunning = false;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null && isRunning) {
                listener.onTick();
                handler.postDelayed(this, delayMillis);
            }
        }
    };

    // 🔹 建構子：允許設定間隔時間與監聽器
    public HandlerTimer(long delayMillis, TimerListener listener) {
        this.delayMillis = delayMillis;
        this.listener = listener;
    }

    // 🔹 開始計時
    public void start() {
        if (!isRunning) {
            isRunning = true;
            handler.post(runnable);
        }
    }

    // 🔹 停止計時
    public void stop() {
        isRunning = false;
        handler.removeCallbacks(runnable);
    }

    // 🔹 重新開始（先停止再開始）
    public void restart() {
        stop();
        start();
    }

    // 🔹 設置新的間隔時間
    public void setDelay(long delayMillis) {
        this.delayMillis = delayMillis;
        restart();
    }

    // 🔹 設置新的監聽器
    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    // 🔹 監聽器接口（讓外部實作）
    public interface TimerListener {
        void onTick();
    }
}
