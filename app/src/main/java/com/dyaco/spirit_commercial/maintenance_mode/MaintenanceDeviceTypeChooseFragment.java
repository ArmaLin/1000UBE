package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.App.getDeviceGEM;
import static com.dyaco.spirit_commercial.MainActivity.UPDATE_FILE_PATH;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CE1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CR1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CT1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.CU1000ENT;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.STEPPER;
import static com.dyaco.spirit_commercial.product_flavor.ModeEnum.UBE;
import static com.dyaco.spirit_commercial.support.CommonUtils.restartApp;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_CANADA;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_US;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_NONE;
import static com.dyaco.spirit_commercial.work_task.WorkManagerUtil.WORK_NOTIFY_UPDATE_MSG_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceDeviceTypeChooseBinding;
import com.dyaco.spirit_commercial.product_flavor.InitProduct;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.room.spirit.SpiritDbManager;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.work_task.WorkManagerUtil;

import java.io.File;
import java.util.ArrayList;


public class MaintenanceDeviceTypeChooseFragment extends BaseBindingDialogFragment<FragmentMaintenanceDeviceTypeChooseBinding> {
    DeviceSettingViewModel deviceSettingViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceSettingViewModel = new ViewModelProvider(requireActivity()).get(DeviceSettingViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
        for (int i = 0; i < getBinding().rgSelectItemTreadmill.getChildCount(); i++) {
            View o = getBinding().rgSelectItemTreadmill.getChildAt(i);
            if (o instanceof RadioButton) {
                radioButtonArrayList.add((RadioButton) o);
                if (radioButtonArrayList.get(i).getTag() != null) {
                    if (deviceSettingViewModel.typeCode.get() == Integer.parseInt((String) radioButtonArrayList.get(i).getTag())) {
                        radioButtonArrayList.get(i).setChecked(true);
                        radioTag = Integer.parseInt((String) radioButtonArrayList.get(i).getTag());
                    }
                }
            }
        }

        initEvent();
    }

    int radioTag = -1;

    private void initEvent() {

        getBinding().rgSelectItemTreadmill.setOnCheckedChangeListener((group, checkedId) -> {
            ArrayList<RadioButton> radioButtonArrayList = new ArrayList<>();
            for (int i = 0; i < getBinding().rgSelectItemTreadmill.getChildCount(); i++) {
                View o = getBinding().rgSelectItemTreadmill.getChildAt(i);
                if (o instanceof RadioButton) {
                    radioButtonArrayList.add((RadioButton) o);
                    if (radioButtonArrayList.get(i).getTag() != null) {
                        if (radioButtonArrayList.get(i).getId() == checkedId) {
                            radioTag = Integer.parseInt((String) radioButtonArrayList.get(i).getTag());
                        }
                    }
                }
            }
        });

        getBinding().btnConvert.setOnClickListener(view -> {

            DeviceSettingBean d = getApp().getDeviceSettingBean();
            int type = d.getType();
            int territoryCode = d.getTerritoryCode();
            ModeEnum modeEnum = CT1000ENT;
            switch (radioTag) {
                case 0:
                    type = DeviceIntDef.DEVICE_TYPE_TREADMILL;
                    modeEnum = CT1000ENT;
                    break;
                case 1:
                    type = DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
                    modeEnum = CE1000ENT;
                    break;
                case 2:
                    type = DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
                    modeEnum = CU1000ENT;
                    break;
                case 3:
                    type = DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
                    modeEnum = CR1000ENT;
                    break;
                case 4:
                    type = DeviceIntDef.DEVICE_TYPE_UBE;
                    modeEnum = UBE;
                    break;
                case 5:
                    type = DeviceIntDef.DEVICE_TYPE_STEPPER;
                    modeEnum = STEPPER;

                  //  setSleepMode(SCREEN_TIMEOUT_NEVER);


                    break;
            }

            isUs = territoryCode == TERRITORY_US || territoryCode == DeviceIntDef.TERRITORY_CANADA;

            //DeviceSettingBean 還原成初始值
            new InitProduct(getApp()).setProductDefault(modeEnum, territoryCode);  // 讓EGYM設定不變  CONSOLE_SYSTEM_SPIRIT
            DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
            deviceSettingBean.setMachineEdit(true); //增加已修改過
            //  deviceSettingBean.setTerritoryCode(territoryCode);

            if (isUs) {
                deviceSettingBean.setVideo(VIDEO_NONE);
            }

            getApp().setDeviceSettingBean(deviceSettingBean);

            //取消WorkManager通知
            new WorkManagerUtil().cancelWorkByTag(WORK_NOTIFY_UPDATE_MSG_TAG);

            //刪除資料庫
            new Thread(() -> SpiritDbManager.getInstance(getApp()).clearTable()).start();

            isTreadmill = modeEnum == CT1000ENT;

            //  isUs = territoryCode == TERRITORY_US;
            isUs = territoryCode == TERRITORY_US || territoryCode == TERRITORY_CANADA;


            getDeviceGEM().systemMessageRestart();
            //    d.setType(type);
            //  getApp().setDeviceSettingBean(d);
            //    new CommonUtils().mmkvDeviceSettingToViewModel(deviceSettingViewModel, getApp().getDeviceSettingBean());

            deleteFile();

            ((MainActivity) requireActivity()).showLoading(true);

            //換機型重啟APP
            new RxTimer().timer(2000, number -> {
                try {

                    restartApp((MainActivity) requireActivity());

                    //     ((MainActivity) requireActivity()).mRestartApp();

                    //   CommonUtils.restartApp((MainActivity) requireActivity());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        });


        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void deleteFile() {
        File directory = new File(UPDATE_FILE_PATH, "");
        File[] files = directory.listFiles();
        if (directory.canRead() && files != null) {
            Log.d("ChooseMode", "資料夾內檔案數量: " + files.length);
            for (File file : files) {
                //刪除其他檔案
                String result = (file.getName() + "," + (file.delete() ? "刪除成功" : "刪除失敗"));
                Log.d("ChooseMode", "getDownloadedFile: " + result);
            }
        } else {
            Log.d("ChooseMode", "it is null");
        }
    }
}