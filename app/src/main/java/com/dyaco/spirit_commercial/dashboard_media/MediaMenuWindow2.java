package com.dyaco.spirit_commercial.dashboard_media;


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.lastMedia;
import static com.dyaco.spirit_commercial.MainActivity.mediaType;
import static com.dyaco.spirit_commercial.dashboard_media.HowToMirrorWindow.isWOn;
import static com.dyaco.spirit_commercial.support.CommonUtils.isRealMove;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_MAINTENANCE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_PAUSE_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_RESUME_WORKOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.NEW_MEDIA_CONTROLLER;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_WEBVIEW_BACK;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.SHOW_PANELS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_B5_RUNNING_AD_CHANGE_RSP;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_PAUSE_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.DS_RUNNING_STANDBY;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_TYPE_APP;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_TYPE_HDMI;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_EZ_CAST;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.alert_message.BackgroundWindow;
import com.dyaco.spirit_commercial.alert_message.WorkoutMediaControllerWindow;
import com.dyaco.spirit_commercial.databinding.WindowMediaMenuBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.base_component.BaseWindow;
import com.dyaco.spirit_commercial.support.custom_view.CustomToast;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MediaMenuWindow2 extends BaseWindow<WindowMediaMenuBinding> {
    public static boolean isShowTv; //只有TV Port1、4 要顯示遙控器
    public static boolean isShowMira; //hot to Miracast
    private boolean isShowBack;
    MainActivity activity;
    int maxWidth = WRAP_CONTENT; //english 304
    // int maxWidth = 361;
//    int minWidth = 136;
    int minWidth = 176;

    int viewHeight;
    private final AppStatusViewModel statusViewModel;
    private final Context mContext;
    private int viewX = 50;
    private int viewY = 80;

    //    int heightIcon2 = 216;//2 icon
    int heightIcon2 = 252;//2 icon
    //    int heightIcon3 = 312;//3 icon
    int heightIcon3 = 348;//3 icon
    //    int heightIcon4 = 408;//4 icon
    int heightIcon4 = 444;//4 icon
    //    int heightIcon5 = 504;//5 icon
    int heightIcon5 = 540;//5 icon

    int heightIcon6 = 636;//5 icon

//    android:animateLayoutChanges="true" 動畫

    public MediaMenuWindow2(Context context, AppStatusViewModel statusViewModel) {
//        super(context, 361, 504, Gravity.START | Gravity.TOP, 50, 80);
        super(context, 0, 636, Gravity.START | Gravity.TOP, 50, 80);
//        super(context, 0, 636, Gravity.END | Gravity.BOTTOM, 20, 180);
        mContext = context;
        activity = (MainActivity) context;
        this.statusViewModel = statusViewModel;

        statusViewModel.isMediaPlaying.set(true);

        //  isShowTv = getApp().getDeviceSettingBean().getVideo() != VIDEO_NONE;

        if (mediaType != MEDIA_TYPE_HDMI) {
            isShowTv = false;
        }

//        if (activity.floatingBottomMenuWindow == null && activity.floatingTopDashBoardWindow == null) {
//            isDashboardHide = true; //關閉中
//        }


        //一開始就隱藏Dashboard
        activity.showMediaFloatingDashboard(false, false);

        if (activity.appStatusViewModel.currentStatus.get() == STATUS_MAINTENANCE) {
            getBinding().btnShowPanels.setEnabled(false);
        }

        initView();

        LiveEventBus.get(MEDIA_MENU_CHANGE).observeForever(observer);

        LiveEventBus.get(SHOW_PANELS, Boolean.class).observeForever(observer2);

        LiveEventBus.get(NEW_MEDIA_CONTROLLER, Boolean.class).observeForever(observer3);


        getMenu();

        //attachBaseContext 不能影響到BaseWindow ,如果有在 attachBaseContext 改字型大小或語言，就要
        getBinding().btnGoBack.setText(mContext.getString(R.string.Go_Back));
        getBinding().btnShowTvController.setText(mContext.getString(R.string.TV_Controller));
        getBinding().btnHowToMiracast.setText(mContext.getString(R.string.how_to_miracast));


//        new RxTimer().timer(100, number -> {
//            if (getBinding() == null) return;
//            maxWidth = getBinding().menuView.getMeasuredWidth();
//            Log.d("@@@EEEE", "$$$$$: " + maxWidth);
//
//            getBinding().btnAllMedia.setWidth(maxWidth);
//        });

        //   Log.d("PPEPFPEP", "MediaMenuWindow2: " + activity.floatingTopDashBoardWindow +","+activity.floatingBottomMenuWindow);



    }

    Observer<Object> observer = s -> getMenu();
    Observer<Boolean> observer2 = this::showPanels;
    Observer<Boolean> observer3 = this::pauseWorkout;


    private void showPanels(Boolean s) {
        isDashboardHide = s;
        getBinding().btnShowPanels.callOnClick();
    }

    private void getMenu() {
        if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE ||
                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY ||
                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_LOGIN_PAGE ||
                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_MAINTENANCE) {
            initIdleMenu();
        } else {
            initWorkoutIngMenu();
        }
    }

    private void initIdleMenu() {
        getBinding().mLine0.setVisibility(View.VISIBLE);
        getBinding().mLine1.setVisibility(View.VISIBLE);
        getBinding().mLine2.setVisibility(View.VISIBLE);
        getBinding().mLine3.setVisibility(View.VISIBLE);
        getBinding().mLine4.setVisibility(View.VISIBLE);
        getBinding().mLine5.setVisibility(View.VISIBLE);

        getBinding().btnPause.setVisibility(View.GONE);
        getBinding().btnHowToMiracast.setVisibility(View.GONE);

        //  viewHeight = isShowTv ? heightIcon4 : heightIcon3;
//        layoutParams.width = isExpand ? minWidth : maxWidth;
        layoutParams.width = isExpand ? minWidth : WRAP_CONTENT;

        getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_menu_1396ef);
        getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_rest);
        getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
        getBinding().btnShowPanels.setText(isExpand ? "" : isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels));
        getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));
        getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));

        if (mediaType == MEDIA_TYPE_HDMI) {

            getBinding().btnGoBack.setVisibility(View.GONE);
            getBinding().mLine2.setVisibility(View.GONE);
            setTopToBottomConstraint(R.id.btnShowPanels, R.id.mLine1);

            if (isShowTv) {
                //idle 有 TvController 沒有back
                getBinding().mLine4.setVisibility(View.INVISIBLE);
                getBinding().mLine5.setVisibility(View.INVISIBLE);

                viewHeight = heightIcon3;
                getBinding().btnShowTvController.setVisibility(View.VISIBLE);
            } else {

                if (isShowMira) {
                    getBinding().btnShowTvController.setVisibility(View.GONE);
                    getBinding().btnHowToMiracast.setVisibility(View.VISIBLE);
                    getBinding().mLine4.setVisibility(View.GONE);
                    getBinding().mLine5.setVisibility(View.GONE);
                    getBinding().mLine3.setVisibility(View.VISIBLE);
                    viewHeight = heightIcon3;
                    //     Log.d("PPPPEEPEEPEP", isShowMira + "," + isShowTv + "," + getBinding().btnHowToMiracast.getVisibility());
                } else {

                    //idle 沒有 TvController 沒有back,ok
                    getBinding().mLine3.setVisibility(View.INVISIBLE);
                    viewHeight = heightIcon2;
                    getBinding().btnShowTvController.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            //idle 沒有 TvController 有back,ok
            getBinding().mLine3.setVisibility(View.INVISIBLE);

            viewHeight = heightIcon3;
            getBinding().btnShowTvController.setVisibility(View.INVISIBLE);
        }


        layoutParams.height = viewHeight;
        mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);

        getBinding().btnShowPanels.setCompoundDrawablesWithIntrinsicBounds(isDashboardHide ? ContextCompat.getDrawable(mContext, R.drawable.icon_screen_off) : ContextCompat.getDrawable(mContext, R.drawable.icon_screen_on), null, null, null);


        // if (!isExpand) maxWidth = getBinding().clBase.getMeasuredWidth();

//        new RxTimer().timer(100, number -> {
//            maxWidth = getBinding().clBase.getMeasuredWidth();
//            Log.d("@@@EEEE", "initIdleMenu: " + maxWidth);
//        });
    }

    private void initWorkoutIngMenu() {
        getBinding().mLine0.setVisibility(View.VISIBLE);
        getBinding().mLine1.setVisibility(View.VISIBLE);
        getBinding().mLine2.setVisibility(View.VISIBLE);
        getBinding().mLine3.setVisibility(View.VISIBLE);
        getBinding().mLine4.setVisibility(View.VISIBLE);
        getBinding().mLine5.setVisibility(View.VISIBLE);

        getBinding().btnPause.setVisibility(View.VISIBLE);
        getBinding().btnHowToMiracast.setVisibility(View.GONE);

        if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {

            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_e24b44);
            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_pause);
            getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.controls));
            getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
            getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
            getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));
            getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.controls));
            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_controls_32), null, null, null);

            getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));

        } else if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {

            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_0dac87);
            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_workout);
            getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
            getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
            getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));
            getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.controls));
            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_controls_32), null, null, null);

            getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));
        }


        if (mediaType == MEDIA_TYPE_HDMI) {

            getBinding().btnGoBack.setVisibility(View.GONE);
            getBinding().mLine2.setVisibility(View.GONE);
            getBinding().mLine5.setVisibility(View.GONE);
            setTopToBottomConstraint(R.id.btnShowPanels, R.id.mLine1);

            if (isShowTv) {
                //workout 有 TvController 沒有back,ok
                viewHeight = heightIcon4;
                getBinding().btnShowTvController.setVisibility(View.VISIBLE);
                setTopToBottomConstraint(R.id.btnPause, R.id.mLine4);
            } else {

                if (isShowMira) {
                    getBinding().btnShowTvController.setVisibility(View.GONE);
                    getBinding().btnHowToMiracast.setVisibility(View.VISIBLE);
                    getBinding().mLine4.setVisibility(View.VISIBLE);
                    getBinding().mLine5.setVisibility(View.INVISIBLE);
                    getBinding().mLine3.setVisibility(View.VISIBLE);
                    viewHeight = heightIcon4;
                    setTopToBottomConstraint(R.id.btnPause, R.id.mLine4);
                    setTopToBottomConstraint(R.id.btnHowToMiracast, R.id.mLine3);
                } else {

                    //workout 沒有 TvController 沒有back,ok
                    getBinding().mLine4.setVisibility(View.INVISIBLE);
                    getBinding().mLine5.setVisibility(View.INVISIBLE);
                    viewHeight = heightIcon3;
                    getBinding().btnShowTvController.setVisibility(View.INVISIBLE);
                    setTopToBottomConstraint(R.id.btnPause, R.id.mLine3);
                }
            }
        } else {
            //workout 沒有 TvController 有back ,ok
            getBinding().mLine4.setVisibility(View.INVISIBLE);
            getBinding().mLine5.setVisibility(View.INVISIBLE);
            viewHeight = heightIcon4;
            getBinding().btnShowTvController.setVisibility(View.INVISIBLE);
            setTopToBottomConstraint(R.id.btnPause, R.id.mLine3);
        }

        //   viewHeight = isShowTv ? heightIcon5 : heightIcon4;
        layoutParams.height = viewHeight;
        layoutParams.width = isExpand ? minWidth : WRAP_CONTENT;
