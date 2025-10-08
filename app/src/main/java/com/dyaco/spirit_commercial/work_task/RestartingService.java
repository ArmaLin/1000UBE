package com.dyaco.spirit_commercial.work_task;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;

public class RestartingService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private FrameLayout view;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {

        int layout = R.layout.fragment_welcome ;

        view = (FrameLayout) LayoutInflater.from(this).inflate(layout, null);
        windowManager.addView(view, layoutParams);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        try {
            if (windowManager != null) {
                windowManager.removeView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}