package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isEmulator;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.checkSwVersion;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_JAPAN;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_USB_MODE_SET;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.BuildConfig;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceSoftwareTestBinding;
import com.dyaco.spirit_commercial.model.webapi.BaseApi;
import com.dyaco.spirit_commercial.model.webapi.IServiceApi;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.product_flavor.DownloadManagerCustom;
import com.dyaco.spirit_commercial.product_flavor.GetApkSign;
import com.dyaco.spirit_commercial.product_flavor.UpdateAppWindow;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.SilentAppInstaller;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.support.utils.LogS;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class MaintenanceSoftwareTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceSoftwareTestBinding> {

    private UsbReaderKt usbReader;
    private static final String TAG = "USB_UPDATE";
    private final String FILE_JSON_NAME = "update.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.DATA);


        initEvent();

        //  getBinding().tvTftOsVersion.setText(currentVersion());
        getBinding().tvTftOsVersion.setText(checkSwVersion());
        getBinding().tvTftOsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ComponentName mComponentName = new ComponentName("com.redstone.ota.ui", "com.redstone.ota.ui.activity.RsMainActivity");
//                Intent mIntent = new Intent();
//                mIntent.setComponent(mComponentName);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                requireActivity().startActivity(mIntent);
            }
        });

        getBinding().tvConsoleVersion.setText(String.format("%s %s", requireActivity().getString(R.string.Version), new CommonUtils().getLocalVersionName(requireActivity())));

        checkUpdate();

        getBinding().cbAutomaticUpdate.setChecked(getApp().getDeviceSettingBean().isAutoUpdate());

        getBinding().cbAutomaticUpdate.setOnCheckedChangeListener((compoundButton, isCheck) -> {
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setAutoUpdate(isCheck);
            if (!isCheck) {
                //取消WorkManager通知，及TempApk
                new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);
                deviceSettingBean.setTmpApkPath("");
            }

            getApp().setDeviceSettingBean(deviceSettingBean);
        });

        getBinding().tvConsoleUpdate.setOnClickListener(view1 -> {

            if (CheckDoubleClick.isFastClick()) return;

            if (((MainActivity) requireActivity()).downloadManagerCustom != null) {
                Log.d(DownloadManagerCustom.TAG, "取消正在下載的檔案");
                ((MainActivity) requireActivity()).downloadManagerCustom.cancelDownload();
            }

            //   DeviceSettingBean d = getApp().getDeviceSettingBean();

            //   if ("".equals(d.getTmpApkPath())) {
            UpdateAppWindow updateAppWindow = new UpdateAppWindow(requireActivity(), updateBean);
            updateAppWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.BOTTOM, 0, 0);
            updateAppWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
                @Override
                public void onStartDismiss(MsgEvent value) {
                    Log.d("更新", "onStartDismiss: ");
                }

                @Override
                public void onDismiss() {
                    Log.d("更新", "onDismiss: ");
                }
            });
        });


        if (!requireActivity().getPackageManager().canRequestPackageInstalls()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + requireActivity().getPackageName()));
            someActivityResultLauncher.launch(intent);
        }


        initUsbUpdate();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("安裝未知", "OK安裝未知來源權限: " + result);
                }
            });

    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
        //    getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
            closeUsb();
            closeUsbAppUpdateWindow();
            dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.CHARGER);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeUsbAppUpdateWindow();
        closeUsb();
    }

    private UpdateBean updateBean;

    /**
     * 檢查app更新
     */
    public void checkUpdate() {

        int territoryCode = getApp().getDeviceSettingBean().getTerritoryCode();

        String uuRl;
        if (territoryCode == TERRITORY_US) {
            uuRl = BuildConfig.UPDATE_URL_US;
        } else if (territoryCode == TERRITORY_JAPAN) {
            uuRl = BuildConfig.UPDATE_URL_JP;
        } else if (territoryCode == TERRITORY_CANADA) {
            uuRl = BuildConfig.UPDATE_URL_CA;
        } else {
            uuRl = BuildConfig.UPDATE_URL_GLOBAL;
        }

        //   getBinding().tvURL.setText(uuRl);

        BaseApi.request(BaseApi.createApi2(IServiceApi.class, uuRl).apiCheckUpdate(),
                new BaseApi.IResponseListener<UpdateBean>() {
                    @Override
                    public void onSuccess(UpdateBean data) {
                        if (getBinding() == null) return;
                        //TftOs 不update
                        //  getBinding().tvTftOsUpdate.setVisibility(View.VISIBLE);
                        //    getBinding().tvTftOsUpToDate.setVisibility(View.INVISIBLE);

//                        if (convertSwVersion(data.getOS_Version()) > checkSwVersion()) {
//
//                        }

                        LogS.printJson("更新", new Gson().toJson(data), "");
                        try {
                            if (getBinding() == null) return;
                            getBinding().progress.setVisibility(View.GONE);
                            Log.d("更新", "檢查是否需要更新 update.json版本：" + data.getVersionCode() + ", Console實際版本：" + new CommonUtils().getLocalVersionCode() + ", Console儲存的版本：" + getApp().getDeviceSettingBean().getCurrentVersionCode());
                            if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
                                Log.d("更新", "可更新");
                                updateBean = data;
                                getBinding().tvConsoleUpdate.setVisibility(View.VISIBLE);
                                getBinding().tvConsoleUpToDate.setVisibility(View.INVISIBLE);

                            } else {
                                getBinding().tvConsoleUpdate.setVisibility(View.INVISIBLE);
                                getBinding().tvConsoleUpToDate.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            getBinding().progress.setVisibility(View.GONE);
                            showException(e);
                        }
                    }

                    @Override
                    public void onFail() {
                        if (getBinding() == null) return;
                        getBinding().progress.setVisibility(View.GONE);
                        Log.d("更新", "連線失敗");
                    }
                });
    }


    private void initUsbUpdate() {
        ///開啟 USB DATA 模試 ,接收回傳結果
        LiveEventBus.get(ON_USB_MODE_SET, DeviceSpiritC.MCU_SET.class).observe(getViewLifecycleOwner(), s -> {

                    if (s == DeviceSpiritC.MCU_SET.OK) {
                        initUsbReadManager();

                    } else {
                        Toasty.warning(requireActivity(), "USB_MODE ERROR", Toasty.LENGTH_SHORT).show();
                    }
                }
        );



    }


    //USB
    UpdateBean updateBeanUsb;
    UsbAppUpdateWindow usbAppUpdateWindow;

    private void initUsbReadManager() {

        getBinding().tvConsoleUSBUpdate.setOnClickListener(view -> {

            if (CheckDoubleClick.isFastClick()) return;

            if (((MainActivity) requireActivity()).downloadManagerCustom != null) {
                Log.d(DownloadManagerCustom.TAG, "取消正在下載的檔案");
                ((MainActivity) requireActivity()).downloadManagerCustom.cancelDownload();
            }


            if (updateBeanUsb == null || updateBeanUsb.getPATH() == null) return;

            //可以更新
            findApkFile(updateBeanUsb.getPATH());

            closeUsbAppUpdateWindow();

            usbAppUpdateWindow = new UsbAppUpdateWindow(requireActivity());
            usbAppUpdateWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.BOTTOM, 0, 0);
        });

        Log.d(TAG, "initUsbReadManager: ");
        usbReader = new UsbReaderKt(requireActivity());
        usbReader.setListener(new UsbReaderKt.UsbReaderListener() {
            /*
            取得USB檔案的Data值
             */
            @Override
            public void onFindFile(@NonNull String file, @NonNull UsbReaderKt.FileStatus status, @NonNull UsbReaderKt.FileType type, String data, byte[] raw, @NonNull UsbReaderKt.FileKind kind) {
                String log = "on find file, file name: " + file + "\n" + ", status: " + status + "\n" + ", type: " + type + "\n" + "data:" + data + "\n" + ", raw: " + (raw != null ? raw.length : null);
                Log.d(TAG, "onFindFile: " + log);

                if (type == UsbReaderKt.FileType.JSON) {

                    if (status == UsbReaderKt.FileStatus.FILE_FOUND) {
                        //update.json
                        updateBeanUsb = new Gson().fromJson(data, UpdateBean.class);
                        //   String apkName = updateBeanUsb.getPATH();
                        int versionCode = updateBeanUsb.getVersionCode();

                        //可以更新
                        setUpToDate(versionCode >= new CommonUtils().getLocalVersionCode());
                    } else {
                        //找不到update.json

                        Toasty.error(requireActivity(), "update.json NOT FOUND", Toasty.LENGTH_LONG).show();

                        setUpToDate(false);
                    }

                } else if (type == UsbReaderKt.FileType.APK) {

                    if (status == UsbReaderKt.FileStatus.FILE_FOUND) {

                        //this.dir = context.getCacheDir();
                        ///data/user/0/com.dyaco.spirit_commercial/cache/SpiritCommercialV1.0.A0.1.0.0505A_[production].apk
                        Log.d(TAG, "APK取得完成 :" + data);
                        if (updateBeanUsb.getMD5().equalsIgnoreCase(new GetApkSign().getApkMd5(data))) {
                            Log.d(TAG, "MD5正確，開始安裝");

                            //更新時間
                            DeviceSettingBean d2 = getApp().getDeviceSettingBean();
                            d2.setSoftwareUpdatedMillis(Calendar.getInstance().getTimeInMillis());
                            getApp().setDeviceSettingBean(d2);
                            Log.d(DownloadManagerCustom.TAG, "Console更新時間為：" + getApp().getDeviceSettingBean().getSoftwareUpdatedMillis());

                            if (!isEmulator && isTreadmill) {
                                //  ((MainActivity) requireActivity()).uartConsole.setDevMainMode(RESET);
                                getDeviceSpiritC().setMainModeTreadmill(DeviceDyacoMedical.MAIN_MODE.RESET); //停止LWR計數, 以免發time out錯誤
                            }

                            new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);

                            Log.d("UPDATE_APP", "準備開始安裝: ");
                            SilentAppInstaller.install(getApp(), data, new SilentAppInstaller.OnInstallerListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("UPDATE_APP", "安裝完成: ");
                                }

                                @Override
                                public void onError(@NonNull String reason) {
                                    updateFailed();
                                    Log.d("UPDATE_APP", "安裝失敗: " + reason);
                                    closeUsbAppUpdateWindow();
                                    Toasty.error(requireActivity(), "Installation Failed:" + reason, Toasty.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            Log.d(TAG, "MD5錯誤: ");
                            Toast.makeText(requireActivity(), "APK ERROR", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        //找不到APK
                        if (usbAppUpdateWindow != null) {
                            usbAppUpdateWindow.dismiss();
                            usbAppUpdateWindow = null;
                            Toasty.error(requireActivity(), "APK NOT FOUND", Toasty.LENGTH_LONG).show();
                        }
                        setUpToDate(false);
                    }

                    showProgress(false);

                } else {

                    if (usbAppUpdateWindow != null) {
                        usbAppUpdateWindow.dismiss();
                        usbAppUpdateWindow = null;
                    }
                    Toasty.error(requireActivity(), "FILE NOT FOUND", Toasty.LENGTH_LONG).show();

                    setUpToDate(false);
                }
            }

            //插USB
            @Override
            public void onDeviceAttached(@NonNull String name) {
                //取得USB裝置
                Log.d(TAG, "onDeviceAttached: " + "on device attached, device name: " + name);
                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.VISIBLE);
                    getBinding().tvConsoleUSBUpdate.setVisibility(View.INVISIBLE);
                    getBinding().tvConsoleUSBUpToDate.setVisibility(View.VISIBLE);
                }

                if (usbReader == null) {
                    Log.e(TAG, "usbReader is null in onDeviceAttached");
                    return;
                }

                findJsonFile();

                closeUsbAppUpdateWindow();

//                UsbFsDetector.detectUsbFsAsync(type ->
//                        Log.d("UsbFsDetector", "initUsbUpdate: " + type.name()));
            }

            //拔USB
            @Override
            public void onDeviceDetached(@NonNull String name) {
                Log.d(TAG, "onDeviceDetached: " + "on device detached, device name: " + name);
                setUpToDate(false);
                closeUsbAppUpdateWindow();
            }

            @Override
            public void onProgress(long current, long total) {

                if (usbAppUpdateWindow != null) {
                    long progress = (100L * current / total);
                    if (progress == tempProgress) return;
                    tempProgress = progress;
                    usbAppUpdateWindow.setProgress((int) progress);
                    Log.d(TAG, "####: Progress:" + progress + ", CURRENT:" + current + ", TOTAL:" + total);
                }
            }

            @Override
            public void onError(@NonNull UsbReaderKt.UsbError error) {

                switch (error) {
                    case NO_USB_DEVICE:
                    case PARAMETER_ERROR:
                    case DEVICE_NO_PARTITION:
                    case PERMISSION_FAILED:
                    case COPY_FILE_ERROR:
                        tempProgress = 0;
                        closeUsbAppUpdateWindow();
                        break;
                    case BACKGROUND_SEARCH_ERROR:
                    case VIDEO_VALIDATION_FAILED:
                    case UNKNOWN_ERROR:
                }

                Toasty.error(requireActivity(), "Error: " + error.name(), Toasty.LENGTH_SHORT).show();
                Log.e("USB_UPDATE", "errorMsg:" + error.name());

                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.GONE);
                }
            }
        });
    }

    long tempProgress = 0;

    private void closeUsbAppUpdateWindow() {
        if (usbAppUpdateWindow != null) {
            usbAppUpdateWindow.dismiss();
            usbAppUpdateWindow = null;
        }
        tempProgress = 0;
    }


    /**
     * USB讀取Json
     */
    private void findJsonFile() {
        Log.d(TAG, "btnFindJsonFile: ");
        usbReader.autoFindFile(FILE_JSON_NAME, UsbReaderKt.FileType.JSON, UsbReaderKt.FileKind.NORMAL); // >> onFindFile
    }

    /**
     * 讀取USB
     * USB讀取APK
     */
    private void findApkFile(String fileName) {

        showProgress(true);
        usbReader.autoFindFile(fileName, UsbReaderKt.FileType.APK, UsbReaderKt.FileKind.NORMAL);
    }


    //不可更新
    private void setUpToDate(boolean isUpdateOk) {
        if (getBinding() != null) {
            getBinding().tvConsoleUSBUpdate.setVisibility(isUpdateOk ? View.VISIBLE : View.INVISIBLE);
            getBinding().tvConsoleUSBUpToDate.setVisibility(isUpdateOk ? View.INVISIBLE : View.VISIBLE);
            getBinding().progress.setVisibility(View.INVISIBLE);
            getBinding().tvUpdateVersion.setText(isUpdateOk ? "Version " + updateBeanUsb.getVersion() : "");
        }
    }

    private void showProgress(boolean isShow) {
        if (getBinding() != null)
            getBinding().progress.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }


    private void closeUsb() {
        if (usbReader != null) {
            usbReader.closeUsb();
            usbReader = null;
        }

    }

    private void updateFailed() {
        try {
            if (!isEmulator && isTreadmill) {
                ((MainActivity) requireActivity()).uartConsole.setDevMainMode(DeviceDyacoMedical.MAIN_MODE.ENG);
            }
        } catch (Exception e) {
            showException(e);
        }
    }

}