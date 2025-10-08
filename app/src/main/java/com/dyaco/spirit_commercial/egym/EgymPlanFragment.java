package com.dyaco.spirit_commercial.egym;

import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.egym.EgymUtil.EGYM_MACHINE_TYPE;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CE1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CR1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CT1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CU1000ENT;
import static com.dyaco.spirit_commercial.support.CommonUtils.chkDuration;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatMsToM;
import static com.dyaco.spirit_commercial.support.CommonUtils.formatS;
import static com.dyaco.spirit_commercial.support.CommonUtils.setAutoSizeText;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitD;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getEgymUnitS;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getInclinePercentInChart2;
import static com.dyaco.spirit_commercial.support.FormulaUtil.getRpmPercentInChart;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_PROGRAM;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentEgymPlanBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.interaction.DebounceClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.EgymDataViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EgymPlanFragment extends BaseBindingFragment<FragmentEgymPlanBinding> {
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private EgymDataViewModel egymDataViewModel;
    private MainActivity mainActivity;

    int totalPlans = 0;


    int egymUnit;

    private EgymTrainingPlans.TrainerDTO trainerDTO;

    public EgymPlanFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) requireActivity();


        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
        egymDataViewModel = new ViewModelProvider(requireActivity()).get(EgymDataViewModel.class);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setIsTreadmill(isTreadmill);

        egymUnit = egymDataViewModel.unitSystem.get();


        int bType;
        bType = START_THIS_PROGRAM;
        appStatusViewModel.changeMainButtonType(bType);


        totalPlans = Optional.ofNullable(egymDataViewModel)
                .map(viewModel -> viewModel.egymTrainingPlansData)
                .map(LiveData::getValue)
                .map(EgymTrainingPlans::getTrainer)
                .map(List::size) // 如果 Trainer 不為 null，返回其 size
                .orElse(0);

        totalPlans -= 1;

        initEvent();

        initView();


        //   new Handler(Looper.getMainLooper()).postDelayed(this::initData, 200);


