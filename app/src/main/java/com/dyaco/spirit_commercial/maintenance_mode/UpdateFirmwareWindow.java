package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.corestar.libs.ota.ArterySubMcuUpdateManager;
import com.corestar.libs.ota.LwrMcuUpdateManager;
import com.corestar.libs.ota.SubMcuUpdateManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUpdateFirmwareBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

import timber.log.Timber;

public class UpdateFirmwareWindow extends BasePopupWindow<WindowUpdateFirmwareBinding> {
    private final Context mContext;
    private final SeekBar downloadProgress;
    private TextView tvMin;
    private final byte[] binRaw;
    private static final String TAG = "USB_UPDATE";
    private SubMcuUpdateManager subMcuUpdateManager;
    private LwrMcuUpdateManager lwrMcuUpdateManager;
    private int updateType;

    public UpdateFirmwareWindow(Context context, byte[] binRaw, int type ,String binName) {//0:SubMcu, 1 LWR

        super(context, 0, 0, 0, GENERAL.FADE, false, false, true, false);
        mContext = context;
        updateType = type;
        this.binRaw = binRaw;
        tvMin = getBinding().tvMin;
        downloadProgress = getBinding().seekBar;

        getBinding().binName.setText(binName);

//        if (isTreadmill) {
//            App.isFirmwareUpdating = true;
//        } else {
//            getDeviceSpiritC().setEchoMode(DeviceDyacoMedical.ECHO_MODE.AA);
//        }

        new RxTimer().timer(1000, new RxTimer.RxAction() {
            @Override
            public void action(long number) {
                if (type == GENERAL.SUB_MCU) {
                    getBinding().updateTitle.setText(R.string.SUB_MCU_Update);
                    initSubMcuUpdateManager2();
                } else {
                    getBinding().updateTitle.setText(R.string.LWR_Update);
                    initLwrUpdateManager();
                }
            }
        });


        downloadProgress.setProgress(0);

    }

