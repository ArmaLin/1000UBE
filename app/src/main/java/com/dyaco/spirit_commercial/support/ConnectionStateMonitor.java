package com.dyaco.spirit_commercial.support;


import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

@SuppressLint("MissingPermission")
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {
    private static final String TAG = "ConnectionStateMonitor";
    final NetworkRequest networkRequest;
    private Context context;
    private AppStatusViewModel appStatusViewModel;
    private ConnectivityManager connectivityManager;

    public ConnectionStateMonitor() {
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build();
    }

    public void enable(Context context, AppStatusViewModel appStatusViewModel) {
        this.context = context;
        this.appStatusViewModel = appStatusViewModel;
        connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.

    /**
     * 網路可用的回調
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
//        Log.d("網路", "onAvailable: " + network.toString());
//        Log.d("###EGYMMMMM", "onAvailable: " + network.toString());
        LiveEventBus.get(WIFI_WORK).post(true);

        //  if (network.toString().equals("101")) { //重開機會回到 100 ， 每次連線 +1
        //    checkTimeZone();
        //   }
    }

    /**
     * 在網路失去連接的時候回調，但是如果是一個生硬的斷開，他可能不回調
     */
    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
//        Log.d("網路", "onLosing: " + network);
//

//        LiveEventBus.get(WIFI_WORK).post(false);
    }

    /**
     * 網路丟失的回調
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        //   ConnectivityManager connectivityManager = (ConnectivityManager) getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(network);

        //    Log.d("網路", "onLost: " + actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) + "," + actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));

        if (actNw == null || actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            appStatusViewModel.wifiState.set(R.drawable.btn_header_wifi_none_default);
        }


//        LiveEventBus.get(WIFI_WORK).post(false);
        new RxTimer().timer(500, number -> {
//            boolean isNetworkAvailable = CommonUtils.isNetworkAvailable(getApp());
//            Log.d("網路", "onLost:" + "有無網路:" + isNetworkAvailable);
//            LiveEventBus.get(WIFI_WORK).post(isNetworkAvailable);

            int isNetworkAvailable = CommonUtils.getNetWorkType();
            if (isNetworkAvailable == NetworkCapabilities.TRANSPORT_ETHERNET) {
                appStatusViewModel.wifiState.set(R.drawable.btn_wifi_0);
            }

       //     Log.d("網路", "onLost:" + "有無網路:" + isNetworkAvailable);

            LiveEventBus.get(WIFI_WORK).post(isNetworkAvailable > 0);

        });
    }

    /**
     * 按照官方註釋的解釋，是指如果在超時時間內都沒有找到可用的網路時進行回調
     */
    @Override
    public void onUnavailable() {
        super.onUnavailable();
    }

    /**
     * 網路的某個能力發生了變化回調
     *
     * @param network             network
     * @param networkCapabilities networkCapabilities
     */
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);

        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
         //   Log.d("網路", "onCapabilitiesChanged: WIFI:" + networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) + ",乙太:" + networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                try {
                    int wifiIcon = 0;
                    switch (WifiManager.calculateSignalLevel(networkCapabilities.getSignalStrength(), 4)) {
                        case 0:
                            wifiIcon = R.drawable.btn_header_wifi_lv1_default;
                            break;
                        case 1:
                            wifiIcon = R.drawable.btn_header_wifi_lv2_default;
                            break;
                        case 2:
                            wifiIcon = R.drawable.btn_header_wifi_lv3_default;
                            break;
                        case 3:
                            wifiIcon = R.drawable.btn_header_wifi_lv4_default;
                            break;
                        case 4:
                        case -1:
                            wifiIcon = R.drawable.btn_header_wifi_lv0_default;
                            break;
                    }
                    appStatusViewModel.wifiState.set(wifiIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //乙太網路 wifi 都存在時，乙太網路優先
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                appStatusViewModel.wifiState.set(R.drawable.btn_wifi_0);
            }
        }
    }

    /**
     * 當建立網路連接時，回調連接的屬性
     */
    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        //   Log.d("網路", "當建立網路連接時，回調連接的屬性: " + network.toString());
        //  LiveEventBus.get(WIFI_WORK).post(true);
//        if (CheckDoubleClick.isFastClick()) return;
//        checkTimeZone();
    }

//    private void checkTimeZone() {
//        Log.d("TIME_ZONE", "Start checkTimeZone: ");
//        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://www.geoplugin.net/").apiGetTimeZone(),
//                new BaseApi.IResponseListener<TimeZoneBean>() {
//                    @Override
//                    public void onSuccess(TimeZoneBean data) {
//                        try {
//                            Log.d("TIME_ZONE", "onSuccess: " + data.getGeoplugin_timezone());
//                            setTimeZone(data.getGeoplugin_timezone());
//                        } catch (Exception e) {
//                            Log.d("TIME_ZONE", "try catch_onSuccess: ");
//                            checkTimeZone2();
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d("TIME_ZONE", "@@@onFail: ");
//                        checkTimeZone2();
//                    }
//                });
//    }
//
//    private void checkTimeZone2() {
//        Log.d("TIME_ZONE", "Start checkTimeZone2: ");
//        BaseApi.request(BaseApi.createApi2(IServiceApi.class, "http://ip-api.com/").apiGetTimeZone2(),
//                new BaseApi.IResponseListener<TimeZoneBean2>() {
//                    @Override
//                    public void onSuccess(TimeZoneBean2 data) {
//                        try {
//                            Log.d("TIME_ZONE", "onSuccess: " + data.getTimezone());
//                            setTimeZone(data.getTimezone());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d("TIME_ZONE", "try catch_onSuccess: ");
//
//                            setTimeZone(TimeZone.getDefault().getID());
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.d("TIME_ZONE", "onFail: ");
//                        // setTimeZone("Asia/Taipei");
//                        setTimeZone(TimeZone.getDefault().getID());
//                    }
//                });
//    }
//
//    private void setTimeZone(String timeZone) {
//        try {
//            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            am.setTimeZone(timeZone);
//            Log.d("TIME_ZONE", "TIME_ZONE: " + TimeZone.getDefault().getID());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}