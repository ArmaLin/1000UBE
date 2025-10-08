package com.dyaco.spirit_commercial.support.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class LongClickButton extends androidx.appcompat.widget.AppCompatButton {

    private LongClickRepeatListener m_longClickRepeatListener;
    private ButtonHandler m_handler;
    private long m_intervalTime = 250;             // 間隔時間(ms)

    public LongClickButton(Context context) {
        super(context);
        init();
    }

    public LongClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LongClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化監聽
     */
    private void init() {
        m_handler = new ButtonHandler(this);

        setOnLongClickListener(v -> {
            new Thread(new LongClickThread()).start();
            return true;
        });
    }

    /**
     * 長按時，該執行緒將會啟動
     */
    private class LongClickThread implements Runnable {
        private int num;

        @Override
        public void run() {
            while (LongClickButton.this.isPressed()) {
                num++;
                if (num % 5 == 0) {
                    m_handler.sendEmptyMessage(1);
                }
                SystemClock.sleep(m_intervalTime / 5);
            }
        }
    }

    /**
     * 通過handler，使監聽的事件響應在主執行緒中進行
     */
    private static class ButtonHandler extends Handler {
        private final WeakReference<LongClickButton> m_weakReference;

        ButtonHandler(LongClickButton button) {
            m_weakReference = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            LongClickButton longClickButton = m_weakReference.get();
            if (longClickButton != null && longClickButton.m_longClickRepeatListener != null) {
                longClickButton.m_longClickRepeatListener.repeatAction();
            }
        }
    }

    /**
     * 設定長按連續響應的監聽和間隔時間，長按時將會多次呼叫該介面中的方法直到長按結束
     *
     * @param listener     監聽
     * @param intervalTime 間隔時間（ms）
     */
    public void setLongClickRepeatListener(LongClickRepeatListener listener, long intervalTime) {
        this.m_longClickRepeatListener = listener;
        this.m_intervalTime = intervalTime;
    }

    /**
     * 設定長按連續響應的監聽（使用預設間隔時間100ms），長按時將會多次呼叫該介面中的方法直到長按結束
     *
     * @param listener 監聽
     */
    public void setLongClickRepeatListener(LongClickRepeatListener listener) {
        setLongClickRepeatListener(listener, 250);
    }

    public interface LongClickRepeatListener {
        void repeatAction();
    }
}