//        layoutParams.width = isExpand ? minWidth : maxWidth;

        mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);

        getBinding().btnShowPanels.setCompoundDrawablesWithIntrinsicBounds(isDashboardHide ? ContextCompat.getDrawable(mContext, R.drawable.icon_screen_off) : ContextCompat.getDrawable(mContext, R.drawable.icon_screen_on), null, null, null);

        //  if (!isExpand) maxWidth = getBinding().clBase.getMeasuredWidth();

//        new RxTimer().timer(100, number -> {
//            maxWidth = getBinding().clBase.getMeasuredWidth();
//            Log.d("@@@EEEE", "initWorkoutIngMenu: " + maxWidth);
//        });
    }

    boolean isDashboardHide = true; //false 開啓中，true 關閉中    一開始就隱藏Dashboard
    boolean isExpand = false;

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        getBinding().btnAllMedia.setEnabled(false);
        getBinding().btnGoBack.setEnabled(false);
        getBinding().btnHowToMiracast.setEnabled(false);
        getBinding().btnPause.setEnabled(false);

        new RxTimer().timer(2000, number -> {
            if (getBinding() != null) {
                getBinding().btnAllMedia.setEnabled(true);
                getBinding().btnGoBack.setEnabled(true);
                getBinding().btnHowToMiracast.setEnabled(true);
                getBinding().btnPause.setEnabled(true);
            }
        });


        getBinding().btnAllMedia.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            //   statusViewModel.isMediaPauseOrResume.set(false);

            MainActivity.lastMedia = null;
            activity.closeMedia();
            //   dismiss();
        });

        getBinding().btnGoBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;

            if (mediaType == MEDIA_TYPE_APP) {
                new Thread(() -> {
                    try {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);

                        checkForegroundApp();
                    } catch (Exception e) {
                        Log.d("Exception when sendKeyDownUpSync", e.toString());
                    }
                }).start();
            } else {
                LiveEventBus.get(ON_WEBVIEW_BACK).post(true);
            }
        });

        getBinding().btnShowPanels.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            activity.showMediaFloatingDashboard(isDashboardHide, true);
            isDashboardHide = !isDashboardHide;

            getBinding().btnShowPanels.setCompoundDrawablesWithIntrinsicBounds(isDashboardHide ? ContextCompat.getDrawable(mContext, R.drawable.icon_screen_off) : ContextCompat.getDrawable(mContext, R.drawable.icon_screen_on), null, null, null);
            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
        });

