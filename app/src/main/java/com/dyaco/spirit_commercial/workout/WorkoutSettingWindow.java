package com.dyaco.spirit_commercial.workout;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.WORKOUT_STATS_SEND;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_DISTANCE_LEFT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_HEART_RATE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INTERVAL_DISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_REMAINING_TIME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;
import static com.dyaco.spirit_commercial.workout.MainWorkoutTrainingFragment.isGGG;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.AIR_FORCE;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.ARMY;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.COAST_GUARD;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.EGYM;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.MARINE_CORPS;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.NAVY;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.PEB;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.RUN_10K;
import static com.dyaco.spirit_commercial.workout.programs.ProgramsEnum.RUN_5K;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowWorkoutSettingsBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;

public class WorkoutSettingWindow extends BasePopupWindow<WindowWorkoutSettingsBinding> {
    public static boolean isSettingShow = false;
    WorkoutViewModel workoutViewModel;
    DeviceSettingViewModel deviceSettingViewModel;
    int tag;
    int no;
    private final CommonUtils comm;

    int duration = 300;

    public final int PANEL_1 = 1;
    public final int PANEL_2 = 2;
    public final int PANEL_3 = 3;
    public final int PANEL_4 = 4;
    public final int PANEL_5 = 5;
    public final int PANEL_6 = 6;

    int stats1Tag;
    int stats2Tag;
    int stats3Tag;
    int stats4Tag;
    int stats5Tag;
    int stats6Tag;

    public WorkoutSettingWindow(Context context, WorkoutViewModel workoutViewModel, DeviceSettingViewModel deviceSettingViewModel,
                                int tag, int no, int stats1Tag, int stats2Tag, int stats3Tag, int stats4Tag, int stats5Tag, int stats6Tag) {
        super(context, 0, 0, 0, GENERAL.TRANSLATION_Y, false, false, true, true);
        this.workoutViewModel = workoutViewModel;
        this.deviceSettingViewModel = deviceSettingViewModel;
        this.tag = tag;
        this.no = no;


        this.stats1Tag = stats1Tag;
        this.stats2Tag = stats2Tag;
        this.stats3Tag = stats3Tag;
        this.stats4Tag = stats4Tag;
        this.stats5Tag = stats5Tag;
        this.stats6Tag = stats6Tag;

        comm = new CommonUtils();
        getBinding().setIsUs(isUs);
        getBinding().setIsGGG(isGGG);
        getBinding().setIsE(workoutViewModel.selProgram == EGYM);

        isSettingShow = true;

        Looper.myQueue().addIdleHandler(() -> {
            //DataBinding設定的tag 比較慢執行
            initView();
            initEvent();
            LiveEventBus.get(WORKOUT_STATS_SEND).observeForever(observer);
            return false;
        });
    }

