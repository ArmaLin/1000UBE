package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isGMS;
import static com.dyaco.spirit_commercial.support.CommonUtils.closePackage;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.CONSOLE_MEDIA_APP_LIST;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.BuildConfig;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.alert_message.UninstallAlertWindow;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceAppmanagerBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.product_flavor.UpdateOtherAppWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.CommonUtilsKT;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.custom_view.DividerItemDecorator;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.mediaapp.AppManagerAdapter;
import com.dyaco.spirit_commercial.support.mediaapp.AppStoreBean;
import com.dyaco.spirit_commercial.support.mediaapp.AppUpdateManager;
import com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils;
import com.dyaco.spirit_commercial.support.mediaapp.PackageManagerUtils;
import com.dyaco.spirit_commercial.support.mediaapp.QuickReplyItemTouchCallback;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.LogS;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * 第三方APP更新的路徑：
 * https://ucaremedi-ugym.s3.ap-northeast-1.amazonaws.com/CoreStar/production/SpiritCommercial2022/APPs/*.apk
 */
public class MaintenanceAppManagerFragment extends BaseBindingDialogFragment<FragmentMaintenanceAppmanagerBinding> {
    public static final int UPDATE_Y = 1; //已安裝 可更新
    public static final int UPDATE_N = 2; //已安裝 不可更新
    public static final int UPDATE_X = 0; //未安裝 可更新    //console資料庫沒有此app
    private MainActivity mainActivity;

    private AppManagerAdapter appManagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用match 會出問題
//        setDialogType(GENERAL.TRANSLATION_Y);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        App.isShowMediaMenuOnStop = true;

        mainActivity = ((MainActivity) requireActivity());


        packageManagerUtils = new PackageManagerUtils();
        appUpdateManager = new AppUpdateManager(requireActivity(), packageManagerUtils);

        //    initAppData();

        initView();


        if (!isNetworkAvailable(requireActivity().getApplication())) {
            Toasty.warning(requireActivity(), R.string.No_internet_connection, Toasty.LENGTH_LONG).show();
            return;
        }

        getAppStoreApiData();


        //點10次
        TenTapClick.setTenClickListener(getBinding().tvTitle, () -> {
            mainProgress(true);

            //刪除MediaApp資料
            SpiritDbManager.getInstance(getApp()).deleteAllMediaApp(new DatabaseCallback<MediaAppsEntity>() {
                @Override
                public void onDeleted() {
                    super.onDeleted();
                    Log.d("MMEEEEDDDIIAAA", "刪除資料庫全部MediaApp: ");
                    MediaAppUtils.checkConsoleMediaApp();
                }
            });

            new RxTimer().timer(5000, number -> {
                try {
                    getAppStoreApiData();
                    mainProgress(false);
                } catch (Exception e) {
                    showException(e);
                }
            });
        });


