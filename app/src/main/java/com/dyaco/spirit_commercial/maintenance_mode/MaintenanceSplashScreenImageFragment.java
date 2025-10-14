package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.splashImagePathJPG;
import static com.dyaco.spirit_commercial.support.CommonUtils.getMimeType;
import static com.dyaco.spirit_commercial.support.CommonUtils.isJPGs;
import static com.dyaco.spirit_commercial.support.CommonUtils.isPNGs;
import static com.dyaco.spirit_commercial.support.CommonUtils.showException;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_USB_MODE_SET;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.corestar.libs.device.DeviceDyacoMedical;
import com.corestar.libs.device.DeviceSpiritC;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceSplashScreenImageBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.bumptech.glide.Glide;
import com.dyaco.spirit_commercial.support.SplashImageProcessor;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.interaction.TenTapClick;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;

import es.dmoral.toasty.Toasty;

/**
 * usb讀取若有問題 > console重開看看
 */
@SuppressLint("CustomSplashScreen")
public class MaintenanceSplashScreenImageFragment extends BaseBindingDialogFragment<FragmentMaintenanceSplashScreenImageBinding> {
    boolean findImageDone = false;
    String fileNameJPG = "welcome.jpg";
    String fileNamePNG = "welcome.png";
    public WorkoutViewModel workoutViewModel;

    private UsbReaderKt usbReader;
    private static final String TAG = "USB_UPDATE";
    private DeviceSettingViewModel deviceSettingViewModel;
    private MainActivity m;

    String tempPath;
    String tempPathJPG;
    String tempPathPNG;

    boolean nowIsPng;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogType(GENERAL.TRANSLATION_X);
        m = ((MainActivity) requireActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //開啟 USB DATA 模試
        getDeviceSpiritC().setUsbMode(DeviceDyacoMedical.USB_MODE.DATA);

        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
        getBinding().setDeviceSettingViewModel(deviceSettingViewModel);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);


        initEvent();


        //  videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/" + retailVideoName;

        initUsbUpdate();

