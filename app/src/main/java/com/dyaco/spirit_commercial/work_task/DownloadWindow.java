package com.dyaco.spirit_commercial.work_task;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.dyaco.spirit_commercial.databinding.WindowDownloadBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BaseWindow;

public class DownloadWindow extends BaseWindow<WindowDownloadBinding> {
    private final Context context;

    @SuppressLint("ClickableViewAccessibility")
    public DownloadWindow(Context context) {
        super(context, 50, 50, Gravity.TOP | Gravity.CENTER, 10, 10);
        getBinding().baseView.setOnTouchListener(touchListener);
        this.context = context;
    }

    public void setProgress(int progress) {
        if (getBinding() != null) {
            CommonUtils.iExc(() -> getBinding().circleProgress.setProgress(progress));
        }
    }


    private int viewX = 0;
    private int viewY = 0;
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    viewX = viewX + movedX;
                    viewY = viewY + movedY;
                    layoutParams.x = viewX;
                    layoutParams.y = viewY;
                    mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);
//                    update(viewX, viewY, -1, -1, true);
                    break;
                case MotionEvent.ACTION_UP:
                    getBinding().baseView.performClick();
                    break;
            }
            return true;
        }
    };
}
