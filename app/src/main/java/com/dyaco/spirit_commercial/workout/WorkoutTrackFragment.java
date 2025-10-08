package com.dyaco.spirit_commercial.workout;


import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_CHARTS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_GARMIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_STATS;
import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentWorkoutTrackBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.LevelImageView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class WorkoutTrackFragment extends BaseBindingFragment<FragmentWorkoutTrackBinding> {
    double laps;
    private ObjectAnimator objectAnimator;
    private long currentPlayTime;
    int speed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutViewModel workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        DeviceSettingViewModel deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setWorkoutViewModel(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setComm(new CommonUtils());
        getBinding().setIsUs(isUs);
        getBinding().setIsGGG(isGGG);
        initView();
        initEvent();

        onSelect();

        initTrack();
    }

    public void stopAnimation() {
        currentPlayTime = objectAnimator.getCurrentPlayTime();
        objectAnimator.cancel();
    }

    public void startAnimation() {
        if (objectAnimator != null) {
            objectAnimator.start();
            objectAnimator.setCurrentPlayTime(currentPlayTime);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (objectAnimator != null) objectAnimator.cancel();

        objectAnimator = null;
    }

    /**
     * 變更速度
     *
     * @param milliSecond 跑完一圈需要的 milliSecond
     */
    public void updateLapSpeed(double milliSecond, int unit) {
        try {
            final int METRIC = 400;
            final int IMPERIAL = 1320;
            int playground = unit == DeviceIntDef.IMPERIAL ? IMPERIAL : METRIC;
            laps = Math.floor(milliSecond / playground);
            double remainder = (milliSecond % playground);
            int schedule = (int) Math.ceil((remainder / ((double) playground / 25)));
            parent.runOnUiThread(() -> getBinding().tvLapCount.setText(String.valueOf(Math.round(laps))));
            objectAnimator.setIntValues(schedule, schedule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTrack() {

        //用wrap_content的話 圖尺寸會變
        speed = 1130000;
        LevelImageView ivLap = getBinding().ivLapAnimation;
        ivLap.setImageResource(R.drawable.track_animation);
        objectAnimator = ObjectAnimator.ofInt(ivLap, "imageLevel", 1, 25);
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.setDuration(speed);
        objectAnimator.start();
    }

    private void initEvent() {

    }

    private void initView() {

    }

    private void onSelect() {
        getBinding().btnStats.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_STATS));
        getBinding().btnCharts.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_CHARTS));
        getBinding().btnGarmin.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_GARMIN));
    }


}


/**
 *
 package com.dyaco.spirit_commercial.workout;

 import static com.dyaco.spirit_commercial.MainActivity.isUs;
 import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
 import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_CHARTS;
 import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_GARMIN;
 import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_STATS;
 import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;

 import android.animation.ObjectAnimator;
 import android.os.Bundle;
 import android.view.View;
 import android.view.animation.LinearInterpolator;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.lifecycle.ViewModelProvider;

 import com.dyaco.spirit_commercial.R;
 import com.dyaco.spirit_commercial.databinding.FragmentWorkoutTrackBinding;
 import com.dyaco.spirit_commercial.support.CommonUtils;
 import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
 import com.dyaco.spirit_commercial.support.custom_view.LevelImageView;
 import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
 import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
 import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
 import com.jeremyliao.liveeventbus.LiveEventBus;

 public class WorkoutTrackFragment extends BaseBindingFragment<FragmentWorkoutTrackBinding> {

 private ObjectAnimator objectAnimator;
 private long currentPlayTime;

 @Override
 public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
 super.onViewCreated(view, savedInstanceState);

 WorkoutViewModel workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
 DeviceSettingViewModel deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

 getBinding().setWorkoutViewModel(workoutViewModel);
 getBinding().setDeviceSetting(deviceSettingViewModel);
 getBinding().setComm(new CommonUtils());
 getBinding().setIsUs(isUs);
 getBinding().setIsGGG(isGGG);
 initView();
 initEvent();
 onSelect();
 initTrack();
 }

 public void stopAnimation() {
 if (objectAnimator != null) {
 currentPlayTime = objectAnimator.getCurrentPlayTime();
 objectAnimator.cancel();
 }
 }

 public void startAnimation() {
 if (objectAnimator != null) {
 objectAnimator.start();
 objectAnimator.setCurrentPlayTime(currentPlayTime);
 }
 }

 @Override
 public void onDestroy() {
 super.onDestroy();
 if (objectAnimator != null) {
 objectAnimator.cancel();
 objectAnimator = null;
 }
 }
public void updateLapSpeed(double milliSecond, int unit) {
    final int METRIC = 400;
    final int IMPERIAL = 1320;
    int playground = (unit == DeviceIntDef.IMPERIAL ? IMPERIAL : METRIC);

    // 計算完成的圈數與當前圈進度（對應 drawable level 範圍 1 ~ 25）
    double laps = Math.floor(milliSecond / playground);
    double remainder = milliSecond % playground;
    int frame = (int) Math.ceil(remainder / ((double) playground / 25));

    requireActivity().runOnUiThread(() -> {
        getBinding().tvLapCount.setText(String.valueOf((int) Math.round(laps)));
    });

    // 調整動畫目前播放時間，確保動畫畫面與 lap 進度同步
    if (objectAnimator != null) {
        long newPlayTime = frame * (objectAnimator.getDuration() / 25);
        objectAnimator.setCurrentPlayTime(newPlayTime);
    }
}

private void initTrack() {
    LevelImageView ivLap = getBinding().ivLapAnimation;
    ivLap.setImageResource(R.drawable.track_animation);

    // 建立由 1 到 25 的連續動畫，設定為無限循環
    objectAnimator = ObjectAnimator.ofInt(ivLap, "imageLevel", 1, 25);
    objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    objectAnimator.setInterpolator(new LinearInterpolator());
    objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    objectAnimator.setDuration(1130000); // 原設定的動畫持續時間
    objectAnimator.start();
}

private void initEvent() {
    // 其他事件初始化放這邊
}

private void initView() {
    // 其他 view 初始化放這邊
}

private void onSelect() {
    getBinding().btnStats.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_STATS));
    getBinding().btnCharts.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_CHARTS));
    getBinding().btnGarmin.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_GARMIN));
}
}

 *
 */