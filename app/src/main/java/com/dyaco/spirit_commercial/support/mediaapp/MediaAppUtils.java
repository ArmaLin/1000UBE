package com.dyaco.spirit_commercial.support.mediaapp;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isGMS;
import static com.dyaco.spirit_commercial.support.CommonUtils.downloadImage;
import static com.dyaco.spirit_commercial.support.CommonUtils.drawableToByteArray;
import static com.dyaco.spirit_commercial.support.CommonUtils.getFileExtension;
import static com.dyaco.spirit_commercial.support.CommonUtils.isNetworkAvailable;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_GLOBAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_JAPAN;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.FIRST_MEDIA_APP;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.BuildConfig;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.CommonUtilsKT;
import com.dyaco.spirit_commercial.support.InstallXapk.XapkInstaller;
import com.dyaco.spirit_commercial.support.InstallXapk.XapkManager;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.download.DownloadListener;
import com.dyaco.spirit_commercial.support.download.DownloadUtil;
import com.dyaco.spirit_commercial.support.room.DatabaseCallback;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManagerR;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.work_task.InstallCallback;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.disposables.Disposable;

public class MediaAppUtils {

    public static int MAX_MEDIA_APP_COUNT = 12;

    public static void checkConsoleMediaApp() {
        SpiritDbManagerR.getInstance(getApp()).getMediaAppList(new DatabaseCallback<MediaAppsEntity>() {
            @Override
            public void onDataLoadedList(List<MediaAppsEntity> mediaAppsEntityList) {
                super.onDataLoadedList(mediaAppsEntityList);
                CONSOLE_MEDIA_APP_LIST.clear();
                CONSOLE_MEDIA_APP_LIST.addAll(mediaAppsEntityList);

                //無資料，新增預設
                Log.d("MMMMEEEEDDDD", "checkConsoleMediaApp: " + mediaAppsEntityList.size());
                if (mediaAppsEntityList.isEmpty()) {
                    initDefaultMediaApp();
                }
                CONSOLE_MEDIA_APP_LIST.sort(new MediaAppEntitySort());
            }
        });


//        SpiritDbManager.getInstance(getApp()).getMediaAppList(new DatabaseCallback<MediaAppsEntity>() {
//            @Override
//            public void onDataLoadedList(List<MediaAppsEntity> mediaAppsEntityList) {
//                super.onDataLoadedList(mediaAppsEntityList);
//                CONSOLE_MEDIA_APP_LIST.clear();
//                CONSOLE_MEDIA_APP_LIST.addAll(mediaAppsEntityList);
//
//
//                //無資料，新增預設
//                Log.d("MMMMEEEEDDDD", "checkConsoleMediaApp: " + mediaAppsEntityList.size());
//                if (mediaAppsEntityList.isEmpty()) {
//           //         Log.d("MMMMEEEEDDDD", "checkConsoleMediaApp: 無資料，新增預設 " );
//                    initDefaultMediaApp();
//                }
//
//
//                CONSOLE_MEDIA_APP_LIST.sort(new MediaAppEntitySort());
//
//         //       Log.d("MMMMEEEEDDDD", "checkConsoleMediaApp: 檢查media app 完畢: " + CONSOLE_MEDIA_APP_LIST.size());
//
////                for (MediaAppsEntity mediaAppsEntity : mediaAppsEntityList) {
////                }
//
//            }
//        });
    }

    public static List<MediaAppsEntity> CONSOLE_MEDIA_APP_LIST = new ArrayList<>();

