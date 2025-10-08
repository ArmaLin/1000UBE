package com.dyaco.spirit_commercial.maintenance_mode;


import static com.dyaco.spirit_commercial.MainActivity.mRetailVideoPath;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.dyaco.spirit_commercial.databinding.WindowRetailVideoBinding;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RetailVideoWindow extends BasePopupWindow<WindowRetailVideoBinding> {

    private static final String TAG = "RetailVideoWindow";
    private final List<String> videoPathList = new ArrayList<>();
    private int currentVideoIndex = 0;

    public RetailVideoWindow(Context context) {
        super(context, 1000, 0, 0, GENERAL.FADE, false, false, true, false);
        playVideo();
    }

    @Override
    public void dismiss() {
        if (getBinding() == null) {
            super.dismiss();
            return;
        }
        // 2️⃣ 停止播放
        if (getBinding().videoView.isPlaying()) {
            getBinding().videoView.stopPlayback();
        }


        // 1️⃣ 黑幕先蓋住（避免閃現）
        getBinding().videoOverlay.animate()
                .alpha(1f)
                .setDuration(1000)
                .withEndAction(() -> {
                    if (getBinding() != null) {
                        getBinding().videoView.setVisibility(View.INVISIBLE);
                    }
                    super.dismiss();
                })
                .start();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void playVideo() {
        initVideoList();
        getBinding().videoOverlay.setAlpha(0f);

        if (videoPathList.isEmpty()) {
            initNoRetail();
            return;
        }

        playNextVideo();

        getBinding().videoView.setOnCompletionListener(mp -> {
            currentVideoIndex = (currentVideoIndex + 1) % videoPathList.size();
            playNextVideo();
        });

        getBinding().videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            Log.e(TAG, "❌ 播放錯誤，影片檔案: " + videoPathList.get(currentVideoIndex));

            currentVideoIndex = (currentVideoIndex + 1) % videoPathList.size();
            playNextVideo();
            return true;
        });

        getBinding().videoView.setOnTouchListener((view, motionEvent) -> {
            dismiss();
            return false;
        });
    }

    private void playNextVideo() {
        if (videoPathList.isEmpty()) {
            initNoRetail();
            return;
        }

        String path = videoPathList.get(currentVideoIndex);
        File file = new File(path);

        if (!file.exists()) {
            Log.e(TAG, "⚠️ 播放失敗，影片檔案不存在: " + path);
            currentVideoIndex = (currentVideoIndex + 1) % videoPathList.size();
            playNextVideo();
            return;
        }

        String fileName = file.getName();
        Log.d(TAG, "▶️ 正在播放: " + fileName);


// 淡出動畫
        if (getBinding() == null) return;

// 1️⃣ 切換並播放影片（立刻開始）
        getBinding().videoView.setVideoPath(path);
        getBinding().videoView.start();

// 2️⃣ 黑幕淡入（0 → 1）
        getBinding().videoOverlay.setAlpha(0f); // 確保一開始是透明的
        getBinding().videoOverlay.animate()
                .alpha(0.8f)
                .setDuration(500) // 黑畫面淡入時間
                .withEndAction(() -> {

                    // 3️⃣ 黑幕淡出（1 → 0）
                    getBinding().videoOverlay.animate()
                            .alpha(0f)
                            .setDuration(2000) // 黑畫面淡出時間
                            .start();
                })
                .start();

    }


    private boolean isOne = true;//是否要輪播, true > 只播一個
    private void initVideoList() {
        videoPathList.clear();

        if (isOne) {
            File file = new File(mRetailVideoPath);
            Log.d(TAG, "檔案路徑: " + file.getAbsolutePath());
            Log.d(TAG, "檔案存在嗎: " + file.exists());

            if (file.exists()) {
                videoPathList.add(file.getAbsolutePath());
                Log.d(TAG, "✅ 加入影片: " + file.getAbsolutePath());
            } else {
                Log.w(TAG, "❌ 找不到影片: " + file.getAbsolutePath());
            }
        } else {
            for (int i = 0; i < 100; i++) {
                String fileName = String.format(Locale.getDefault(), "/CoreStar/Dyaco/Spirit/retail_%02d.mp4", i);
                addVideoIfExists(fileName);
            }
        }

        Log.d(TAG, "總共載入: " + videoPathList.size() +"部影片");

        currentVideoIndex = 0;
    }

    private void addVideoIfExists(String relativePath) {
        File file = new File(Environment.getExternalStorageDirectory(), relativePath);
        if (file.exists()) {
            videoPathList.add(file.getAbsolutePath());
        } else {
            Log.w(TAG, "影片不存在: " + file.getAbsolutePath());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initNoRetail() {
        getBinding().videoView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "沒有任何影片可播放");

        getBinding().ttt.setOnTouchListener((view, motionEvent) -> {
            dismiss();
            return false;
        });

        dismiss();
    }
}


//import static com.dyaco.spirit_commercial.MainActivity.mRetailVideoPath;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//
//import com.dyaco.spirit_commercial.databinding.WindowRetailVideoBinding;
//import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
//import com.dyaco.spirit_commercial.support.intdef.GENERAL;
//
//public class RetailVideoWindow extends BasePopupWindow<WindowRetailVideoBinding> {
//    private static final String TAG = "RetailVideoWindow";
//    public RetailVideoWindow(Context context) {
//        super(context, 1000, 0, 0, GENERAL.FADE, false, false, true, false);
//
//
//        playVideo();
//
//    }
//
//    @Override
//    public void dismiss() {
//        if (getBinding() == null) {
//            super.dismiss();
//            return;
//        }
//        // 2️⃣ 停止播放
//        if (getBinding().videoView.isPlaying()) {
//            getBinding().videoView.stopPlayback();
//        }
//
//
//        // 1️⃣ 黑幕先蓋住（避免閃現）
//        getBinding().videoOverlay.animate()
//                .alpha(1f)
//                .setDuration(1000)
//                .withEndAction(() -> {
//                    if (getBinding() != null) {
//                        getBinding().videoView.setVisibility(View.INVISIBLE);
//                    }
//                    super.dismiss();
//                })
//                .start();
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void playVideo() {
//
//        getBinding().videoOverlay.setAlpha(0f);
//
//        getBinding().videoView.setVideoPath(mRetailVideoPath);
//        getBinding().videoView.start();
//        getBinding().videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
//
//        getBinding().videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
//            Log.d(TAG, "onError: " +what +","+ extra);
//
//                initNoRetail();
//
//            return true; //false 會彈出警告視窗
//        });
//
//        getBinding().videoView.setOnTouchListener((view, motionEvent) -> {
//            dismiss();
//            return false;
//        });
//
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initNoRetail() {
//        getBinding().videoView.setVisibility(View.INVISIBLE);
//        Log.d(TAG, "沒影片: ");
//
//        getBinding().ttt.setOnTouchListener((view, motionEvent) -> {
//            dismiss();
//            return false;
//        });
//
//        dismiss();
//    }
//
//}
