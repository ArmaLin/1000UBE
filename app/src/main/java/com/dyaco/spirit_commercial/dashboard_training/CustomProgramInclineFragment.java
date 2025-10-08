package com.dyaco.spirit_commercial.dashboard_training;


import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getSpeedLevel;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP3_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DEFAULT_SEEK_VALUE_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.PREVIOUS_AND_START;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.alert_message.AlertCustomExitWindow;
import com.dyaco.spirit_commercial.databinding.FragmentProgramCustomInclineNewBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CircleBarView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
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

public class CustomProgramInclineFragment extends BaseBindingFragment<FragmentProgramCustomInclineNewBinding> {

    private ProgramsEnum programsEnum;
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private MainActivity mainActivity;

    public CustomProgramInclineFragment() {
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

        appStatusViewModel.changeMainButtonType(PREVIOUS_AND_START);

        initEvent();

        initView();

        initBar();
    }

    private void initView() {

        int defValue = OPT_SETTINGS.TARGET_TIME_DEF;
        if (mainActivity.customBean.getTotalTime() >= 0) {
            defValue = mainActivity.customBean.getTotalTime() / 60;
        }
        getBinding().rulerViewMonth.setSelectedValue(defValue);
        mainActivity.customBean.setTotalTime(defValue * 60);


        workoutViewModel.selMaxSpeedOrLevel.set(getSpeedLevel((float) (UNIT_E == DeviceIntDef.METRIC ? mainActivity.customBean.getMaxSpeedKmh() : mainActivity.customBean.getMaxSpeedMph())) );

    }


