package com.dyaco.spirit_commercial.dashboard_training;

import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.EVENT_SET_UNIT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NO_HR_HIDE_BUTTON;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_STOP_BACK_TO_MAIN_TRAINING;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.PROGRAM_CHOOSE;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.CHOOSE;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.DISAPPEAR;
import static com.dyaco.spirit_commercial.support.intdef.MainDashboardBottomButtonIntDef.START_THIS_TEST;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentProgramsBannerBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.banner.Banner;
import com.dyaco.spirit_commercial.support.custom_view.banner.indicator.CircleIndicator;
import com.dyaco.spirit_commercial.support.custom_view.banner.listener.OnPageChangeListener;
import com.dyaco.spirit_commercial.support.custom_view.banner.util.BannerUtils;
import com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

public class ProgramsBannerFragment extends BaseBindingFragment<FragmentProgramsBannerBinding> {
    private Banner banner;
    private int programId;
    private int programType;
    private WorkoutViewModel workoutViewModel;
    private AppStatusViewModel appStatusViewModel;
    private DeviceSettingViewModel deviceSettingViewModel;
    private ProgramsBannerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.isOnStopBackToMainTraining = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        programId = ProgramsBannerFragmentArgs.fromBundle(getArguments()).getProgramId();
        programType = ProgramsBannerFragmentArgs.fromBundle(getArguments()).getProgramType();

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);


        //下方按鈕
//        appStatusViewModel.changeMainButtonType((programType != WorkoutIntDef.DEFAULT_PROGRAM) || (programId == R.id.btn_FitnessTest) ? START_THIS_TEST : DISAPPEAR);

