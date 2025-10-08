package com.dyaco.spirit_commercial.support;

import android.os.SystemClock;
import android.view.View;

public abstract class SafeClickListener implements View.OnClickListener {

    private static final long DEFAULT_INTERVAL = 500; // 預設間隔時間為 1 秒
    private long lastClickTime = 0;
    private final long interval;

    // 使用預設間隔時間
    public SafeClickListener() {
        this.interval = DEFAULT_INTERVAL;
    }

    // 使用自定義間隔時間
    public SafeClickListener(long interval) {
        this.interval = interval;
    }

    @Override
    public final void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime;
            onSafeClick(v); // 安全點擊事件
        }
    }

    public abstract void onSafeClick(View v);
}