    Observer<Object> observer = s -> updateStatsData();

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);

        ObjectAnimator.ofFloat(getBinding().rgSelectItemTreadmill, "translationY", getHeight(), 0).setDuration(duration).start();
        ObjectAnimator.ofFloat(getBinding().rgSelectItemBike, "translationY", getHeight(), 0).setDuration(duration).start();
        ObjectAnimator.ofFloat(getBinding().btnClose, "translationY", getHeight(), 0).setDuration(duration).start();
    }

    @Override
    public void dismiss() {

        getBinding().statsNormal1.setVisibility(View.INVISIBLE);
        getBinding().statsNormal2.setVisibility(View.INVISIBLE);
        getBinding().statsNormal3.setVisibility(View.INVISIBLE);

        getBinding().statsEgym1.setVisibility(View.INVISIBLE);
        getBinding().statsEgym2.setVisibility(View.INVISIBLE);
        getBinding().statsEgym3.setVisibility(View.INVISIBLE);
        getBinding().statsEgym4.setVisibility(View.INVISIBLE);
        getBinding().statsEgym5.setVisibility(View.INVISIBLE);
        getBinding().statsEgym6.setVisibility(View.INVISIBLE);


        getBinding().vBG.setVisibility(View.GONE);
        getBinding().rgSelectItemTreadmill.animate().translationY(getHeight()).setDuration(duration).start();
        getBinding().rgSelectItemBike.animate().translationY(getHeight()).setDuration(duration).start();
        getBinding().btnClose.animate().translationY(getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                superDismiss();
            }
        }).setDuration(duration).start();

        if (onCustomDismissListener != null) {
            onCustomDismissListener.onStartDismiss(returnValue);
        }
        isSettingShow = false;
        LiveEventBus.get(WORKOUT_STATS_SEND).removeObserver(observer);
    }

    private void initEvent() {

        ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
        if (isTreadmill) {

            for (int i = 0; i < getBinding().rgSelectItemTreadmill.getChildCount(); i++) {
                View o = getBinding().rgSelectItemTreadmill.getChildAt(i);
                if (o instanceof RadioButton) {
                    radioButtonArrayList.add((RadioButton) o);

                    int radioTag = 0;
                    if (radioButtonArrayList.get(i).getTag() != null) {
                        radioTag = Integer.parseInt((String) radioButtonArrayList.get(i).getTag());
                    }


                    radioButtonArrayList.get(i).setChecked(radioTag == tag);

                    //上數沒有 Time Left
                    if (workoutViewModel.selWorkoutTime.get() == UNLIMITED) {
                        disabledItem(radioTag, STATS_REMAINING_TIME, radioButtonArrayList.get(i), 0);
                    }

                    //檢查有DistanceLeft的Program
                    if (!checkDistanceLeft()) {
                        disabledItem(radioTag, STATS_DISTANCE_LEFT, radioButtonArrayList.get(i), 0);
                    }

                    //Spirit > stats1Tag,stats2Tag,stats3Tag
                    //EGYM > stats1Tag,stats2Tag,stats3Tag,stats4Tag.stats5Tag.stats6Tag
                    //EGYM,美版或 美版非GERKIN > stats1Tag,stats2Tag,stats4Tag.stats5Tag
                    if (radioTag != tag) {
                        disabledItem(radioTag, stats1Tag, radioButtonArrayList.get(i), 1);
                        disabledItem(radioTag, stats2Tag, radioButtonArrayList.get(i), 1);

                        if (workoutViewModel.selProgram != EGYM) {
                            disabledItem(radioTag, stats3Tag, radioButtonArrayList.get(i), 1);
                        }

                        if (workoutViewModel.selProgram == EGYM) {
                            disabledItem(radioTag, stats4Tag, radioButtonArrayList.get(i), 1);
                            disabledItem(radioTag, stats5Tag, radioButtonArrayList.get(i), 1);
                            //美版或 美版非GERKIN, stats只有4個
                            //Gerkin, WFI, CttPerformance, CttPrediction 中間頁籤大小跟國際版一樣
                            if (!(isUs && !isGGG)) {
                                disabledItem(radioTag, stats6Tag, radioButtonArrayList.get(i), 1);
                                disabledItem(radioTag, stats3Tag, radioButtonArrayList.get(i), 1);
                            }

                        }
                    }

                }
            }

            getBinding().rgSelectItemTreadmill.setVisibility(View.VISIBLE);
            getBinding().rgSelectItemBike.setVisibility(View.GONE);

            getBinding().rgSelectItemTreadmill.setOnCheckedChangeListener((group, checkedId) -> {
                getBinding().statsNormal1.setVisibility(View.INVISIBLE);
                getBinding().statsNormal2.setVisibility(View.INVISIBLE);
                getBinding().statsNormal3.setVisibility(View.INVISIBLE);


                getBinding().statsEgym1.setVisibility(View.INVISIBLE);
                getBinding().statsEgym2.setVisibility(View.INVISIBLE);
                getBinding().statsEgym3.setVisibility(View.INVISIBLE);
                getBinding().statsEgym4.setVisibility(View.INVISIBLE);
                getBinding().statsEgym5.setVisibility(View.INVISIBLE);
                getBinding().statsEgym6.setVisibility(View.INVISIBLE);

                ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
                for (int i = 0; i < getBinding().rgSelectItemTreadmill.getChildCount(); i++) {
                    View o = getBinding().rgSelectItemTreadmill.getChildAt(i);
                    if (o instanceof RadioButton) {
                        listOfRadioButtons.add((RadioButton) o);
                        if (listOfRadioButtons.get(i).getId() == checkedId) {
                            returnValue(new MsgEvent(listOfRadioButtons.get(i).getTag()));
                        }
                    }
                }

                dismiss();
            });
        } else {

            //BIKE
            for (int i = 0; i < getBinding().rgSelectItemBike.getChildCount(); i++) {
                View o = getBinding().rgSelectItemBike.getChildAt(i);
                if (o instanceof RadioButton) {
                    radioButtonArrayList.add((RadioButton) o);
                    int radioTag = 0;
                    if (radioButtonArrayList.get(i).getTag() != null) {
                        radioTag = Integer.parseInt((String) radioButtonArrayList.get(i).getTag());
                    }
                    radioButtonArrayList.get(i).setChecked(radioTag == tag);

                    //上數沒有 Time Left
                    if (workoutViewModel.selWorkoutTime.get() == UNLIMITED) {
                        disabledItem(radioTag, STATS_REMAINING_TIME, radioButtonArrayList.get(i), 0);
                    }

                    if (radioTag != tag) {
                        disabledItem(radioTag, stats1Tag, radioButtonArrayList.get(i), 1);
                        disabledItem(radioTag, stats2Tag, radioButtonArrayList.get(i), 1);
                        disabledItem(radioTag, stats3Tag, radioButtonArrayList.get(i), 1);

                        if (workoutViewModel.selProgram == EGYM) {
                            disabledItem(radioTag, stats4Tag, radioButtonArrayList.get(i), 1);
                            disabledItem(radioTag, stats5Tag, radioButtonArrayList.get(i), 1);
                            disabledItem(radioTag, stats6Tag, radioButtonArrayList.get(i), 1);
                        }
                    }
                }
            }

            getBinding().rgSelectItemTreadmill.setVisibility(View.GONE);
            getBinding().rgSelectItemBike.setVisibility(View.VISIBLE);

            getBinding().rgSelectItemBike.setOnCheckedChangeListener((group, checkedId) -> {
                getBinding().statsNormal1.setVisibility(View.INVISIBLE);
                getBinding().statsNormal2.setVisibility(View.INVISIBLE);
                getBinding().statsNormal3.setVisibility(View.INVISIBLE);

                getBinding().statsEgym1.setVisibility(View.INVISIBLE);
                getBinding().statsEgym2.setVisibility(View.INVISIBLE);
                getBinding().statsEgym3.setVisibility(View.INVISIBLE);
                getBinding().statsEgym4.setVisibility(View.INVISIBLE);
                getBinding().statsEgym5.setVisibility(View.INVISIBLE);
                getBinding().statsEgym6.setVisibility(View.INVISIBLE);


                ArrayList<RadioButton> listOfRadioButtons = new ArrayList<>();
                for (int i = 0; i < getBinding().rgSelectItemBike.getChildCount(); i++) {
                    View o = getBinding().rgSelectItemBike.getChildAt(i);
                    if (o instanceof RadioButton) {
                        listOfRadioButtons.add((RadioButton) o);
                        if (listOfRadioButtons.get(i).getId() == checkedId) {
                            returnValue(new MsgEvent(listOfRadioButtons.get(i).getTag()));
                        }
                    }
                }
                dismiss();
            });
        }
    }

    private boolean checkDistanceLeft() {
        return (workoutViewModel.selProgram == ARMY || workoutViewModel.selProgram == AIR_FORCE || workoutViewModel.selProgram == COAST_GUARD || workoutViewModel.selProgram == NAVY || workoutViewModel.selProgram == PEB || workoutViewModel.selProgram == RUN_5K || workoutViewModel.selProgram == RUN_10K || workoutViewModel.selProgram == MARINE_CORPS);
    }

    private void disabledItem(int radioTag, int disabledTag, RadioButton radioButton, int type) {
        if (radioTag == disabledTag) {

            radioButton.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.color505a7085));
            radioButton.setEnabled(false);
