package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.hrms.HeartRateDeviceManager;
import com.corestar.libs.utils.GarminUtil;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowGarminPairBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.garmin.health.GarminDeviceScanner;
import com.garmin.health.PairingCallback;
import com.garmin.health.ScannedDevice;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import kotlin.coroutines.Continuation;

/**
 * 3 選擇配對裝置
 **/
public class GarminPairWindow extends BasePopupWindow<WindowGarminPairBinding> {
    private boolean isPairing = false;
    private final DeviceManager garminDeviceManager;
    private GarminSelectDeviceAdapter garminDeviceAdapter;
    private final MainActivity mainActivity;
    // private Future mPairingFuture;
    private ListenableFuture<Device> mPairingFuture;
    private DevicePairingCallback devicePairingCallback;
    private RxTimer consolePairTimer;
    private ScannedDevice reScanDevice;
    private GarminConnectFailedWindow garminConnectFailedWindow;
    private final List<ScannedDevice> scannedDeviceList = new ArrayList<>();
    private RxTimer scanTimer;

    public GarminPairWindow(Context context) {
        super(context, 300, 0, 0, GENERAL.FADE, false, false, false, false);

        mainActivity = (MainActivity) mContext;

        garminDeviceManager = mainActivity.mGarminDeviceManager;
        //   ((MainActivity) context).workoutViewModel.isGarminConnected.set(true);

        initGarminDeviceAdapter();

        getBinding().btnPairClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CheckDoubleClick.isFastClick()) return;

