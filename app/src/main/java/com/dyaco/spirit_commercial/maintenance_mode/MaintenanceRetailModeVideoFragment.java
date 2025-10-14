package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.mRetailVideoPath;
import static com.dyaco.spirit_commercial.support.CommonUtils.getMimeType;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_USB_MODE_SET;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceRetailModeVideoBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MaintenanceRetailModeVideoFragment extends BaseBindingDialogFragment<FragmentMaintenanceRetailModeVideoBinding> {
    String fileName = "retail.mp4";
    public WorkoutViewModel workoutViewModel;
    String tempPath;

    private UsbReaderKt usbReader;
    private static final String TAG = "USB_UPDATE";
    private DeviceSettingViewModel deviceSettingViewModel;

    private MainActivity m;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
        m = ((MainActivity) requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.DATA);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);


        initEvent();


        //  videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/" + retailVideoName;

        initUsbUpdate();


    }

    private void initUsbUpdate() {
        LiveEventBus.get(ON_USB_MODE_SET, DeviceSpiritC.MCU_SET.class).observe(getViewLifecycleOwner(), s -> {
                    if (s == DeviceSpiritC.MCU_SET.OK) {
                        initUsbReadManager();
                    } else {
                        Toasty.warning(requireActivity(), "USB_MODE ERROR", Toasty.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void initUsbReadManager() {

        Log.d(TAG, "initUsbReadManager: ");
        usbReader = new UsbReaderKt(requireActivity());
        usbReader.setListener(new UsbReaderKt.UsbReaderListener() {

            @Override
            public void onFindFile(@NonNull String file, @NonNull UsbReaderKt.FileStatus status, @NonNull UsbReaderKt.FileType type, String path, byte[] raw, @NonNull UsbReaderKt.FileKind kind) {
//                String log = "########on find file, " + "\n" + "file name: " + file + "\n" + ", status: " + status + "\n" + ", type: " + type + "\n" + "data:" + path + "\n" + ", raw: " + (raw != null ? raw.length : null);
//                Log.d(TAG, "onFindFile: " + log);

                Log.d(TAG, "#######onFindFile: ");
                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.GONE);
                }


                if (type == UsbReaderKt.FileType.MP4) {
                    if (status == UsbReaderKt.FileStatus.FILE_FOUND) {
                        checkVideo(path);
                    } else {
                        //not found
                        getBinding().videoView.setVisibility(View.GONE);
                        getBinding().gError.setVisibility(View.VISIBLE);
                        getBinding().btnApply.setEnabled(false);
                    }
                }
            }

            //插USB
            @Override
            public void onDeviceAttached(@NonNull String name) {
                //取得USB裝置
                Log.d(TAG, "onDeviceAttached: " + "取得USB裝置, device name: " + name);
                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.VISIBLE);
                    getBinding().gError.setVisibility(View.GONE);
                }

                findVideoFile();
            }

            //拔USB
            @Override
            public void onDeviceDetached(@NonNull String name) {
                Log.d(TAG, "onDeviceDetached: " + "on device detached, device name: " + name);

                initV();
            }

            @Override
            public void onProgress(long current, long total) {

                long progress = (100L * current / total);
                //    Log.d(TAG, "####: Progress:" + progress + ", CURRENT:" + current + ", TOTAL:" + total);

                getBinding().tvPer.setText(String.format(Locale.getDefault(), "%d %%", progress));

            }

            @Override
            public void onError(@NonNull UsbReaderKt.UsbError error) {

                switch (error) {
//                    case NO_USB_DEVICE:
//                    case PARAMETER_ERROR:
//                    case DEVICE_NO_PARTITION:
//                    case PERMISSION_FAILED:
//                    case COPY_FILE_ERROR:
//                    case BACKGROUND_SEARCH_ERROR:
                    case VIDEO_VALIDATION_FAILED:
                        if (getBinding() != null) {
                            getBinding().progress.setVisibility(View.GONE);

                            int[] errors = usbReader.getVideoValidationErrors();
                            Log.e(TAG, "影片有問題: " + Arrays.toString(errors));
                            //0 沒檢查, 1ok, 2fail
                            getBinding().tvW1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), errors[0] == 1 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
                            getBinding().tvW2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
                            getBinding().tvW3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), errors[2] == 1 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
                            getBinding().tvW4.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_tick_16), null, null, null);

                            Log.e(TAG, "GERROR: 2222222 ");
                            getBinding().gError.setVisibility(View.VISIBLE);
                            getBinding().btnApply.setEnabled(false);
                        }
                        break;
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

    private void findVideoFile() {
        usbReader.autoFindFile(fileName, UsbReaderKt.FileType.MP4, UsbReaderKt.FileKind.NORMAL);
    }

    private void initEvent() {
        getBinding().btnDone.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
            if (getBinding() != null) {
                getBinding().videoView.stopPlayback(); // 1. 停止播放並釋放資源
                getBinding().videoView.setVisibility(View.GONE); // 2. 隱藏畫面（可選）
                getBinding().bgVideo.setVisibility(View.VISIBLE);
            }
         //   getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
            closeUsb();
            dismiss();
        });

        getBinding().btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastClick()) return;
                applyVideo();
            }
        });


        TenTapClick.setTenClickListener(getBinding().tvTitle1, true, () -> {
            File f2 = new File(mRetailVideoPath);
            boolean boo2 = f2.delete();
            if (boo2) {
                Toasty.success(getApp(), "OK", Toasty.LENGTH_SHORT).show();
            } else {
                Toasty.error(getApp(), "ERROR", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void applyVideo() {

        if (tempPath == null) return;

        m.showLoading(true);
        new RxTimer().timer(3000, number -> {
            m.showLoading(false);
            Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();
//            RetailVideoWindow retailVideoWindow = new RetailVideoWindow(requireActivity());
//            retailVideoWindow.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
        });


        try {
            FileUtils.copy(Files.newInputStream(Paths.get(tempPath)), Files.newOutputStream(Paths.get(MainActivity.mRetailVideoPath)));
        } catch (IOException e) {
            Toasty.error(requireActivity(), "Update Failed.", Toasty.LENGTH_LONG).show();
            Log.d(TAG, "Exception: " + e.getLocalizedMessage());
        }
    }


    private void initVideo(String path) {
        if (getBinding() == null) return;
        getBinding().videoView.setVisibility(View.VISIBLE);
        getBinding().videoView.setVideoPath(path);
        getBinding().videoView.start();
        getBinding().videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));

        getBinding().videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
            Log.d(TAG, "onError: " + what + "," + extra);

//            if ("".equals(mLocalRetailVideoPath)){
//                initNoRetail();
//            } else {
//                getBinding().videoView.setVideoPath(mLocalRetailVideoPath);
//                getBinding().videoView.start();
//            }

            return true; //false 會彈出警告視窗
        });
    }


    private void closeUsb() {
        try {
//            if (csUsbDeviceList != null) {
//                if (!csUsbDeviceList.isEmpty()) {
//                    csUsbDeviceList.get(0).getDevice().close();
//                    Log.d(TAG, "closeUsb: " + csUsbDeviceList.size());
//                }
//                csUsbDeviceList = null;
//            }
            if (usbReader != null) {
                Log.d(TAG, "usbReader: unregisterListener ");
                usbReader.closeUsb();
                usbReader = null;

            }
        } catch (Exception e) {
            showException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        closeUsb();//加在 btnDone 沒效果
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.CHARGER);
    }

    private void checkVideo(String path) {

        if (path == null) return;

        tempPath = path;

        File file = new File(path);

        boolean w1 = false, w2 = false, w3 = false, w4 = false;
        int width = 0, height = 0;
        try (final MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(path);
            Bitmap frame = retriever.getFrameAtTime();
            width = frame.getWidth();
            height = frame.getHeight();
        } catch (Exception e) {
            showException(e);
        }


        if ("video/mp4".equals(getMimeType(path))) {
            w1 = true;
        }

        //解析度
        if (width == 1920 && height == 1080) {
            w2 = true;
        }

        float fileSize = CommonUtils.byte2Mb(file.length());
        if (fileSize <= 120) { //6980598   6.980598
            w3 = true;
        }

        if (fileName.equals(file.getName())) {
            w4 = true;
        }

        if (getBinding() == null) return;
        getBinding().tvW1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w1 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w2 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w3 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW4.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w4 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);


        Log.e("USB_UPDATE", "format: " + getMimeType(path));
        Log.e("USB_UPDATE", "checkVideo: width:" + width + ", height:" + height);
        Log.e("USB_UPDATE", "size: " + fileSize);
        Log.e("USB_UPDATE", "fileName: " + file.getName());


        if (w1 && w2 && w3 && w4) {
            getBinding().btnApply.setEnabled(true);
            initVideo(path);
            getBinding().gError.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "GERROR: 3333333 ");
            getBinding().gError.setVisibility(View.VISIBLE);
            getBinding().btnApply.setEnabled(false);
        }


//        if (w1 && w3 && w4) {
//            getBinding().gError.setVisibility(View.GONE);
//            getBinding().btnApply.setEnabled(true);
//            initVideo(path);
//        }

    }


    private void initV() {
        try {

            getBinding().videoView.stopPlayback();
            m.showLoading(false);
            getBinding().videoView.setVisibility(View.GONE);
            getBinding().progress.setVisibility(View.GONE);
            getBinding().gError.setVisibility(View.GONE);
            getBinding().btnApply.setEnabled(false);
            getBinding().tvPer.setText(R.string.no_usb_device_detected);
            getBinding().tvW1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
            getBinding().tvW2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
            getBinding().tvW3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
            getBinding().tvW4.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);

        } catch (Exception e) {
            showException(e);
        }
    }

}