package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.isCustomAllBarSetValue;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NO_HR_HIDE_BUTTON;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DEFAULT_SEEK_VALUE_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_PROGRAM;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.alert_message.AlertCustomExitWindow;
import com.dyaco.spirit_commercial.databinding.FragmentProgramCustomLevelBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CircleBarView;
import com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomProgramLevelFragment extends BaseBindingFragment<FragmentProgramCustomLevelBinding> {

    private ProgramsEnum programsEnum;
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private MainActivity mainActivity;

    public CustomProgramLevelFragment() {
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

        programsEnum = SetTimeFragmentArgs.fromBundle(getArguments()).getProgramEnum();


        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);

        workoutViewModel.selProgram = programsEnum;

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        getBinding().setWorkoutData(workoutViewModel);
        getBinding().setIsTreadmill(isTreadmill);

        appStatusViewModel.changeMainButtonType(START_THIS_PROGRAM);


        initView();


        initEvent();


        workoutViewModel.selWorkoutTime.set(OPT_SETTINGS.TARGET_TIME_DEF * 60);


        initBar();
    }

    private void initView() {

        int defValue = OPT_SETTINGS.TARGET_TIME_DEF;
        if (mainActivity.customBean.getTotalTime() >= 0) {
            defValue = mainActivity.customBean.getTotalTime() / 60;
        }
        getBinding().rulerViewMonth.setSelectedValue(defValue);
        mainActivity.customBean.setTotalTime(defValue * 60);
    }


    private void initEvent() {


        getBinding().rgHrFilter.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbHard) {
                changeChart(0);
            } else if (checkedId == R.id.rbModerate){
                changeChart(1);
            } else {
                changeChart(2);
            }
        });


        new CommonUtils().addAutoClick(getBinding().btnTimePlus);
        new CommonUtils().addAutoClick(getBinding().btnTimeMinus);

        getBinding().rulerViewMonth.setOnValueChangeListener((view, value) -> {
            getBinding().tvTimeNum.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

            workoutViewModel.selWorkoutTime.set(Math.round(value * 60));
            mainActivity.customBean.setTotalTime(Math.round(value * 60));
        });

        getBinding().btnTimePlus.setOnClickListener(v ->
                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) + 1));

        getBinding().btnTimeMinus.setOnClickListener(v ->
                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) - 1));


        getBinding().btnClearDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastClick()) return;
                getBinding().editProfileView.reset();
//                getBinding().LeditProfileView.reset();
                resetSbText();
            }
        });


        getBinding().btnBack.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;


            mainActivity.popupWindow = new AlertCustomExitWindow(requireActivity());
            mainActivity.popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((AlertCustomExitWindow) mainActivity.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null && ((boolean) value.getObj())) {
                        try {
                            new FormulaUtil().clearWorkoutViewModel(workoutViewModel);

                            mainActivity.customBean.setDiagramIncline("");
                            mainActivity.customBean.setDiagramLevelOrSpeed("");
                            mainActivity.customBean.setTotalTime(-1);
                            mainActivity.customBean.setMaxSpeedKmh(0);
                            mainActivity.customBean.setMaxSpeedMph(0);
                            
                            appStatusViewModel.changeMainButtonType(DISAPPEAR);
                            Navigation.findNavController(v).navigate(CustomProgramLevelFragmentDirections.actionCustomProgramLevelFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onDismiss() {
                    mainActivity.popupWindow = null;
                }
            });
        });


        getBinding().editProfileView.setLevelChangedListener(new CircleBarView.LevelChangedListener() {
            @Override
            public void onLevelChanged(int bar, int level) {
//                sbTextsList.set(bar, String.valueOf(level * 4));
                sbTextsList.set(bar, String.valueOf(level * xValue));

                if (isCustomAllBarSetValue(sbTextsList)) {
                    saveDiagram();
                }
            }


            @Override
            public void onLastBarSelect(boolean a) {
                if (isCustomAllBarSetValue(sbTextsList)) {
                    saveDiagram();
                }
            }
        });



