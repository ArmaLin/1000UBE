package com.dyaco.spirit_commercial.dashboard_media;

import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.TV_TUNER_VOLUME;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.VIDEO_TV;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_CHANNEL_LIST;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_CURRENT_CHANNEL;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.GET_TV_SUBTITLE_OPTION;
import static com.dyaco.spirit_commercial.support.intdef.EventKey.TV_SENT_CHANNEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.TV_TUNER_SIGNAL_DIGITAL;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.corestar.libs.device.DeviceCab;
import com.corestar.libs.device.DeviceTvTuner;
import com.corestar.libs.tvtuner.Channel;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.databinding.WindowTvControllerBinding;
import com.dyaco.spirit_commercial.support.CommonUtils;
import com.dyaco.spirit_commercial.support.MsgEvent;
import com.dyaco.spirit_commercial.support.RxTimer;
import com.dyaco.spirit_commercial.support.base_component.BasePopupWindow;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.utils.CheckDoubleClick;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.ArrayList;
import java.util.List;

public class TvControllerWindow extends BasePopupWindow<WindowTvControllerBinding> {
    //HDMI 開啟中 會可以點
    public static boolean isTvControllerOn;
    MainActivity m;
    int mv = 0;
    boolean isTvTuner;

    public TvControllerWindow(Context context,boolean isTouchCancel) {
        super(context, 500, 0, 362, GENERAL.TRANSLATION_X, isTouchCancel, false, true, true);
        isTvControllerOn = true;
        m = (MainActivity) context;

        isTvTuner = getApp().getDeviceSettingBean().getVideo() == VIDEO_TV;

        initController();

        if (isUs) {
            getBinding().tvTitle.setText("---");
            getBinding().tvTitle2.setText("---");
        }


    }

    private DeviceTvTuner.SUBTITLE_OPTION subtitleOption;
    List<DeviceTvTuner.SUBTITLE_OPTION> optionsList;
    private int subNum = 0;
    Observer<DeviceTvTuner.SUBTITLE_OPTION> observer3 = subtitleOption -> {
        this.subtitleOption = subtitleOption;
        subNum = 0;
        optionsList = null;
        optionsList = new ArrayList<>();
        optionsList.add(DeviceTvTuner.SUBTITLE_OPTION.SUBTITLE_OFF);
        for (int i = 1; i == subtitleOption.code; i++) {
            DeviceTvTuner.SUBTITLE_OPTION option = DeviceTvTuner.SUBTITLE_OPTION.get(i);
            optionsList.add(option);
        }

        getBinding().btnSub.setEnabled(subtitleOption != DeviceTvTuner.SUBTITLE_OPTION.SUBTITLE_OFF);
    };


    int mainChannelNumber = 1;
    Channel[] mChannel;
    Observer<Channel[]> observer2 = channels -> {
        mChannel = channels;
        setChannelData();
    };

    //GET_TV_CURRENT_CHANNEL
    Observer<Integer> observer = number -> {
        mainChannelNumber = number;

        if (mChannel == null) {

            //取得頻道
            if (getApp().getDeviceSettingBean().getTvTunerSignal() == TV_TUNER_SIGNAL_DIGITAL) {
                //數位
                m.deviceTvTuner.getDTVChannelListExtension();
            } else {
                //類比
                m.deviceTvTuner.getChannelList();
            }

        } else {
            setChannelData();
        }
    };