    /**
     * 預設的 Media APP
     */
    public static void initDefaultMediaApp() {
        CONSOLE_MEDIA_APP_LIST.clear();

//        //共同
//        CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.NETFLIX, 0));
//        CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.YOUTUBE, 1));
//        //  CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.SPOTIFY, 2));
//        CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.KINOMAP, 3));
//        //   CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.CNN_NEWS, 4));
//        //   CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.DISNEY_PLUS, 5));
//
//
//        CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.BIT_GYM, 2));

        int territoryCode = getApp().getDeviceSettingBean().getTerritoryCode();
//        boolean isCanada = territoryCode == TERRITORY_CANADA;
        switch (territoryCode) {
            case TERRITORY_GLOBAL:
            case TERRITORY_CANADA:
            case TERRITORY_JAPAN:
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.ESPN, 0));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.CNN_NEWS, 1));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.BBC, 2));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.SPOTIFY, 3));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.IHEART, 4));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.NETFLIX, 5));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.YOUTUBE, 6));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.WEATHER, 7));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.BIT_GYM, 8));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.FACEBOOK, 9));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.INSTAGRAM, 10));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.TWITTER, 11));
                break;
            case TERRITORY_US:
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.YOUTUBE, 0));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.NETFLIX, 1));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.PARAMOUNT_PLUS, 2));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.DISNEY_PLUS, 3));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.HBO_MAX, 4));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.PEACOCK, 5));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.IMDB_TV, 6));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.PLUTO_TV, 7));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.SPOTIFY, 8));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.KINOMAP, 9));
                CONSOLE_MEDIA_APP_LIST.add(new MediaAppsEntity(MediaAppEnum.BIT_GYM, 10));
                break;
        }


        if (isNetworkAvailable(getApp())) {
            //初始化後先抓 app_store.json 上面的資料，覆蓋CONSOLE_MEDIA_APP_LIST ，再存入資料庫
            Log.d("MMMMEEEEDDDD", "有網路，抓網路上的app store資料: ");
            updateAll();
        } else {
            //直接用本機預設的media資料存入資料庫
            Log.d("MMMMEEEEDDDD", "梅有網路，使用預設 media app 資料: ");
            insertMediaAppList();
        }
    }

    /**
     * 將 CONSOLE_MEDIA_APP_LIST  存入資料庫
     */
    private static void insertMediaAppList() {
        SpiritDbManager.getInstance(getApp()).insertMediaAppList(CONSOLE_MEDIA_APP_LIST,
                new DatabaseCallback<List<MediaAppsEntity>>() {

                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                        Log.d("MMMMEEEEDDDD", "insertMediaAppList: 存入資料庫");
                        checkConsoleMediaApp();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });
    }


    public static void deleteAllMediaApp() {
        SpiritDbManager.getInstance(getApp()).deleteAllMediaApp(new DatabaseCallback<MediaAppsEntity>() {
            @Override
            public void onDeleted() {
                super.onDeleted();
            }
        });
    }

    public static void deleteMediaByPkgName(String packageName) {
        SpiritDbManager.getInstance(getApp()).deleteMediaApp(packageName, new DatabaseCallback<MediaAppsEntity>() {
            @Override
            public void onDeleted() {
                super.onDeleted();

                //     checkConsoleMediaApp();


                ///////////////////////////////////////////////////////////
                for (MediaAppsEntity mediaAppsEntity : CONSOLE_MEDIA_APP_LIST) {
                    if (mediaAppsEntity.getPackageName().equalsIgnoreCase(packageName)) {
                        CONSOLE_MEDIA_APP_LIST.remove(mediaAppsEntity);
                        break;
                    }
                }

                CONSOLE_MEDIA_APP_LIST.sort(new MediaAppEntitySort());
                int i = 0;
                for (MediaAppsEntity mediaAppsEntity : CONSOLE_MEDIA_APP_LIST) {
                    mediaAppsEntity.setSort(i);
                    CONSOLE_MEDIA_APP_LIST.set(i, mediaAppsEntity);
                    i++;
                }

                SpiritDbManager.getInstance(getApp()).insertMediaAppList(CONSOLE_MEDIA_APP_LIST,
                        new DatabaseCallback<List<MediaAppsEntity>>() {
                            @Override
                            public void onUpdated() {
                                super.onUpdated();
                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                            }
                        });
            }
        });
    }


    private static List<AppStoreBean.AppUpdateBeansDTO> appStoreList = new ArrayList<>();

    public static void updateAll() {


        BaseApi.request(BaseApi.createApi2(IServiceApi.class, BuildConfig.APP_STORE_URL).apiAppStore(),
                new BaseApi.IResponseListener<AppStoreBean>() {
                    @Override
                    public void onSuccess(AppStoreBean storeBean) {

                        try {
                            appStoreList = storeBean.getAppUpdateBeans();
                            Log.d("MMMMEEEEDDDD", "getAppStore:" + appStoreList.size());
                            for (int i = 0; i < CONSOLE_MEDIA_APP_LIST.size(); i++) {
                                for (AppStoreBean.AppUpdateBeansDTO appStoreBean : appStoreList) {
                                    if (CONSOLE_MEDIA_APP_LIST.get(i).getPackageName().equalsIgnoreCase(appStoreBean.getPackageName())) {
                                        final byte[][] bytesM = {null};
                                        int finalI = i;
                                        Disposable disposable = downloadImage(appStoreBean.getIcon().get(0).getAppIconMediumUrl())
                                                .subscribe(
                                                        imageBytes -> {
                                                            bytesM[0] = imageBytes;

                                                            MediaAppsEntity mediaAppsEntityN = new MediaAppsEntity(appStoreBean.getPackageName(), appStoreBean.getAppName(), appStoreBean.getType(), appStoreBean.getVersion(), appStoreBean.getComment(), appStoreBean.getGmsNeeded(), appStoreBean.getForceUpdates(), appStoreBean.getVersionCode(), finalI, appStoreBean.getApk().get(0).getMd5(), appStoreBean.getApk().get(0).getDownloadUrl(), appStoreBean.getApk().get(0).getPath(), appStoreBean.getWeb().get(0).getWebUrl(), 0, new byte[0], bytesM[0], new byte[0]);
                                                            CONSOLE_MEDIA_APP_LIST.set(finalI, mediaAppsEntityN);
                                                        },
                                                        error -> {
                                                            MediaAppsEntity mediaAppsEntityN = new MediaAppsEntity(appStoreBean.getPackageName(), appStoreBean.getAppName(), appStoreBean.getType(), appStoreBean.getVersion(), appStoreBean.getComment(), appStoreBean.getGmsNeeded(), appStoreBean.getForceUpdates(), appStoreBean.getVersionCode(), finalI, appStoreBean.getApk().get(0).getMd5(), appStoreBean.getApk().get(0).getDownloadUrl(), appStoreBean.getApk().get(0).getPath(), appStoreBean.getWeb().get(0).getWebUrl(), 0, new byte[0], drawableToByteArray(MediaAppEnum.getMediaApp(appStoreBean.getPackageName()).getAppIcon()), new byte[0]);
                                                            CONSOLE_MEDIA_APP_LIST.set(finalI, mediaAppsEntityN);
                                                        }
                                                );

                                    }
                                }
                            }
                            Log.d("MMMMEEEEDDDD", "取得AppStore上 media app 的name和icon...等 資料");
                            new RxTimer().timer(2000, v -> insertMediaAppList());

                        } catch (Exception e) {
                            showException(e);
                            insertMediaAppList();
                        }

                    }

                    @Override
                    public void onFail() {
                        insertMediaAppList();
                    }
                });
    }