//        getBinding().btnExpand.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            expandMenu(isExpand);
//            isExpand = !isExpand;
//        });

        getBinding().viewExpand.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            expandMenu(isExpand);
            isExpand = !isExpand;
        });


        getBinding().viewExpand.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                getBinding().btnExpand.setAlpha(0.7f);
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                getBinding().btnExpand.setAlpha(1f);
                view.performClick();
                return false;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                getBinding().btnExpand.setAlpha(1f);
                return false;
            } else {
                return false;
            }
        });



        getBinding().btnShowTvController.setOnClickListener(v -> {

            if (CheckDoubleClick.isFastClick()) return;

            if (TvControllerWindow.isTvControllerOn) return;

            BackgroundWindow backgroundWindow = new BackgroundWindow(mContext);
            backgroundWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

            TvControllerWindow popupWindow = new TvControllerWindow(mContext,true);
            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);

            popupWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null) {
                        Toasty.warning(mContext, "TV Tuner ERROR", Toasty.LENGTH_SHORT).show();
                    }
                    backgroundWindow.dismiss();
                }

                @Override
                public void onDismiss() {

                }
            });
        });

        getBinding().btnPause.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            //   statusViewModel.isMediaPauseOrResume.set(true);

            Log.d("PPPPPPPP", "showWorkoutController: ");
            showWorkoutController();


