package com.dyaco.spirit_commercial.settings;

//public class BtAudioWindow extends BasePopupWindow<WindowBtAudioBinding> {
//    //private boolean isFFFScan = false;
//    private static final String TAG = "###BLUETOOTH";
//
//    private final MainActivity m;
//
//    private List<AudioDeviceWatcher.AudioDevice> mBluetoothDevicesList;
//
//
//    public BtAudioWindow(Context context, boolean isFloat) {
//        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, isFloat, true);
//
//        m = ((MainActivity) context);
//
////        mBluetoothDevicesList.clear();
//
////        new RxTimer().timer((scanTime + 3) * 1000, number -> {
////            try {
////                if (getBinding() != null) {
////                    getBinding().progress.setVisibility(View.GONE);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
//
//        initView();
//        initCheckbox();
//
//        initHeartRateDevice();
//
//        getBinding().cbHR.setChecked(MainActivity.isCbBtOn);
//
//
//        boolean isCheck = getBinding().cbHR.isChecked();
//        getBinding().noHrIcon.setVisibility(isCheck ? View.GONE : View.VISIBLE);
//        getBinding().noHrText.setVisibility(isCheck ? View.GONE : View.VISIBLE);
//        getBinding().recyclerView.setVisibility(isCheck ? View.VISIBLE : View.GONE);
//        if (isCheck) {
//            startS();
//        }
//
//
//        LiveEventBus.get(ON_BT_DEVICE_FOUND, BluetoothDevice.class).observeForever(observer);
//        LiveEventBus.get(ON_BT_DEVICE_SCAN, Boolean.class).observeForever(observer2);
//        LiveEventBus.get(ON_BT_DEVICE_BOND_NONE, Boolean.class).observeForever(observer3);
//
//    }
//
//    //取得設備
//    Observer<BluetoothDevice> observer = this::onFoundNewDevice;
//    Observer<Boolean> observer2 = s -> startS();
//
//    Observer<Boolean> observer3 =  s -> stopDismissTimer();
//
//    private void startS() {
//        Log.d(TAG, "開始掃描: ");
//
//        m.getAudioDeviceWatcher().stopScan();
//        new RxTimer().timer(500,number -> {
//            m.getAudioDeviceWatcher().startScan();
//        });
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        m.getAudioDeviceWatcher().stopScan();
//        LiveEventBus.get(ON_BT_DEVICE_FOUND, BluetoothDevice.class).removeObserver(observer);
//        LiveEventBus.get(ON_BT_DEVICE_SCAN, Boolean.class).removeObserver(observer2);
//        LiveEventBus.get(ON_BT_DEVICE_BOND_NONE, Boolean.class).removeObserver(observer3);
//        itemsBtDeviceListBinding = null;
////        if (dismissTimer != null) {
////            dismissTimer.cancel();
////            dismissTimer = null;
////        }
//    }
//
//
//    @SuppressLint("MissingPermission")
//    private void onFoundNewDevice(BluetoothDevice bluetoothDevices) {
//
//      //  if (isFFFScan) return;
//
//        //map to list
////        ArrayList<Integer> keyList = new ArrayList<Integer>(myMap.keySet());
////        ArrayList<String> valueList = new ArrayList<String>(myMap.values());
//
//
//        Log.d(TAG, "##################onFoundNewDevice: size: " + m.getAudioDeviceWatcher().getAudioDevices().size());
//        mBluetoothDevicesList = new ArrayList<>(m.getAudioDeviceWatcher().getAudioDevices().values());
//
////        for (AudioDeviceWatcher.AudioDevice s : m.getAudioDeviceWatcher().getAudioDevices().values()) {
////            Log.d(TAG, "onFoundNewDevice: " + s.getDevice().getName() + ", " + s.getDevice().getAddress() + ", 綁定：" + s.isBonded() + ", 連線：" + s.isConnected());
////        }
//
//
//        //已配對的排最上面
//        mBluetoothDevicesList.sort(new BtSort());
//
//        getBinding().tvNoDevice.setVisibility(View.GONE);
//        btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
//
//    }
//
//    private void initCheckbox() {
//
//
//        getBinding().cbHR.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                //開始掃描
//                //   getBinding().progress.setVisibility(View.VISIBLE);
//                getBinding().noHrIcon.setVisibility(View.GONE);
//                getBinding().noHrText.setVisibility(View.GONE);
//                getBinding().recyclerView.setVisibility(View.VISIBLE);
//                startS();
//
//                if (mBluetoothDevicesList != null) {
//                    mBluetoothDevicesList.clear();
//                    btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
//                }
//
//                MainActivity.isCbBtOn = true;
//            } else {
//                //停止掃描
//                m.getAudioDeviceWatcher().stopScan();
//
//                //   getBinding().progress.setVisibility(View.GONE);
//                getBinding().noHrIcon.setVisibility(View.VISIBLE);
//                getBinding().noHrText.setVisibility(View.VISIBLE);
//                getBinding().recyclerView.setVisibility(View.INVISIBLE);
//
//                m.unBondAudio();
//
//                getBinding().tvNoDevice.setVisibility(View.GONE);
//                MainActivity.isCbBtOn = false;
//
//                if (mBluetoothDevicesList != null) {
//                    mBluetoothDevicesList.clear();
//                    btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
//                }
//            }
//        });
//    }
//
//
//    BtAudioDeviceAdapter btAudioDeviceAdapter;
//
//    @SuppressLint("MissingPermission")
//    private void initHeartRateDevice() {
//
//        RecyclerView recyclerView = getBinding().recyclerView;
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        recyclerView.setHasFixedSize(true);
//        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
//        recyclerView.addItemDecoration(dividerItemDecoration);
//        btAudioDeviceAdapter = new BtAudioDeviceAdapter(mContext);
//        recyclerView.setAdapter(btAudioDeviceAdapter);
//
//
//        //點擊
////        btAudioDeviceAdapter.setOnItemClickListener((heartRateDevice, view1, view2, progress) -> {
//        btAudioDeviceAdapter.setOnItemClickListener((heartRateDevice, binding) -> {
//
//            if (getBinding() == null) return;
//
//            if (CheckDoubleClick.isFastClick()) return;
//
//            if (heartRateDevice.isBonded()) {
//
//                Log.d(TAG, "###解除配對: " + heartRateDevice);
//
//                hrConnectingAnimation(binding);
//                //斷線
//                m.getAudioDeviceWatcher().removeBondDevice(heartRateDevice.getDevice());
//            } else {
//                Log.d(TAG, "連線: " + heartRateDevice);
//
//                //切斷其他連線
//                //    m.unBondAudio();
//
//                hrConnectingAnimation(binding);
//
//                m.getAudioDeviceWatcher().bondOneDevice(heartRateDevice.getDevice());
//
//
//                //連線 (bondOneDevice  > Bond device and remove bond others)
//                //連線之前再執行一次掃描
////                startS();
////                //避免讀取動畫被scan刷新
////                isFFFScan = true;
////                new RxTimer().timer(500, number -> {
////                    try {
////                        m.getAudioDeviceWatcher().bondOneDevice(heartRateDevice.getDevice());
////                        new RxTimer().timer(4000, number1 -> {
////                            isFFFScan = false;
////                        });
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                });
//
//            }
//
//        });
//    }
//
////    private RxTimer dismissTimer;
////
////    private void startDismissTimer() {
////
////        if (dismissTimer != null) {
////            dismissTimer.cancel();
////            dismissTimer = null;
////        }
////
////        getBinding().hideView2.setVisibility(View.VISIBLE);
////        dismissTimer = new RxTimer();
////        dismissTimer.timer(3000, number -> {
////            try {
////                if (getBinding() != null)
////                    dismiss();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////    }
//
//    private void stopDismissTimer() {
////        if (dismissTimer != null) {
////            dismissTimer.cancel();
////            dismissTimer = null;
////        }
////        Log.d(TAG, "#########stopDismissTimer:重新取得清單 ");
////        if (btAudioDeviceAdapter != null && mBluetoothDevicesList != null) {
//////            btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
////            onFoundNewDevice(bluetoothDevice);
////        }
//
//        try {
//            itemsBtDeviceListBinding.tvBtTitle.setTextColor(ContextCompat.getColorStateList(mContext, R.color.white));
//            itemsBtDeviceListBinding.vConnectedUnderline.setVisibility(View.INVISIBLE);
//            itemsBtDeviceListBinding.clBase.setBackgroundColor(0);
//            itemsBtDeviceListBinding.progress.setVisibility(View.INVISIBLE);
//            itemsBtDeviceListBinding.ivBtIcon.setBackgroundResource(R.drawable.icon_bluetooth_default);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    ItemsBtDeviceListBinding itemsBtDeviceListBinding;
//    private void hrConnectingAnimation(ItemsBtDeviceListBinding binding) {
//
//        try {
//            itemsBtDeviceListBinding = binding;
//            //讓全部不能按
//            //      HRPopupWindow.this.getBinding().hideView.setVisibility(View.VISIBLE);
//
//
//            binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));
//            binding.progress.setVisibility(View.VISIBLE);
//            binding.ivBtIcon.setBackgroundResource(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        view2.setBackgroundResource(R.drawable.load_animation);
////        AnimationDrawable rocketAnimation = (AnimationDrawable) view2.getBackground();
////        rocketAnimation.start();
//    }
//
//    private void initView() {
//
//        getBinding().btnClose.setOnClickListener(v -> {
//
//            if (CheckDoubleClick.isFastClick()) return;
//
//            //停止掃描
//            getDeviceGEM().customMessageStopDiscoveryHeartRateDevice();
//
//            dismiss();
//        });
//    }
//
//
//}