        initDisplayBrightnessBar();

    }

    private void initUsbUpdate() {
        ///開啟 USB DATA 模試 ,接收回傳結果
        LiveEventBus.get(ON_USB_MODE_SET, DeviceSpiritC.MCU_SET.class).observe(getViewLifecycleOwner(), s -> {
                    if (s == DeviceSpiritC.MCU_SET.OK) {
                        //USB_MODE OK 執行USB
                        initUsbReadManager();

//                        UsbReaderExfat.waitAndMountUsb("update", ".json", new UsbReaderExfat.MountCallback() {
//                            @Override
//                            public void onMountSuccess(@NonNull String mountPath, @NonNull List<File> matchedFiles) {
//                                for (File file : matchedFiles) {
//                                    Log.d("UsbReaderExfat", "找到檔案：" + file.getAbsolutePath());
//                                    // 你可以自行處理複製、顯示、上傳等
//
//                                    try {
//                                        BufferedReader reader = new BufferedReader(new FileReader(file));
//                                        StringBuilder text = new StringBuilder();
//                                        String line;
//                                        while ((line = reader.readLine()) != null) {
//                                            text.append(line).append("\n");
//                                        }
//                                        reader.close();
//                                   //     UpdateBean updateBeanUsb = new Gson().fromJson(text.toString(), UpdateBean.class);
//                                        Log.d("UsbReaderExfat", "文字內容：" + text.toString());
//                                    } catch (IOException e) {
//                                        Log.e("UsbReaderExfat", "讀檔失敗：" + e.getMessage());
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onMountFailed(@NonNull String errorMessage) {
//                                Log.e("ExfatMounter", "掛載失敗：" + errorMessage);
//                            }
//                        });



                    } else {
                        Toasty.warning(requireActivity(), "USB_MODE ERROR", Toasty.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void initUsbReadManager() {

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

                Toasty.error(requireActivity(), "Error: " + error.name(), Toasty.LENGTH_SHORT).show();
                Log.e("USB_UPDATE", "errorMsg:" + error.name());

                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.GONE);
                }
            }

            /*
            取得USB檔案的Data值
             */
            @Override
            public void onFindFile(@NonNull String file, @NonNull UsbReaderKt.FileStatus status, @NonNull UsbReaderKt.FileType type, String path, byte[] raw, @NonNull UsbReaderKt.FileKind kind) {
                String log = "########on find file, " + "\n" + "file name: " + file + "\n" + ", status: " + status + "\n" + ", type: " + type + "\n" + "data:" + path + "\n" + ", raw: " + (raw != null ? raw.length : null);
                Log.d(TAG, "onFindFile: " + log);

                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.GONE);
                }


                if (type == UsbReaderKt.FileType.IMAGE) {

                    if (status == UsbReaderKt.FileStatus.FILE_FOUND) {
                        //第一次找JPG，第二次找PNG
                        if (isJPGs(file)) {
                            tempPathJPG = path;
                            findFile(fileNamePNG);
                            checkImage(path);
                        }

                        if (isPNGs(file)) {
                            tempPathPNG = path;
                            checkImage(path);
                        }

                    } else {
                        if (isJPGs(file)) {
                            tempPathJPG = "";
                            findFile(fileNamePNG);
                        }

                        if (isPNGs(file)) {
                            tempPathPNG = "";
                            if ("".equals(tempPathJPG)) {
                                //都沒找到
                                if (isAdded()) {
                                    requireActivity().runOnUiThread(() -> {
                                        if (getBinding() != null) {
                                            getBinding().gShow.setVisibility(View.INVISIBLE);
                                            getBinding().gError.setVisibility(View.VISIBLE);
                                            getBinding().btnApply.setEnabled(false);
                                        }
                                    });
                                }
                            }
                        }

                    }
                }
            }

            //插USB
            @Override
            public void onDeviceAttached(@NonNull String name) {
                //取得USB裝置
                Log.d(TAG, "onDeviceAttached: " + "取得USB裝置, device name: " + name);
                findImageDone = false;
                tempPathPNG = "";
                tempPathJPG = "";

                if (getBinding() != null) {
                    getBinding().progress.setVisibility(View.VISIBLE);
                }
                //取得USB裝置後就執行 autoFindFile 讀取USB內容
                findFile(fileNameJPG);
            }

            //拔USB
            @Override
            public void onDeviceDetached(@NonNull String name) {
                Log.d(TAG, "onDeviceDetached: " + "on device detached, device name: " + name);

                initV();
            }

            @Override
            public void onProgress(long current, long total) {
             //   Log.d(TAG, "onProgress: " + current +","+ total);
            }

        });

    }


    private void findFile(String fileName) {
        // 呼叫 autoFindFile 自動流程，傳入檔名、檔案類型 (IMAGE)、檔案種類 (NORMAL)
        usbReader.autoFindFile(fileName, UsbReaderKt.FileType.IMAGE, UsbReaderKt.FileKind.NORMAL);
    }


    private void applyImage() {

        if (tempPath == null) return;

        m.showLoading(true);


        Log.d("PPPPPPPP", "applyImage: " + newAlpha);
        SplashImageProcessor processor = new SplashImageProcessor(getApp(), splashImagePathJPG);
        processor.processImage(tempPath, (int) newAlpha, new SplashImageProcessor.OnProcessCallback() {
            @Override
            public void onSuccess() {
                m.showLoading(false);
                Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull String errorMessage) {
                Toasty.error(requireActivity(), errorMessage, Toasty.LENGTH_SHORT).show();
            }
        });



//        new Thread(() -> {
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(new File(tempPath).getAbsolutePath(), bmOptions);
//            bitmap = Bitmap.createBitmap(bitmap);
//            addAlphaAndSave(bitmap);
//            requireActivity().runOnUiThread(() -> {
//                m.showLoading(false);
//                Toasty.success(requireActivity(), "", Toasty.LENGTH_SHORT).show();
//            });
//        }).start();

    }