//            if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
//
//                //等狀態
//                if (isTreadmill && (activity.uartConsole.getDevStep() != DS_RUNNING_STANDBY && activity.uartConsole.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
//                    return;
//                }
//
//                //在Running時 暫停Workout
//                getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_0dac87);
//                getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_workout);
//                getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_start), null, null, null);
//                getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.Resume_Workout));
//                getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
//                getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
//                getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
//                getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));
//
//                getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));
//
//                LiveEventBus.get(MEDIA_PAUSE_WORKOUT).post("");
//
//            } else if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
//
//                //Resume Workout
//
//                if (isTreadmill) {
//                    //等狀態
//                    if (activity.uartConsole.getDevStep() != DS_PAUSE_STANDBY && !isEmulator) {
//                        CustomToast.showToast(activity, mContext.getString(R.string.resume_waiting));
//                        return;
//                    }
//                }
//
//                getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_e24b44);
//                getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_pause);
//                getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause), null, null, null);
//                getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.Pause_Workout));
//                getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
//                getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
//                getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
//                getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));
//
//                getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));
//
//                LiveEventBus.get(MEDIA_RESUME_WORKOUT).post("");
//
//            }
        });


        getBinding().btnHowToMiracast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastClick()) return;
                if (isWOn) return;
                activity.popupWindow = new HowToMirrorWindow(activity, PORT_EZ_CAST, GENERAL.NONE);
                activity.popupWindow.setAnimationStyle(R.style.PopupAnimation);
                activity.popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, -50);
            }
        });