        //   installXapk();
    }

    private void checkConsoleApp() {

        for (AppStoreBean.AppUpdateBeansDTO appStoreBean : appStoreList) {

            for (int i = 0; i < CONSOLE_MEDIA_APP_LIST.size(); i++) {
                String appStorePkName = appStoreBean.getPackageName();
                if (CONSOLE_MEDIA_APP_LIST.get(i).getPackageName().equals(appStorePkName)) {
                    String appStoreVersion = appStoreBean.getVersion();
                    //     Log.d("UPDATE_APP", "=======================APP: " + appStoreBean.getAppName() + ", " + packageManagerUtils.getPackageVersionCode2(getApp(), appStorePkName));
                    boolean isUpgrade = packageManagerUtils.isUpgrade(appUpdateManager.getPackageInfoVersionName(appStorePkName), appStoreVersion);
//                    Log.d("MMEEEEDDDIIAAA", "版本: " + appStoreBean.getAppName() + ", " + appUpdateManager.getPackageInfoVersionName(appStorePkName) + " ," + appStoreVersion);

                    if (isUpgrade) {
                        //json 改成不用 web > console原本是web >  刪掉重裝
                        if (!isGMS && "YES".equalsIgnoreCase(CONSOLE_MEDIA_APP_LIST.get(i).getGmsNeeded())) {
                            isUpgrade = false;
                        }
                    }

                    //   Log.d("UPDATE_APP", "版本: " + appStoreBean.getAppName() + ", " + (isUpgrade ? "要更新" : "不用更新"));

                    appStoreBean.setIsUpdate(isUpgrade ? UPDATE_Y : UPDATE_N);

                    //      Log.d("KKKKKKKK", "checkConsoleApp: " + appStoreBean.getIsUpdate() + "," + appStoreBean.getAppName());
                }
            }
        }

    }


    private void initView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        getBinding().recyclerView.setLayoutManager(linearLayoutManager);
        getBinding().recyclerView.setHasFixedSize(true);
        DividerItemDecorator dividerItemDecoration = new DividerItemDecorator(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line_252e37)));
        getBinding().recyclerView.addItemDecoration(dividerItemDecoration);
        appManagerAdapter = new AppManagerAdapter(requireActivity(), appUpdateManager);
        getBinding().recyclerView.setAdapter(appManagerAdapter);

        appManagerAdapter.setData2View(appStoreList);


        QuickReplyItemTouchCallback mCallback = new QuickReplyItemTouchCallback(appManagerAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(getBinding().recyclerView);


        appManagerAdapter.setOnDragStartListener(new AppManagerAdapter.OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });

        //更新APP
        appManagerAdapter.setOnItemClickListener(this::updateApp);


        //刪除APP
        appManagerAdapter.setOnDelListener(bean -> {

//            if (!isNetworkAvailable(getApp())) {
//                Toasty.warning(requireActivity(), R.string.No_internet_connection, Toasty.LENGTH_SHORT).show();
//                return;
//            }

            //只剩一個不給刪
            if (CONSOLE_MEDIA_APP_LIST.size() == 1) {
                //     Toasty.error(requireActivity(), "ONE", Toasty.LENGTH_LONG).show();
                return;
            }

            UninstallAlertWindow uninstallAlertWindow = new UninstallAlertWindow(requireActivity());
            uninstallAlertWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
            uninstallAlertWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    if (value != null && ((boolean) value.getObj())) {

//                        if (!isGMS && "YES".equalsIgnoreCase(bean.getGmsNeeded())) {
//                            //網頁
//                            Log.d("MMEEEEDDDIIAAA", "刪除APP 網頁: ");
//
//                            //解除安裝 app
//                            CommonUtils.uninstallByPackageManager(getApp(), bean.getPackageName());
//                        } else {
//                            Log.d("MMEEEEDDDIIAAA", "刪除APP: ");
//                            //解除安裝 app
//                            CommonUtils.uninstallByPackageManager(getApp(), bean.getPackageName());
//                        }

                        mainProgress(true);

                        boolean isUninstall = CommonUtils.uninstallByPackageManager(getApp(), bean.getPackageName());

                        Log.d("Uninstall", "isUninstall: " + isUninstall);

                        if (isUninstall) {

                            //刪除Console資料庫 app 資料
                            MediaAppUtils.deleteMediaByPkgName(bean.getPackageName());
                            new RxTimer().timer(3000, b -> getAppStoreApiData());

                        } else {
                            mainProgress(false);
                            Toasty.error(requireActivity(), "Uninstall ERROR", Toasty.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onDismiss() {

                }
            });
        });

        getBinding().btnDone.setOnClickListener(v -> {
            dismiss();

            //更新資料庫
//            for (int i = 0; i < CONSOLE_MEDIA_APP_LIST.size(); i++) {
//                for (AppStoreBean.AppUpdateBeansDTO s : appManagerAdapter.getAppStoreList()) {
//                    if (CONSOLE_MEDIA_APP_LIST.get(i).getPackageName().equalsIgnoreCase(s.getPackageName())) {
//                        CONSOLE_MEDIA_APP_LIST.get(i).setSort(s.getSort());
//                   //     CONSOLE_MEDIA_APP_LIST.get(i).setAppName(s.getAppName());
//
//                        Log.d("EEEEQQEWQEQW", "initView: " + s.getAppName() + "," + s.getSort());
//                    }
//                }
//                CONSOLE_MEDIA_APP_LIST.set(i, CONSOLE_MEDIA_APP_LIST.get(i));
//            }
//
//            SpiritDbManager.getInstance(getApp()).insertMediaAppList(CONSOLE_MEDIA_APP_LIST,
//                    new DatabaseCallback<List<MediaAppsEntity>>() {
//
//                        @Override
//                        public void onUpdated() {
//                            super.onUpdated();
//                            CONSOLE_MEDIA_APP_LIST.sort(new MediaAppEntitySort());
//                            Log.d("EEEEQQEWQEQW", "onUpdated: 更新完成");
//                        }
//
//                        @Override
//                        public void onError(String err) {
//                            super.onError(err);
//                        }
//                    });


        });


        appManagerAdapter.setOnItemClickListener2(new AppManagerAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick(AppStoreBean.AppUpdateBeansDTO beansDTO) {
                //   startMedia(bean);

//               final byte[][] bytesM = {null};
//               Disposable disposable = downloadImage(beansDTO.getIcon().get(0).getAppIconMediumUrl())
//                       .subscribe(
//                               imageBytes -> {
//                                   bytesM[0] = imageBytes;
//                                   MediaAppsEntity mediaAppsEntity = new MediaAppsEntity(beansDTO.getPackageName(), beansDTO.getAppName(), beansDTO.getType(), beansDTO.getVersion(), beansDTO.getComment(), beansDTO.getGmsNeeded(), beansDTO.getForceUpdates(), beansDTO.getVersionCode(), beansDTO.getSort(), beansDTO.getApk().get(0).getMd5(), beansDTO.getApk().get(0).getDownloadUrl(), beansDTO.getApk().get(0).getPath(), beansDTO.getWeb().get(0).getWebUrl(), 0, new byte[0], bytesM[0], new byte[0]);
//                                   updateMediaApp(mediaAppsEntity);
//                                   Log.d("EEEEQQEWQEQW", "SORT: " + beansDTO.getSort() +","+ beansDTO.getAppName());
//                               },
//                               error -> Log.d("MMEEEEDDDIIAAA", "imageError: ")
//                       );

            }
        });

    }


    private void updateApp(AppStoreBean.AppUpdateBeansDTO appUpdateBeansDTO) {
        Log.d("MMEEEEDDDIIAAA", "$$$$$$$$$$$$$$$$$$$$updateApp: ");
        if (CheckDoubleClick.isFastClick()) return;

        if (!isNetworkAvailable(getApp())) {

            Toasty.warning(requireActivity(), R.string.No_internet_connection, Toasty.LENGTH_SHORT).show();
            return;
        }

        if (appUpdateBeansDTO.getIcon().isEmpty() || appUpdateBeansDTO.getApk().isEmpty() || appUpdateBeansDTO.getWeb().isEmpty()) {
            Log.d("MMEEEEDDDIIAAA", "updateApp: " + appUpdateBeansDTO.getPackageName());
            Toasty.error(requireActivity(), "#appstore_update.json ERROR", Toasty.LENGTH_LONG).show();
            return;
        }


        mainProgress(true);

        String pkgName = appUpdateBeansDTO.getPackageName();
        String newVersion = appUpdateManager.getAppUpdateVersion(appUpdateBeansDTO, pkgName);//WebApi上面的
        String updateUrl = appUpdateManager.getAppUpdateDownloadUrl(appUpdateBeansDTO, pkgName);//WebApi上面的

        String packageVersion = appUpdateManager.getPackageInfoVersionName(pkgName);//Console的
        String packageName = appUpdateManager.getPackageInfoPackageName(pkgName);//Console的
        boolean isForceUpdates = appUpdateManager.isForceUpdates(appUpdateBeansDTO, packageName);

        Log.d("MMEEEEDDDIIAAA", "newVersion: " + newVersion + ", updateUrl:" + updateUrl + ", packageVersion:" + packageVersion + ", packageName:" + packageName + ", isForceUpdates:" + isForceUpdates);
        boolean isInstalled;
        if (packageVersion.isEmpty() && packageName.isEmpty()) {
            Log.d("MMEEEEDDDIIAAA", "沒安裝: ");
            isInstalled = false;
        } else {
            isInstalled = true;
            Log.d("MMEEEEDDDIIAAA", "已安裝: ");
        }


        if (!isGMS && "YES".equalsIgnoreCase(appUpdateBeansDTO.getGmsNeeded())) {
            //網頁
            Log.d("MMEEEEDDDIIAAA", "更新 網頁: ");
            updateWeb(appUpdateBeansDTO);
        } else {
            //  if (packageManagerUtils.isUpgrade(packageVersion, newVersion)) {
            //新版本可以更新,且有設定更新選項
            Log.d("MMEEEEDDDIIAAA", "新版本可以更新");
            updateStart("".equals(packageName) ? pkgName : packageName, updateUrl, appUpdateBeansDTO, isInstalled);
            //    }
        }
    }

    private void updateWeb(AppStoreBean.AppUpdateBeansDTO beansDTO) {

//        final byte[][] bytesM = {null};
//        disposable = downloadImage(beansDTO.getIcon().get(0).getAppIconMediumUrl())
//                .subscribe(
//                        imageBytes -> {
//                            bytesM[0] = imageBytes;
//                            Log.d("MMEEEEDDDIIAAA", "圖片下載成功:  圖圖圖圖: " + Arrays.toString(bytesM[0]));
//                            insertAndRefresh(beansDTO, bytesM);
//                        },
//                        error -> {
//                            mainProgress(false);
//                            Toasty.warning(requireActivity(), "icon download error", Toasty.LENGTH_SHORT).show();
//                            //     insertAndRefresh(beansDTO, bytesM);
//                            Log.d("MMEEEEDDDIIAAA", "imageError: ");
//                        }
//                );


        String iconUrl = beansDTO.getIcon().get(0).getAppIconMediumUrl();
        CommonUtilsKT.downloadImage(iconUrl, new CommonUtilsKT.DownloadCallback() {
            @Override
            public void onSuccess(@NonNull byte[] imageBytes) {
                byte[][] wrappedImage = CommonUtilsKT.wrapByteArray(imageBytes);
                Log.d("UPDATE_APP", "WEB圖片下載成功 圖圖圖圖: " + imageBytes.length);
                insertAndRefresh(beansDTO, wrappedImage);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                mainProgress(false);
                Toasty.warning(requireActivity(), "icon download error", Toasty.LENGTH_SHORT).show();
                Log.d("UPDATE_APP", "imageError: " + throwable.getMessage());
            }
        });



    }

    public String updatedPackage;

    //啟動下載視窗
    private void updateStart(String packageName, String updateUrl, AppStoreBean.AppUpdateBeansDTO beansDTO, boolean isInstalled) {

        String iconUrl = beansDTO.getIcon().get(0).getAppIconMediumUrl();
        CommonUtilsKT.downloadImage(iconUrl, new CommonUtilsKT.DownloadCallback() {
            @Override
            public void onSuccess(@NonNull byte[] imageBytes) {
                byte[][] wrappedImage = CommonUtilsKT.wrapByteArray(imageBytes);
                Log.d("UPDATE_APP", "XXX圖片下載成功 圖圖圖圖: " + imageBytes.length);

                if (!isInstalled) {
                    mainProgress(false);
                    Log.d("UPDATE_APP", "此App沒有存在資料庫，且Console尚未安裝: ");
                    showUpdateAppWindow(new byte[][]{imageBytes}, packageName, updateUrl, beansDTO);
                } else {
                    if (beansDTO.getIsUpdate() == UPDATE_Y) {
                        Log.d("UPDATE_APP", "已存在資料庫，已安裝，可更新 ");
                        showUpdateAppWindow(wrappedImage, packageName, updateUrl, beansDTO);
                    } else {
                        Log.d("UPDATE_APP", "尚未存在資料庫，已安裝: 直接存入資料庫");
                        insertAndRefresh(beansDTO, wrappedImage);
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                mainProgress(false);
                Toasty.warning(requireActivity(), "icon download error", Toasty.LENGTH_SHORT).show();
                Log.d("UPDATE_APP", "imageError: " + throwable.getMessage());
            }
        });
    }

    private void showUpdateAppWindow(byte[][] bytesM, String packageName, String updateUrl, AppStoreBean.AppUpdateBeansDTO beansDTO) {
        UpdateOtherAppWindow updateAppWindow = new UpdateOtherAppWindow(requireActivity(), packageName, updateUrl);
        updateAppWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.BOTTOM, 0, 0);
        updateAppWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                Timber.tag("UPDATE_APP").d("刷新選單: ");
                if (value != null) {
                    mainProgress(true);
                    insertAndRefresh(beansDTO, bytesM);
                } else {
                    mainProgress(false);
                }

            }

            @Override
            public void onDismiss() {
                Timber.tag("UPDATE_APP").d("onDismiss: ");
            }
        });
    }


    //新增media app 預設 sort  >>>>>  MAX_MEDIA_APP_COUNT
    private void insertAndRefresh(AppStoreBean.AppUpdateBeansDTO beansDTO, byte[][] bytesM) {

        MediaAppUtils.insertMediaApp(beansDTO, bytesM);

        new RxTimer().timer(500, b -> getAppStoreApiData());

        updatedPackage = beansDTO.getPackageName();


    }


    private AppUpdateManager appUpdateManager;
    private PackageManagerUtils packageManagerUtils;
    private List<AppStoreBean.AppUpdateBeansDTO> appStoreList = new ArrayList<>();

    private void getAppStoreApiData() {

        BaseApi.request(BaseApi.createApi2(IServiceApi.class, BuildConfig.APP_STORE_URL).apiAppStore(),
                new BaseApi.IResponseListener<AppStoreBean>() {
                    @Override
                    public void onSuccess(AppStoreBean appStoreBean) {
                        LogS.printJson("MMEEEEDDDIIAAA", new Gson().toJson(appStoreBean), "");
                        try {
                            appStoreList = appStoreBean.getAppUpdateBeans();

                            //根據資料庫的MediaApp資料做排序
                            appStoreList = MediaAppUtils.sortAppStoreBean(appStoreBean);

                            appManagerAdapter.setData2View(appStoreList);
                            checkConsoleApp();


                            // === 新增這段：捲到剛剛更新的那個 item
                            if (updatedPackage != null && !updatedPackage.isEmpty()) {
                                int pos = -1;
                                for (int i = 0; i < appStoreList.size(); i++) {
                                    if (appStoreList.get(i).getPackageName().equalsIgnoreCase(updatedPackage)) {
                                        pos = i;
                                        break;
                                    }
                                }
                                final int finalPos = pos;
                                if (finalPos >= 0) {
                                    getBinding().recyclerView.post(() ->
                                            getBinding().recyclerView.smoothScrollToPosition(finalPos)
                                    );
                                }
                                updatedPackage = null;
                            }


                        } catch (Exception e) {
                            showException(e);
                            Timber.tag("UPDATE_APP").d("@@#########checkAppUpdate Exception:" + e.toString());
                        }

                        hideProgress();

                        mainProgress(false);
                    }

                    @Override
                    public void onFail() {
                        try {
                            hideProgress();
                            //  Log.d("MMEEEEDDDIIAAA", "json error ");
                            mainProgress(false);
                            Toasty.error(requireActivity(), "APP STORE DATA ERROR", Toasty.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            showException(e);
                            Timber.tag("UPDATE_APP").d("@@#########checkAppUpdate Exception:" + e.toString());
                        }
                    }
                });
    }


    private void hideProgress() {
        if (getBinding() != null) {
            try {
                getBinding().progress.setVisibility(View.GONE);
            } catch (Exception e) {
                showException(e);
            }
        }
    }

    private void showProgress() {
        if (getBinding() != null) {
            try {
                getBinding().progress.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                showException(e);
            }
        }
    }


    private void mainProgress(boolean isShow) {
        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoading(isShow);
        }
    }


    private void startMedia(AppStoreBean.AppUpdateBeansDTO beansDTO) {
        Log.d("OOOOOEEEWWW", "startMedia: " + beansDTO.getAppName() + "," + beansDTO.getIsUpdate());
        if (beansDTO.getIsUpdate() == UPDATE_X) return;

        if (!isGMS && "YES".equalsIgnoreCase(beansDTO.getGmsNeeded())) {
            //APP需要GMS,但此機器無GMS,使用WEB開啟
            openWeb(beansDTO);
        } else {
            openMediaApp(beansDTO);
        }

        mainActivity.showMediaFloatingDashboard(false, false);
    }

    private void openWeb(AppStoreBean.AppUpdateBeansDTO beansDTO) {
        if (CheckDoubleClick.isFastClick()) return;

        String url = beansDTO.getWeb().get(0).getWebUrl();

        if (!isNetworkAvailable(requireActivity().getApplication())) return;

        closePackage(requireActivity());

        mainActivity.showYouTube(true, url);
        mainActivity.showMediaMenu(true);
    }

    private void openMediaApp(AppStoreBean.AppUpdateBeansDTO beansDTO) {

        if (CheckDoubleClick.isFastClick()) return;
        if (!isNetworkAvailable(requireActivity().getApplication())) return;


        String packageName = beansDTO.getPackageName();
        String appName = beansDTO.getAppName();

        try {

            try {
                Intent intent;
                PackageManager packageManager = getApp().getPackageManager();
                intent = packageManager.getLaunchIntentForPackage(packageName);

                if (intent == null) {
                    Toasty.warning(requireActivity(), appName + " App Not Found", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                requireActivity().overridePendingTransition(0, 0);
            } catch (Exception e) {
                Toasty.warning(requireActivity(), appName + " App Not Found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toasty.warning(requireActivity(), appName + " App Not Found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // requireActivity().overridePendingTransition(R.anim.fade_in_2s, R.anim.fade_in_2s);
        Timber.tag("WEB_VIEW").d("onResume: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        App.isShowMediaMenuOnStop = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        setAni(false);
    }
}