//        if (programId == R.id.btn_Watts) {
//            appStatusViewModel.changeMainButtonType(DISAPPEAR);
//        } else {
        appStatusViewModel.changeMainButtonType((programType != WorkoutIntDef.DEFAULT_PROGRAM) || (programId == R.id.btn_FitnessTest) ? START_THIS_TEST : CHOOSE);
        //     }


        if (isTreadmill) {
            getBinding().programTypeName.setText(programType == WorkoutIntDef.DEFAULT_PROGRAM ? R.string.default_programs : R.string.fitness_tests);
        } else {
            getBinding().programTypeName.setText(R.string.Program_Info);
        }
        //  Looper.myQueue().addIdleHandler(() -> {
        initView();
        initEvent();
        //      return false;
        //   });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initEvent() {
        getBinding().btnBack.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;

            try {
                appStatusViewModel.changeMainButtonType(DISAPPEAR);
                parent.hrNotifyWarringWindow(false);
                //  Bundle bundle = new ProgramsBannerFragmentArgs.Builder().setProgramType(programType).build().toBundle();
                Navigation.findNavController(v).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToProgramsFragment(programType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //改變單位
        LiveEventBus.get(EVENT_SET_UNIT).observe(getViewLifecycleOwner(), s -> {
            adapter.notifyDataSetChanged();

            // adapter.changeUnit();
        });

        LiveEventBus.get(PROGRAM_CHOOSE).observe(getViewLifecycleOwner(), b -> {

            if (workoutViewModel.selProgram != ProgramsEnum.CUSTOM) {
                try {
                    Navigation.findNavController(requireView()).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToSetTimeFragment(workoutViewModel.selProgram));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                if (isTreadmill) {
                    try {
                        Navigation.findNavController(requireView()).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToSetupCustomProgramFragment(workoutViewModel.selProgram));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Navigation.findNavController(requireView()).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToCustomProgramLevelFragment(workoutViewModel.selProgram));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //HR連線or斷線
        workoutViewModel.getIsHrConnected().observe(getViewLifecycleOwner(), item -> noHrCheck(20));


        LiveEventBus.get(ON_STOP_BACK_TO_MAIN_TRAINING, Boolean.class).observe(getViewLifecycleOwner(), s -> {

            if (getBinding() != null) {
                getBinding().btnBack.callOnClick();
            }
        });
    }


    private void initView() {
        List<ProgramsEnum> programInfoList = getProgramInfoList();

        CircleIndicator indicator = getBinding().indicator;
        banner = getBinding().programsBanner;

        int position;
        if (programId == R.id.btn_manual || programId == R.id.btn_AirForce) {
            position = 1;
        } else if (programId == R.id.btn_Hill || programId == R.id.btn_Army) {
            position = 2;
        } else if (programId == R.id.btn_Fatburn || programId == R.id.btn_CoastGuard) {
            position = 3;
        } else if (programId == R.id.btn_Cardio || programId == R.id.btn_Gerkin) {
            position = 4;
        } else if (programId == R.id.btn_Strength || programId == R.id.btn_PEB) {
            position = 5;
        } else if (programId == R.id.btn_HIIT || programId == R.id.btn_MarineCorps) {
            position = 6;
        } else if (programId == R.id.btn_5KRun || programId == R.id.btn_Navy || programId == R.id.btn_HeartRate_BIKE) {
            position = 7;
        } else if (programId == R.id.btn_10KRun || programId == R.id.btn_WFI || programId == R.id.btn_Watts) {
            position = 8;
        } else if (programId == R.id.btn_HeartRate || programId == R.id.btn_CTTPrediction || programId == R.id.btn_FitnessTest) {
            position = 9;
        } else if (programId == R.id.btn_CTTPerformance || programId == R.id.btn_Interval) {
            position = 10;
        } else if (programId == R.id.btn_Custom) {
            position = 11;
        } else {
            position = 0;
        }

        adapter = new ProgramsBannerAdapter(requireActivity(), programInfoList, workoutViewModel, deviceSettingViewModel);
        banner.setStartPosition(position);
        banner.setAdapter(adapter);
        banner.isAutoLoop(false);
        banner.setBannerGalleryEffect(0, 0, 24, 1);

        indicator.setVisibility(View.VISIBLE);
        banner.setIndicator(indicator, false);
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(12));
        banner.setIndicatorWidth((int) BannerUtils.dp2px(12), (int) BannerUtils.dp2px(12));
        banner.setIndicatorHeight((int) BannerUtils.dp2px(12));
        banner.setIndicatorNormalColorRes(R.color.color323f4b);
        banner.setIndicatorSelectedColorRes(R.color.color1396ef);
        banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
        banner.setIndicatorRadius(0);

        adapter.setOnItemClickListener(isForward -> banner.setCurrentItem(getPosition(isForward), true));


        adapter.setOnImageClickListener(programsEnum -> {
//            if (programsEnum == ProgramsEnum.WATTS) {
//                try {
//                    Navigation.findNavController(requireView()).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToSetTimeFragment(programsEnum));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        });

        workoutViewModel.setSelProgram(programInfoList.get(position - 1));
        if (programType == WorkoutIntDef.FITNESS_TESTS) {
            //FITNESS_TESTS預設值
            workoutViewModel.selWeightMU.set((int) userProfileViewModel.getWeight_metric());
            workoutViewModel.selWeightIU.set((int) userProfileViewModel.getWeight_imperial());
            workoutViewModel.selGender.set(userProfileViewModel.getUserGender());
            workoutViewModel.selYO.set(userProfileViewModel.getUserAge());
            workoutViewModel.selWorkoutTime.set(UNLIMITED);
        }

        noHrCheck(0);

        banner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                workoutViewModel.setSelProgram(programInfoList.get(position));

                if (!isTreadmill) {
                    //bike
                    if (programInfoList.get(position).getCode() == ProgramsEnum.FITNESS_TEST.getCode()) {
                        appStatusViewModel.changeMainButtonType(START_THIS_TEST);
                        //noHrCheck(0);
//                    } else if (programInfoList.get(position).getCode() == ProgramsEnum.WATTS.getCode()) {
//                        appStatusViewModel.changeMainButtonType(DISAPPEAR);
                    } else {
                        appStatusViewModel.changeMainButtonType(CHOOSE);
                    }
                }

                noHrCheck(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private int getPosition(boolean isForward) {
        int targetItem;
        int count = banner.getRealCount();
        int currentItem = banner.getCurrentItem();
        if (isForward) {
            if (currentItem == count) {
                targetItem = 1;
            } else {
                targetItem = currentItem + 1;
            }
        } else {
            if (currentItem == 1) {
                targetItem = count;
            } else {
                targetItem = currentItem - 1;
            }
        }
        return targetItem;
    }


    /**
     * 取得 Program 列表
     */
    private List<ProgramsEnum> getProgramInfoList() {
        List<ProgramsEnum> programInfoList = new ArrayList<>();
        if (isTreadmill) {
            if (programType == WorkoutIntDef.DEFAULT_PROGRAM) {
                programInfoList.add(ProgramsEnum.MANUAL);
                programInfoList.add(ProgramsEnum.HILL);
                programInfoList.add(ProgramsEnum.FATBURN);
                programInfoList.add(ProgramsEnum.CARDIO);
                programInfoList.add(ProgramsEnum.STRENGTH);
                programInfoList.add(ProgramsEnum.HIIT);
                programInfoList.add(ProgramsEnum.RUN_5K);
                programInfoList.add(ProgramsEnum.RUN_10K);
                programInfoList.add(ProgramsEnum.HEART_RATE);
                //TODO:
                programInfoList.add(ProgramsEnum.INTERVAL);
                programInfoList.add(ProgramsEnum.CUSTOM);
            } else {
                programInfoList.add(ProgramsEnum.AIR_FORCE);
                programInfoList.add(ProgramsEnum.ARMY);
                programInfoList.add(ProgramsEnum.COAST_GUARD);
                programInfoList.add(ProgramsEnum.GERKIN);
                programInfoList.add(ProgramsEnum.PEB);
                programInfoList.add(ProgramsEnum.MARINE_CORPS);
                programInfoList.add(ProgramsEnum.NAVY);
                programInfoList.add(ProgramsEnum.WFI);
                programInfoList.add(ProgramsEnum.CTT_PREDICTION);
                programInfoList.add(ProgramsEnum.CTT_PERFORMANCE);
            }
        } else {
            programInfoList.add(ProgramsEnum.MANUAL);
            programInfoList.add(ProgramsEnum.HILL);
            programInfoList.add(ProgramsEnum.FATBURN);
            programInfoList.add(ProgramsEnum.CARDIO);
            programInfoList.add(ProgramsEnum.STRENGTH);
            programInfoList.add(ProgramsEnum.HIIT);
            programInfoList.add(ProgramsEnum.HEART_RATE);
            programInfoList.add(ProgramsEnum.WATTS);
            programInfoList.add(ProgramsEnum.FITNESS_TEST);
            // TODO:
            programInfoList.add(ProgramsEnum.INTERVAL);
            programInfoList.add(ProgramsEnum.CUSTOM);
        }
        return programInfoList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //   if (programType != WorkoutIntDef.DEFAULT_PROGRAM)
        //      appStatusViewModel.changeMainButtonType(DISAPPEAR);
        banner = null;
        adapter = null;

        //   Log.d("OOOIIIIEEE", "ProgramsbannerFragment onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        banner = null;
        adapter = null;
    }


    private void noHrCheck(int delay) {
        boolean isHide;
        //  if (isEmulator) return;
        //無心跳不可執行
        switch (workoutViewModel.selProgram) {
            case CTT_PREDICTION:
            case GERKIN:
            case WFI:
            case FITNESS_TEST:
            case HEART_RATE:
                isHide = !workoutViewModel.getIsHrConnected().getValue();
                break;
            default:
                isHide = false;
        }

        //提示
        parent.hrNotifyWarringWindow(isHide);

        //沒hr就不能啟動的program，下方button true 隱藏
        //   new RxTimer().timer(delay, number -> {
        LiveEventBus.get(NO_HR_HIDE_BUTTON).post(isHide);
        //   });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d("OOOIIIIEEE", "onResume: " + App.isShowMediaMenuOnStop);
//        if (App.isShowMediaMenuOnStop) {
//            Navigation.findNavController(requireView()).navigate(ProgramsBannerFragmentDirections.actionProgramsBannerFragmentToDashboardTrainingFragment());
//        }
    }
}