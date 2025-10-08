package com.dyaco.spirit_commercial.viewmodel;

import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.NFC_GYM_KIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.PROTOCOL_CSAFE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TV_DCI_OSD;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_STB;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dyaco.spirit_commercial.support.SingleLiveEvent;

import java.util.Locale;


public class DeviceSettingViewModel extends ViewModel {

    public final ObservableField<Locale> locale = new ObservableField<>();

    public final ObservableInt consoleSystem = new ObservableInt(CONSOLE_SYSTEM_SPIRIT);

    public final ObservableInt territoryCode = new ObservableInt();
    public final ObservableInt modelCode = new ObservableInt();
    public final ObservableInt typeCode = new ObservableInt();
    public final ObservableInt unitCode = new ObservableInt();//0英 1公

    public final ObservableInt time_unit = new ObservableInt();
    public final ObservableLong sleepAfter = new ObservableLong();
    public final ObservableLong pauseAfter = new ObservableLong();//秒

//    public final ObservableInt isUseTimeLimit = new ObservableInt(0); //0:都不限制, 1:限制 distance,2:限制 time
//    public final ObservableLong useTimeLimit = new ObservableLong(0); //限制的時間
//    public final ObservableInt useDistanceLimitKM = new ObservableInt(0); // 限制的距離
//
//    public final ObservableDouble useDistanceLimitMI = new ObservableDouble(0);
//
//    public final ObservableField<String> limitCode = new ObservableField<>("");

    public final ObservableInt minSpeedIu = new ObservableInt(0);
    public final ObservableInt minSpeedMu = new ObservableInt(0);


    public final ObservableField<String> modelName = new ObservableField<>();
    public final ObservableField<String> typeName = new ObservableField<>();

    public final ObservableField<String> lwrMcuFwVer = new ObservableField<>();
    public final ObservableField<String> subMcuFwVer = new ObservableField<>();
    public final ObservableField<String> subMcuHwVer = new ObservableField<>();
    public final ObservableField<String> lwrMcuHwVer = new ObservableField<>();

    public final ObservableField<String> timeText = new ObservableField<>();

    public final ObservableField<String> ftmsName = new ObservableField<>();

    public final ObservableInt alertNotifyCount = new ObservableInt(0);

    public final SingleLiveEvent<Integer> frontInclineAd = new SingleLiveEvent<>(0);
    public final SingleLiveEvent<Integer> rotationalSpeedValue = new SingleLiveEvent<>(0);
//    public final SingleLiveEvent<Integer> electricCurrentValue = new SingleLiveEvent<>(0);
    public final ObservableFloat electricCurrentValue = new ObservableFloat(0.0f);

    public final ObservableBoolean gsMode = new ObservableBoolean(true); // incline return >  true GsMode關, false GsMode開

    public final ObservableBoolean isEnablePort1 = new ObservableBoolean(false);
    public final ObservableBoolean isEnablePort2 = new ObservableBoolean(false);
    public final ObservableBoolean isEnablePort3 = new ObservableBoolean(false);
    public final ObservableBoolean isEnablePort4 = new ObservableBoolean(false);

    public final ObservableBoolean isGarminEnabled = new ObservableBoolean(false);

    //public final ObservableBoolean beep = new ObservableBoolean(false); //0 off, 1 on

    public final SingleLiveEvent<Boolean> beep = new SingleLiveEvent<>(false);  //0 off, 1 on
    public LiveData<Boolean> getBeep = Transformations.distinctUntilChanged(beep);

    public final SingleLiveEvent<Integer> protocol = new SingleLiveEvent<>(PROTOCOL_CSAFE); //0 csafe, 1 cab
    public LiveData<Integer> onProtocolChanged = Transformations.distinctUntilChanged(protocol);

    public final SingleLiveEvent<Integer> video = new SingleLiveEvent<>(VIDEO_STB);
    public LiveData<Integer> onVideoChanged = Transformations.distinctUntilChanged(video);

    public final SingleLiveEvent<Integer> tv = new SingleLiveEvent<>(TV_DCI_OSD); //0 DCI OSD, 1 IPTV
    public LiveData<Integer> onTvChanged = Transformations.distinctUntilChanged(tv);

    public final SingleLiveEvent<Integer> nfc = new SingleLiveEvent<>(NFC_GYM_KIT); //0 csafe, 1 cab
    public LiveData<Integer> onNfcChanged = Transformations.distinctUntilChanged(nfc);

//    public final SingleLiveEvent<Integer> pauseMode = new SingleLiveEvent<>(ON); //0 csafe, 1 cab
    //public LiveData<Integer> onPauseModeChanged = Transformations.distinctUntilChanged(pauseMode);
    public final ObservableInt pauseMode = new ObservableInt(ON);

    public final SingleLiveEvent<Integer> sleepMode = new SingleLiveEvent<>(ON); //0 off 1on
    public LiveData<Integer> onSleepModeChanged = Transformations.distinctUntilChanged(sleepMode);

    //Auto Pause只會在 workout開始後才會偵測，當橋接板error code2 bit4 =1時，
    // 表示跑帶在動作但無人在使用；因此，當此狀況持續時間超過上表設定時間後，上表直接結束此次workout，並logout，回到login畫面；
    public final SingleLiveEvent<Integer> autoPause = new SingleLiveEvent<>(ON);
    public LiveData<Integer> onAutoPauseChanged = Transformations.distinctUntilChanged(autoPause);



    public final SingleLiveEvent<Integer> antPlusDeviceId = new SingleLiveEvent<>(0); //0 csafe, 1 cab
    public LiveData<Integer> onAntPlusDeviceIdChanged = Transformations.distinctUntilChanged(antPlusDeviceId);

    //在背景時無作用
 //   public final SingleLiveEvent<String> timeText = new SingleLiveEvent<>();

}