//    private void addAlphaAndSave(Bitmap originalBitmap) {
//
//        Bitmap newBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(newBitmap);
//        canvas.drawBitmap(originalBitmap, 0, 0, new Paint());
//        Paint aPaint = new Paint();
//        aPaint.setStyle(Paint.Style.FILL);
//        aPaint.setColor(Color.BLACK);
//        aPaint.setAlpha(Math.round((newAlpha * 0.01f) * 255f));
//        canvas.drawRect(0F, 0F, (float) originalBitmap.getWidth(), (float) originalBitmap.getHeight(), aPaint);
//
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(splashImagePathJPG);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
////        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); //png 壓不了
//        newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//
//
//        if (!newBitmap.isRecycled()) {
//            newBitmap.recycle();
//        }
//
//    }

    private void initEvent() {

        getBinding().ivPPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkImage(nowIsPng ? tempPathJPG : tempPathPNG);
            }
        });

        getBinding().ivNNN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkImage(nowIsPng ? tempPathJPG : tempPathPNG);
            }
        });

        getBinding().btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckDoubleClick.isFastClick()) return;
                applyImage();
            }
        });


        getBinding().btnDone.setOnClickListener(view -> {
            if (CheckDoubleClick.isFastClick()) return;
       //     getDeviceSpiritC().setUsbMode(DeviceSpiritC.USB_MODE.CHARGER);
            closeUsb();
            dismiss();
        });
    }


    private void initImage(String path) {

        getBinding().imageView.setVisibility(View.VISIBLE);

        Glide.with(getApp())
                .load(path)
                .placeholder(R.color.black)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(getBinding().imageView);
    }


    private void closeUsb() {
        if (usbReader != null) {
            usbReader.closeUsb();

        }

//        try {
//            if (csUsbDeviceList != null) {
//                if (!csUsbDeviceList.isEmpty()) {
//                    csUsbDeviceList.get(0).getDevice().close();
//                    Log.d(TAG, "closeUsb: " + csUsbDeviceList.size());
//                }
//                csUsbDeviceList = null;
//            }
//            if (usbReader != null) {
//                Log.d(TAG, "usbReader: unregisterListener ");
//                usbReader.unregisterListener();
//                usbReader = null;
//
//            }
//        } catch (Exception e) {
//           showException(e);
//        }
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


    boolean isJPG;
    boolean isPNG;
    String fileName;

    private void checkImage(String path) {

        if (!"".equals(tempPathJPG) && !"".equals(tempPathPNG)) {
            getBinding().ivPPP.setVisibility(View.VISIBLE);
            getBinding().ivNNN.setVisibility(View.VISIBLE);
            getBinding().tvMorePhoto.setVisibility(View.VISIBLE);
//            getBinding().tvMorePhoto.setText(String.format("%s 1 / 2", getString(R.string.File)));
        } else {
            getBinding().tvMorePhoto.setVisibility(View.GONE);
            getBinding().ivPPP.setVisibility(View.GONE);
            getBinding().ivNNN.setVisibility(View.GONE);
        }


        nowIsPng = isPNGs(path);

        if (nowIsPng) {
            getBinding().tvMorePhoto.setText(String.format("%s 1 / 2", getString(R.string.File)));
        } else {
            getBinding().tvMorePhoto.setText(String.format("%s 2 / 2", getString(R.string.File)));
        }

        tempPath = path;

        File file = new File(path);

        boolean w1 = false, w2 = false, w3 = false, w4 = false;
        int width, height;

        isJPG = "image/jpeg".equals(getMimeType(path));
        isPNG = "image/png".equals(getMimeType(path));
        //MimeType
        if (isJPG || isPNG) {
            w1 = true;
        }

        //Resolution
        BitmapFactory.Options bitMapOption = new BitmapFactory.Options();
        bitMapOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bitMapOption);
        width = bitMapOption.outWidth;
        height = bitMapOption.outHeight;
        if (width == 1920 && height == 1080) {
            w2 = true;
        }

        //Size
        float fileSize = CommonUtils.byte2Mb(file.length());
        if (fileSize <= 12) {
            w3 = true;
        }

        fileName = file.getName();
        //FileName
        if (fileNameJPG.equals(file.getName()) || fileNamePNG.equals(file.getName())) {
            w4 = true;
        }

        getBinding().tvW1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w1 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w2 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w3 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);
        getBinding().tvW4.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), w4 ? R.drawable.icon_tick_16 : R.drawable.icon_crossed_16), null, null, null);


        if (w1 && w2 && w3 && w4) {
            getBinding().btnApply.setEnabled(true);
            initImage(path);
            getBinding().gShow.setVisibility(View.VISIBLE);
            getBinding().gError.setVisibility(View.INVISIBLE);
        } else {
            getBinding().gShow.setVisibility(View.INVISIBLE);
            getBinding().gError.setVisibility(View.VISIBLE);
            getBinding().btnApply.setEnabled(false);
        }


        //test
//        initImage(path);
//        getBinding().gShow.setVisibility(View.VISIBLE);
//        getBinding().gError.setVisibility(View.INVISIBLE);
//        getBinding().btnApply.setEnabled(true);


    }


    float newAlpha;

    private void initDisplayBrightnessBar() {

        getBinding().DisplayBrightnessBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float brightnessPresent, float rightValue, boolean isFromUser) {

                try {
                    int v = Math.round(brightnessPresent);
                    getBinding().tvShadingValue.setText(String.valueOf(v));
                    newAlpha = v;
                    getBinding().ivShadow.setAlpha(newAlpha / 100f);
                } catch (Exception e) {
                    showException(e);
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });


        getBinding().DisplayBrightnessBar.setProgress(0);


        TenTapClick.setTenClickListener(getBinding().tvTitle1, true, () -> {
            File f2 = new File(splashImagePathJPG);
            boolean boo2 = f2.delete();
            if (boo2) {
                Toasty.success(getApp(), "", Toasty.LENGTH_SHORT).show();
            } else {
                Toasty.error(getApp(), "ERROR", Toasty.LENGTH_SHORT).show();
            }
        });
    }


    private void initV() {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            try {

                findImageDone = false;
                tempPathPNG = "";
                tempPathJPG = "";

                m.showLoading(false);

                getBinding().tvMorePhoto.setVisibility(View.INVISIBLE);
                getBinding().ivPPP.setVisibility(View.GONE);
                getBinding().ivNNN.setVisibility(View.GONE);
                getBinding().progress.setVisibility(View.GONE);
                getBinding().gShow.setVisibility(View.GONE);
                getBinding().gError.setVisibility(View.GONE);
                getBinding().btnApply.setEnabled(false);
                getBinding().DisplayBrightnessBar.setProgress(0);
                getBinding().tvW1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
                getBinding().tvW2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
                getBinding().tvW3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);
                getBinding().tvW4.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_dot_16), null, null, null);

            } catch (Exception e) {
                showException(e);
            }
        });
    }


}