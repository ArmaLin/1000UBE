package com.dyaco.spirit_commercial.workout;


import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SELECT_WORKOUT_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_CHARTS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_STATS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.WORKOUT_PAGE_TRACK;
import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.databinding.FragmentWorkoutGarminBinding;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class WorkoutGarminFragment extends BaseBindingFragment<FragmentWorkoutGarminBinding> {
    private AppStatusViewModel appStatusViewModel;
    private WorkoutViewModel workoutViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setFormulaUtil(new FormulaUtil());

        getBinding().setWorkoutViewModel(workoutViewModel);
        getBinding().setDeviceSetting(deviceSettingViewModel);
        getBinding().setIsUs(isUs);
        getBinding().setIsGGG(isGGG);
        onSelect();

//        new RxTimer().timer(9000, number -> {
//            Log.d("VVVVVVVVVV", "setGarmin: ");
//            getBinding().gsHeartRate.setShowProgress(false);
//            Log.d("VVVVVVVVVV", "onViewCreated: " + getBinding().gsHeartRate.getShowProgress());
//        });


        setGarmin();

        /**
         *  (1) HR:
         *  Max HR= 220-Age,
         *  Very Light Max HR * 60%,
         *  Light: Max HR * 70%,
         *  Moderate: Max HR * 80%,
         *  HARD: Max HR * 90%,
         *  Maximum: Max HR
         *  (2) Respiration Rate: Max Value 60
         *  (3) Body Battery: 100
         */
    }

    private void setGarmin() {

//        getBinding().gsHeartRate
        //app:progress="@{formulaUtil.setHrProgress(workoutViewModel.currentHeartRate)}"


        //Body Battery: 100
//        getBinding().gsBody.setProgressChangedCallback(num -> {
//            getBinding().tvBodyBatteryNum.setText(workoutViewModel.garminBodyBatteryLevel.get() <= 0 ? "--" :String.format(Locale.getDefault(), "%.0f", num * 100));
//            return null;
//        });

        getBinding().gsBody.setProgress(new FormulaUtil().setGarminBodyBatteryProgress(workoutViewModel.garminBodyBatteryLevel.get()));


        //呼吸強度 Respiration Rate: Max Value 60
//        getBinding().gsRespiration.setProgressChangedCallback(num -> {
//            //progress 1  >  0.9 * 60 = 54  ,max 60
////            getBinding().tvRespirationRateNum.setText(workoutViewModel.garminRespirationRate.get() <= 0 ? "--" : String.format(Locale.getDefault(), "%.0f", num * 60));
//         //   Log.d("GARMIN", "#####setGarmin: " + num +","+ String.format(Locale.getDefault(), "%.0f", num * 60) +","+ workoutViewModel.garminRespirationRate.get());
//            return null;
//        });

        getBinding().gsRespiration.setProgress(workoutViewModel.garminRespirationRate.get());
    }


    private void onSelect() {
        getBinding().btnStats.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_STATS));
        getBinding().btnCharts.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_CHARTS));
        getBinding().btnTrack.setOnClickListener(v -> LiveEventBus.get(SELECT_WORKOUT_PAGE).post(WORKOUT_PAGE_TRACK));
    }

//    public void setGarminData() {
//        float hr = (float) (workoutViewModel.currentHeartRate.get() * 0.005);
//        getBinding().gsHeartRate.setProgress(hr);
//
//
//        getBinding().tvGarminHeartRateNum.setText(String.valueOf(workoutViewModel.currentHeartRate.get()));
//    }

}