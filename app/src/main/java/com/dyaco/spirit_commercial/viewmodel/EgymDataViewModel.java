package com.dyaco.spirit_commercial.viewmodel;

import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.METRIC;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dyaco.spirit_commercial.model.webapi.bean.CreateWorkoutParam;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymTrainingPlans;
import com.dyaco.spirit_commercial.model.webapi.bean.EgymUserDetailsBean;

import java.util.ArrayList;
import java.util.List;

public class EgymDataViewModel extends ViewModel {
    public final ObservableInt eTime = new ObservableInt(0);
    public final MutableLiveData<EgymTrainingPlans> egymTrainingPlansData = new MutableLiveData<>();
    public final ObservableInt currentPlanNum = new ObservableInt(0);

    public final ObservableInt unitSystem = new ObservableInt(METRIC);

    public final MutableLiveData<EgymUserDetailsBean> egymUserDetailsModel = new MutableLiveData<>();

    public final ObservableField<byte[]> userImg = new ObservableField<>();

//    public List<byte[]> coachImages = new ArrayList<>();

    public final MutableLiveData<List<byte[]>> coachImages = new MutableLiveData<>(new ArrayList<>());

    public EgymTrainingPlans.TrainerDTO selTrainer ;

//    public final ObservableField<String> currentExerciseName = new ObservableField<>();
//    public final ObservableLong trainingPlanExerciseId = new ObservableLong(0);
//    public final ObservableLong trainingPlanId = new ObservableLong(0);
//    public final ObservableInt frequency = new ObservableInt(0);
    public CreateWorkoutParam createWorkoutParam;

    //計算每個set在 20個 bar 中的位置  7 SET>> [3, 7, 11, 16, 17, 18, 19]
    public List<Integer> setsTimePosition = new ArrayList<>(); // 用不到了

    //每個Set的秒數 [120, 360]
    //[60, 120, 60, 120, 60, 120, 60, 120, 60, 120, 60, 120]
    public List<Integer> durationTimesList = new ArrayList<>();

    //保留 -99, 有0秒的
    public List<Integer> durationTimesListKnowZero = new ArrayList<>();

    //每個Set在第幾秒 [120, 480]  最後一個+2秒, 因為要到summary才算
    //[60, 180, 240, 360, 420, 540, 600, 720, 780, 900, 960, 1082]
    //每個Set在整個Program中，所在的秒數
    public List<Integer> durationRealTimesList = new ArrayList<>();

    public int totalSetsTime;

    //Workout時存 Interval Data
    public List<CreateWorkoutParam.IntervalsDTO> woIntervalData = new ArrayList<>();





 /////////////////
//    viewModel.getCurrentStatus().observe(this, status -> {
//        // 更新 UI
//        textView.setText(String.valueOf(status));
//    });
    /////////////////////////
//
//    private final MutableLiveData<Integer> currentStatus = new MutableLiveData<>();
//
    //只要有塞值就會觸發
//    public LiveData<Integer> getCurrentStatus() {
//        return currentStatus;
//    }

    //值沒改變就不會觸發
// public final LiveData<Integer> getCurrentStatus = Transformations.distinctUntilChanged(_counter);
//
//    public void updateCurrentStatus(int status) {
//        currentStatus.setValue(status);  // 在主線程更新
//        // currentStatus.postValue(status); // 在工作線程更新
//    }














//    private final MutableLiveData<EgymLoginBean> loginResult = new MutableLiveData<>();
//    private final MutableLiveData<Throwable> loginError = new MutableLiveData<>();
//
//    public LiveData<EgymLoginBean> getLoginResult() {
//        return loginResult;
//    }
//
//    public LiveData<Throwable> getLoginError() {
//        return loginError;
//    }
//
//    /**
//     * 呼叫 Egym 登入 API
//     *
//     * @param header HTTP Header 資訊，例如 "Authorization" 等
//     * @param params POST 參數，例如 "grant_type", "rfid", "machine_type" 等
//     */
//    public void loginEgym(Map<String, String> header, Map<String, String> params) {
//        // 直接呼叫 Kotlin 的 EgymUtil.loginEgym() 方法
//        EgymApiManager.loginEgym(header, params, new ApiResponseListener<EgymLoginBean>() {
//            @Override
//            public void onSuccess(EgymLoginBean data) {
//                // 利用 postValue() 更新 LiveData，保證在主線程更新 UI
//                loginResult.postValue(data);
//            }
//
//            @Override
//            public void onFailure(@NonNull Throwable error, @Nullable Integer httpCode) {
//                loginError.postValue(error);
//            }
//        });
//    }

//    // 監聽登入成功與失敗的 LiveData
//        egymDataViewModel.getLoginResult().observe(this, result -> {
//        // 登入成功處理，例如更新 UI
//    });
//        egymDataViewModel.getLoginError().observe(this, error -> {
//        // 登入失敗處理，例如顯示錯誤訊息
//    });
//        egymDataViewModel.loginEgym(header, params);




}
