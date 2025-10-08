package com.dyaco.spirit_commercial.maintenance_mode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spirit_commercial.databinding.FragmentMaintenanceWifiBinding;
import com.dyaco.spirit_commercial.support.base_component.BaseBindingDialogFragment;


public class MaintenanceWifiTestFragment extends BaseBindingDialogFragment<FragmentMaintenanceWifiBinding> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();


      //  WifiUtils.withContext(getApp()).scanWifi(this::getScanResults).start();

    }

//    private void getScanResults(@NonNull final List<ScanResult> results) {
//        if (results.isEmpty()) {
//            Log.i("WWWWWWWQQQ", "SCAN RESULTS IT'S EMPTY");
//            return;
//        }
//        Log.i("WWWWWWWQQQ", "GOT SCAN RESULTS " + results);
//
//
//        WifiUtils.withContext(getApp())
//                .connectWith("CoreStarCo", "18:31:bf:e3:39:78", "core5379star5507")
//                .onConnectionResult(new ConnectionSuccessListener() {
//                    @Override
//                    public void success() {
//                        Log.d("WWWWWWWQQQ", "OK: ");
//                    }
//
//                    @Override
//                    public void failed(@NonNull ConnectionErrorCode errorCode) {
//                        Log.d("WWWWWWWQQQ", "NO" + errorCode);
//                    }
//                })
//                .start();
//
////        WifiUtils.withContext(getApp())
////                .connectWithScanResult("core5379star5507", scanResults -> scanResults.get(0))
////                .onConnectionResult(new ConnectionSuccessListener() {
////                    @Override
////                    public void success() {
////                        Log.d("WWWWWWWQQQ", "OK: ");
////                    }
////
////                    @Override
////                    public void failed(@NonNull ConnectionErrorCode errorCode) {
////                        Log.d("WWWWWWWQQQ", "NO" + errorCode);
////                    }
////                })
////                .start();
//
//    }

    private void initEvent() {

        getBinding().btnDone.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}