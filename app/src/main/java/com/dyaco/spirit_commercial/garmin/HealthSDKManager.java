package com.dyaco.spirit_commercial.garmin;

import static com.dyaco.spirit_commercial.garmin.GarminDevicesWindow.GARMIN_TAG;

import android.content.Context;
import android.util.Log;

import com.dyaco.spirit_commercial.R;
import com.garmin.health.GarminHealth;
import com.garmin.health.GarminHealthInitializationException;
import com.garmin.health.HealthSDKLogging;
import com.garmin.health.LoggingLevel;
import com.google.common.util.concurrent.ListenableFuture;

public class HealthSDKManager {

    public static ListenableFuture<Boolean> initializeHealthSDK(Context context) throws GarminHealthInitializationException {
        if (!GarminHealth.isInitialized()) {
            HealthSDKLogging.setLoggingLevel(LoggingLevel.QUIET);
        }
        Log.d(GARMIN_TAG, "initializeHealthSDK.");
        return GarminHealth.initialize(context, context.getString(R.string.companion_license));
    }
}


//public class HealthSDKManager {
//    /**
//     * Initializes the health SDK for streaming
//     * License should be acquired by contacting Garmin. Each license has restriction on the type of data that can be accessed.
//     *
//     * @param context
//     * @throws GarminHealthInitializationException
//     */
//    public static ListenableFuture<Boolean> initializeHealthSDK(Context context) throws GarminHealthInitializationException {
//        if (!GarminHealth.isInitialized()) {
//            HealthSDKLogging.setLoggingLevel(LoggingLevel.VERBOSE);
//        }
//
//        return GarminHealth.initialize(context, context.getString(R.string.companion_license));
//    }
//}