//            radioButton.setBackgroundResource(0);
            if (type == 0) {
                //禁用的選項
                radioButton.setBackgroundResource(R.drawable.btn_toggle_unactive);
            } else {
                //已被選中的選項
                radioButton.setBackgroundResource(R.drawable.selected_check_press_341_xx);
            }

        }
    }

    private void changeState(int no) {

        TextView stateShow = getBinding().tvStateShow1;
        TextView stateShowSelect = getBinding().tvStateShowSelect1;
        TextView stateShowUnit = getBinding().tvStateShow1Unit;
        switch (no) {
            case PANEL_1:
                stateShow = getBinding().tvStateShow1;
                stateShowSelect = getBinding().tvStateShowSelect1;
                stateShowUnit = getBinding().tvStateShow1Unit;
                break;
            case PANEL_2:
                stateShow = getBinding().tvStateShow2;
                stateShowSelect = getBinding().tvStateShowSelect2;
                stateShowUnit = getBinding().tvStateShow2Unit;
                break;
            case PANEL_3:
                stateShow = getBinding().tvStateShow3;
                stateShowSelect = getBinding().tvStateShowSelect3;
                stateShowUnit = getBinding().tvStateShow3Unit;
                break;
        }

        comm.setStats(stateShow, stateShowSelect, stateShowUnit,null, tag, workoutViewModel, mContext);
    }


    private void changeStateEgym(int no) {

        TextView stateShow = getBinding().tvShow1Egym;
        TextView stateShow2 = getBinding().tvShow2Egym;
        TextView stateShowSelect = getBinding().tvShow1SelectEgym;
        TextView stateShowUnit = getBinding().tvShow1UnitEgym;
        switch (no) {
            case PANEL_1:
                stateShow = getBinding().tvShow1Egym;
                stateShow2 = getBinding().tvShow12Egym;
                stateShowSelect = getBinding().tvShow1SelectEgym;
                stateShowUnit = getBinding().tvShow1UnitEgym;
                break;
            case PANEL_2:
                stateShow = getBinding().tvShow2Egym;
                stateShow2 = getBinding().tvShow22Egym;
                stateShowSelect = getBinding().tvShow2SelectEgym;
                stateShowUnit = getBinding().tvShow2UnitEgym;
                break;
            case PANEL_3:
                stateShow = getBinding().tvShow3Egym;
                stateShow2 = getBinding().tvShow32Egym;
                stateShowSelect = getBinding().tvShow3SelectEgym;
                stateShowUnit = getBinding().tvShow3UnitEgym;
                break;
            case PANEL_4:
                stateShow = getBinding().tvShow4Egym;
                stateShow2 = getBinding().tvShow42Egym;
                stateShowSelect = getBinding().tvShow4SelectEgym;
                stateShowUnit = getBinding().tvShow4UnitEgym;
                break;
            case PANEL_5:
                stateShow = getBinding().tvShow5Egym;
                stateShow2 = getBinding().tvShow52Egym;
                stateShowSelect = getBinding().tvShow5SelectEgym;
                stateShowUnit = getBinding().tvShow5UnitEgym;
                break;
            case PANEL_6:
                stateShow = getBinding().tvShow6Egym;
                stateShow2 = getBinding().tvShow62Egym;
                stateShowSelect = getBinding().tvShow6SelectEgym;
                stateShowUnit = getBinding().tvShow6UnitEgym;
                break;
        }

        comm.setStats(stateShow, stateShowSelect, stateShowUnit,stateShow2, tag, workoutViewModel, mContext);
    }

    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> dismiss());


        if (workoutViewModel.selProgram != EGYM) {

            //顯示高亮
            switch (no) {
                case PANEL_1:
                    getBinding().statsNormal1.setVisibility(View.VISIBLE);
                    break;
                case PANEL_2:
                    getBinding().statsNormal2.setVisibility(View.VISIBLE);
                    break;
                case PANEL_3:
                    getBinding().statsNormal3.setVisibility(View.VISIBLE);
                    break;
            }


            changeState(no);


            getBinding().statsEgym1.setVisibility(View.GONE);
            getBinding().statsEgym2.setVisibility(View.GONE);
            getBinding().statsEgym3.setVisibility(View.GONE);
            getBinding().statsEgym4.setVisibility(View.GONE);
            getBinding().statsEgym5.setVisibility(View.GONE);
            getBinding().statsEgym6.setVisibility(View.GONE);


        } else {

            //顯示高亮
            switch (no) {
                case PANEL_1:
                    getBinding().statsEgym1.setVisibility(View.VISIBLE);

                    break;
                case PANEL_2:
                    getBinding().statsEgym2.setVisibility(View.VISIBLE);
                    break;
                case PANEL_3:
                    getBinding().statsEgym3.setVisibility(View.VISIBLE);
                    break;
                case PANEL_4:
                    getBinding().statsEgym4.setVisibility(View.VISIBLE);
                    break;
                case PANEL_5:
                    getBinding().statsEgym5.setVisibility(View.VISIBLE);
                    break;
                case PANEL_6:
                    getBinding().statsEgym6.setVisibility(View.VISIBLE);
                    break;
            }


            changeStateEgym(no);


            getBinding().statsNormal1.setVisibility(View.GONE);
            getBinding().statsNormal2.setVisibility(View.GONE);
            getBinding().statsNormal3.setVisibility(View.GONE);



            if (isUs && !isGGG) {
                getBinding().statsEgym3.setVisibility(View.GONE);
                getBinding().statsEgym6.setVisibility(View.GONE);
            }

            if (isTreadmill) {
                getBinding().rbTimeLeft.setText(String.format("%s #", mContext.getString(R.string.Interval)));
                getBinding().rbTimeLeft.setTag(String.valueOf(GENERAL.STATS_INTERVAL));

                getBinding().rbDistance.setText(mContext.getString(R.string.Total_Distance));

                getBinding().rbDistanceLeft.setText(mContext.getString(R.string.interval_distance));
                getBinding().rbDistanceLeft.setTag(String.valueOf(GENERAL.STATS_INTERVAL_DISTANCE));

     //       } else {
//                getBinding().rbTimeLeftBike.setText(String.format("%s #", mContext.getString(R.string.Interval)));
//                getBinding().rbTimeLeftBike.setTag(String.valueOf(GENERAL.STATS_INTERVAL));
//
//                getBinding().rbDistanceBike.setText(mContext.getString(R.string.Total_Distance));
//                getBinding().rbDistanceBike.setTag(String.valueOf(GENERAL.STATS_INTERVAL_DISTANCE));

                //     getBinding().rbDistanceBike.setText(mContext.getString(R.string.interval_distance));
            }
        }

    }

    //每秒執行,只顯示和跟新被點擊的stats
    public void updateStatsData() {
        if (workoutViewModel.selProgram != EGYM) {
            switch (no) {
                case PANEL_1:
                    getBinding().tvStateShow1.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    break;
                case PANEL_2:
                    getBinding().tvStateShow2.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    break;
                case PANEL_3:
                    getBinding().tvStateShow3.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    break;

            }
        } else {
            switch (no) {
                case PANEL_1:
                    getBinding().tvShow1Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow12Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow12Egym);
                    break;
                case PANEL_2:
                    getBinding().tvShow2Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow22Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow22Egym);
                    break;
                case PANEL_3:
                    getBinding().tvShow3Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow32Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow32Egym);
                    break;
                case PANEL_4:
                    getBinding().tvShow4Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow42Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow42Egym);
                    break;
                case PANEL_5:
                    getBinding().tvShow5Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow52Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow52Egym);
                    break;
                case PANEL_6:
                    getBinding().tvShow6Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,false));
                    getBinding().tvShow62Egym.setText(comm.getStatsValue(tag, workoutViewModel, isTreadmill,true));

                    changeViewColor(tag, getBinding().tvShow62Egym);
                    break;

            }
        }
    }

    int cc1 = ContextCompat.getColor(getApp(), R.color.white);
    int cc2 = ContextCompat.getColor(getApp(), R.color.white_20);
    //讓沒設定Target的變色
    private void changeViewColor(int tag, TextView view) {
        String value = "1";
        switch (tag) {
            case STATS_SPEED:
            case STATS_LEVEL:
                value = workoutViewModel.egymTargetSpeed.get();
                break;
            case STATS_INCLINE:
                value = workoutViewModel.egymTargetIncline.get();
                break;
            case STATS_INTERVAL_DISTANCE:
                value = workoutViewModel.egymTargetDistance.get();
                break;
            case STATS_HEART_RATE:
                value = workoutViewModel.egymTargetHeartRate.get();
                break;
        }
        updateTextViewColor(view, value);
    }

    private void updateTextViewColor(TextView textView, String parameter) {
        int currentColor = textView.getCurrentTextColor();
        int targetColor = (E_BLANK.equals(parameter)) ? cc2 : cc1;

        if (currentColor != targetColor) { // 只有當顏色不同時才執行 setTextColor
            textView.setTextColor(targetColor);
        }
    }
}
