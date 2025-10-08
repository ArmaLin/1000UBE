//package com.dyaco.spirit_commercial.dashboard_media;
//
//
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_MENU_CHANGE;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_PAUSE_WORKOUT;
//import static com.dyaco.spirit_commercial.support.intdef.EventKey.MEDIA_RESUME_WORKOUT;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.LinearInterpolator;
//
//import androidx.core.content.ContextCompat;
//import androidx.lifecycle.Observer;
//
//import com.dyaco.spirit_commercial.MainActivity;
//import com.dyaco.spirit_commercial.R;
//import com.dyaco.spirit_commercial.alert_message.BackgroundWindow;
//import com.dyaco.spirit_commercial.databinding.WindowMediaMenuBinding;
//import com.dyaco.spirit_commercial.support.MsgEvent;
//import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
//import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
//import com.dyaco.spirit_commercial.support.intdef.GENERAL;
//import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
//import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//
//public class MediaMenuWindow extends BasePopupWindow<WindowMediaMenuBinding> {
//    MainActivity activity;
//    int maxWidth = 361;
//    int minWidth = 136;
//
//    int viewHeight;
//    private final AppStatusViewModel statusViewModel;
//
//    public MediaMenuWindow(Context context, AppStatusViewModel statusViewModel) {
//        super(context, 0, 408, 361, GENERAL.TRANSLATION_Y, false, false, true,true);
//        activity = (MainActivity) context;
//        this.statusViewModel = statusViewModel;
//
//
//        statusViewModel.isMediaPlaying.set(true);
//        initView();
//
//        LiveEventBus.get(MEDIA_MENU_CHANGE).observeForever(observer);
//
//    }
//
//    Observer<Object> observer = s -> getMenu();
//
//    @Override
//    public void showAtLocation(View parent, int gravity, int x, int y) {
//        super.showAtLocation(parent, gravity, x, y);
//        getMenu();
//    }
//
//    private void getMenu() {
//        if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_IDLE ||
//                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_SUMMARY ||
//                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_LOGIN_PAGE ||
//                statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_MAINTENANCE) {
//            initIdleMenu();
//        } else {
//            initWorkoutIngMenu();
//        }
//    }
//
//    private void initIdleMenu() {
//        viewHeight = 312;
//        getBinding().btnPause.setVisibility(View.GONE);
//        update(maxWidth, viewHeight);
//        getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_menu_1396ef);
//        getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_rest);
//    }
//
//    private void initWorkoutIngMenu() {
//        viewHeight = 408;
//        getBinding().btnPause.setVisibility(View.VISIBLE);
//        update(maxWidth, viewHeight);
//        if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
//            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_e24b44);
//            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_pause);
//            getBinding().btnPause.setText(R.string.Pause_Workout);
//            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause), null, null, null);
//
//        } else if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
//            getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_0dac87);
//            getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_workout);
//            getBinding().btnPause.setText(R.string.Resume_Workout);
//            getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_start), null, null, null);
//
//        }
//    }
//
//    boolean isDashboardOpen = false;
//    boolean isExpand = false;
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initView() {
//
//        getBinding().btnGoHome.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            //   statusViewModel.isMediaPauseOrResume.set(false);
//            activity.closeMedia();
//        });
//
//        getBinding().btnShowPanels.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            activity.showMediaFloatingDashboard(isDashboardOpen,true);
//            isDashboardOpen = !isDashboardOpen;
//
//            getBinding().btnShowPanels.setCompoundDrawablesWithIntrinsicBounds(isDashboardOpen ? ContextCompat.getDrawable(mContext, R.drawable.icon_screen_off) : ContextCompat.getDrawable(mContext, R.drawable.icon_screen_on), null, null, null);
//            getBinding().btnShowPanels.setText(isExpand ? "" : (isDashboardOpen ? mContext.getString(R.string.Show_Panels) : mContext.getString(R.string.Hide_Panels)));
//        });
//
//        getBinding().btnExpand.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            expandMenu(isExpand);
//            isExpand = !isExpand;
//        });
//
//        getBinding().btnShowTvController.setOnClickListener(v -> {
//
//            if (CheckDoubleClick.isFastClick()) return;
//
//            BackgroundWindow backgroundWindow = new BackgroundWindow(mContext);
//            backgroundWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//
//            TvControllerWindow popupWindow = new TvControllerWindow(mContext);
//            popupWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//
//            popupWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
//                @Override
//                public void onStartDismiss(MsgEvent value) {
//                    backgroundWindow.dismiss();
//                }
//
//                @Override
//                public void onDismiss() {
//
//                }
//            });
//        });
//
//        getBinding().btnPause.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            //   statusViewModel.isMediaPauseOrResume.set(true);
//
//            if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_RUNNING) {
//
//                getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_0dac87);
//                getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_workout);
//                getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_start), null, null, null);
//                getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.Resume_Workout));
//                LiveEventBus.get(MEDIA_PAUSE_WORKOUT).post("");
//            } else if (statusViewModel.currentStatus.get() == AppStatusIntDef.STATUS_PAUSE) {
//
//                getBinding().menuView.setBackgroundResource(R.drawable.panel_bg_all_20_e24b44);
//                getBinding().btnExpand.setBackgroundResource(R.drawable.btn_menu_drawer_pause);
//                getBinding().btnPause.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.icon_pause), null, null, null);
//                getBinding().btnPause.setText(isExpand ? "" : mContext.getString(R.string.Pause_Workout));
//
//                LiveEventBus.get(MEDIA_RESUME_WORKOUT).post("");
//
////                ResumingWindow popupWindow = new ResumingWindow(mContext);
////                popupWindow.showAtLocation(((MainActivity) mContext).getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
//            }
//        });
//
//        getBinding().btnDragMenu.setOnTouchListener(touchListener);
//        getBinding().btnDragMenu2.setOnTouchListener(touchListener);
//
////        if (MainActivity.mediaStatus == GENERAL.MEDIA_NOT_PLAYING) {
////            Log.d("MMMEEEMEME", "initView: ");
////
////            new RxTimer().timer(200, number -> {
////                getBinding().btnShowPanels.callOnClick();
////                getBinding().btnExpand.callOnClick();
////            });
////        }
//
//    }
//
//
//    View.OnTouchListener touchListener = new View.OnTouchListener() {
//        private int x;
//        private int y;
//
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    x = (int) event.getRawX();
//                    y = (int) event.getRawY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int nowX = (int) event.getRawX();
//                    int nowY = (int) event.getRawY();
//                    int movedX = nowX - x;
//                    int movedY = nowY - y;
//                    x = nowX;
//                    y = nowY;
//                    viewX = viewX + movedX;
//                    viewY = viewY + movedY;
//                    update(viewX, viewY, -1, -1, true);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    getBinding().btnDragMenu.performClick();
//                    break;
//            }
//            return true;
//        }
//    };
//
////    View.OnTouchListener touchListener = new View.OnTouchListener() {
////        int orgX, orgY;
////        int offsetX, offsetY;
////
////        @Override
////        public boolean onTouch(View v, MotionEvent event) {
////            switch (event.getAction()) {
////                case MotionEvent.ACTION_DOWN:
////                    orgX = (int) event.getX();
////                    orgY = (int) event.getY();
////                    break;
////                case MotionEvent.ACTION_MOVE:
////                    offsetX = (int) event.getRawX() - orgX;
////                    offsetY = (int) event.getRawY() - orgY;
////                    update(offsetX, offsetY, -1, -1, true);
////                    break;
////                case MotionEvent.ACTION_UP:
////                    getBinding().btnDragMenu.performClick();
////                    break;
////            }
////            return true;
////        }
////    };
//
//    public void expandMenu(Boolean isExpand) {
//
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(getWidth(), isExpand ? maxWidth : minWidth);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setDuration(100);
//        valueAnimator.addUpdateListener(animation ->
//                update((int) animation.getAnimatedValue(), viewHeight));
//        valueAnimator.start();
//
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//
//                getBinding().btnGoHome.setText(isExpand ? mContext.getString(R.string.Go_Back) : "");
//                getBinding().btnShowPanels.setText(isExpand ? mContext.getString(R.string.Show_Panels) : "");
//                getBinding().btnShowTvController.setText(isExpand ? mContext.getString(R.string.TV_Controller) : "");
//                getBinding().btnPause.setText(isExpand ? mContext.getString(R.string.Pause_Workout) : "");
//                getBinding().iconMenuArrow.setBackgroundResource(isExpand ? R.drawable.icon_fold : R.drawable.icon_extend);
//            }
//        });
//        //   update(isExpand ? 300 : 100,320);
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();

//
//
//      //  if (!App.SETTING_SHOW) {
//            //如果系統設定頁面開啟中，就不改
//            statusViewModel.isMediaPlaying.set(false);
//     //   }
//
//        activity = null;
//      //  MainActivity.mediaStatus = GENERAL.MEDIA_NOT_PLAYING;
//
//
//        LiveEventBus.get(MEDIA_MENU_CHANGE).removeObserver(observer);
//
//    }
//}