//        egymDataViewModel.egymTrainingPlansData.observe(getViewLifecycleOwner(), data -> {
//            // 更新 UI
//            Log.d("@@@@@@@@@FFFF", "1111111: ");
//            initData();
//        });

        Looper.myQueue().addIdleHandler(() -> {
            initData();
            return false;
        });


    }

    private void initWorkoutData(int workoutTime) {
        workoutViewModel.selProgram = ProgramsEnum.EGYM;
        workoutViewModel.selWorkoutTime.set(workoutTime);//時間18分鐘
    }

    private void initChart() {
        // 設定範例數值
//        float[] speed = {0.1f, 0.1f, 0.6f, 0.3f};//藍色 << 會被覆蓋
//        float[] incline = {0.9f, 0.9f, 0.2f, 0.2f};//紫色
//        float[] heartRate = {0.0f, 0.1f, 0.0f, 0.9f};
//        float[] barWidths = {0.3f, 0.2f, 0.3f, 0.2f}; // 每個柱子的寬度比例

//        if (intervalsDTOList == null) {
//            getBinding().tvNoChart.setVisibility(View.VISIBLE);
//            return;
//        }
//        getBinding().tvNoChart.setVisibility(View.INVISIBLE);


        getBinding().customBarChartView.setSpeedBarColors(ContextCompat.getColor(requireActivity(), R.color.color1396ef), ContextCompat.getColor(requireActivity(), R.color.color1396ef_20));
        getBinding().customBarChartView.setInclineBarColors(ContextCompat.getColor(requireActivity(), R.color.colorCd5bff), ContextCompat.getColor(requireActivity(), R.color.colorCd5bff_20));
        getBinding().customBarChartView.setHeartRateBarColors(ContextCompat.getColor(requireActivity(), R.color.colorFF715e), ContextCompat.getColor(requireActivity(), R.color.colorFF715e_20));


        int totalSets = intervalsDTOList.size(); // set 數量
        float[] speed = new float[totalSets];//藍色 << 會被覆蓋
        float[] incline = new float[totalSets];//紫色
        float[] heartRate = new float[totalSets];
        float[] barWidths = new float[totalSets]; // 每個柱子的寬度比例

        double sum = 0;
        double[] time = new double[totalSets];
        for (int x = 0; x < totalSets; x++) {
//            time[x] = intervalsDTOList.get(x).getDuration();
            time[x] = chkDuration(intervalsDTOList.get(x).getDuration());
            sum += time[x];
        }


        for (int i = 0; i < totalSets; i++) {
            float speedValue;
            if (MODE == ModeEnum.CT1000ENT) {
                speedValue = intervalsDTOList.get(i).getSpeed() != null ? intervalsDTOList.get(i).getSpeed().floatValue() : 0;
            } else {
                speedValue = intervalsDTOList.get(i).getResistance() != null ? intervalsDTOList.get(i).getResistance().floatValue() : 0;
            }

            if (speedValue > 0) {
                if (MODE == ModeEnum.CT1000ENT) {
                    if (egymDataViewModel.unitSystem.get() == METRIC) {
                        speedValue = FormulaUtil.getKmPercentInChart(speedValue);
                    } else {
                        speedValue = FormulaUtil.getMiPercentInChart(speedValue);
                    }
                } else {
                    speedValue = FormulaUtil.getLevelPercentInChart(speedValue);
                }
            }
            speed[i] = speedValue;

            if (MODE == ModeEnum.CT1000ENT) {
                incline[i] = intervalsDTOList.get(i).getIncline() != null ? getInclinePercentInChart2(intervalsDTOList.get(i).getIncline()) : 0;
            } else if (MODE == CE1000ENT) {
                incline[i] = intervalsDTOList.get(i).getStepsPerMinute() != null ? getRpmPercentInChart(intervalsDTOList.get(i).getStepsPerMinute()) : 0;
            } else {
                incline[i] = intervalsDTOList.get(i).getRotations() != null ? getRpmPercentInChart(intervalsDTOList.get(i).getRotations()) : 0;
            }


            heartRate[i] = intervalsDTOList.get(i).getHeartRate() != null ? FormulaUtil.getHrPercentInChart(intervalsDTOList.get(i).getHeartRate()) : 0;

//            barWidths[i] = (float) ((time[i] / sum) * 100 * 0.01);
//            for (int c = 0; c < time.length; c++) {
//                barWidths[c] = (float) ((time[c] / sum) * 100 * 0.01);
//                if (Float.isNaN(barWidths[c])) {
//                    barWidths[c] = 1.0f; // 若計算結果有 NaN，則回傳 [1.0]
//                }
//            }


            float minThreshold = 0.001f;
            float normalizedMin = 0.01f;
            float totalExcess = 0f;
            int countToDistribute = 0;

// Step 1: Normalize time to percentages
            for (int i1 = 0; i1 < time.length; i1++) {
                barWidths[i1] = (float) ((time[i1] / sum) * 100 * 0.01);
                if (Float.isNaN(barWidths[i1])) {
                    barWidths[i1] = 1.0f;
                }
            }

// Step 2: Handle too small values
            for (int i2 = 0; i2 < barWidths.length; i2++) {
                if (barWidths[i2] < minThreshold) {
                    totalExcess += (normalizedMin - barWidths[i2]);
                    barWidths[i2] = normalizedMin;
                } else {
                    countToDistribute++;
                }
            }

// Step 3: Subtract excess from larger values
            if (countToDistribute > 0 && totalExcess > 0f) {
                float deduction = totalExcess / countToDistribute;
                for (int i3 = 0; i3 < barWidths.length; i3++) {
                    if (barWidths[i3] >= minThreshold + deduction) {
                        barWidths[i3] -= deduction;
                    }
                }
            }



        }


//        Log.d("NNNNNAAAAA", "initChart: " + Arrays.toString(speed));
//        Log.d("NNNNNAAAAA", "initChart: " + Arrays.toString(incline));
//        Log.d("NNNNNAAAAA", "initChart: " + Arrays.toString(heartRate));
        Log.d("NNNNNAAAAA", "initChart: " +barWidths.length +", "+ Arrays.toString(barWidths));

        getBinding().customBarChartView.setValues(speed, incline, heartRate, barWidths);

    }


    List<EgymTrainingPlans.TrainerDTO.IntervalsDTO> intervalsDTOList;

    private void initData() {

        int position;
        if (egymDataViewModel.currentPlanNum.get() <= totalPlans) {
            position = egymDataViewModel.currentPlanNum.get();
        } else {
            position = 0;
        }

//        Log.d("EGEGEGEGEGE", "position: " + position);
//        Log.d("EGEGEGEGEGE", "egymTrainingPlansData: " + egymDataViewModel.egymTrainingPlansData.getValue());

        trainerDTO = Optional.ofNullable(egymDataViewModel)
                .map(viewModel -> viewModel.egymTrainingPlansData)
                .map(LiveData::getValue)
                .map(EgymTrainingPlans::getTrainer)
                .filter(trainers -> position >= 0 && position < trainers.size())
                .map(trainers -> trainers.get(position))
                .orElse(null);

        setAutoSizeText(getBinding().tvSessionName, 32, 16);


        if (trainerDTO != null && trainerDTO.getIntervals() != null) {
            intervalsDTOList = trainerDTO.getIntervals();
        }

        if (egymDataViewModel != null && trainerDTO != null) {
            egymDataViewModel.selTrainer = trainerDTO;

            if (MODE == CT1000ENT) {
                workoutViewModel.egymTargetSpeed.set(getEgymUnitS(egymUnit, egymDataViewModel.selTrainer.getIntervals().get(0).getSpeed()));
                workoutViewModel.egymTargetIncline.set(formatS(egymDataViewModel.selTrainer.getIntervals().get(0).getIncline()));
            } else if (MODE == CE1000ENT) {

                if (egymDataViewModel.selTrainer.getIntervals().get(0).getResistance() != null) {
                    workoutViewModel.egymTargetSpeed.set(String.valueOf(egymDataViewModel.selTrainer.getIntervals().get(0).getResistance()));
                } else {
                    workoutViewModel.egymTargetSpeed.set(E_BLANK);
                }

                if (egymDataViewModel.selTrainer.getIntervals().get(0).getStepsPerMinute() != null) {
                    workoutViewModel.egymTargetIncline.set(formatS(Double.valueOf(egymDataViewModel.selTrainer.getIntervals().get(0).getStepsPerMinute())));
                } else {
                    workoutViewModel.egymTargetIncline.set(E_BLANK);
                }
            } else {
                if (egymDataViewModel.selTrainer.getIntervals().get(0).getResistance() != null) {
                    workoutViewModel.egymTargetSpeed.set(String.valueOf(egymDataViewModel.selTrainer.getIntervals().get(0).getResistance()));
                } else {
                    workoutViewModel.egymTargetSpeed.set(E_BLANK);
                }

                if (egymDataViewModel.selTrainer.getIntervals().get(0).getRotations() != null) {
                    workoutViewModel.egymTargetIncline.set(formatS(Double.valueOf(egymDataViewModel.selTrainer.getIntervals().get(0).getRotations())));
                } else {
                    workoutViewModel.egymTargetIncline.set(E_BLANK);
                }
            }

            workoutViewModel.egymTargetDistance.set(
                    getEgymUnitD(egymUnit, egymDataViewModel.selTrainer.getIntervals()
                            .get(0).getDistance()));


            workoutViewModel.egymTargetHeartRate.set(Optional.ofNullable(
                    egymDataViewModel.selTrainer.getIntervals().get(0).getHeartRate()
            ).map(String::valueOf).orElse(E_BLANK));
        }

//        setTextWithFade(getBinding().tvSessionName,trainerDTO.getSessionName());
//        setTextWithFade(getBinding().tvCoachText,String.format("%s %s", trainerDTO.getAuthor().getLastName(), trainerDTO.getAuthor().getFirstName()));
//        setTextWithFade(getBinding().tvTotalSetsText,String.format(Locale.getDefault(), "%d %s", trainerDTO.getIntervals().size(), requireActivity().getString(R.string.sets)));

        getBinding().tvExerciseName.setText(String.format("%s %s", EGYM_MACHINE_TYPE, requireActivity().getString(R.string.Activity)));

        Log.d("KKKAAAAAA", "initData: " + trainerDTO.getSessionName());
        getBinding().tvSessionName.setText(trainerDTO.getSessionName());
        getBinding().tvCoachText.setText(String.format("%s %s", trainerDTO.getAuthor().getLastName(), trainerDTO.getAuthor().getFirstName()));

        String setX = trainerDTO.getIntervals().size() > 1 ? requireActivity().getString(R.string.sets) : requireActivity().getString(R.string.set);
        getBinding().tvTotalSetsText.setText(String.format(Locale.getDefault(), "%d %s", trainerDTO.getIntervals().size(), setX));

        int durationTime = 0;
        for (EgymTrainingPlans.TrainerDTO.IntervalsDTO intervalsDTO : trainerDTO.getIntervals()) {
//            durationTime += intervalsDTO.getDuration();
            durationTime += chkDuration(intervalsDTO.getDuration());
        }
        //    setTextWithFade(getBinding().tvTotalDurationText,formatMsToM(durationTime));
        getBinding().tvTotalDurationText.setText(formatMsToM(durationTime));


        initChart();

        initRecyclerView();

        initWorkoutData((durationTime / 1000));
    }

    EgymStepsAdapter egymStepsAdapter;

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        getBinding().recyclerView.setLayoutManager(linearLayoutManager);
        getBinding().recyclerView.setHasFixedSize(true);
        egymStepsAdapter = new EgymStepsAdapter(requireActivity(), workoutViewModel, trainerDTO, egymDataViewModel);
        getBinding().recyclerView.setAdapter(egymStepsAdapter);


        egymStepsAdapter.setOnItemClickListener(historyEntity -> {

        });
    }

    private void initView() {

        int text1 = R.string.speed;
        int text2 = R.string.incline;
        if (MODE == CE1000ENT) {
            text1 = R.string.Resistance;
            text2 = R.string.Cadence;
        } else if (MODE == CR1000ENT || MODE == CU1000ENT) {
            text1 = R.string.Resistance;
            text2 = R.string.Cadence;
        }

        getBinding().tvSpeedQ.setText(text1);
        getBinding().tvInclineQ.setText(text2);

    }

