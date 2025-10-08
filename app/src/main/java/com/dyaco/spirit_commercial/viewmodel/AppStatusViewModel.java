package com.dyaco.spirit_commercial.viewmodel;

import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.CURRENT_PAGE_TRAINING;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.support.SingleLiveEvent;

import java.io.Serializable;

public class AppStatusViewModel extends ViewModel implements Serializable {

    public final ObservableBoolean topHrIconConnected = new ObservableBoolean(false);

    //private boolean isGem3On;
    public final ObservableBoolean isBtOn = new ObservableBoolean(false);
    public final ObservableBoolean isGem3On = new ObservableBoolean(false);
    public final ObservableBoolean isAudioConnected = new ObservableBoolean(false);

    public final ObservableBoolean isFtmsConnected = new ObservableBoolean(false);

    public final ObservableInt currentStatus = new ObservableInt();

    public final ObservableInt wifiState = new ObservableInt(R.drawable.btn_header_wifi_lv0_default);
    public final ObservableBoolean isMediaPauseOrResume = new ObservableBoolean();

    public final ObservableBoolean isMediaPlaying = new ObservableBoolean(false);

    public final ObservableInt currentPage = new ObservableInt(CURRENT_PAGE_TRAINING);

    public SingleLiveEvent<Integer> getSetTimeFragmentNavigate() {
        return setTimeFragmentNavigate;
    }

    private final SingleLiveEvent<Integer> mainButtonType = new SingleLiveEvent<>(0);
    public void changeMainButtonType(Integer item) {
        mainButtonType.setValue(item); // ui thread
       // selectedItem.postValue(item); thread
    }
//    public SingleLiveEvent<Integer> getMainButtonType() {
//        return mainButtonType;
//    }
    //只有數值有變時才呼叫 (Transformations.distinctUntilChanged)
    public LiveData<Integer> getMainButtonType = Transformations.distinctUntilChanged(mainButtonType);


   // private final MutableLiveData<Integer> setTimeFragmentNavigate = new MutableLiveData<>();
    //避免重複觸發
    private final SingleLiveEvent<Integer> setTimeFragmentNavigate = new SingleLiveEvent<>(0);
    public void selectSetTimeFragmentNavigate(Integer item) {
        setTimeFragmentNavigate.setValue(item); // ui thread
    }
    public SingleLiveEvent<Integer> changeNavigate() {
        return setTimeFragmentNavigate;
    }


//    public boolean isGem3On() {
//        return isGem3On;
//    }
//
//    public void setGem3On(boolean gem3On) {
//        isGem3On = gem3On;
//    }
}