//        getBinding().LeditProfileView.setLevelChangedListener(new CircleBarView.LevelChangedListener() {
//            @Override
//            public void onLevelChanged(int bar, int level) {
////                sbTextsList.set(bar, String.valueOf(level * 4));
//                sbTextsList.set(bar, String.valueOf(level * xValue));
//
//                if (isCustomAllBarSetValue(sbTextsList)) {
//                    saveDiagram();
//                }
//            }
//
//
//            @Override
//            public void onLastBarSelect(boolean a) {
//                if (isCustomAllBarSetValue(sbTextsList)) {
//                    saveDiagram();
//                }
//            }
//        });
    }


    private final List<String> sbTextsList = new ArrayList<>();

    public void resetSbText() {

        int[] array = getDiagramArray(DEFAULT_SEEK_VALUE_LEVEL);

        for (int i = 0; i < sbTextsList.size(); i++) {
            sbTextsList.set(i, (String.valueOf(array[i])));
        }

        mainActivity.customBean.setDiagramLevelOrSpeed("");

        LiveEventBus.get(NO_HR_HIDE_BUTTON).post(!isCustomAllBarSetValue(sbTextsList));

    }

    public int[] getDiagramArray(String diagramStr) {

        return Arrays.stream(diagramStr.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
    }


    private void initBar() {

        changeChart(charType);


        //不儲存
      //  String values = mainActivity.customBean.getDiagramLevelOrSpeed();
        String values = "";

        if (values == null || "".equals(values)) {
            values = DEFAULT_SEEK_VALUE_LEVEL;
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(true);
        } else {
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);
        }

        int[] array = getDiagramArray(values);

        new RxTimer().timer(200, number -> {
            if (getBinding() != null) {
                for (int i = 0; i < getBinding().editProfileView.getBarCount(); i++) {
                    if (getBinding() == null) return;
//                    getBinding().editProfileView.setBarLevel(i, array[i] / 4);

                    getBinding().editProfileView.setBarLevel(i, array[i] / xValue);

//                    if (charType == 0) {
//                        getBinding().editProfileView.setBarLevel(i, array[i] / xValue);
//                    } else {
//                        getBinding().LeditProfileView.setBarLevel(i, array[i] / xValue);
//                    }

                    sbTextsList.add(String.valueOf(array[i]));
                }
            }
        });

    }


    private void saveDiagram() {
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            int x = Integer.parseInt(sbTextsList.get(i));
            num.append(x).append("#");
        }
        num = num.deleteCharAt(num.length() - 1);

        mainActivity.customBean.setDiagramLevelOrSpeed(num.toString());

        workoutViewModel.selProgram.setBikeLevelNum(num.toString());

//        // TODO:
//          workoutViewModel.selMaxSpeedOrLevel.set(5);

        if (isCustomAllBarSetValue(sbTextsList)) {
            //允許next
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);

            Log.d("KWWOOPP", "saveDiagram: " + num);
        }

        //   LiveEventBus.get(NO_HR_HIDE_BUTTON).post(!isCustomAllBarSetValue(sbTextsList));
    }



    int charType = 0;
    int xValue = 5;

    private void changeChart(int chartType) {
        if (getBinding() == null) return;
        this.charType = chartType;

        getBinding().editProfileView.reset();
     //   getBinding().LeditProfileView.reset();
        resetSbText();



        if (chartType == 0 || chartType == 1) {
            getBinding().groupHard.setVisibility(View.VISIBLE);
            getBinding().groupLight.setVisibility(View.GONE);

            getBinding().tt01.setText(chartType == 0 ? "5" : "3");
            getBinding().tt02.setText(chartType == 0 ? "10" : "6");
            getBinding().tt03.setText(chartType == 0 ? "15" : "9");
            getBinding().tt04.setText(chartType == 0 ? "20" : "12");
            getBinding().tt05.setText(chartType == 0 ? "25" : "15");
            getBinding().tt06.setText(chartType == 0 ? "30" : "18");
            getBinding().tt07.setText(chartType == 0 ? "35" : "21");
            getBinding().tt08.setText(chartType == 0 ? "40" : "24");
            getBinding().tt09.setText(chartType == 0 ? "45" : "27");
            getBinding().tt10.setText(chartType == 0 ? "50" : "30");

            xValue = chartType == 0 ? 5 : 3;
        } else {
            getBinding().groupHard.setVisibility(View.GONE);
            getBinding().groupLight.setVisibility(View.VISIBLE);

            xValue = 1;
        }

        getBinding().editProfileView.setBarMaxLevel(chartType == 2 ? 15 : 10);

    }

}