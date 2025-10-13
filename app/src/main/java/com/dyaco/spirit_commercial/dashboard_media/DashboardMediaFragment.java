package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isGMS;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.MainActivity.lastMedia;
import static com.dyaco.spirit_commercial.MainActivity.mediaType;
import static com.dyaco.spirit_commercial.MainActivity.userProfileViewModel;
import static com.dyaco.spirit_commercial.dashboard_media.HowToMirrorWindow.isWOn;
import static com.dyaco.spirit_commercial.dashboard_media.MediaMenuWindow2.isShowMira;
import static com.dyaco.spirit_commercial.dashboard_media.MediaMenuWindow2.isShowTv;
import static com.dyaco.spirit_commercial.dashboard_media.YouTubeWindow.isWebViewOn;
import static com.dyaco.spirit_commercial.support.CommonUtils.clearAppData;
import static com.dyaco.spirit_commercial.support.CommonUtils.clearCookies;
import static com.dyaco.spirit_commercial.support.CommonUtils.closePackage;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_NONE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_STB;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FIRST_MEDIA_APP;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FIRST_MEDIA_APP_PROGRESS;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_TYPE_APP;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.MEDIA_TYPE_WEBVIEW;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_EZ_CAST;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_MIRA_CAST;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_TV_TUNER;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.PORT_WIRE_CAST;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.CONSOLE_MEDIA_APP_LIST;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.BuildConfig;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentDashboardMediaBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingFragment;
import com.dyaco.spirit_commercial.support.custom_view.GridViewSpaceItemDecoration;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.mediaapp.AppStoreBean;
import com.dyaco.spirit_commercial.support.mediaapp.AppUpdateManager;
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppEnum;
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils;
import com.dyaco.spirit_commercial.support.mediaapp.PackageManagerUtils;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.ViewUtils;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class DashboardMediaFragment extends BaseBindingFragment<FragmentDashboardMediaBinding> {

    private MainActivity mainActivity;
    private DeviceSettingViewModel deviceSettingViewModel;
    private AppStatusViewModel appStatusViewModel;
    boolean isCanada;

    public DashboardMediaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = ((MainActivity) requireActivity());

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        appStatusViewModel = new ViewModelProvider(requireActivity()).get(AppStatusViewModel.class);

        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);
        getBinding().setIsUs(isUs);

        isCanada = deviceSettingViewModel.territoryCode.get() == TERRITORY_CANADA;
        getBinding().setIsGanada(isCanada);

        //  Log.d("SSSSSTTTTTTT", "onViewCreated: " + deviceSettingViewModel.isEnablePort1.get()  + ","+ deviceSettingViewModel.isEnablePort4.get());

        packageManagerUtils = new PackageManagerUtils();
        appUpdateManager = new AppUpdateManager(requireActivity(), packageManagerUtils);

//        WebView myWebView =getBinding().webview;
//        myWebView.loadUrl("https://www.youtube.com/");

        initView();
        initEvent();

        getBinding().setUserProfileViewModel(userProfileViewModel);

        if (!isNetworkAvailable(requireActivity().getApplication())) {
            parent.internetNotifyWarringWindow(true);
            getBinding().vBackground.setVisibility(View.VISIBLE);
        }
        //   parent.showNotifyWarringWindow("HR");

        LiveEventBus.get(WIFI_WORK, Boolean.class).observe(getViewLifecycleOwner(), s -> {
            parent.internetNotifyWarringWindow(!s);
            getBinding().vBackground.setVisibility(!s ? View.VISIBLE : View.GONE);
            getBinding().tvWifiName.setText(new CommonUtils().getSSID(requireActivity()));

        });


        mainActivity.initTvTuner();


//        Looper.myQueue().addIdleHandler(() -> {
        initMediaApp();

