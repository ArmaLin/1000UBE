package com.dyaco.spirit_commercial.settings;

import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.DISCOVERY_TIMEOUT;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_CONNECTED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_RESCAN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.BLE_DEVICE_EVENT_WARRING;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corestar.libs.device.DeviceGEM;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowHrBinding;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;
import java.util.Objects;


/**
 * 1.已被其他裝置連線的裝置掃描不到
 * 2.若已有連線的裝置，無法執行掃描
 * 3.曾經連過的裝置，即使不在掃描範圍，掃描也會出現
 * 4.藍芽沒斷線就 重build 或 crash 會找不到裝置和無法掃描，只能斷電重啟 > customMessageDisconnectHeartRateDevice() 先斷線
 */
public class HRPopupWindow extends BasePopupWindow<WindowHrBinding> {
    private DeviceGEM mDeviceGem;
   // private RotateAnimation am;
    private final int scanTime = 30;
//    private DeviceGEM.HeartRateDevice mHeartRateDevice;
//    private boolean isReConnect = true;
//    private View mView1;
//    private View mView2;
//    private ProgressBar mProgressBar;

    public HRPopupWindow(Context context) {
        super(context, 500, 0, 795, GENERAL.TRANSLATION_X, false, true, true, true);
        //   Toasty.warning(mContext, "ERROR", Toasty.LENGTH_LONG).show();
        mDeviceGem = getDeviceGEM();
        //
////        //idle時才能連線
//        DeviceGEM.FITNESS_EQUIPMENT_STATE state = DeviceGEM.FITNESS_EQUIPMENT_STATE.IDLE;
//        Map<DeviceGEM.EQUIPMENT_CONTROL_PARAMETER, Integer> parameters = new HashMap<>();
//       // parameters.put(TARGET_SPEED,1);
//        getDeviceGEM().gymConnectMessageSetFitnessEquipmentState(state, parameters);
////
////        //
//        Map<DeviceGEM.WORKOUT_DATA_FIELD, Integer> workoutData = new HashMap<>();
//        workoutData.put(DeviceGEM.WORKOUT_DATA_FIELD.ELAPSED_WORKOUT_TIME, 678);
//        getDeviceGEM().gymConnectMessageUpdateWorkoutData(workoutData);


        new RxTimer().timer((scanTime + 3) * 1000, number -> {
            try {
                if (getBinding() != null) {
              //      getBinding().hideView.setVisibility(View.GONE);
                    getBinding().progress.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        initView();
        initCheckbox();

        initHeartRateDevice();

        getBinding().cbHR.setChecked(MainActivity.isCbHrOn);

        //InitAnimation

    //    getBinding().reScan.setAnimation(am);

        boolean isCheck = getBinding().cbHR.isChecked();
        getBinding().noHrIcon.setVisibility(isCheck ? View.GONE : View.VISIBLE);
        getBinding().noHrText.setVisibility(isCheck ? View.GONE : View.VISIBLE);
        getBinding().recyclerView.setVisibility(isCheck ? View.VISIBLE : View.GONE);
    //    getBinding().reScan.setVisibility(isCheck ? View.VISIBLE : View.GONE);
        if (isCheck) {
            mDeviceGem.customMessageStartDiscoveryHeartRateDevice(scanTime);
        }


        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED, Integer.class).observeForever(observer);
        LiveEventBus.get(DISCOVERY_TIMEOUT, Boolean.class).observeForever(observer2);

        getHeartRateDevice();


//        getBinding().reScan.setOnClickListener(v -> {
//            try {
//                Log.d(DeviceGemEventListener.TAG, "開始掃描: ");
//                mDeviceGem.customMessageStartDiscoveryHeartRateDevice(scanTime);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    //取得設備
    Observer<Integer> observer = s -> {

        getHeartRateDevice();
        //   hideProgress();

        //斷線事件
        if (s == BLE_DEVICE_EVENT_RESCAN) {
           // hideProgress();

            //斷線後開始掃描
         //   getBinding().hideView.setVisibility(View.VISIBLE);
            getBinding().progress.setVisibility(View.VISIBLE);
            mDeviceGem.customMessageStartDiscoveryHeartRateDevice(scanTime);
        }

        //連線失敗
        if (s == BLE_DEVICE_EVENT_WARRING) {


            hideProgress();
            // TODO: 失敗
        //    Toasty.warning(mContext, R.string.Connection_Failure, Toasty.LENGTH_LONG).show();

        }

        //連線事件
        if (s == BLE_DEVICE_EVENT_CONNECTED) {
            //停止掃描
            hideProgress();
       //     getBinding().reScan.clearAnimation();
        }
    };

    Observer<Boolean> observer2 = s -> {
        if (s) {
            //開始掃描
       //     getBinding().reScan.startAnimation(am);
        } else {
            //TimeOut

            List<DeviceGEM.HeartRateDevice> heartRateDeviceList = mDeviceGem.customMessageGetHeartRateDevices();
            if (heartRateDeviceList.size() <= 0) {
                getBinding().tvNoDevice.setVisibility(View.VISIBLE);
            } else {
                getBinding().tvNoDevice.setVisibility(View.GONE);
            }

            hideProgress();
        //    getBinding().reScan.clearAnimation();
        }
    };

    private void hideProgress() {
     //   getBinding().hideView.setVisibility(View.GONE);
        getBinding().progress.setVisibility(View.GONE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LiveEventBus.get(ON_CUSTOM_EVENT_HEART_RATE_DEVICES_DISCOVERED, Integer.class).removeObserver(observer);
        LiveEventBus.get(DISCOVERY_TIMEOUT, Boolean.class).removeObserver(observer2);
    }

    private void getHeartRateDevice() {
        List<DeviceGEM.HeartRateDevice> heartRateDeviceList = mDeviceGem.customMessageGetHeartRateDevices();
        //  Log.d(DeviceGemEventListener.TAG, "取得HR Device 清單: " + heartRateDeviceList.toString() + ",裝置數量：" + heartRateDeviceList.size());
        if (heartRateDeviceList.size() > 0) {
            getBinding().tvNoDevice.setVisibility(View.GONE);
            heartRateDeviceAdapter.setData2View(heartRateDeviceList);
        }
    }

    private void initCheckbox() {



        getBinding().cbHR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //開始掃描
                mDeviceGem.customMessageStartDiscoveryHeartRateDevice(scanTime);
                getBinding().progress.setVisibility(View.VISIBLE);
                getBinding().noHrIcon.setVisibility(View.GONE);
                getBinding().noHrText.setVisibility(View.GONE);
                getBinding().recyclerView.setVisibility(View.VISIBLE);

            //    getBinding().reScan.setVisibility(View.VISIBLE);

                MainActivity.isCbHrOn = true;
            } else {
                //停止掃描
                getDeviceGEM().customMessageStopDiscoveryHeartRateDevice();
                getBinding().progress.setVisibility(View.GONE);
                getBinding().noHrIcon.setVisibility(View.VISIBLE);
                getBinding().noHrText.setVisibility(View.VISIBLE);
                getBinding().recyclerView.setVisibility(View.INVISIBLE);

//                getBinding().reScan.clearAnimation();
//
//                getBinding().reScan.setVisibility(View.INVISIBLE);


                for (DeviceGEM.HeartRateDevice hrDevice : mDeviceGem.customMessageGetHeartRateDevices()) {
                    if (hrDevice.isConnected()) {
                        //切斷其他連線裝置
                        mDeviceGem.customMessageDisconnectHeartRateDevice(hrDevice);
                        break;
                    }
                }

                getBinding().tvNoDevice.setVisibility(View.GONE);
          //      getBinding().hideView.setVisibility(View.GONE);
                MainActivity.isCbHrOn = false;
            }
        });
    }


    HeartRateDeviceAdapter heartRateDeviceAdapter;

    private void initHeartRateDevice() {

        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.divider_line_252e37);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(drawable));
        recyclerView.addItemDecoration(dividerItemDecoration);
        heartRateDeviceAdapter = new HeartRateDeviceAdapter(mContext);
        recyclerView.setAdapter(heartRateDeviceAdapter);


        //點擊
        heartRateDeviceAdapter.setOnItemClickListener((heartRateDevice, view1, view2, progress) -> {

            if (heartRateDevice.isConnected()) {

                hrConnectingAnimation(view1, view2, progress);
                //斷線
                mDeviceGem.customMessageDisconnectHeartRateDevice(heartRateDevice);
            } else {

                boolean isDisconnecting = false;
                for (DeviceGEM.HeartRateDevice hrDevice : mDeviceGem.customMessageGetHeartRateDevices()) {
                    if (hrDevice.isConnected()) {
                        //切斷其他連線裝置
                        //   mDeviceGem.customMessageDisconnectHeartRateDevice(hrDevice);
                        isDisconnecting = true;
                        break;
                    }
                }

                //無其他連線裝置
                if (!isDisconnecting) {

                    hrConnectingAnimation(view1, view2, progress);

//                        mHeartRateDevice = heartRateDevice;
//                        mView1 = view1;
//                        mView2 = view2;
//                        mProgressBar = progress;
//                        isReConnect = true;
                    //連線
                    mDeviceGem.customMessageConnectHeartRateDevice(heartRateDevice, 7);
                }
//
//
//                if (isDisconnecting) {
//                    //過一下再連
//                    new RxTimer().timer(3000, n ->
//                            mDeviceGem.customMessageConnectHeartRateDevice(heartRateDevice, 10));
//                } else {
//                    //連線
//                    mDeviceGem.customMessageConnectHeartRateDevice(heartRateDevice, 10);
//                }
            }

        });
    }

    private void hrConnectingAnimation(View clBase, View ivHrIcon, ProgressBar progressBar) {

        try {
            //讓全部不能按
      //      HRPopupWindow.this.getBinding().hideView.setVisibility(View.VISIBLE);

            clBase.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color252e37));

            progressBar.setVisibility(View.VISIBLE);

            ivHrIcon.setBackgroundResource(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        view2.setBackgroundResource(R.drawable.load_animation);
//        AnimationDrawable rocketAnimation = (AnimationDrawable) view2.getBackground();
//        rocketAnimation.start();
    }

    private void initView() {


     //   am = MyAnimationUtils.getRotateAnimation();


        getBinding().btnClose.setOnClickListener(v -> {

            //停止掃描
            getDeviceGEM().customMessageStopDiscoveryHeartRateDevice();
//            try {
//                am.cancel();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            dismiss();
        });
    }


}