    private void initSubMcuUpdateManager2(){

        ArterySubMcuUpdateManager arterySubMcuUpdateManager = getDeviceSpiritC().getArterySubMcuUpdateManager();

    //    Timber.tag("🦐🦐").d("onUpdateState: Length=%d", binRaw != null ? binRaw.length : 0);
        arterySubMcuUpdateManager.setListener(new ArterySubMcuUpdateManager.ArterySubMcuUpdateEventListener() {
            @Override
            public void onUpdateState(ArterySubMcuUpdateManager.UPDATE_STATE updateState, ArterySubMcuUpdateManager.MCU_STATE mcuState) {
                Timber.tag("🦐🦐").d("onUpdateState: " + updateState +", "+ mcuState);
                switch (updateState) {
                    case START:
                        Timber.tag("🦐🦐").d("onUpdateState: START");
                        break;

                    case RUNNING:   // 成功啟動更新時通知一次
                        Timber.tag("🦐🦐").d("onUpdateState: RUNNING");
                        break;

                    case FINAL:        // 更新結束時通知一次
                        Timber.tag("🦐🦐").d("onUpdateState: FINAL");
                        if (isShowing()) {
//                            getBinding().getRoot().post(() -> showUiState(UiState.RESULT_SUCCESS));
                        }

//                        showUiState(UsbUpdateWindow.UiState.COPYING);
                        break;

                    case FINISH:
                        Timber.tag("🦐🦐").d("onUpdateState: FINISH 成功");
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, true));
                            dismiss();
                        });
                        break;

                    case ERROR:        // 更新結束時通知一次
                        Timber.tag("🦐🦐").d("onUpdateState: ERROR");
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, false));
                            dismiss();
                        });
                        break;
                }
            }

            @Override
            public void onUpdateProgress(int current, int total) {

                int progress = (100 * current / total);
                if (total > 0) {
                    ((MainActivity) mContext).runOnUiThread(() -> {
                        downloadProgress.setProgress(progress);
                        if (progress >= 100) {
                            getBinding().progress.setVisibility(View.VISIBLE);
                        }
                    });
                }

                Timber.tag("🦐🦐").d("onUpdateState: " + progress + " / " + total);

            }

            @Override
            public void onTimeOut(String timeOut) {
                ((MainActivity) mContext).runOnUiThread(() -> {
                    returnValue(new MsgEvent(updateType, false));
                    dismiss();
                });

            }
        });

        new RxTimer().timer(500, number -> arterySubMcuUpdateManager.updateMcu(binRaw));
    }

    private void initSubMcuUpdateManager() {

        subMcuUpdateManager = getDeviceSpiritC().getSubMcuUpdateManager();
        subMcuUpdateManager.setListener(new SubMcuUpdateManager.SubMcuUpdateEventListener() {
            @Override
            public void onUpdateState(SubMcuUpdateManager.MCU_TYPE mcu_type, SubMcuUpdateManager.STATE state) {
                Log.d(TAG, "SUB_MCU_onUpdateState: " + mcu_type + ", state: " + state);
                // 成功啟動更新時通知一次，state = RUNNING
                // 更新結束時通知一次，state = OK/NG]

                switch (state) {
                    case RUNNING:   // 成功啟動更新時通知一次
                    case IDLE:
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            getBinding().progress.setVisibility(View.GONE);
                        });
                        break;

                    case OK:        // 更新結束時通知一次
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, true));
                            dismiss();
                        });
                        break;

                    case FINISH:    // 不知發生何事, 不會回OK或NG, 結束更新 -> 視同失敗
                    case NG:        // 更新結束時通知一次
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, false));
                            dismiss();
                        });
                        break;
                }
            }

            @Override
            public void onUpdateProgress(int current, int total) {
                int progress = (100 * current / total);
             //   Log.d(TAG, "SUB_MCU_onUpdateProgress: current: " + current + ", total: " + total + "," + progress);
                // 更新時通知當前更新筆數及總筆數
                ((MainActivity) mContext).runOnUiThread(() -> {
                    downloadProgress.setProgress(progress);
                    if (progress >= 100) {
                        getBinding().progress.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        new RxTimer().timer(500, number -> updateSubMcu(binRaw));
    }

    private void updateSubMcu(byte[] firmware) {
        subMcuUpdateManager.updateMcu(firmware, SubMcuUpdateManager.MCU_TYPE.SUB_MCU);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    private void initLwrUpdateManager() {

        lwrMcuUpdateManager = getDeviceSpiritC().getLwrMcuUpdateManager();
        lwrMcuUpdateManager.setListener(new LwrMcuUpdateManager.UpdateEventListener() {
            @Override
            public void onUpdateState(LwrMcuUpdateManager.UPDATE_STATE updateState) {
                Log.d(TAG, "onUpdateState: " + updateState);

                switch (updateState) {
                    //     case IDLE:
                    //   case STANDBY:
                    case RUNNING:
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            downloadProgress.setProgress(0);
                            getBinding().progress.setVisibility(View.GONE);
                        });
                        break;

                    case FAIL:
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, false));
                            dismiss();
                        });
                        break;

                    case FINISH:
                        ((MainActivity) mContext).runOnUiThread(() -> {
                            returnValue(new MsgEvent(updateType, true));
                            dismiss();
                        });

                        break;
                }

            }

            @Override
            public void onUpdateProgress(int current, int total) {
                int progress = (100 * current / total);
                //   Log.d(TAG, "onUpdateProgress: current: " + current + ", total: " + total + "," + progress);
                // 更新時通知當前更新筆數及總筆數

                ((MainActivity) mContext).runOnUiThread(() -> {
                    downloadProgress.setProgress(progress);
                    if (progress >= 100) {
                        getBinding().progress.setVisibility(View.VISIBLE);
                    }
                });

                //Incorrect eeprom operation code
            }
        });

        new RxTimer().timer(500, number -> updateLwr(binRaw));
    }

    private void updateLwr(byte[] firmware) {
        Log.d(TAG, "###########updateLwr: " + firmware.length);
        lwrMcuUpdateManager.updateLwrMcu(firmware);
    }


}
