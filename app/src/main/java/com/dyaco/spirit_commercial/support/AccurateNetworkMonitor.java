package com.dyaco.spirit_commercial.support;

import static com.dyaco.spirit_commercial.support.intdef.EventKey.WIFI_WORK;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.viewmodel.AppStatusViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccurateNetworkMonitor extends ConnectivityManager.NetworkCallback {

    private static final String TAG = "AccurateNetworkMonitor";
    // 設定檢查間隔，避免太頻繁檢查（單位：毫秒），例如 10 秒
    private static final long CHECK_INTERVAL_MS = 10_000;
    private long lastCheckTime = 0;

    private final NetworkRequest networkRequest;
    private final Context context;
    private final AppStatusViewModel appStatusViewModel;
    private final ConnectivityManager connectivityManager;

    public AccurateNetworkMonitor(Context context, AppStatusViewModel appStatusViewModel) {
        this.context = context.getApplicationContext();
        this.appStatusViewModel = appStatusViewModel;
        connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 建立同時支援 Wi-Fi 與乙太網路的 NetworkRequest
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build();
    }

    public void enable() {
        Log.d(TAG, "監聽網路變化啟用");
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    public void disable() {
        Log.d(TAG, "監聽網路變化停用");
        connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.d(TAG, "onAvailable: 有網路可用 → " + network.toString());
        // 檢查上次檢查時間，避免短時間內重複檢查
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCheckTime < CHECK_INTERVAL_MS) {
            Log.d(TAG, "檢查過於頻繁，跳過此次連網檢查");
            return;
        }
        lastCheckTime = currentTime;
        checkRealInternet(network);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities capabilities) {
        super.onCapabilitiesChanged(network, capabilities);
        Log.d(TAG, "onCapabilitiesChanged: 網路能力變更");

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            try {
                // 注意：capabilities.getSignalStrength() 可能需視設備 API 版本與權限調整
                int signalStrength = capabilities.getSignalStrength();
                int level = WifiManager.calculateSignalLevel(signalStrength, 4);
                int wifiIcon;
                switch (level) {
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
                    default:
                        wifiIcon = R.drawable.btn_header_wifi_lv0_default;
                        break;
                }
                Log.d(TAG, "Wi-Fi 訊號強度等級: " + level + " → 設定圖示: " + wifiIcon);
                appStatusViewModel.wifiState.set(wifiIcon);
            } catch (Exception e) {
                Log.e(TAG, "處理 Wi-Fi 圖示時發生錯誤", e);
            }
        }

        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.d(TAG, "偵測到乙太網路 → 設定圖示");
            appStatusViewModel.wifiState.set(R.drawable.btn_wifi_0);
        }
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.w(TAG, "onLost: 網路已失去連接 → " + network.toString());
        appStatusViewModel.wifiState.set(R.drawable.btn_header_wifi_none_default);
        LiveEventBus.get(WIFI_WORK).post(false);
        Log.d(TAG, "LiveEventBus 發送: WIFI_WORK = false");
    }

    private void checkRealInternet(Network network) {
        new Thread(() -> {
            boolean hasInternet = false;
            HttpURLConnection urlConnection = null;
            Log.d(TAG, "開始進行實際連網測試...");
            try {
                URL url = new URL("https://clients3.google.com/generate_204");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("User-Agent", "Android");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(1500);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                int contentLength = urlConnection.getContentLength();
                hasInternet = (responseCode == 204 && contentLength == 0);
                Log.d(TAG, "實際連網檢測結果: " + hasInternet);
            } catch (IOException e) {
                Log.e(TAG, "檢查連網時發生錯誤", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            boolean finalResult = hasInternet;
            new Handler(Looper.getMainLooper()).post(() -> {
                LiveEventBus.get(WIFI_WORK).post(finalResult);
                Log.d(TAG, "LiveEventBus 發送: WIFI_WORK = " + finalResult);
            });
        }).start();
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        Log.w(TAG, "onUnavailable: 在逾時期間內無法找到可用網路");
        LiveEventBus.get(WIFI_WORK).post(false);
        Log.d(TAG, "LiveEventBus 發送: WIFI_WORK = false");
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        Log.d(TAG, "onLinkPropertiesChanged: 網路屬性變更 → " + linkProperties.toString());
    }
}