    private void setChannelData() {
        try {
            Channel currentChannel = mChannel[mainChannelNumber - 1];
//            int mainChannel = currentChannel.getMainChannel();
            int mainChannel = currentChannel.getChannelNumber();
            getBinding().tvChannelNum.setText(String.valueOf(mainChannel));
            getBinding().tvTitle.setText(isUs ? "---" : currentChannel.getStation());
            getBinding().tvTitle2.setText(isUs ? "---" : "");

            LiveEventBus.get(TV_SENT_CHANNEL).post(mainChannelNumber - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initController() {

        if (isTvTuner) {

            if (m.deviceTvTuner == null) {

                new RxTimer().timer(500, number -> {
                    returnValue(new MsgEvent(false));
                    dismiss();
                });

            } else {
                LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).observeForever(observer2);
                LiveEventBus.get(GET_TV_CURRENT_CHANNEL, Integer.class).observeForever(observer);
                LiveEventBus.get(GET_TV_SUBTITLE_OPTION, DeviceTvTuner.SUBTITLE_OPTION.class).observeForever(observer3);

                m.deviceTvTuner.getCurrentChannelNumber();
                setVolume();
                getBinding().tvVolumeNum.setText(String.valueOf(TV_TUNER_VOLUME));

                m.deviceTvTuner.getSubtitleOption();
            }
        } else {
            getBinding().btnSoundOff.setAlpha(0.4f);
            getBinding().btnSoundOff.setClickable(false);

            getBinding().btnSub.setAlpha(0.4f);
            getBinding().btnSub.setClickable(false);
        }


        getBinding().btnSoundOff.setChecked(TV_TUNER_VOLUME > 1);

        getBinding().btnNum0.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum1.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum2.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum3.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum4.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum5.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum6.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum7.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum8.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnNum9.setOnClickListener(view -> sendCommand((String) view.getTag()));

        //  getBinding().btnSoundOff.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnVolumeMinus.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnVolumePlus.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnChannelDown.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnChannelUp.setOnClickListener(view -> sendCommand((String) view.getTag()));

        getBinding().btnSub.setOnClickListener(view -> sendCommand((String) view.getTag()));

        new CommonUtils().addAutoClick(getBinding().btnVolumePlus);
        new CommonUtils().addAutoClick(getBinding().btnVolumeMinus);

        getBinding().btnSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                mv = TV_TUNER_VOLUME;
                TV_TUNER_VOLUME = 1;
            } else {
                TV_TUNER_VOLUME = mv > 1 ? mv : 2;
            }

            setVolume();
        });
    }


    StringBuffer sb = new StringBuffer();

    private void sendCommand(String tag) {
        // Log.d("TvTuner", "sendCommand: " + tag);


        switch (tag) {
            case "btn_num0":
                if (isTvTuner) {
                    setTvChannel("0");
                } else {
                    Log.d("RRRRRRRRRR", "sendCommand: ");
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_0);
                }
                break;
            case "btn_num1":
                if (isTvTuner) {
                    setTvChannel("1");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_1);
                }
                break;
            case "btn_num2":
                if (isTvTuner) {
                    setTvChannel("2");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_2);
                }
                break;
            case "btn_num3":
                if (isTvTuner) {
                    setTvChannel("3");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_3);
                }
                break;
            case "btn_num4":
                if (isTvTuner) {
                    setTvChannel("4");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_4);
                }
                break;
            case "btn_num5":
                if (isTvTuner) {
                    setTvChannel("5");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_5);
                }
                break;
            case "btn_num6":
                if (isTvTuner) {
                    setTvChannel("6");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_6);
                }
                break;
            case "btn_num7":
                if (isTvTuner) {
                    setTvChannel("7");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_7);
                }
                break;
            case "btn_num8":
                if (isTvTuner) {
                    setTvChannel("8");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_8);
                }
                break;
            case "btn_num9":
                if (isTvTuner) {
                    setTvChannel("9");
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.DIGIT_9);
                }
                break;
            case "btn_sound_off":
                if (isTvTuner) {
                    TV_TUNER_VOLUME = 1;
                    setVolume();
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.MUTE);
                }
                break;
            case "btn_sub":
                if (isTvTuner) {

                    if (subtitleOption == DeviceTvTuner.SUBTITLE_OPTION.SUBTITLE_OFF) return;
                    try {
                        subNum += 1;
                        if (subNum > (optionsList.size() - 1)) {
                            subNum = 0;
                        }
                        m.deviceTvTuner.setSubtitleOption(optionsList.get(subNum));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
                break;
            case "btnVolumePlus":
                if (isTvTuner) {

                    if (TV_TUNER_VOLUME < 20) {
                        TV_TUNER_VOLUME += 1;
                    }

                    setVolume();
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.VOL_UP);
                }
                break;
            case "btnVolumeMinus":
                if (isTvTuner) {

                    if (TV_TUNER_VOLUME > 1) {
                        TV_TUNER_VOLUME -= 1;
                    }

                    setVolume();
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.VOL_DOWN);
                }
                break;
            case "btnChannelUp":
                if (isTvTuner) {
                    if (CheckDoubleClick.isFastClick()) return;
                    m.deviceTvTuner.channelUp();
                    m.deviceTvTuner.getCurrentChannelNumber();
                    m.deviceTvTuner.getSubtitleOption();
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.CH_UP);
                }
                break;
            case "btnChannelDown":
                if (isTvTuner) {
                    if (CheckDoubleClick.isFastClick()) return;
                    m.deviceTvTuner.channelDown();
                    m.deviceTvTuner.getCurrentChannelNumber();
                    m.deviceTvTuner.getSubtitleOption();
                } else {
                    m.sendCabCommand(DeviceCab.FUNCTIONS.CH_DOWN);
                }
                break;
        }
    }

    private void removeChannelTimer() {
        if (setChannelTimer != null) {
            setChannelTimer.cancel();
            setChannelTimer = null;
        }
    }

    RxTimer setChannelTimer;

    private void setTvChannel(String num) {

        if (sb.length() >= 3) return;

        removeChannelTimer();
        setChannelTimer = new RxTimer();
        setChannelTimer.timer(2000, number -> {
            removeChannelTimer();
            try {
              //  int c = m.deviceTvTuner.getChannelNumber(mChannel, Integer.parseInt(sb.toString()));
                int c = Integer.parseInt(sb.toString());
                if (c != 0) {
                    Log.d("EEEEEEEEE", "setTvChannel: " + c);
                    m.deviceTvTuner.setChannel(c);
                    m.deviceTvTuner.getSubtitleOption();
                    LiveEventBus.get(TV_SENT_CHANNEL).post(c - 1);
                }
                sb.delete(0, sb.length());
            } catch (Exception e) {

                e.printStackTrace();
            }

            m.deviceTvTuner.getCurrentChannelNumber();
        });

        sb.append(num);
        getBinding().tvChannelNum.setText(sb.toString());
    }

    private void setVolume() {

        if (isTvTuner) {
            if (m.deviceTvTuner == null) return;
            m.deviceTvTuner.setVolume(TV_TUNER_VOLUME);
        }
        getBinding().tvVolumeNum.setText(String.valueOf(TV_TUNER_VOLUME));

        getBinding().btnSoundOff.setChecked(TV_TUNER_VOLUME > 1);

        saveVolume();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        removeChannelTimer();
        LiveEventBus.get(GET_TV_CHANNEL_LIST, Channel[].class).removeObserver(observer2);
        LiveEventBus.get(GET_TV_CURRENT_CHANNEL, Integer.class).removeObserver(observer);
        LiveEventBus.get(GET_TV_SUBTITLE_OPTION, DeviceTvTuner.SUBTITLE_OPTION.class).removeObserver(observer3);

        isTvControllerOn = false;
    }

    private void saveVolume() {

        DeviceSettingBean deviceSettingBean = getApp().getDeviceSettingBean();
        deviceSettingBean.setTvTunerVolume(TV_TUNER_VOLUME);
        getApp().setDeviceSettingBean(deviceSettingBean);
    }
}