//        getBinding().btnDragMenuA.setOnTouchListener(touchListener);
//        getBinding().btnAllMedia.setOnTouchListener(touchListener2);


        getBinding().btnDragMenu.setOnTouchListener(touchListener2);
        getBinding().btnDragMenu2.setOnTouchListener(touchListener2);
        getBinding().btnDragMenuA.setOnTouchListener(touchListener2);

        getBinding().btnShowPanels.setOnTouchListener(touchListener2);
        getBinding().btnAllMedia.setOnTouchListener(touchListener2);
        getBinding().btnGoBack.setOnTouchListener(touchListener2);
        getBinding().btnPause.setOnTouchListener(touchListener2);
        getBinding().btnShowTvController.setOnTouchListener(touchListener2);
        getBinding().btnHowToMiracast.setOnTouchListener(touchListener2);


        //   expandViewTouchDelegate(getBinding().btnDragMenu2,130,130,130,130);
        //    CommonUtils.expandViewTouchDelegate(getBinding().btnDragMenu, 0, 0, 0, 130);
    }

    private final PointF pointF = new PointF();
    private boolean isActionUp = true;

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();


                    isActionUp = true;
                    pointF.set(event.getX(), event.getY());
                    view.setPressed(true);

                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    viewX = viewX + movedX;
                    viewY = viewY + movedY;
                    layoutParams.x = viewX;
                    layoutParams.y = viewY;
                    mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);
//                    update(viewX, viewY, -1, -1, true);


//                    if (isRelMove(pointF, event)) {
//                        isActionUp = false;
//                        view.setPressed(false);
//                    } else {
                    isActionUp = true;
//                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (isActionUp) {
                        view.performClick();
                    }
                    view.setPressed(false);
                    //   view.performClick(); //up 時觸發click
                    break;
            }
            return true; // false 會觸發 click, true 不會
        }
    };









    private final PointF downPoint = new PointF();
    private boolean isClick = true;

    View.OnTouchListener touchListener2 = new View.OnTouchListener() {
        private int lastRawX;
        private int lastRawY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastRawX = rawX;
                    lastRawY = rawY;
                    isClick = true;
                    // 改為記錄螢幕絕對座標
                    downPoint.set(event.getRawX(), event.getRawY());
                    view.setPressed(true);
                    break;

                case MotionEvent.ACTION_MOVE:
                    int deltaX = rawX - lastRawX;
                    int deltaY = rawY - lastRawY;
                    lastRawX = rawX;
                    lastRawY = rawY;

                    viewX += deltaX;
                    viewY += deltaY;

                    layoutParams.x = viewX;
                    layoutParams.y = viewY;
                    mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);

                    // 使用 raw 座標判斷是否有真實移動
                    if (isRealMove(downPoint, event)) {
                        isClick = false;
                        view.setPressed(false);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (isClick) {
                        view.performClick();
                    }
                    view.setPressed(false);
                    break;
            }
            return true;
        }
    };







    public void expandMenu(Boolean isExpand) {

        if (!isExpand) {
            //收縮
            getBinding().btnAllMedia.setText("");
            getBinding().btnShowPanels.setText("");
            getBinding().btnShowTvController.setText("");
            getBinding().btnPause.setText("");
            getBinding().btnGoBack.setText("");
            getBinding().btnHowToMiracast.setText("");
        }

        final int targetWidth = getBinding().clBase.getMeasuredWidth();
        //    Log.d("PPPPEPEPEFF", "expandMenu: " + isExpand + ","+ targetWidth);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(isExpand ? minWidth : targetWidth, isExpand ? targetWidth : minWidth);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(animation -> {
            layoutParams.height = viewHeight;
            layoutParams.width = (int) animation.getAnimatedValue();
            mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);

            //  update((int) animation.getAnimatedValue(), viewHeight);
        });
        valueAnimator.start();

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                getBinding().btnAllMedia.setText(isExpand ? mContext.getString(R.string.All_Media) : "");
                getBinding().btnGoBack.setText(isExpand ? mContext.getString(R.string.Go_Back) : "");

                //    getBinding().btnShowPanels.setText(isExpand ? mContext.getString(R.string.Show_Panels) : "");
                getBinding().btnShowPanels.setText(isExpand ? (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)) : "");
                getBinding().btnShowTvController.setText(isExpand ? mContext.getString(R.string.TV_Controller) : "");
