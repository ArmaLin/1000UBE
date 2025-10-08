package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getDeviceSpiritC;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.SpiritCommercialUart.isFirmwareUpdating;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.corestar.libs.device.DeviceSpiritC;
import com.corestar.libs.ota.LwrMcuUpdateManager;
import com.corestar.libs.ota.SubMcuUpdateManager;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.databinding.WindowUpdateFirmwareBinding;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;

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

        if (isTreadmill) {
            isFirmwareUpdating = true;
        } else {
            getDeviceSpiritC().setEchoMode(DeviceSpiritC.ECHO_MODE.AA);
        }

        if (type == GENERAL.SUB_MCU) {
            getBinding().updateTitle.setText(R.string.SUB_MCU_Update);
            initSubMcuUpdateManager();
        } else {
            getBinding().updateTitle.setText(R.string.LWR_Update);
            initLwrUpdateManager();
        }

        downloadProgress.setProgress(0);

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