    private void initEvent() {

        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> {
            initView();
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


        //回上一頁
        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
            if (action == STEP3_TO_STEP_2) {
                try {
                    Navigation.findNavController(requireView()).navigate(CustomProgramInclineFragmentDirections.actionCustomProgramInclineFragmentToSetupCustomProgramFragment(programsEnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        getBinding().btnClearDiagram.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            getBinding().editProfileView.reset();
            resetSbText();
        });


        getBinding().btnBack.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;


            mainActivity.popupWindow = new AlertCustomExitWindow(requireActivity());
            mainActivity.popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            ((AlertCustomExitWindow)mainActivity.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null && ((boolean) value.getObj())){
                        try {
                            new FormulaUtil().clearWorkoutViewModel(workoutViewModel);

                            mainActivity.customBean.setDiagramIncline("");
                            mainActivity.customBean.setDiagramLevelOrSpeed("");
                            mainActivity.customBean.setTotalTime(-1);
                            mainActivity.customBean.setMaxSpeedKmh(0);
                            mainActivity.customBean.setMaxSpeedMph(0);

                            appStatusViewModel.changeMainButtonType(DISAPPEAR);
                            Navigation.findNavController(v).navigate(CustomProgramInclineFragmentDirections.actionCustomProgramInclineFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
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

                sbTextsList.set(bar, String.valueOf((level) * 2));  //存成level所以乘以2，還原時除以2

                saveDiagram();
            }


            @Override
            public void onLastBarSelect(boolean a) {
                saveDiagram();
            }
        });
    }


    private final List<String> sbTextsList = new ArrayList<>();

    /**
     * 將Level全部改為預設值,ex 0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0
     */
    public void resetSbText() {

        int[] array = getDiagramArray(DEFAULT_SEEK_VALUE_INCLINE);

        for (int i = 0; i < sbTextsList.size(); i++) {
            sbTextsList.set(i, (String.valueOf(array[i])));

            getBinding().editProfileView.setBarLevel(i, array[i]); //1 ~ 15
        }

        mainActivity.customBean.setDiagramIncline(DEFAULT_SEEK_VALUE_INCLINE);
//        workoutViewModel.selProgram.setTreadmillInclineNum(DEFAULT_SEEK_VALUE_INCLINE);
        workoutViewModel.selProgram.setTreadmillInclineNum("0#" + "0#" + "0#" + DEFAULT_SEEK_VALUE_INCLINE +"#0" + "#0" +"#0");

    }

    public int[] getDiagramArray(String diagramStr) {

        return Arrays.stream(diagramStr.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
    }


    private void initBar() {

        String values = mainActivity.customBean.getDiagramIncline();

        if (values == null || "".equals(values)) {
            values = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0"; // 20列
//            workoutViewModel.selProgram.setTreadmillInclineNum(DEFAULT_SEEK_VALUE_INCLINE); //26列
            workoutViewModel.selProgram.setTreadmillInclineNum("0#" + "0#" + "0#" + DEFAULT_SEEK_VALUE_INCLINE +"#0" + "#0" +"#0");


//            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(true);
//        } else {
//            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);
        }

        int[] array = getDiagramArray(values);

        new RxTimer().timer(200, number -> {
            if (getBinding() != null) {
                for (int i = 0; i < getBinding().editProfileView.getBarCount(); i++) {
                    if (getBinding() == null) return;
                    getBinding().editProfileView.setBarLevel(i, (array[i] / 2)); //存成level所以乘以2，還原時除以2
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

        mainActivity.customBean.setDiagramIncline(num.toString());
        //缺cooldown warmup
        workoutViewModel.selProgram.setTreadmillInclineNum("0#" + "0#" + "0#" + num +"#0" + "#0" +"#0");



    }

}
//public class CustomProgramInclineFragment extends BaseBindingFragment<FragmentProgramCustomInclineBinding> {
//
//    private ProgramsEnum programsEnum;
//    private WorkoutViewModel workoutViewModel;
//    private AppStatusViewModel appStatusViewModel;
//    private DeviceSettingViewModel deviceSettingViewModel;
//    private MainActivity mainActivity;
//
//    public CustomProgramInclineFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MainActivity.isOnStopBackToMainTraining = true;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mainActivity = (MainActivity) requireActivity();
//
//        programsEnum = SetTimeFragmentArgs.fromBundle(getArguments()).getProgramEnum();
//
//
//        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
//        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);
//
//        workoutViewModel.selProgram = programsEnum;
//
//        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
//
//        getBinding().setWorkoutData(workoutViewModel);
//        getBinding().setIsTreadmill(isTreadmill);
//
//        appStatusViewModel.changeMainButtonType(PREVIOUS_AND_START);
//
//        initEvent();
//
//        initView();
//
//        initBar();
//    }
//
//    private void initView() {
//
//        int defValue = OPT_SETTINGS.TARGET_TIME_DEF;
//        if (mainActivity.customBean.getTotalTime() >= 0) {
//            defValue = mainActivity.customBean.getTotalTime() / 60;
//        }
//        getBinding().rulerViewMonth.setSelectedValue(defValue);
//        mainActivity.customBean.setTotalTime(defValue * 60);
//
//
//        workoutViewModel.selMaxSpeedOrLevel.set(getSpeedLevel((float) (UNIT_E == DeviceIntDef.METRIC ? mainActivity.customBean.getMaxSpeedKmh() : mainActivity.customBean.getMaxSpeedMph())) );
//
//    }
//
//
//    private void initEvent() {
//
//        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> {
//            initView();
//        });
//
//        new CommonUtils().addAutoClick(getBinding().btnTimePlus);
//        new CommonUtils().addAutoClick(getBinding().btnTimeMinus);
//
//        getBinding().rulerViewMonth.setOnValueChangeListener((view, value) -> {
//            getBinding().tvTimeNum.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
//
//            workoutViewModel.selWorkoutTime.set(Math.round(value * 60));
//            mainActivity.customBean.setTotalTime(Math.round(value * 60));
//
//        });
//
//        getBinding().btnTimePlus.setOnClickListener(v ->
//                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) + 1));
//
//        getBinding().btnTimeMinus.setOnClickListener(v ->
//                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) - 1));
//
//
//        //回上一頁
//        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
//            if (action == STEP3_TO_STEP_2) {
//                try {
//                    Navigation.findNavController(requireView()).navigate(CustomProgramInclineFragmentDirections.actionCustomProgramInclineFragmentToSetupCustomProgramFragment(programsEnum));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//
//        getBinding().btnClearDiagram.setOnClickListener(view -> {
//            if (CheckDoubleClick.isFastClick()) return;
//           // getBinding().editProfileView.reset(); // 1~15
//            resetSbText();
//        });
//
//
//        getBinding().btnBack.setOnClickListener(v -> {
//
//            if (CheckDoubleClick.isFastClick()) return;
//
//
//            mainActivity.popupWindow = new AlertCustomExitWindow(requireActivity());
//            mainActivity.popupWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            ((AlertCustomExitWindow)mainActivity.popupWindow).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                  if (value != null && ((boolean) value.getObj())){
//                      try {
//                          new FormulaUtil().clearWorkoutViewModel(workoutViewModel);
//
//                          mainActivity.customBean.setDiagramIncline("");
//                          mainActivity.customBean.setDiagramLevelOrSpeed("");
//                          mainActivity.customBean.setTotalTime(0);
//                          mainActivity.customBean.setMaxSpeedKmh(0);
//                          mainActivity.customBean.setMaxSpeedMph(0);
//
//                          appStatusViewModel.changeMainButtonType(DISAPPEAR);
//                          Navigation.findNavController(v).navigate(CustomProgramInclineFragmentDirections.actionCustomProgramInclineFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
//                      } catch (Exception e) {
//                          e.printStackTrace();
//                      }
//                  }
//                }
//
//                @Override
//                public void onDismiss() {
//                  mainActivity.popupWindow = null;
//                }
//            });
//
//        });
//
//
//        getBinding().editProfileView.setLevelChangedListener(new CircleBarView.LevelChangedListener() {
//            @Override
//            public void onLevelChanged(int bar, int level) {
//
////                sbTextsList.set(bar, String.valueOf(level)); // 1~15
//
//                sbTextsList.set(bar, String.valueOf((level - 1) * 2)); // 0~15
//
//                saveDiagram();
//            }
//
//
//            @Override
//            public void onLastBarSelect(boolean a) {
//                saveDiagram();
//            }
//        });
//    }
//
//
//    private final List<String> sbTextsList = new ArrayList<>();
//
//    /**
//     * 將Level全部改為預設值,ex 0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0
//     */
//    public void resetSbText() {
//
//        int[] array = getDiagramArray(DEFAULT_SEEK_VALUE_INCLINE);
//
//        for (int i = 0; i < sbTextsList.size(); i++) {
//            sbTextsList.set(i, (String.valueOf(array[i])));
//
//            getBinding().editProfileView.setBarLevel(i, array[i] + 1); //0 ~ 15
//        }
//
//        mainActivity.customBean.setDiagramIncline(DEFAULT_SEEK_VALUE_INCLINE);
//        workoutViewModel.selProgram.setTreadmillInclineNum(DEFAULT_SEEK_VALUE_INCLINE);
//
//    }
//
//    public int[] getDiagramArray(String diagramStr) {
//
//        return Arrays.stream(diagramStr.split("#", -1))
//                .mapToInt(Integer::parseInt)
//                .toArray();
//    }
//
//
//    private void initBar() {
//
//        String values = mainActivity.customBean.getDiagramIncline();
//
//        if (values == null || "".equals(values)) {
//            values = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0"; // 20列
//            workoutViewModel.selProgram.setTreadmillInclineNum(DEFAULT_SEEK_VALUE_INCLINE); //26列
////            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(true);
////        } else {
////            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);
//        }
//
//        int[] array = getDiagramArray(values);
//
//        new RxTimer().timer(200, number -> {
//            if (getBinding() != null) {
//                for (int i = 0; i < getBinding().editProfileView.getBarCount(); i++) {
//                    if (getBinding() == null) return;
////                    getBinding().editProfileView.setBarLevel(i, array[i]); //1 ~ 15
//                    getBinding().editProfileView.setBarLevel(i, (array[i] / 2) + 1); //0 ~ 15
//                    sbTextsList.add(String.valueOf(array[i]));
//                }
//            }
//        });
//
//    }
//
//
//    private void saveDiagram() {
//        StringBuilder num = new StringBuilder();
//        for (int i = 0; i < 20; i++) {
//            int x = Integer.parseInt(sbTextsList.get(i));
//            num.append(x).append("#");
//        }
//        num = num.deleteCharAt(num.length() - 1);
//
//        mainActivity.customBean.setDiagramIncline(num.toString());
//        //缺cooldown warmup
//        workoutViewModel.selProgram.setTreadmillInclineNum("0#" + "0#" + "0#" + num +"#0" + "#0" +"#0");
//
//    }
//
//}