//    private void testCreateWorkout() {
//        new EgymUtil().apiCreateWorkouts();
//    }


    private void initEvent() {


        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> initData());


        DebounceClick.attach(getBinding().btnNext, v -> {
                    if (egymDataViewModel.egymTrainingPlansData.getValue() != null) {
                        if (egymDataViewModel.egymTrainingPlansData.getValue().getTrainer().size() <= 1)
                            return;
                    }

                    if (egymDataViewModel.currentPlanNum.get() < totalPlans) {
                        egymDataViewModel.currentPlanNum.set(egymDataViewModel.currentPlanNum.get() + 1);
                    } else {
                        egymDataViewModel.currentPlanNum.set(0);
                    }

                    initData();
                }
        );


        DebounceClick.attach(getBinding().btnPrevious, v -> {
                    if (egymDataViewModel.egymTrainingPlansData.getValue() != null) {
                        if (egymDataViewModel.egymTrainingPlansData.getValue().getTrainer().size() <= 1)
                            return;
                    }

                    if (egymDataViewModel.currentPlanNum.get() > 0) {
                        egymDataViewModel.currentPlanNum.set(egymDataViewModel.currentPlanNum.get() - 1);
                    } else {
                        egymDataViewModel.currentPlanNum.set(totalPlans);
                    }

                    initData();
                }
        );

        DebounceClick.attach(getBinding().btnBack, v -> {
            try {

                appStatusViewModel.changeMainButtonType(DISAPPEAR);

                NavController navController = NavHostFragment.findNavController(EgymPlanFragment.this);
                navController.popBackStack(navController.getGraph().getStartDestinationId(), false);


//                    NavController navController = NavHostFragment.findNavController(EgymPlanFragment.this);
//                    navController.navigateUp();
                //  Navigation.findNavController(requireView()).navigate(EgymPlanFragmentDirections.actionEgymPlanFragmentToDashboardTrainingFragment());
            } catch (Exception e) {
                showException(e);
            }
        });
    }


}