import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_BT_DEVICE_BOND_NONE;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_BT_DEVICE_FOUND;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_BT_DEVICE_SCAN;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.audio.AudioDeviceWatcher;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.ItemsBtDeviceListBinding;
import com.dyaco.spirit_commercial.databinding.WindowBtAudioBinding;
import com.dyaco.spirit_commercial.support.BtSort;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BtAudioWindow extends BasePopupWindow<WindowBtAudioBinding> {
    private boolean isFFFScan = false;
    private static final String TAG = "###BLUETOOTH";

    private final MainActivity m;
    private List<AudioDeviceWatcher.AudioDevice> mBluetoothDevicesList;
    public BtAudioWindow(Context context, boolean isFloat) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, isFloat, true);
        m = ((MainActivity) context);
//        mBluetoothDevicesList.clear();
//        new RxTimer().timer((scanTime + 3) * 1000, number -> {
//            try {
//                if (getBinding() != null) {
//                    getBinding().progress.setVisibility(View.GONE);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
        initView();
        initCheckbox();
        initHeartRateDevice();
        getBinding().cbHR.setChecked(MainActivity.isCbBtOn);
        boolean isCheck = getBinding().cbHR.isChecked();
        getBinding().noHrIcon.setVisibility(isCheck ? View.GONE : View.VISIBLE);
        getBinding().noHrText.setVisibility(isCheck ? View.GONE : View.VISIBLE);
        getBinding().recyclerView.setVisibility(isCheck ? View.VISIBLE : View.GONE);
        if (isCheck) {
            startS();
        }
        LiveEventBus.get(ON_BT_DEVICE_FOUND, BluetoothDevice.class).observeForever(observer);
        LiveEventBus.get(ON_BT_DEVICE_SCAN, Boolean.class).observeForever(observer2);
        LiveEventBus.get(ON_BT_DEVICE_BOND_NONE, BluetoothDevice.class).observeForever(observer3);
    }
    //取得設備
    Observer<BluetoothDevice> observer = this::onFoundNewDevice;
    Observer<Boolean> observer2 = s -> startS();
    Observer<BluetoothDevice> observer3 = this::stopDismissTimer;
    private void startS() {
        Log.d(TAG, "開始掃描: ");
        m.getAudioDeviceWatcher().stopScan();
        new RxTimer().timer(500, number -> {
            m.getAudioDeviceWatcher().startScan();
        });
    }
    @Override
    public void dismiss() {
        super.dismiss();
        m.getAudioDeviceWatcher().stopScan();
        LiveEventBus.get(ON_BT_DEVICE_FOUND, BluetoothDevice.class).removeObserver(observer);
        LiveEventBus.get(ON_BT_DEVICE_SCAN, Boolean.class).removeObserver(observer2);
        LiveEventBus.get(ON_BT_DEVICE_BOND_NONE, BluetoothDevice.class).removeObserver(observer3);
        if (dismissTimer != null) {
            dismissTimer.cancel();
            dismissTimer = null;
        }
    }
    @SuppressLint("MissingPermission")
    private void onFoundNewDevice(BluetoothDevice bluetoothDevices) {

//        if (isFFFScan) return;

        //map to list
//        ArrayList<Integer> keyList = new ArrayList<Integer>(myMap.keySet());
//        ArrayList<String> valueList = new ArrayList<String>(myMap.values());
        Log.d(TAG, "##################onFoundNewDevice: size: " + m.getAudioDeviceWatcher().getAudioDevices().size());
        mBluetoothDevicesList = new ArrayList<>(m.getAudioDeviceWatcher().getAudioDevices().values());
        for (AudioDeviceWatcher.AudioDevice s : m.getAudioDeviceWatcher().getAudioDevices().values()) {
            Log.d(TAG, "onFoundNewDevice: " + s.getDevice().getName() + ", " + s.getDevice().getAddress() + ", 綁定：" + s.isBonded() + ", 連線：" + s.isConnected());
        }
        //已配對的排最上面
        mBluetoothDevicesList.sort(new BtSort());
        getBinding().tvNoDevice.setVisibility(View.GONE);
        btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
    }
    private void initCheckbox() {
        getBinding().cbHR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //開始掃描
                //   getBinding().progress.setVisibility(View.VISIBLE);
                getBinding().noHrIcon.setVisibility(View.GONE);
                getBinding().noHrText.setVisibility(View.GONE);
                getBinding().recyclerView.setVisibility(View.VISIBLE);
                startS();
                if (mBluetoothDevicesList != null) {
                    mBluetoothDevicesList.clear();
                    btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
                }
                MainActivity.isCbBtOn = true;
            } else {
                //停止掃描
                m.getAudioDeviceWatcher().stopScan();
                //   getBinding().progress.setVisibility(View.GONE);
                getBinding().noHrIcon.setVisibility(View.VISIBLE);
                getBinding().noHrText.setVisibility(View.VISIBLE);
                getBinding().recyclerView.setVisibility(View.INVISIBLE);
                m.unBondAudio();
                getBinding().tvNoDevice.setVisibility(View.GONE);
                MainActivity.isCbBtOn = false;
                if (mBluetoothDevicesList != null) {
                    mBluetoothDevicesList.clear();
                    btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
                }
            }
        });
    }
    BtAudioDeviceAdapter btAudioDeviceAdapter;
    @SuppressLint("MissingPermission")
    private void initHeartRateDevice() {
        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);
        btAudioDeviceAdapter = new BtAudioDeviceAdapter(mContext);
        recyclerView.setAdapter(btAudioDeviceAdapter);
        //點擊
        btAudioDeviceAdapter.setOnItemClickListener((heartRateDevice, binding) -> {
            if (getBinding() == null) return;
            if (CheckDoubleClick.isFastClick()) return;
            if (heartRateDevice.isBonded()) {
                Log.d(TAG, "###解除配對: " + heartRateDevice);
                hrConnectingAnimation(binding);
                //斷線
                m.getAudioDeviceWatcher().removeBondDevice(heartRateDevice.getDevice());
            } else {
                Log.d(TAG, "連線: " + heartRateDevice);

                hrConnectingAnimation(binding);

                m.getAudioDeviceWatcher().bondOneDevice(heartRateDevice.getDevice());



//                //連線 (bondOneDevice  > Bond device and remove bond others)
//                //連線之前再執行一次掃描
//                startS();
//                //避免讀取動畫被scan刷新
//                isFFFScan = true;
//                new RxTimer().timer(500, number -> {
//                    try {
//                        m.getAudioDeviceWatcher().bondOneDevice(heartRateDevice.getDevice());
//
//                        new RxTimer().timer(4000, number1 -> {
//                            isFFFScan = false;
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });

            }

        });
    }
    private RxTimer dismissTimer;
    private void startDismissTimer() {
        if (dismissTimer != null) {
            dismissTimer.cancel();
            dismissTimer = null;
        }
        getBinding().hideView2.setVisibility(View.VISIBLE);
        dismissTimer = new RxTimer();
        dismissTimer.timer(3000, number -> {
            try {
                if (getBinding() != null)
                    dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void stopDismissTimer(BluetoothDevice bluetoothDevice) {
        if (dismissTimer != null) {
            dismissTimer.cancel();
            dismissTimer = null;
        }
        Log.d(TAG, "#########stopDismissTimer:重新取得清單 ");
        if (btAudioDeviceAdapter != null && mBluetoothDevicesList != null) {
//            btAudioDeviceAdapter.setData2ViewALL(mBluetoothDevicesList);
            onFoundNewDevice(bluetoothDevice);
        }
//        if (bluetoothDevice == null) return;
//
//        startDismissTimer();
//        Log.d(TAG, "###########重連: ");
//        m.getAudioDeviceWatcher().bondOneDevice(bluetoothDevice);
    }

        ItemsBtDeviceListBinding itemsBtDeviceListBinding;
    private void hrConnectingAnimation(ItemsBtDeviceListBinding binding) {

        try {
            itemsBtDeviceListBinding = binding;
            //讓全部不能按
            //      HRPopupWindow.this.getBinding().hideView.setVisibility(View.VISIBLE);


            binding.clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));
            binding.progress.setVisibility(View.VISIBLE);
            binding.ivBtIcon.setBackgroundResource(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initView() {
        getBinding().btnClose.setOnClickListener(v -> {
            if (CheckDoubleClick.isFastClick()) return;
            //停止掃描
            getDeviceGEM().customMessageStopDiscoveryHeartRateDevice();
            dismiss();
        });
    }
}