//                getBinding().btnPause.setText(isExpand ? (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE ? mContext.getString(R.string.Resume_Workout) : mContext.getString(R.string.Pause_Workout)) : "");
                getBinding().btnPause.setText(isExpand ? mContext.getString(R.string.controls) : "");
                getBinding().iconMenuArrow.setBackgroundResource(isExpand ? R.drawable.icon_fold : R.drawable.icon_extend);

                getBinding().btnHowToMiracast.setText(isExpand ? mContext.getString(R.string.how_to_miracast) : "");

                layoutParams.height = viewHeight;
                layoutParams.width = isExpand ? WRAP_CONTENT : minWidth;
                mWindowManager.updateViewLayout(getBinding().getRoot(), layoutParams);
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();

        //  if (!App.SETTING_SHOW) {
        //如果系統設定頁面開啟中，就不改
        statusViewModel.isMediaPlaying.set(false);
        //   }

    //    activity = null;
        //  MainActivity.mediaStatus = GENERAL.MEDIA_NOT_PLAYING;


        LiveEventBus.get(MEDIA_MENU_CHANGE).removeObserver(observer);

        LiveEventBus.get(SHOW_PANELS,Boolean.class).removeObserver(observer2);

        LiveEventBus.get(NEW_MEDIA_CONTROLLER,Boolean.class).removeObserver(observer3);

        try {
            if (activity.popupWindow3 != null) {
                activity.popupWindow3.dismiss();
                activity.popupWindow3 = null;
            }
        } catch (Exception e) {
            showException(e);
        }

    }


    private void setTopToBottomConstraint(int viewTop, int viewBottom) {
        ConstraintLayout constraintLayout = getBinding().clBase;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(viewTop, ConstraintSet.TOP, viewBottom, ConstraintSet.BOTTOM, 0);//layout_constraintTop_toBottomOf
        constraintSet.applyTo(constraintLayout);
    }


    private void showWorkoutController() {

        if (WorkoutMediaControllerWindow.isMediaWorkoutController) return;

        activity.popupWindow3 = new WorkoutMediaControllerWindow(activity);
        activity.popupWindow3.showAtLocation(activity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        ((WorkoutMediaControllerWindow)activity.popupWindow3).setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                if (value != null && ((boolean) value.getObj())){

                }
            }

            @Override
            public void onDismiss() {
                WorkoutMediaControllerWindow.isMediaWorkoutController = false;
                activity.popupWindow3 = null;
            }
        });
    }


    private void pauseWorkout(boolean isPause) {
        if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {

            //等狀態
            if (isTreadmill && (activity.uartConsole.getDevStep() != DS_RUNNING_STANDBY && activity.uartConsole.getDevStep() != DS_B5_RUNNING_AD_CHANGE_RSP) && !isEmulator) {
                return;
            }

            //在Running時 暫停Workout
            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_0dac87);
            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_workout);
//            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_start), null, null, null);
            getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.controls));
            getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
            getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
            getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));

            getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));

            LiveEventBus.get(MEDIA_PAUSE_WORKOUT).post("");

        } else if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {

            //Resume Workout

            if (isTreadmill) {
                //等狀態
                if (activity.uartConsole.getDevStep() != DS_PAUSE_STANDBY && !isEmulator) {
                    CustomToast.showToast(activity, mContext.getString(R.string.resume_waiting));
                    return;
                }
            }

            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_e24b44);
            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_pause);
//            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause), null, null, null);
           getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.controls));
            getBinding().btnAllMedia.setText(isExpand ? "" : mContext.getString(R.string.All_Media));
            getBinding().btnGoBack.setText(isExpand ? "" : mContext.getString(R.string.Go_Back));
            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardHide ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
            getBinding().btnShowTvController.setText(isExpand ? "" : mContext.getString(R.string.TV_Controller));

            getBinding().btnHowToMiracast.setText(isExpand ? "" : mContext.getString(R.string.how_to_miracast));

            LiveEventBus.get(MEDIA_RESUME_WORKOUT).post("");

        }
    }



    private String lastPackageName = "";

    private void checkForegroundApp() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            if (taskInfo != null && !taskInfo.isEmpty()) {
                ComponentName topActivity = taskInfo.get(0).topActivity;
                if (topActivity != null) {
                    String currentPackage = topActivity.getPackageName();

                    if (!currentPackage.equals(lastPackageName)) {
                        Log.d("BBBBBBBB", "前台應用變更: " + currentPackage);
                        String applicationId = getApp().getPackageName();
                        if (applicationId.equalsIgnoreCase(currentPackage)) {
                            lastMedia = null;
                            Log.d("BBBBBBBB", "回到CONSOLE: " + currentPackage);
                        }
                        // 如果變成桌面，代表應用被關閉
                        if (currentPackage.contains("launcher")) {
                            Log.d("BBBBBBBB", "應用因返回鍵退出！");
                        }

                        lastPackageName = currentPackage;
                    }
                }
            }
        }
    }

}