                dismiss();
            }
        });

        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });

        //配對按鈕
        garminDeviceAdapter.setOnItemClickListener(scannerDevice -> {
            if (CheckDoubleClick.isFastClick()) return;

            //配對時停止掃描
            stopScan();
            //開始配對
            startPair(scannerDevice);

        });

        //一進來就開始掃描
        Looper.myQueue().addIdleHandler(() -> {
            scanGarminDevice();
            return false;
        });

        //***************沒找到裝置時才顯示***************
        getBinding().btnTryAgain.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            scanGarminDevice();
        });
        getBinding().btnBack.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            dismiss();
        });

    }


    //***********掃描START*************//

    //掃描裝置的Listener
    private final GarminDeviceScanner garminDeviceScanner = new GarminDeviceScanner() {
        @Override
        public void onScannedDevice(ScannedDevice device) {
            HeartRateDeviceManager.setHrsDevice(device.getAddress());
            Log.d(GARMIN_TAG, "onScannedDevice: 找到裝置" + device.getAddress() + "," + device.getFriendlyName());

            scannedDeviceList.add(device);

            mainActivity.runOnUiThread(() -> {
                getBinding().groupNoDevice.setVisibility(View.GONE);
                garminDeviceAdapter.setData2View(scannedDeviceList);
            });

            showProgress(false);
        }

        @Override
        public void onScanFailed(Integer errorCode) {
            Log.d(GARMIN_TAG, "onScanFailed: ");
            showProgress(false);
            stopScan();
        }
    };

    //初始化Adapter
    private void initGarminDeviceAdapter() {
        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);
        garminDeviceAdapter = new GarminSelectDeviceAdapter(mContext);
        recyclerView.setAdapter(garminDeviceAdapter);
    }

    //開始掃描可配對裝置
    private void scanGarminDevice() {

        getBinding().groupNoDevice.setVisibility(View.GONE);

        //計時掃描N秒
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }

        scanTimer = new RxTimer();
        scanTimer.timer(18000, number -> {
            if (!isPairing) {
                showProgress(false);
            }
            stopScan();
        });
        showProgress(true);
        garminDeviceManager.registerGarminDeviceScanner(garminDeviceScanner);

        Log.d(GARMIN_TAG, "開始掃描Garmin裝置: ");
    }

    //停止掃描可配對裝置
    private void stopScan() {
        Log.d(GARMIN_TAG, "stopScan: ");

        //沒找到裝置
        if (scannedDeviceList == null || scannedDeviceList.isEmpty()) {
            if (getBinding() != null)
                getBinding().groupNoDevice.setVisibility(View.VISIBLE);
        }

        //移除ScanListener
        if (garminDeviceScanner != null) {
            garminDeviceScanner.clearScannedDevices();
            garminDeviceManager.unregisterGarminDeviceScanner(garminDeviceScanner);
        }

        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer = null;
        }
    }

    //************掃描END**************

    //-----------------------------------------------------------------------------------

    //************配對START**************
    //點擊配對後執行 開始配對
    @SuppressLint("MissingPermission")
    private void startPair(ScannedDevice scannedDevice) {

        HeartRateDeviceManager.setHrsDevice(scannedDevice.getAddress());

        isPairing = true;
        showProgress(true);

        reScanDevice = scannedDevice; //重新配對時使用

        stopConsolePairTimer();
        consolePairTimer = new RxTimer();//Console的配對
        consolePairTimer.timer(40000, number -> {
            stopConsolePairTimer();
            connectFailedWindow(scannedDevice);
        });

        Log.d(GARMIN_TAG, "開始檢查Console有無綁定此裝置. Device: " + scannedDevice.getAddress());

        //  garminSdkPairStart(scannedDevice);


        //CoreStarLib 檢查配對 & 使用Android系統配對
        GarminUtil garminUtil = new GarminUtil();

        //檢查Console有無綁定此Garmin手錶
        garminUtil.checkDeviceBondState(mContext, scannedDevice.getAddress(), new GarminUtil.BondStateListener() {
            @Override
            public void onCheckDeviceBondState(String s, GarminUtil.BOND_STATE bond_state) {

                if (bond_state != GarminUtil.BOND_STATE.BOND_NONE) {
                    Log.d(GARMIN_TAG, "Console已綁定此裝置，開始GarminSDK配對: " + bond_state + "," + s);
                    garminSdkPairStart(scannedDevice);
                    return;
                }

                Log.d(GARMIN_TAG, "Console尚未綁定此裝置，開始Console配對: " + bond_state + "," + s);
                //CoreStarLib配對
                garminUtil.bondDevice(mContext, scannedDevice.getAddress(), new GarminUtil.BondStateListener() {
                    @Override
                    public void onCheckDeviceBondState(String s, GarminUtil.BOND_STATE bond_state) {
                    }

                    @Override
                    public void onBondDevice(String s, GarminUtil.BOND_STATE bond_state) {
                        //garmin 配對  //BOND_NONE timeout
                        if (bond_state == GarminUtil.BOND_STATE.BOND_BONDED) {

                            if (CheckDoubleClick.isFastClick())
                                return;//第一次無回應後，第二次 garminUtil.util 連續回兩次

                            Log.d(GARMIN_TAG, "Console綁定此裝置成功，開始GarminSDK配對: " + bond_state + "," + s);
                            garminSdkPairStart(scannedDevice);
                        } else {
                            Log.d(GARMIN_TAG, "Console綁定此裝置失敗: " + bond_state + "," + s);
                            connectFailedWindow(scannedDevice);
                            stopConsolePairTimer();
                        }
                    }
                });
            }

            @Override
            public void onBondDevice(String s, GarminUtil.BOND_STATE bond_state) {
            }
        });
    }

    //GarminSDK的配對
    @SuppressLint("MissingPermission")
    private void garminSdkPairStart(ScannedDevice scannedDevice) {

        stopConsolePairTimer();

        //避免跳出上方配對通知
        //已用Console連線配對，所以不需要
//        BluetoothAdapter.getDefaultAdapter().startDiscovery();
//        SystemClock.sleep(1000);

        devicePairingCallback = new DevicePairingCallback();

        ThreadUtil.executor().execute(() -> {

            mPairingFuture = DeviceManager.asyncPair(scannedDevice, devicePairingCallback);

            Futures.addCallback(mPairingFuture, new FutureCallback<Device>() {
                @Override
                public void onSuccess(Device device) {
                    MainActivity.currentGarminAddress = "";
                    Log.d(GARMIN_TAG, "GarminSDK配對成功. device : " + device.address());
                    //  Log.d(GARMIN_TAG, "####0: " + Thread.currentThread().getName());
                    //回GarminDevicesWindow 重刷
                    new RxTimer().timer(1500, number -> {
                    //    Log.d(GARMIN_TAG, "####1: " + Thread.currentThread().getName());
                        Log.d(GARMIN_TAG, "回GarminDevicesWindow 重刷 ");
                        dismiss();
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.d(GARMIN_TAG, "GarminSDK配對失敗: " + t.getLocalizedMessage());
                    connectFailedWindow(reScanDevice);
                }
            }, ThreadUtil.executor());
        });


//        mPairingFuture = DeviceManager.asyncPair(scannedDevice, devicePairingCallback);
//
//        Futures.addCallback(mPairingFuture, new FutureCallback<Device>() {
//            @Override
//            public void onSuccess(Device device) {
//                MainActivity.currentGarminAddress = "";
//                Log.d(GARMIN_TAG, "GarminSDK配對成功. device : " + device.address());
//
//                //回GarminDevicesWindow 重刷
//                new RxTimer().timer(1500, number -> dismiss());
//            }
//
//            @Override
//            public void onFailure(@NonNull Throwable t) {
//                Log.d(GARMIN_TAG, "GarminSDK配對失敗: " + t.getLocalizedMessage());
//                connectFailedWindow(reScanDevice);
//           //     if (t instanceof PairingFailedException) {
//                    //       pairingFailed((PairingFailedException) t);
//             //   } else {
//                    //       pairingFailed(PairingFailedException.createFromException(t, PairingFailedException.REASON_UNKNOWN_CHECK_EXCEPTION));
//            //    }
//            }
//        }, ThreadUtil.executor());

    }

    //配對失敗
    private void connectFailedWindow(ScannedDevice scannedDevice) {
        showProgress(false);
        if (garminConnectFailedWindow != null && garminConnectFailedWindow.isShowing()) return;
        if (isDone) return; //window關閉時
        ((Activity) mContext).runOnUiThread(() -> {
            try {
                cancelPair();
                Log.d("GARMINNN", "connectFailedWindow:6666666 ");
                garminConnectFailedWindow = new GarminConnectFailedWindow(mContext);
                garminConnectFailedWindow.showAtLocation(mainActivity.getWindow().getDecorView(), Gravity.END | Gravity.BOTTOM, 0, 0);
                garminConnectFailedWindow.setOnCustomDismissListener(new OnCustomDismissListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onStartDismiss(MsgEvent value) {
                        //點擊TryAgain執行Pair
                        if (value == null) return;
                        if ((boolean) value.getObj()) {
                            if (scannedDevice != null) {
                                startPair(scannedDevice);
                            }
                        } else {
                            dismiss();
                        }
                    }

                    @Override
                    public void onDismiss() {
                        garminConnectFailedWindow = null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //沒作用
    private class DevicePairingCallback implements PairingCallback {

        @Override
        public void authTimeout() {

            Log.d("GARMINNN", "authTimeout.");
            cancelPair();
        }

        @Nullable
        @Override
        public Object authRequested(@NonNull Continuation<? super Integer> continuation) {
            Log.d("GARMINNN", "authRequested: ");
            Future future = SettableFuture.create();
            try {
                return future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //取消配對
    private void cancelPair() {

        isPairing = false;

        try {
            if (mPairingFuture != null) {
                mPairingFuture.cancel(true);
                mPairingFuture = null;
                devicePairingCallback = null;
            }

            stopScan();

            stopConsolePairTimer();

            stopSdkPairTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopConsolePairTimer() {

        if (consolePairTimer != null) {
            consolePairTimer.cancel();
            consolePairTimer = null;
        }
    }

    private void stopSdkPairTimer() {
    }

    private void showProgress(boolean isShow) {
        try {
            mainActivity.runOnUiThread(() -> {
                if (getBinding() != null) {
                    try {
                        getBinding().pairProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
                        getBinding().pairBG.setVisibility(isShow ? View.VISIBLE : View.GONE);
                        getBinding().btnPairClose.setVisibility(isShow ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************配對END**************

    private boolean isDone = false;

    @Override
    public void dismiss() {

        isDone = true;

        isPairing = false;

        try {

            if (mPairingFuture != null && !mPairingFuture.isDone()) {
                mPairingFuture.cancel(true);
            }

            mPairingFuture = null;
            devicePairingCallback = null;

            stopScan();

            stopConsolePairTimer();

            stopSdkPairTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.dismiss();

    }


}