//            return false;
//        });

    }

    MediaListAdapter mediaListAdapter;

    private void initMediaApp() {

//        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), isUs ? 6 : 4) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 11) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        RecyclerView recyclerView = getBinding().recyclerview;
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridViewSpaceItemDecoration(40, 0, requireActivity()));

        recyclerView.setItemAnimator(null);//不要動畫

        mediaListAdapter = new MediaListAdapter(requireActivity(), gridLayoutManager);
        recyclerView.setAdapter(mediaListAdapter);

        mediaListAdapter.setOnItemClickListener(this::startMedia);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (isLoaded) return;
            if (isAdded() && getBinding() != null) {
                mediaListAdapter.setData2View(CONSOLE_MEDIA_APP_LIST);
                ViewUtils.forceLayoutNow(getBinding().recyclerview);
                getBinding().appProgress.setVisibility(View.GONE);
                checkAppUpdate();
                isLoaded = true;
           //     Log.d("MMEEEEDDDIIAAA", "延遲2秒載入Media: ");
            }
        }, 2000);


    //    Log.d("MMEEEEDDDIIAAA", "initMediaApp: ");


        LiveEventBus.get(FIRST_MEDIA_APP, Boolean.class).observe(getViewLifecycleOwner(), s -> {
       //     Log.d("MMEEEEDDDIIAAA", "initMediaApp2: ");
            for (int i = 0; i < CONSOLE_MEDIA_APP_LIST.size(); i++) {
                if (CONSOLE_MEDIA_APP_LIST.get(i).getPackageName().equals(MainActivity.forcePkName)) {
                    CONSOLE_MEDIA_APP_LIST.get(i).setUpdate(false);
                    CONSOLE_MEDIA_APP_LIST.set(i, CONSOLE_MEDIA_APP_LIST.get(i));
                    break;
                }
            }
            mediaListAdapter.setData2View(CONSOLE_MEDIA_APP_LIST);
        });

        LiveEventBus.get(FIRST_MEDIA_APP_PROGRESS, Integer.class).observe(getViewLifecycleOwner(), s -> {
            mediaListAdapter.updateProgress(s);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    //    Log.d("MMEEEEDDDIIAAA", "onResume: ");
    }

    private void initView() {

        getBinding().tvWifiName.setText(new CommonUtils().getSSID(requireActivity()));

        if (deviceSettingViewModel.video.getValue() == VIDEO_NONE) {
            getBinding().tvTvText.setText(R.string.TV_is_not_available_on_this_equipment);
            getBinding().btnWatchTv.setVisibility(View.INVISIBLE);
            getBinding().ivTvController.setBackgroundResource(R.drawable.icon_tv_controller_off);
            getBinding().viewChannel.setEnabled(false);
            getBinding().viewChannel.setClickable(false);
        }
    }

    private void initEvent() {
//        getBinding().btnAllChannels.setOnClickListener(v -> {
//            if (CheckDoubleClick.isFastClick()) return;
//            Navigation.findNavController(v).navigate(DashboardMediaFragmentDirections.actionDashboardMediaFragmentToChannelsFragment());
//        });


        /**HDMI**/
//        getBinding().btnWatchTv.setOnClickListener(view -> {
//            if (deviceSettingViewModel.video.getValue() == VIDEO_NONE) return;
//            openHdmiMedia(getBinding().tvProgress, PORT_TV_TUNER);
//        });

        getBinding().viewChannel.setOnClickListener(view -> {
            if (deviceSettingViewModel.video.getValue() == VIDEO_NONE) return;

            isShowTv = true;
            isShowMira = false;
            //STB 如果開啟，就用 Port 4，如果是TV就用 Port 1
            if (deviceSettingViewModel.video.getValue() == VIDEO_STB) {
                //   if (!deviceSettingViewModel.isEnablePort4.get()) return;
//                openHdmiMedia(getBinding().tvProgress, PORT_MIRA_CAST,deviceSettingViewModel.isEnablePort4.get());

                openHdmiMedia(PORT_MIRA_CAST);
            } else {
                //    if (!deviceSettingViewModel.isEnablePort1.get()) return;
//                openHdmiMedia(getBinding().tvProgress, PORT_TV_TUNER,deviceSettingViewModel.isEnablePort1.get());

                if (!MainActivity.isTvOn) {
                    if (CheckDoubleClick.isFastClick()) return;
                    Toasty.warning(requireActivity(), R.string.tvtuner_try_again, Toasty.LENGTH_SHORT).show();
                    mainActivity.initTvTuner();
                    //     Toasty.error(requireActivity(), "TvTuner ERROR", Toasty.LENGTH_LONG).show();
                    return;
                }

                openHdmiMedia(PORT_TV_TUNER);

            }
        });

        //Miracast
        //右上
        getBinding().viewMirror.setOnClickListener(view -> {
            isShowTv = false;
            isShowMira = true;
            //    if (!deviceSettingViewModel.isEnablePort2.get()) return;
//            openHdmiMedia(getBinding().mirrorProgress, PORT_EZ_CAST,deviceSettingViewModel.isEnablePort2.get());
            openHdmiMedia(PORT_EZ_CAST);
        });

        //右下
        getBinding().viewWireCast.setOnClickListener(view -> {
            isShowTv = false;
            isShowMira = false;
            openHdmiMedia(PORT_WIRE_CAST);
            //   openHdmiMedia(PORT_EZ_CAST);
        });

        getBinding().btnHowtoMirror.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (isWOn) return;
            parent.popupWindow = new HowToMirrorWindow(requireActivity(), PORT_EZ_CAST, GENERAL.TRANSLATION_Y);
            parent.popupWindow.showAtLocation(parent.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, -50);
        });

        getBinding().btnHowtoWireCast.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (isWOn) return;
            parent.popupWindow = new HowToMirrorWindow(requireActivity(), PORT_WIRE_CAST, GENERAL.TRANSLATION_Y);
            parent.popupWindow.showAtLocation(parent.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, -50);
        });


        /**Media App**/


        if (isUs) {
            getBinding().btnLogoutAll.setOnClickListener(view -> {

                mainActivity.showLoading(true);

                clearCookies();
                clearAppData();
                closePackage(requireActivity());

                new RxTimer().timer(1000, u -> mainActivity.showLoading(false));

                MediaAppUtils.checkForceUpdate();
            });

        } else {

//            /**YouTube**/
//            getBinding().btnYoutube.setOnClickListener(v -> startMedia(MediaAppEnum.YOUTUBE));
//
//            //News
//            getBinding().btnESPN.setOnClickListener(v -> startMedia(MediaAppEnum.ESPN));
//            getBinding().btnCNN.setOnClickListener(v -> startMedia(MediaAppEnum.CNN_NEWS));
//            getBinding().btnBBC.setOnClickListener(v -> startMedia(MediaAppEnum.BBC));
//
//            //Video Content
//            getBinding().btnNetflix.setOnClickListener(v -> startMedia(MediaAppEnum.NETFLIX));
//
//            //Social
//            getBinding().btnFB.setOnClickListener(v -> startMedia(MediaAppEnum.FACEBOOK));
//            getBinding().btnIG.setOnClickListener(v -> startMedia(MediaAppEnum.INSTAGRAM));
//            getBinding().btnTwitter.setOnClickListener(v -> startMedia(MediaAppEnum.TWITTER));
//
//            //Music
//            getBinding().btnSpotify.setOnClickListener(v -> startMedia(MediaAppEnum.SPOTIFY));
//            getBinding().btnIheart.setOnClickListener(v -> startMedia(MediaAppEnum.IHEART));
//
//            //Other
//            getBinding().btnWeather.setOnClickListener(v -> startMedia(MediaAppEnum.WEATHER));
//            getBinding().btnBitGym.setOnClickListener(v -> startMedia(MediaAppEnum.BIT_GYM));
//            getBinding().btnKinomap.setOnClickListener(v -> startMedia(MediaAppEnum.KINOMAP));
        }

    }

    private void openWeb(MediaAppsEntity mediaAppsEntity) {
        if (CheckDoubleClick.isFastClick()) return;

        if (isWebViewOn) return;

        //   String packageName = mediaAppEnum.getAppPackageName();
        String url = mediaAppsEntity.getWebUrl();

        if (!isNetworkAvailable(requireActivity().getApplication())) return;
        lastMedia = mediaAppsEntity;
        mediaType = MEDIA_TYPE_WEBVIEW;
        closePackage(requireActivity());
        showBackground(4500);
   //     Log.d("CCCCCCCCCC", "######openWeb: ");
        parent.showYouTube(true, url);
        parent.showMediaFloatingDashboard(true, true);
        parent.showMediaMenu(true);
    }

    //HDMI連線進入背景會斷掉
    private void openHdmiMedia(int hdmiSource) {

        if (CheckDoubleClick.isFastClick()) return;

        if (mainActivity == null || mainActivity.hdmiIn == null) {
            Toasty.warning(requireActivity(), "HDMI ERROR", Toasty.LENGTH_LONG).show();
            return;
        }

        mainActivity.showLoading(true);


        mainActivity.hdmiIn.ScanHdmiSource();

        new RxTimer().timer(500, u -> {
            //     Log.d("SSSSSTTTTTTT", "1111111111111: ");
            if (!mainActivity.hdmiIn.CheckHdmiSource(hdmiSource)) {
                //Hdmi Port 無連接
                mainActivity.showLoading(false);
                //     Log.d("SSSSSTTTTTTT", "CheckHdmiSource: ");
                return;
            }
            mainActivity.switchHdmi(hdmiSource);
        });

    }

    private void startMedia(MediaAppsEntity mediaAppsEntity) {
        if (!isGMS && "YES".equalsIgnoreCase(mediaAppsEntity.getGmsNeeded())) {
            //APP需要GMS,但此機器無GMS,使用WEB開啟
            openWeb(mediaAppsEntity);
        } else {
            openMediaApp(mediaAppsEntity);
        }
    }

    private void openMediaApp(MediaAppsEntity mediaAppsEntity) {

        MainActivity.isGymApp = false;

        isShowTv = false;
        isShowMira = false;
        if (CheckDoubleClick.isFastClick()) return;
        if (!isNetworkAvailable(requireActivity().getApplication())) return;


//        if ("".equals(lastMedia)){
//            closePackage(requireActivity());
//        }


        String packageName = mediaAppsEntity.getPackageName();
        //   String className = mediaAppEnum.getAppClassName();
        String appName = mediaAppsEntity.getAppName();


        lastMedia = mediaAppsEntity;


        mediaType = MEDIA_TYPE_APP;

        try {
            showBackground(5000);

            //在多媒體app出現之前，若浮動視窗就出現，有bug會不能按，先暫時出現一下，再由MainActivity onStop 重新產生
            parent.showMediaFloatingDashboard(true, false);

            try {
                Intent intent;
                PackageManager packageManager = getApp().getPackageManager();
                intent = packageManager.getLaunchIntentForPackage(packageName);

                if (intent == null) {
                    lastMedia = null;
                    parent.getBinding().vBackground.setVisibility(View.GONE);
                    parent.showMediaFloatingDashboard(false, false);
                    Toasty.warning(requireActivity(), appName + " " + getString(R.string.not_installed), Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                requireActivity().overridePendingTransition(0, 0);
            } catch (Exception e) {
                lastMedia = null;
                parent.getBinding().vBackground.setVisibility(View.GONE);
                parent.showMediaFloatingDashboard(false, false);
                Toasty.warning(requireActivity(), appName + " " + getString(R.string.not_installed), Toast.LENGTH_SHORT).show();
            }


            MainActivity.isGymApp = packageName.equals(MediaAppEnum.BIT_GYM.getAppPackageName()) || packageName.equals(MediaAppEnum.KINOMAP.getAppPackageName());

        } catch (Exception e) {
            lastMedia = null;
            parent.getBinding().vBackground.setVisibility(View.GONE);
            parent.showMediaFloatingDashboard(false, false);
            Toasty.warning(requireActivity(), appName + " " + getString(R.string.not_installed), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isLoaded = false;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    //    Log.d("OOOOOOOOOOOO", "onHiddenChanged: " + hidden);
        if (!isNetworkAvailable(requireActivity().getApplication())) {
            parent.internetNotifyWarringWindow(true);
            getBinding().vBackground.setVisibility(View.VISIBLE);
            lastMedia = null;
        } else {

            if (!hidden) reStartMedia();
        }


        if (checkHdmiTimer != null) {
            checkHdmiTimer.cancel();
            checkHdmiTimer = null;
        }

        if (!hidden) {
            setCheckHdmiTimer();

            //     Looper.myQueue().addIdleHandler(() -> {
            //      Log.d("MMEEEEDDDIIAAA", "########onHiddenChanged: LOAD");
            if (!isLoaded) {
                isLoaded = true;
                mediaListAdapter.setData2View(CONSOLE_MEDIA_APP_LIST);
                getBinding().appProgress.setVisibility(View.GONE);
           //     Log.d("MMEEEEDDDIIAAA", "切換頁面時載入Media");
            }

            if (appStatusViewModel.currentStatus.get() != AppStatusIntDef.STATUS_RUNNING)
                checkAppUpdate();
            //            return false;
            //        });

            mainActivity.btNotifyWindow(true);

//            checkAppUpdate();

        }
        //   Log.d("HDMI_IN", "onHiddenChanged: " + hidden);

    }

    private AppUpdateManager appUpdateManager;
    private PackageManagerUtils packageManagerUtils;
    private List<AppStoreBean.AppUpdateBeansDTO> appUpdateBeans;

    private void checkAppUpdate() {
//        packageManagerUtils = new PackageManagerUtils();
//        appUpdateManager = new AppUpdateManager(requireActivity(), packageManagerUtils);

        BaseApi.request(BaseApi.createApi2(IServiceApi.class, BuildConfig.APP_STORE_URL).apiAppStore(),
                new BaseApi.IResponseListener<AppStoreBean>() {
                    @Override
                    public void onSuccess(AppStoreBean data) {

                        //       Log.d("MMEEEEDDDIIAAA", "checkAppUpdate -> onSuccess -> AppUpdateData:" + new Gson().toJson(data));
                        try {
                            appUpdateBeans = data.getAppUpdateBeans();
                            if (appUpdateBeans != null) {
                                for (AppStoreBean.AppUpdateBeansDTO appUpdateBean : appUpdateBeans) {


                                    for (int i = 0; i < CONSOLE_MEDIA_APP_LIST.size(); i++) {
                                        String packageName = appUpdateBean.getPackageName();
                                        if (CONSOLE_MEDIA_APP_LIST.get(i).getPackageName().equals(packageName)) {
                                            String version = appUpdateBean.getVersion();
                                            boolean isUpgrade = packageManagerUtils.isUpgrade(appUpdateManager.getPackageInfoVersionName(packageName), version);
                                            //     Log.d("MMEEEEDDDIIAAA", "版本: " +appUpdateManager.getPackageInfoVersionName(packageName) +" ,"+ version);

                                            if (isUpgrade) {
                                                //console 沒有 gms， 此media app 需要 gms ，變成 web
                                                if (!isGMS && "YES".equalsIgnoreCase(CONSOLE_MEDIA_APP_LIST.get(i).getGmsNeeded())) {
                                                    isUpgrade = false;
                                                }
                                            }

                                            CONSOLE_MEDIA_APP_LIST.get(i).setUpdate(isUpgrade);
                                            CONSOLE_MEDIA_APP_LIST.set(i, CONSOLE_MEDIA_APP_LIST.get(i));

                                            //   mediaListAdapter.updateData(i, CONSOLE_MEDIA_APP_LIST.get(i));

                                            mediaListAdapter.updateNotify(i);
                                        }
                                    }
                                }

                            }
                        } catch (Exception e) {
                            Log.d("UPDATE_APP", "checkAppUpdate" + "checkUpdate Exception:" + e.toString());
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("UPDATE_APP", "checkAppUpdate" + "checkUpdate error:");
                    }
                });
    }

    private RxTimer checkHdmiTimer;

    private void setCheckHdmiTimer() {
        checkHdmiTimer = new RxTimer();
        checkHdmiTimer.timer(500, number -> {

            if (mainActivity.hdmiIn != null) {
                mainActivity.hdmiIn.ScanHdmiSource();
            }

            //mainActivity.checkHdmiPort();
        });
    }

    private void showBackground(long staySec) {
        try {
            mainActivity.getBinding().vBackground.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            showException(e);
        }
        new RxTimer().timer(staySec, number -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                try {
                    mainActivity.getBinding().vBackground.setVisibility(View.GONE);
                } catch (Exception e) {
                    showException(e);
                }
            });
        });
    }


    private void reStartMedia() {
        if (lastMedia != null) {
            startMedia(lastMedia);
        }
    }
}