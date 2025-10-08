package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.isCustomAllBarSetValue;
import static com.dyaco.spirit_commercial.support.FormulaUtil.km2mi;
import static com.dyaco.spirit_commercial.support.FormulaUtil.mi2km;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedMinValue;
import static com.dyaco.spirit_commercial.support.WorkoutUtil.getMaxSpeedValue;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NO_HR_HIDE_BUTTON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.STEP1_TO_STEP_2;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DEFAULT_SEEK_VALUE_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DEFAULT_SEEK_VALUE_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.JUST_NEXT;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_PROGRAM;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_INC;

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
import com.dyaco.spirit_commercial.databinding.FragmentProgramCustomSpeedBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.FormulaUtil;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.CircleBarView;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
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

public class CustomProgramSpeedFragment extends BaseBindingFragment<FragmentProgramCustomSpeedBinding> {
    private float mphIU;
    private float mphMU;
    private boolean isRule2Float = true; //2是否有小數點
    private float MAX_VALUE_2 = 99;// 分
    private float MIN_VALUE_2 = 0;// 分
    private ProgramsEnum programsEnum;
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private MainActivity mainActivity;

    public CustomProgramSpeedFragment() {
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

        int bType;
        if (isTreadmill) {
            bType = JUST_NEXT;
        } else {
            bType = START_THIS_PROGRAM;
        }
        appStatusViewModel.changeMainButtonType(bType);


        initEvent();

        initView();


        initBar();
    }

    private void initView() {
        int tvSelectTitle;
        if (isTreadmill) {
            tvSelectTitle = UNIT_E == DeviceIntDef.IMPERIAL ? R.string.select_max_mph : R.string.select_max_kph;
        } else {
            tvSelectTitle = R.string.total_time_min;
        }

        getBinding().tvSelectTitle.setText(tvSelectTitle);


        MAX_VALUE_2 = getMaxSpeedValue(programsEnum);
        MIN_VALUE_2 = getMaxSpeedMinValue(programsEnum);

        getBinding().rulerViewMonth.setMaxValue(MAX_VALUE_2);
        getBinding().rulerViewMonth.setMinValue(MIN_VALUE_2);

        getBinding().rulerViewMonth.setIntervalValue(MAX_SPD_INC / 10f);
        getBinding().rulerViewMonth.setRetainLength(1);


        float defValue = MIN_VALUE_2;
        if (mainActivity.customBean.getMaxSpeedKmh() > 0) {
            defValue = (float) (UNIT_E == DeviceIntDef.IMPERIAL ? mainActivity.customBean.getMaxSpeedMph() : mainActivity.customBean.getMaxSpeedKmh());
        }


        getBinding().rulerViewMonth.setSelectedValue(defValue);

        //Workout Data 初始設定
     //   workoutViewModel.selMaxSpeedOrLevel.set(getSpeedLevel(MIN_VALUE_2));



//        mainActivity.customBean.setMaxSpeedKmh(UNIT_E == DeviceIntDef.METRIC ? value : mi2km(value));
//        mainActivity.customBean.setMaxSpeedMph(UNIT_E == DeviceIntDef.IMPERIAL ? value : km2mi(value));



    }


    private void initEvent() {


        new CommonUtils().addAutoClick(getBinding().btnTimePlus);
        new CommonUtils().addAutoClick(getBinding().btnTimeMinus);

        getBinding().rulerViewMonth.setOnValueChangeListener((view, value) -> {

            getBinding().tvTimeNum.setText(String.valueOf(value));

        //    workoutViewModel.selMaxSpeedOrLevel.set(getSpeedLevel(value));

            mainActivity.customBean.setMaxSpeedKmh(UNIT_E == DeviceIntDef.METRIC ? value : mi2km(value));
            mainActivity.customBean.setMaxSpeedMph(UNIT_E == DeviceIntDef.IMPERIAL ? value : km2mi(value));

        });

        getBinding().btnTimePlus.setOnClickListener(v ->
                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) + 0.1f));

        getBinding().btnTimeMinus.setOnClickListener(v ->
                getBinding().rulerViewMonth.setSelectedValue(Float.parseFloat(getBinding().tvTimeNum.getText().toString()) - 0.1f));


        getBinding().btnClearDiagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastClick()) return;
                getBinding().editProfileView.reset();
                resetSbText();
            }
        });

        appStatusViewModel.changeNavigate().observe(getViewLifecycleOwner(), action -> {
            if (action == STEP1_TO_STEP_2) {
                //app:popUpToInclusive="true" 沒加的話 SetTimeFragment 不會Destroy
                try {
                    Navigation.findNavController(requireView()).navigate(CustomProgramSpeedFragmentDirections.actionSetupCustomProgramFragmentToCustomProgramInclineFragment(programsEnum));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("BBBFFFBBB", "######initEvent: " + workoutViewModel);
            }
        });


        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> {
            initView();
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
                            Navigation.findNavController(v).navigate(CustomProgramSpeedFragmentDirections.actionSetupCustomProgramFragmentToProgramsFragment(WorkoutIntDef.DEFAULT_PROGRAM));
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
                sbTextsList.set(bar, String.valueOf(level * 10));

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
    }


    private final List<String> sbTextsList = new ArrayList<>();

    /**
     * 將Level全部改為預設值,ex 0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0
     */
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

        String values = mainActivity.customBean.getDiagramLevelOrSpeed();

        if (values == null || "".equals(values)) {
            values = DEFAULT_SEEK_VALUE_SPEED;
            //disabled按鈕
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(true);
        } else {
            //允許next
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);
        }

        int[] array = getDiagramArray(values);

        Log.d("BBBBVVCCCXX", "initBar: " + Arrays.toString(array));

        new RxTimer().timer(200, number -> {
            if (getBinding() != null) {
                for (int i = 0; i < getBinding().editProfileView.getBarCount(); i++) {
                    if (getBinding() == null) return;
                    getBinding().editProfileView.setBarLevel(i, array[i] / 10);
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


        //缺cooldown warmup
        workoutViewModel.selProgram.setTreadmillSpeedNum("20#" + "30#" + "40#" + num +"#40" + "#30" +"#20");


        if (isCustomAllBarSetValue(sbTextsList)) {
            //允許next
            LiveEventBus.get(NO_HR_HIDE_BUTTON).post(false);
        }
     //   LiveEventBus.get(NO_HR_HIDE_BUTTON).post(!isCustomAllBarSetValue(sbTextsList));


    }

}