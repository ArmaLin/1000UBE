package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.support.CommonUtils.restartApp;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_USB_MODE_SET;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceFirmwareTestBinding;
import com.dyaco.spirit_commercial.model.webapi.bean.UpdateBean;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;

import es.dmoral.toasty.Toasty;


public class MaintenanceFirmwareTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceFirmwareTestBinding> {

    //private SubMcuUpdateManager subMcuUpdateManager;
    private UsbReaderKt usbReader;
    private UpdateBean updateBean;
    private byte[] binRawSubMcu;
    private byte[] binRawLwr;
    private String subMcuBinName;
    private String lwrBinName;
    private static final String TAG = "USB_UPDATE";
    private final String FILE_JSON_NAME = "update.json";
    private DeviceSettingViewModel deviceSettingViewModel;
//    private final String FILE_JSON_NAME = "update_fw.json";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);

        initEvent();

        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.DATA);


        LiveEventBus.get(ON_USB_MODE_SET, DeviceDyacoMedical.MCU_SET.class)
                .observe(getViewLifecycleOwner(), s -> {
                            if (s == DeviceDyacoMedical.MCU_SET.OK) {
                                initUsbReadManager();
                            } else {
                                getBinding().progress.setVisibility(View.INVISIBLE);
                                Toasty.warning(requireActivity(), "USB_MODE ERROR", Toasty.LENGTH_SHORT).show();
                            }
                        }
                );


        getBinding().tvSubMcuVersion.setText(String.format("Version %s", deviceSettingViewModel.subMcuFwVer.get()));

        getBinding().tvLwrVersion.setText(String.format("Version %s", deviceSettingViewModel.lwrMcuFwVer.get()));

    }


    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> {
            closeUsb();
            //    getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
            dismiss();
        });


        getBinding().btnUpdateSubMcu.setOnClickListener(view -> {
            //  usbReader.unregisterListener();
            // updateMcu(binRaw);
            updateStart(GENERAL.SUB_MCU);
        });

        getBinding().btnUpdateLwr.setOnClickListener(view -> {
            //  usbReader.unregisterListener();
            // updateMcu(binRaw);
            updateStart(GENERAL.LWR);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeUsb();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.CHARGER);
    }


    private void initUsbReadManager() {
        Log.d(TAG, "initUsbReadManager: ");
        usbReader = new UsbReaderKt(requireActivity());
        usbReader.setListener(new UsbReaderKt.UsbReaderListener() {
            @Override
            public void onError(@NonNull UsbReaderKt.UsbError error) {

                switch (error) {
                    case NO_USB_DEVICE:
                    case PARAMETER_ERROR:
                    case DEVICE_NO_PARTITION:
                    case PERMISSION_FAILED:
                    case COPY_FILE_ERROR:
                    case BACKGROUND_SEARCH_ERROR:
                    case VIDEO_VALIDATION_FAILED:
                    case UNKNOWN_ERROR:
                }

                closeUpdateWindow();
                Toasty.error(requireActivity(), error.name(), Toasty.LENGTH_LONG).show();
            }

            @Override
            public void onFindFile(@NonNull String file, @NonNull UsbReaderKt.FileStatus status, @NonNull UsbReaderKt.FileType type, @org.jetbrains.annotations.Nullable String data, @org.jetbrains.annotations.Nullable byte[] raw, @NonNull UsbReaderKt.FileKind kind) {
                String log = "on find file, file name: " + file + "\n" + ", status: " + status + "\n" + ", kind: " + kind + "\n" + ", type: " + type + "\n" + "data:" + data + "\n" + ", raw: " + (raw != null ? raw.length : null);
                Log.d(TAG, "onFindFile: " + log);

                if ((type == UsbReaderKt.FileType.JSON) && status == UsbReaderKt.FileStatus.FILE_FOUND) {
                    Log.d(TAG, "1###找到 update.json 檔");
                    //update.json
                    updateBean = new Gson().fromJson(data, UpdateBean.class);
//                    String binName = updateBean.getPATH();
                    // updateBean.setSub_Mcu("kkkkkk");
//                    updateBean.setLwr("submcu_led_slow.bin");
                    subMcuBinName = updateBean.getSub_Mcu();
                    lwrBinName = updateBean.getLwr(); //submcu_led_slow.bin submcu_led_fast.bin
                    int versionCode = updateBean.getVersionCode();

                    if (subMcuBinName != null && !subMcuBinName.isEmpty()) {
                        Log.d(TAG, "2###sub mcu有檔名，找sub mcu檔案");
                        //sub mcu有檔名，找sub mcu檔案
                        findBinFile(subMcuBinName, UsbReaderKt.FileKind.SUB_MCU);
                    } else {
                        //sub mcu無檔名，找lwr
                        setUpToDateSubMcu();

                        Log.d(TAG, "###sub mcu無檔名，找lwr");
                        if (lwrBinName != null && !lwrBinName.isEmpty()) {
                            Log.d(TAG, "###lwr有檔名，找lwr檔案");
                            //lwr有檔名，找lwr檔案
                            findBinFile(lwrBinName, UsbReaderKt.FileKind.LWR);
                        } else {
                            Log.d(TAG, "###sub mcu無檔名，lwr無檔名，結束");
                            //sub mcu無檔名，lwr無檔名，結束
                            setUpToDate();
                        }
                    }



                } else if ((type == UsbReaderKt.FileType.BIN) && status == UsbReaderKt.FileStatus.FILE_FOUND) {
                    //FILE_FOUND & BIN

                    if (!isAdded()) return;
                    //MCU更新

                    // if (file.equals(updateBean.getSub_Mcu())) {
                    if (kind == UsbReaderKt.FileKind.SUB_MCU) {
                        getBinding().btnUpdateSubMcu.setVisibility(raw != null ? View.VISIBLE : View.INVISIBLE);
                        getBinding().btnNoUpdateSubMcu.setVisibility(raw != null ? View.INVISIBLE : View.VISIBLE);
                        binRawSubMcu = raw;
                        Log.d(TAG, "3###取得sub mcu bin");

                        Log.d(TAG, "4###處理完sub mcu, 檢查lwr");
                        //處理完sub mcu, lwr有檔名
                        if (updateBean.getLwr() != null && !"".equals(updateBean.getLwr())) {
                            Log.d(TAG, "5###LWR有檔名，找LWR檔案");
                            //LWR有檔名，找LWR檔案
                            findBinFile(updateBean.getLwr(), UsbReaderKt.FileKind.LWR);
                        } else {
                            Log.d(TAG, "###LWR沒檔名，停止progress");
                            //LWR沒檔名，停止progress
                            getBinding().progress.setVisibility(View.INVISIBLE);
                        }
                    }

                    if (!isAdded()) return;

                    if (kind == UsbReaderKt.FileKind.LWR) {
                        binRawLwr = raw;
                        getBinding().btnUpdateLwr.setVisibility(raw != null ? View.VISIBLE : View.INVISIBLE);
                        getBinding().btnNoUpdateLwr.setVisibility(raw != null ? View.INVISIBLE : View.VISIBLE);
                        getBinding().progress.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "6###取得lwr bin");
                    }


                    //binRaw = raw;

                } else {

                    if (type == UsbReaderKt.FileType.JSON) {
                        Log.d(TAG, "###找不到 update.json");

                        showToast("<update.json> NOT FOUND");

                        //   requireActivity().runOnUiThread(() -> getBinding().progress.setVisibility(View.INVISIBLE));

                        return;
                    }

                    //FILE_NOT_FOUND
                    if (kind == UsbReaderKt.FileKind.SUB_MCU) {
                        if (!isAdded()) return;
                        //Sub Mcu 找不到檔案

                        Log.d(TAG, "###找不到 sub mcu bin");
                        getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                        getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);

                        Log.d(TAG, "###檢查lwr");
                        if (updateBean.getLwr() != null && !"".equals(updateBean.getLwr())) {
                            Log.d(TAG, "###LWR有檔名，找LWR檔案");
                            //LWR有檔名，找LWR檔案
                            findBinFile(updateBean.getLwr(), UsbReaderKt.FileKind.LWR);

                        } else {
                            Log.d(TAG, "###LWR沒檔名，結束");
                            //LWR沒檔名，結束
                            setUpToDate();
                        }

//                            if (updateBean.getLwr() == null || "".equals(updateBean.getLwr())) {
//                                getBinding().progress.setVisibility(View.INVISIBLE);
//                            }

                    }

                    //LWR 找不到檔案
                    if (kind == UsbReaderKt.FileKind.LWR) {
                        Log.d(TAG, "###找不到 lwr bin");
                        if (!isAdded()) return;

                        Toasty.warning(requireActivity(), "LWR:" + file + " Not Found", Toasty.LENGTH_LONG).show();
                        setUpToDateLwr();
                    }
                }
            }

            //插USB
            @Override
            public void onDeviceAttached(@NonNull String name) {
                //取得USB裝置
                Log.d(TAG, "onDeviceAttached: " + "on device attached, device name: " + name);
                if (updateFirmwareWindow != null && updateFirmwareWindow.isShowing()) return;

                if (!isAdded()) return;
                getBinding().progress.setVisibility(View.VISIBLE);
                findJsonFile();
            }

            //拔USB
            @Override
            public void onDeviceDetached(@NonNull String name) {
                Log.d(TAG, "onDeviceDetached: " + "on device detached, device name: " + name);
                if (updateFirmwareWindow != null && updateFirmwareWindow.isShowing()) return;
                setUpToDate();
                closeUpdateWindow();
            }

            @Override
            public void onProgress(long current, long total) {

            }

        });
    }


    /**
     * 讀取USB
     * USB讀取Json
     */
    private void findJsonFile() {
        Log.d(TAG, "FindJsonFile: ");
        usbReader.autoFindFile(FILE_JSON_NAME, UsbReaderKt.FileType.JSON, UsbReaderKt.FileKind.NORMAL);
    }

    /*
    讀取USB
    USB讀取BIN
    */
    private void findBinFile(String binName, UsbReaderKt.FileKind kind) {
        Log.d(TAG, "findBinFile BIN NAME: " + binName);
        usbReader.autoFindFile(binName, UsbReaderKt.FileType.BIN, kind);
    }



    //不可更新
    private void setUpToDate() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                closeUpdateWindow();

                if (getBinding() == null) return;
                getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);

                getBinding().btnUpdateLwr.setVisibility(View.INVISIBLE);
                getBinding().btnNoUpdateLwr.setVisibility(View.VISIBLE);

                getBinding().progress.setVisibility(View.INVISIBLE);
            });
        }
    }

    private void setUpToDateSubMcu() {
        requireActivity().runOnUiThread(() -> {
            if (getBinding() == null) return;
            getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
            getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);
            getBinding().progress.setVisibility(View.INVISIBLE);
        });
    }

    private void setUpToDateLwr() {
        requireActivity().runOnUiThread(() -> {
            if (getBinding() == null) return;
            getBinding().btnUpdateLwr.setVisibility(View.INVISIBLE);
            getBinding().btnNoUpdateLwr.setVisibility(View.VISIBLE);
            getBinding().progress.setVisibility(View.INVISIBLE);
        });
    }

    private void closeUpdateWindow() {
        Log.d(TAG, "#######closeUpdateWindow: ");
        if (updateFirmwareWindow != null) {
            updateFirmwareWindow.dismiss();
            updateFirmwareWindow = null;
        }
    }

    UpdateFirmwareWindow updateFirmwareWindow;

    private void updateStart(int type) {

        if (CheckDoubleClick.isFastClick()) return;

        closeUpdateWindow();

        byte[] binRaw = type == GENERAL.SUB_MCU ? binRawSubMcu : binRawLwr;
        String binName = type == GENERAL.SUB_MCU ? subMcuBinName : lwrBinName;
        updateFirmwareWindow = new UpdateFirmwareWindow(requireActivity(), binRaw, type, binName);
        updateFirmwareWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.START | Gravity.BOTTOM, 0, 0);
        updateFirmwareWindow.setOnCustomDismissListener(new BasePopupWindow.OnCustomDismissListener() {
            @Override
            public void onStartDismiss(MsgEvent value) {
                //  isFirmwareUpdating = false;
                if (value != null) {
                    Log.d(TAG, "onStartDismiss: " + value.getType());
                    if (value.getType() == GENERAL.SUB_MCU) {
                        //sub mcu
                        if (!(boolean) value.getObj()) {
                            //失敗
                            Toasty.error(requireActivity(), "", Toasty.LENGTH_LONG).show();
                            if (isTreadmill) {
                                App.isFirmwareUpdating = false;
                            } else {
                                getDeviceSpiritC().setEchoMode(DeviceDyacoMedical.ECHO_MODE.SECOND);
                            }
                            if (getBinding() != null) {
                                getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                                getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);
                                getBinding().progress.setVisibility(View.VISIBLE);
                                findJsonFile();
                            }
                        } else {
                            if (getBinding() != null) {
                                getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                                getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);
                            }

                            Toasty.success(requireActivity(), "", Toasty.LENGTH_LONG).show();
                            //    ((MainActivity) requireActivity()).mRestartApp();

                            Log.d(TAG, "重啟APP: ");
                            restartApp((MainActivity) requireActivity());

                        }
                    } else {
                        //LWR 下控
                        if (!(boolean) value.getObj()) {
                            Toasty.error(requireActivity(), "LWR Install Failed", Toasty.LENGTH_LONG).show();
                            if (isTreadmill) {
                                App.isFirmwareUpdating = false;
                            } else {
                                getDeviceSpiritC().setEchoMode(DeviceDyacoMedical.ECHO_MODE.SECOND);
                            }
                            if (getBinding() != null) {
                                getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                                getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);
                                getBinding().progress.setVisibility(View.VISIBLE);
                                findJsonFile();
                            }
                        } else {
                            //更新完成
                            if (getBinding() != null) {
                                getBinding().btnUpdateSubMcu.setVisibility(View.INVISIBLE);
                                getBinding().btnNoUpdateSubMcu.setVisibility(View.VISIBLE);
                                //   ((MainActivity) requireActivity()).mRestartApp();

                                if (MainActivity.isTreadmill) {
                                    ((MainActivity) requireActivity()).uartConsole.resetLwrAfterUpdate();
                                    ((MainActivity) requireActivity()).showLoading(true);
                                } else {
                                    restartApp((MainActivity) requireActivity());
                                }

                            }


                        }
                    }
                }
            }

            @Override
            public void onDismiss() {
            }
        });
    }

    private void closeUsb() {

        closeUpdateWindow();

        try {
            if (usbReader != null) {
                usbReader.closeUsb();
                usbReader = null;
            }
            binRawLwr = null;
            binRawSubMcu = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String str) {

        if (isAdded()) {
            requireActivity().runOnUiThread(() ->
                    Toasty.warning(requireActivity(), str, Toasty.LENGTH_LONG).show());
        }
    }


}