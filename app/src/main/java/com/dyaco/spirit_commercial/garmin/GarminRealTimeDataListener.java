package com.dyaco.spirit_commercial.garmin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.garmin.device.realtime.RealTimeBodyBattery;
import com.garmin.device.realtime.RealTimeDataType;
import com.garmin.device.realtime.RealTimeHeartRate;
import com.garmin.device.realtime.RealTimeHeartRateSource;
import com.garmin.device.realtime.RealTimeRespiration;
import com.garmin.device.realtime.RealTimeResult;
import com.garmin.device.realtime.RealTimeSpo2;
import com.garmin.device.realtime.listeners.RealTimeDataListener;

import java.util.HashMap;

public class GarminRealTimeDataListener implements RealTimeDataListener {
    WorkoutViewModel w;
    int dd = 0;
    private final HashMap<String, HashMap<RealTimeDataType, RealTimeResult>> mLatestData = new HashMap<>();

    public GarminRealTimeDataListener(WorkoutViewModel workoutViewModel) {
        w = workoutViewModel;
    }

    @Override
    public void onDataUpdate(@NonNull String macAddress, @NonNull RealTimeDataType realTimeDataType, @NonNull RealTimeResult realTimeResult) {

//        dd += 1;
//        if (dd % 24 == 0) {
//            Log.d("GARMINNN", "mRealTimeDataListener: mac:" + macAddress + "," + realTimeDataType + ",isGarminConnected:" + w.isGarminConnected.get());
//        }


        if (!w.isGarminConnected.get()) return;

//        HashMap<RealTimeDataType, RealTimeResult> latestData = mLatestData.computeIfAbsent(macAddress, k -> new HashMap<>());
//        latestData.put(realTimeDataType, realTimeResult);
//        w.setGarminHr(Objects.requireNonNull(latestData.get(RealTimeDataType.HEART_RATE)).getHeartRate().getCurrentHeartRate());


        logRealTimeData(macAddress, realTimeDataType, realTimeResult);
        //  LiveEventBus.get(GARMIN_ON_DATA_UPDATE).post(new GarminDataBean(macAddress, realTimeDataType, realTimeResult));
    }

    @Override
    public void onServiceDisconnected() {
        Log.d("GARMINNN", "onServiceDisconnected: ");
    }


    // static Long lastTime = null;

    public void logRealTimeData(String macAddress, RealTimeDataType dataType, RealTimeResult result) {
        //  String value = "";

        switch (dataType) {
            case STEPS:
                //      value = String.valueOf(result.getSteps().getCurrentStepCount());
                break;
            case HEART_RATE_VARIABILITY:
                //    value = String.valueOf(result.getHeartRateVariability().getHeartRateVariability());
                break;
            case CALORIES:
                //    value = String.valueOf(result.getCalories().getCurrentTotalCalories());
                break;
            case ASCENT:
                //    value = String.valueOf(result.getAscent().getCurrentMetersClimbed());
                break;
            case INTENSITY_MINUTES:
                //   value = String.valueOf(result.getIntensityMinutes().getTotalWeeklyMinutes());
                break;
            case HEART_RATE:
                RealTimeHeartRate heartRate = result.getHeartRate();
                int ghr = heartRate.getCurrentHeartRate();
                if (heartRate.getHeartRateSource() == RealTimeHeartRateSource.NO_SOURCE) {
                    ghr = 0;
                }
                w.setGarminHr(ghr);
                Log.d("GARMINNN", "RealTimeData HEART_RATE:" + ghr + ", Device:" + macAddress + ", source:" + heartRate.getHeartRateSource());
                break;
            case STRESS:
                //  RealTimeStress stress = result.getStress();
                String stress = result.getStress().getStressScore() <= 0 ? "--" : String.valueOf(result.getStress().getStressScore());

                w.garminStress.set(stress);
                Log.d("GARMINNN", "RealTimeData STRESS:" + stress + ", Device:" + macAddress);

                break;
            case ACCELEROMETER:
//                RealTimeAccelerometer realTimeAccelerometer = result.getAccelerometer();
//
//                int x = realTimeAccelerometer.getAccelerometerSamples().get(0).getX();
//                int y = realTimeAccelerometer.getAccelerometerSamples().get(0).getY();
//                int z = realTimeAccelerometer.getAccelerometerSamples().get(0).getZ();
//                long ts1 = realTimeAccelerometer.getAccelerometerSamples().get(0).getMillisecondTimestamp();
//                long ts2 = realTimeAccelerometer.getAccelerometerSamples().get(1).getMillisecondTimestamp();
//                long ts3 = realTimeAccelerometer.getAccelerometerSamples().get(2).getMillisecondTimestamp();
//
//                double magnitude = Math.sqrt(x * x + y * y + z * z) / 1000;
//                value = "magnitude: " + magnitude + " x: " + x + " y: " + y + " z: " + z;
//
//                long currentTime = realTimeAccelerometer.getAccelerometerSamples().get(0).getMillisecondTimestamp();
//
//                if (lastTime != null) {
//                 //   Log.i("GARMINNN", String.format("Accel Timestamp Diff: %d", currentTime - lastTime));
//                }
//
//                lastTime = currentTime;

                break;
            case SPO2://手錶戴緊 不要動
                RealTimeSpo2 realTimeSpo2 = result.getSpo2();
                String spo2 = realTimeSpo2.getSpo2Reading() <= 0 ? "--" : String.valueOf(realTimeSpo2.getSpo2Reading());
                w.garminSpo2.set(spo2);
                Log.d("GARMINNN", "RealTimeData SPO2:" + spo2 + ", Device:" + macAddress);
                break;
            case RESPIRATION:
                RealTimeRespiration realTimeRespiration = result.getRespiration();
                w.garminRespirationRate.set(realTimeRespiration.getRespirationRate());
                Log.d("GARMINNN", "RealTimeData RESPIRATION:" + realTimeRespiration.getRespirationRate() + ", Device:" + macAddress);
                break;
            case BODY_BATTERY:
                RealTimeBodyBattery realTimeBodyBattery = result.getBodyBattery();
                int x = realTimeBodyBattery.getBodyBatteryLevel();
                if (x >= 127) {
                    x = 0;
                }
                w.garminBodyBatteryLevel.set(x);
                Log.d("GARMINNN", "RealTimeData BODY_BATTERY:" + realTimeBodyBattery.getBodyBatteryLevel() + ", Device:" + macAddress);

                break;
        }
    }


//    /**
//     * 開啟RealTimeData
//     * @param device d
//     */
//    public void enableRealTimeData(com.garmin.health.Device device) {
//        try {
//            w.isGarminConnected.set(true);
//            currentGarminAddress = device.address();
//            stopListening();
//
//            new RxTimer().timer(200, number -> {
//                DeviceManager.getDeviceManager().enableRealTimeData(device.address(), EnumSet.allOf(RealTimeDataType.class));
//                currentGarminAddress = device.address();
//                Log.d(GARMIN_TAG, "切換 開啟RealTimeData:" + device.address() + "," + currentGarminAddress);
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void stopListening() {
//        if (DeviceManager.getDeviceManager().getPairedDevices() == null) return;
//        for (com.garmin.health.Device device : DeviceManager.getDeviceManager().getPairedDevices()) {
//            Log.d("GARMINNN", "關閉: " + device.address());
//            DeviceManager.getDeviceManager().disableRealTimeData(device.address(), EnumSet.allOf(RealTimeDataType.class));
//        }
//    }
}