//    public static void getMediaAppByPkgName(String packageName) {
//        SpiritDbManager.getInstance(getApp()).getMediaAppByPackageName(packageName, new DatabaseCallback<MediaAppsEntity>() {
//            @Override
//            public void onDataLoadedBean(MediaAppsEntity entity) {
//                super.onDataLoadedBean(entity);
//
//                //   entity.setSort(3);
//                //   updateMediaApp(entity);
//            }
//        });
//    }

    public static void insertMediaApp(AppStoreBean.AppUpdateBeansDTO beansDTO, byte[][] bytesM) {
        //預設 sort  >>>>>  MAX_MEDIA_APP_COUNT  改成 CONSOLE_MEDIA_APP_LIST.size()
//        MediaAppsEntity mediaAppsEntity = new MediaAppsEntity(beansDTO.getPackageName(), beansDTO.getAppName(), beansDTO.getType(), beansDTO.getVersion(), beansDTO.getComment(), beansDTO.getGmsNeeded(), beansDTO.getForceUpdates(), beansDTO.getVersionCode(), (MAX_MEDIA_APP_COUNT - 1), beansDTO.getApk().get(0).getMd5(), beansDTO.getApk().get(0).getDownloadUrl(), beansDTO.getApk().get(0).getPath(), beansDTO.getWeb().get(0).getWebUrl(), 0, new byte[0], bytesM[0], new byte[0]);
        MediaAppsEntity mediaAppsEntity = new MediaAppsEntity(beansDTO.getPackageName(), beansDTO.getAppName(), beansDTO.getType(), beansDTO.getVersion(), beansDTO.getComment(), beansDTO.getGmsNeeded(), beansDTO.getForceUpdates(), beansDTO.getVersionCode(), (CONSOLE_MEDIA_APP_LIST.size()), beansDTO.getApk().get(0).getMd5(), beansDTO.getApk().get(0).getDownloadUrl(), beansDTO.getApk().get(0).getPath(), beansDTO.getWeb().get(0).getWebUrl(), 0, new byte[0], bytesM[0], new byte[0]);

        //檢查此MediaApp是否存在Console資料庫中
        SpiritDbManager.getInstance(getApp()).getMediaAppByPackageName(mediaAppsEntity.getPackageName(), new DatabaseCallback<MediaAppsEntity>() {
            @Override
            public void onDataLoadedBean(MediaAppsEntity entity) {
                super.onDataLoadedBean(entity);
                mediaAppsEntity.setSort(entity.getSort());
                updateMediaApp(mediaAppsEntity);
            }

            @Override
            public void onNoData() {
                super.onNoData();
                SpiritDbManager.getInstance(getApp()).insertMediaApp(mediaAppsEntity, new DatabaseCallback<MediaAppsEntity>() {
                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        checkConsoleMediaApp();
                    }
                });
            }
        });

    }

    public static void updateMediaApp(MediaAppsEntity mediaAppsEntity) {
        SpiritDbManager.getInstance(getApp()).updateMediaApp(mediaAppsEntity,
                new DatabaseCallback<MediaAppsEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });
    }


    public static void updateMediaAppSortByPkName(String packageName, int sort, boolean isRefresh) {
        SpiritDbManager.getInstance(getApp()).updateMediaAppSort(packageName, sort,
                new DatabaseCallback<MediaAppsEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                        if (isRefresh) {
                            checkConsoleMediaApp();
                        }
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });
    }


    public static void checkForceUpdate() {
        List<AppStoreBean.AppUpdateBeansDTO> appStoreList = new ArrayList<>();
        PackageManagerUtils packageManagerUtils = new PackageManagerUtils();
        AppUpdateManager appUpdateManager = new AppUpdateManager(getApp(), packageManagerUtils);

        BaseApi.request(BaseApi.createApi2(IServiceApi.class, BuildConfig.APP_STORE_URL).apiAppStore(),
                new BaseApi.IResponseListener<AppStoreBean>() {
                    @Override
                    public void onSuccess(AppStoreBean appStoreBeans) {
                        try {
                            appStoreList.addAll(appStoreBeans.getAppUpdateBeans());

                            for (AppStoreBean.AppUpdateBeansDTO appStoreBean : appStoreList) {

                                //本機沒有GMS,APP 需要 GMS > 跳過   (WEB)
                                if (!isGMS && "YES".equalsIgnoreCase(appStoreBean.getGmsNeeded()))
                                    continue;

                                //不用強制安裝 > 跳過
                                //     if (!"NO".equalsIgnoreCase(appStoreBean.getForceUpdates())) continue; //NO會執行更新
                                if (!"YES".equalsIgnoreCase(appStoreBean.getForceUpdates()))
                                    continue; //YES會執行更新

                                //Console沒有安裝 > 跳過
                                //     if (appUpdateManager.getPackageInfoVersionName(appStoreBean.getPackageName()).isEmpty()) continue;

                                //是否要更新
                                boolean isUpgrade = packageManagerUtils.isUpgrade(appUpdateManager.getPackageInfoVersionName(appStoreBean.getPackageName()), appStoreBean.getVersion());
                                //版號相同，不需要更新 > 跳過
                                if (!isUpgrade) continue;

                                //沒有icon > 跳過
                                if (appStoreBean.getIcon().isEmpty()) continue;


                                //判斷APP是否已存在，不存在的話要先檢查數量有沒有超過
                                SpiritDbManager.getInstance(getApp()).getMediaAppByPackageName(appStoreBean.getPackageName(), new DatabaseCallback<MediaAppsEntity>() {
                                    @Override
                                    public void onDataLoadedBean(MediaAppsEntity entity) {
                                        super.onDataLoadedBean(entity);
                                        MainActivity.forcePkName = entity.getPackageName();
                                        MainActivity.forcePkNameSort = (entity.getSort());
                                        //已存在，開始下載
                                        Log.d("UPDATE_APP", "#######APP: " + entity.getPackageName() + "需要更新");
                                        iconDownload(appStoreBean);
                                    }

                                    @Override
                                    public void onNoData() {
                                        super.onNoData();
                                        //不存在，檢查數量
//                                                if (CONSOLE_MEDIA_APP_LIST.size() < MAX_MEDIA_APP_COUNT) {
//                                                    iconDownload(appStoreBean);
//                                                } else {
//                                                }
                                    }
                                });
                                //    return;
                            }


                        } catch (Exception e) {
                            showException(e);
                        }

                    }

                    @Override
                    public void onFail() {
                    }
                });
    }

    private static void iconDownload(AppStoreBean.AppUpdateBeansDTO appStoreBean) {

        String iconUrl = appStoreBean.getIcon().get(0).getAppIconMediumUrl();
        CommonUtilsKT.downloadImage(iconUrl, new CommonUtilsKT.DownloadCallback() {
            @Override
            public void onSuccess(@NonNull byte[] imageBytes) {
                Log.d("UPDATE_APP", "XXX圖片下載成功 圖圖圖圖: " + imageBytes.length);
                byte[][] wrappedImage = CommonUtilsKT.wrapByteArray(imageBytes);
                downloadAppAndUpdate(appStoreBean, wrappedImage);
            }
            @Override
            public void onError(@NonNull Throwable error) {
                Log.d("UPDATE_APP", "imageError: " + error.getMessage());
            }
        });



//        final byte[][] bytesM = {null};
//        Disposable disposable = downloadImage(appStoreBean.getIcon().get(0).getAppIconMediumUrl())
//                .subscribe(
//                        imageBytes -> {
//                            bytesM[0] = imageBytes;
//                            downloadAppAndUpdate(appStoreBean, bytesM);
//                        },
//                        error -> Log.d("MMEEEEDDDIIAAA", "imageError: ")
//                );
    }

    public static DownloadUtil downloadUtil;

    private static void downloadAppAndUpdate(AppStoreBean.AppUpdateBeansDTO appUpdateBeansDTO, byte[][] bytesM) {
        if (appUpdateBeansDTO.getApk().isEmpty()) return;
        String updateUrl = appUpdateBeansDTO.getApk().get(0).getDownloadUrl();
        final XapkInstaller[] xapkInstaller = new XapkInstaller[1];
        // DownloadUtil downloadUtil;
        downloadUtil = new DownloadUtil(getFileExtension(updateUrl));
        downloadUtil.deleteFile();
        downloadUtil.downloadFile(updateUrl, new DownloadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(final int currentLength, final long count, final long total) {

                //Media頁面下載百分比
                //    LiveEventBus.get(FIRST_MEDIA_APP_PROGRESS,Integer.class).post(currentLength);
            }

            @Override
            public void onFinish(final String localPath) {
                //XAPK
                Log.d("UPDATE_APP", "onFinish: " + localPath);
                if (getFileExtension(localPath).equalsIgnoreCase("xapk")) {
                    xapkInstaller[0] = XapkManager.createXapkInstaller(getApp(), localPath);
                    ExecutorService installExecutor = Executors.newSingleThreadExecutor();
                    installExecutor.execute(() -> {
                        xapkInstaller[0].setOnXapkInstallerListener(new XapkInstaller.OnXapkInstallerListener() {
                            @Override
                            public void onInstaller() {
                            }

                            @Override
                            public void onErr(String err) {
                            }
                        });
                        xapkInstaller[0].install();

                        insertMediaApp(appUpdateBeansDTO, bytesM);

                        new RxTimer().timer(3000, number -> LiveEventBus.get(FIRST_MEDIA_APP).post(true));
                    });
                } else {

                    new CommonUtils().install2(getApp(), localPath, new InstallCallback() {
                        @Override
                        public void onSuccess() {
                            insertMediaApp(appUpdateBeansDTO, bytesM);
                            new RxTimer().timer(3000, number -> LiveEventBus.get(FIRST_MEDIA_APP).post(true));
                        }

                        @Override
                        public void onFail(String s) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(final String errorInfo) {
            }
        });
    }


    //排序
    public static List<AppStoreBean.AppUpdateBeansDTO> sortAppStoreBean(AppStoreBean appStoreBean) {

        List<AppStoreBean.AppUpdateBeansDTO> appStoreList = new ArrayList<>();

        for (AppStoreBean.AppUpdateBeansDTO appStoreBeans : appStoreBean.getAppUpdateBeans()) {
            for (MediaAppsEntity mediaAppsEntity : CONSOLE_MEDIA_APP_LIST) {
                if (appStoreBeans.getPackageName().equalsIgnoreCase(mediaAppsEntity.getPackageName())) {
                    appStoreBeans.setSort(mediaAppsEntity.getSort());
                }
            }
            appStoreList.add(appStoreBeans);
            appStoreList.sort(new AppStoreSort());
        }


        return appStoreList;
    }


    //    //取得DeviceSettings的 Media JSON 資料 放入 mediaAppEnumList
//    public static void setMediaAppEnumList() {
//
//        //清除
////        DeviceSettingBean d = getApp().getDeviceSettingBean();
////        d.setMediaApps("");
////        getApp().setDeviceSettingBean(d);
//
//        String mediasSetting = getApp().getDeviceSettingBean().getMediaApps();
//
//        //如果無資料，用預設
//        if (mediasSetting == null || mediasSetting.isEmpty()) {
//            switch (getApp().getDeviceSettingBean().getTerritoryCode()) {
//                case TERRITORY_GLOBAL:
//                case TERRITORY_JAPAN:
//                case TERRITORY_CANADA:
//                    mediasSetting = new Gson().toJson(medias_default_global);
//                    break;
//                default: //TERRITORY_US:
//                    mediasSetting = new Gson().toJson(medias_default_us);
//                    break;
//            }
//
//            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//            deviceSettingBean.setMediaApps(mediasSetting);
//            getApp().setDeviceSettingBean(deviceSettingBean);
//        }
//
//        //將DeviceSettings的 Media JSON資料 放入 mediaAppEnumList
//        List<MyMediaAppBean> myMediaAppBeanList = new Gson().fromJson(mediasSetting,
//                new TypeToken<ArrayList<MyMediaAppBean>>() {
//                }.getType());
//
//        //排序
//        myMediaAppBeanList.sort(new MediaAppSort());
//        mediaAppEnumList.clear();
//        for (MyMediaAppBean media : myMediaAppBeanList) {
//            mediaAppEnumList.add(MediaAppEnum.getMediaApp(media.getMediaPkName()));
//        }
//
//
////        String[] medias = mediasSetting.split("#");
////        for (String media : medias) {
////            mediaAppEnumList.add(MediaAppEnum.getMediaApp(media));
////        }
//
//    }

//    public static void disabledMediaApp(String pkgName) {
//
//        List<MyMediaAppBean> myMediaAppBeanList = new Gson().fromJson(getApp().getDeviceSettingBean().getMediaApps(),
//                new TypeToken<ArrayList<MyMediaAppBean>>() {
//                }.getType());
//
//        myMediaAppBeanList.removeIf(myMediaAppBean -> myMediaAppBean.getMediaPkName().equals(pkgName));
//
//        String mediasSetting = new Gson().toJson(myMediaAppBeanList);
//
//        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
//        deviceSettingBean.setMediaApps(mediasSetting);
//        getApp().setDeviceSettingBean(deviceSettingBean);
//
//        setMediaAppEnumList();
//    }

    // public static List<MediaAppEnum> mediaAppEnumList = new ArrayList<>();


//    public static String medias_default_us =
//            MediaAppEnum.NETFLIX.getAppPackageName() + "#" +
//                    MediaAppEnum.YOUTUBE.getAppPackageName() + "#" +
//                    MediaAppEnum.YOUTUBE_TV.getAppPackageName() + "#" +
//                    MediaAppEnum.DISNEY_PLUS.getAppPackageName() + "#" +
//                    MediaAppEnum.PRIME_VIDEO.getAppPackageName() + "#" +
//                    MediaAppEnum.HBO_MAX.getAppPackageName() + "#" +
//                    MediaAppEnum.HULU.getAppPackageName() + "#" +
//                    MediaAppEnum.PEACOCK.getAppPackageName() + "#" +
//                    MediaAppEnum.KINOMAP.getAppPackageName() + "#" +
//                    MediaAppEnum.SPOTIFY.getAppPackageName() + "#" +
//                    MediaAppEnum.PLUTO_TV.getAppPackageName() + "#" +
//                    MediaAppEnum.TUBI.getAppPackageName();

//    public static String medias_default_global =
//            MediaAppEnum.BBC.getAppPackageName() + "#" +
//                    MediaAppEnum.CNN_NEWS.getAppPackageName() + "#" +
//                    MediaAppEnum.ESPN.getAppPackageName() + "#" +
//                    MediaAppEnum.IHEART.getAppPackageName() + "#" +
//                    MediaAppEnum.SPOTIFY.getAppPackageName() + "#" +
//                    MediaAppEnum.YOUTUBE.getAppPackageName() + "#" +
//                    MediaAppEnum.NETFLIX.getAppPackageName() + "#" +
//                    MediaAppEnum.BIT_GYM.getAppPackageName() + "#" +
//                    MediaAppEnum.WEATHER.getAppPackageName() + "#" +
//                    MediaAppEnum.TWITTER.getAppPackageName() + "#" +
//                    MediaAppEnum.INSTAGRAM.getAppPackageName() + "#" +
//                    MediaAppEnum.FACEBOOK.getAppPackageName();

    //轉成json擋 存在 DeviceSettingBean
    //  public static List<MyMediaAppBean> medias_default_us = us_medias();

//    private static List<MyMediaAppBean> us_medias() {
//        List<MyMediaAppBean> mediaAppBeanList = new ArrayList<>();
//        mediaAppBeanList.add(new MyMediaAppBean(1, MediaAppEnum.NETFLIX.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(2, MediaAppEnum.YOUTUBE.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(3, MediaAppEnum.YOUTUBE_TV.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(4, MediaAppEnum.DISNEY_PLUS.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(5, MediaAppEnum.PRIME_VIDEO.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(6, MediaAppEnum.HBO_MAX.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(7, MediaAppEnum.HULU.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(8, MediaAppEnum.PEACOCK.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(9, MediaAppEnum.KINOMAP.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(10, MediaAppEnum.SPOTIFY.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(11, MediaAppEnum.PLUTO_TV.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(12, MediaAppEnum.TUBI.getAppPackageName()));
//        return mediaAppBeanList;
//    }

    //轉成json擋 存在 DeviceSettingBean
//    public static List<MyMediaAppBean> medias_default_global = global_medias();

//    private static List<MyMediaAppBean> global_medias() {
//        List<MyMediaAppBean> mediaAppBeanList = new ArrayList<>();
//        mediaAppBeanList.add(new MyMediaAppBean(1, MediaAppEnum.BBC.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(2, MediaAppEnum.CNN_NEWS.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(3, MediaAppEnum.ESPN.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(4, MediaAppEnum.IHEART.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(5, MediaAppEnum.SPOTIFY.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(6, MediaAppEnum.YOUTUBE.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(7, MediaAppEnum.NETFLIX.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(8, MediaAppEnum.BIT_GYM.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(9, MediaAppEnum.WEATHER.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(10, MediaAppEnum.TWITTER.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(11, MediaAppEnum.INSTAGRAM.getAppPackageName()));
//        mediaAppBeanList.add(new MyMediaAppBean(12, MediaAppEnum.FACEBOOK.getAppPackageName()));
//        return mediaAppBeanList;
//    }


//    public static void initMediaEnum() {
//
//        boolean isCanada = getApp().getDeviceSettingBean().getTerritoryCode() == TERRITORY_CANADA;
//        if (isUs) {
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.BBC : MediaAppEnum.NETFLIX);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.CNN_NEWS : MediaAppEnum.YOUTUBE);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.ESPN : MediaAppEnum.CRACKLE);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.IHEART : MediaAppEnum.DISNEY_PLUS);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.SPOTIFY : MediaAppEnum.PRIME_VIDEO);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.YOUTUBE : MediaAppEnum.HBO_MAX);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.NETFLIX : MediaAppEnum.IMDB_TV);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.BIT_GYM : MediaAppEnum.PEACOCK);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.WEATHER : MediaAppEnum.KINOMAP);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.TWITTER : MediaAppEnum.SPOTIFY);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.INSTAGRAM : MediaAppEnum.PLUTO_TV);
//            mediaAppEnumList.add(isCanada ? MediaAppEnum.FACEBOOK : MediaAppEnum.MIDNIGHT_PULP);
//
//        } else {
//
//            mediaAppEnumList.add(MediaAppEnum.ESPN);
//            mediaAppEnumList.add(MediaAppEnum.CNN_NEWS);
//            mediaAppEnumList.add(MediaAppEnum.BBC);
//            mediaAppEnumList.add(MediaAppEnum.SPOTIFY);
//            mediaAppEnumList.add(MediaAppEnum.IHEART);
//            mediaAppEnumList.add(MediaAppEnum.NETFLIX);
//            mediaAppEnumList.add(MediaAppEnum.YOUTUBE);
//            mediaAppEnumList.add(MediaAppEnum.WEATHER);
//            mediaAppEnumList.add(MediaAppEnum.BIT_GYM);
//            mediaAppEnumList.add(MediaAppEnum.FACEBOOK);
//            mediaAppEnumList.add(MediaAppEnum.INSTAGRAM);
//            mediaAppEnumList.add(MediaAppEnum.TWITTER);
//            //     mediaAppEnumList.add(MediaAppEnum.DISNEY_PLUS);
//        }
//
//    }

}