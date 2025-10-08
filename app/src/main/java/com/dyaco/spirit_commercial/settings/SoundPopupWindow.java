package com.dyaco.spirit_commercial.settings;

import static android.content.Context.AUDIO_SERVICE;
import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.dyaco.spirit_commercial.databinding.WindowSoundBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

public class SoundPopupWindow extends BasePopupWindow<WindowSoundBinding> {
    int maxVolume;
    int minVolume;
    VerticalRangeSeekBar soundBar;
    private final AudioManager audioManager;

    public SoundPopupWindow(Context context) {
        super(context, 500, 0, 187, GENERAL.TRANSLATION_X, true, true, true, true);

        audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        initSoundBar();

        initView();

    }

    private void initSoundBar() {

        soundBar = getBinding().soundSeekBar;

        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("音量", "當前音量: " + currentVolume);
        soundBar.setProgress(currentVolume);
        getBinding().cbMute.setChecked(currentVolume > 0);

        soundBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

//                int newVolume = (int) leftValue;
//                Log.d("音量", "調整的音量: " + newVolume);
//                audioManager.setStreamVolume(STREAM_MUSIC, newVolume, 0);

                int newVolume = (int)Math.ceil(leftValue);
                audioManager.setStreamVolume(STREAM_MUSIC, newVolume, 0);
                if (audioManager.getStreamVolume(STREAM_MUSIC) < newVolume) {
                    //音量過大警示
                    audioManager.setStreamVolume(STREAM_MUSIC, newVolume, FLAG_SHOW_UI);
                }

                if (getBinding() != null) getBinding().cbMute.setChecked(newVolume > 0);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

    }

    private void initView() {
        new CommonUtils().addAutoClick(getBinding().btnVolumePlus);
        new CommonUtils().addAutoClick(getBinding().btnVolumeMinus);
        getBinding().btnVolumePlus.setOnClickListener(v -> {
            int x = (int) soundBar.getLeftSeekBar().getProgress() + 1;
            if (x > 15) return;
            soundBar.setProgress(x);
        });

        getBinding().btnVolumeMinus.setOnClickListener(v -> {
            int x = ((int) soundBar.getLeftSeekBar().getProgress()) - 1;
            if (x < 0) return;
            soundBar.setProgress(x);
        });

        getBinding().cbMute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                mv = (int) soundBar.getLeftSeekBar().getProgress();
                soundBar.setProgress(0.0f);
            } else {
                soundBar.setProgress(mv > 0 ? mv : 1);
            }
        });
    }

    int mv = 0;

//    public float getVolumeFromPresent(float value) {
//        return (value * 0.01f) * 15f;
//    }
//
//    private int getVolumeToPresent(float value) {
//        return (Math.round((value / 15) * 100));
//    }
}