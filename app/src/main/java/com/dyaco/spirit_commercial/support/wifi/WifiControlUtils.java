package com.dyaco.spirit_commercial.support.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.model.repository.WifiXmlData;
import com.dyaco.spirit_commercial.support.CommandUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@SuppressWarnings("deprecation")
public class WifiControlUtils {
    private final WifiManager mWifiManager;
    private List<WifiConfiguration> wifiConfigurationList;
    private final Context mContext;
    public static final String TAG = "###WIFI";

    public WifiControlUtils(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mContext = context.getApplicationContext();
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            scanWifi();
        }
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public WifiInfo getWifiInfo() {
        return mWifiManager != null ? mWifiManager.getConnectionInfo() : null;
    }

    @SuppressLint("MissingPermission")
    public void scanWifi() {
        if (mWifiManager == null) return;
        // 向系統請求一次掃描，掃描結果由 onReceive 的 SCAN_RESULTS_AVAILABLE_ACTION 再去處理
        mWifiManager.startScan();
        // 更新已儲存清單（如果有需要的話）
        wifiConfigurationList = mWifiManager.getConfiguredNetworks();
    }


    private String removeQuotes(String ssid) {
        if (ssid == null) return "";
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }


    @SuppressLint("MissingPermission")
    public WifiConfiguration getExitsWifiConfig(String SSID) {
        if (wifiConfigurationList == null) return null;
        for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
            if (wifiConfiguration.SSID.equals("\"" + SSID + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    public boolean removeWifi(String SSID) {
        WifiConfiguration config = getExitsWifiConfig(SSID);
        return config != null && mWifiManager.removeNetwork(config.networkId);
    }

    public boolean disconnectWifi(String SSID) {
        WifiConfiguration config = getExitsWifiConfig(SSID);
        if (config != null) {
            // 停用此 networkId，並斷開
            mWifiManager.disableNetwork(config.networkId);
            mWifiManager.disconnect();
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public List<WifiEntity> modifyWifi() {
        List<ScanResult> results = mWifiManager.getScanResults();
        HashMap<String, WifiEntity> uniqueWifiMap = new HashMap<>();
        String connectedSSID = mWifiManager.getConnectionInfo().getSSID();
        int ipAddress = mWifiManager.getConnectionInfo().getIpAddress();

        for (ScanResult result : results) {
            WifiEntity wifi = WifiEntity.create(result, mWifiManager.getConfiguredNetworks(), connectedSSID, ipAddress);
            if (wifi == null) continue;

            WifiEntity existingWifi = uniqueWifiMap.get(wifi.SSID);
            if (existingWifi == null || wifi.level > existingWifi.level) {
                uniqueWifiMap.put(wifi.SSID, wifi);
            }
        }

        return sortWifiList(new ArrayList<>(uniqueWifiMap.values())); // 直接回傳，不存入 `wifis`
    }



    private List<WifiEntity> sortWifiList(List<WifiEntity> list) {
        list.sort((w1, w2) -> {
            if (w1.isConnected() && !w2.isConnected()) return -1;
            if (!w1.isConnected() && w2.isConnected()) return 1;
            if (w1.isSaved() && !w2.isSaved()) return -1;
            if (!w1.isSaved() && w2.isSaved()) return 1;
            return Integer.compare(w2.level, w1.level);
        });
        return list;
    }


    public void connectWifi(WifiEntity iWifi, String tagPassword) {
        Log.d(TAG, "connectWifi: " + iWifi.name);
        String ssid = "\"" + iWifi.name + "\"";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;

        // 先判斷是否為 WEP 網路 (注意：WEP 可能在 WifiEntity.create 中被判定為非加密)
        if (iWifi.capabilities != null && iWifi.capabilities.toUpperCase().contains("WEP")) {
            // WEP 網路處理
            if (iWifi.isSaved()) {
                // 已儲存的網路：依需求可以保留舊密碼，這邊示範將密碼設為 null
                conf.wepKeys = new String[1];
                conf.wepKeys[0] = null;
            } else {
                conf.wepKeys = new String[1];
                conf.wepKeys[0] = "\"" + tagPassword + "\"";
            }
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.clear();
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.clear();
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (!iWifi.isEncrypt()) {
            // 開放網路處理
            conf.preSharedKey = "";
            conf.allowedKeyManagement.clear();
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            // WPA/WPA2 網路 (完全不做改動)
            String password = "\"" + tagPassword + "\"";
            if (iWifi.isSaved()) {
                conf.preSharedKey = null;
            } else {
                conf.preSharedKey = password;
            }
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }

        conf.status = WifiConfiguration.Status.ENABLED;
        int networkId = mWifiManager.addNetwork(conf);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(networkId, true);
        mWifiManager.reconnect();
    }




    private WifiXmlData myWifiXmlData;
    private final List<WifiXmlData> wifiXmlDataList = new ArrayList<>();
    public String getSharedWifi() {
        String sharedWifiQrcode = "";
        String x = "cat /data/misc/apexdata/com.android.wifi/WifiConfigStore.xml";

        String wifiConfigXml = CommandUtil.execute(x);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(wifiConfigXml)));
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nodeList1 = doc.getElementsByTagName("Network");
            for (int i = 0; i < nodeList1.getLength(); i++) {
                WifiXmlData wifiXmlData = new WifiXmlData();
                Node node1 = nodeList1.item(i);
                NodeList nodeList2 = node1.getChildNodes();
                for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                    Node node2 = nodeList2.item(i2);
                    if ("WifiConfiguration".equals(node2.getNodeName())) {
                        NodeList nodeList3 = node2.getChildNodes();

                        for (int i3 = 0; i3 < nodeList3.getLength(); i3++) {
                            Node node3 = nodeList3.item(i3);
                            NamedNodeMap nnm = node3.getAttributes();

                            Node n = nnm.item(0); // 例如：<string name="SSID">"CoreStarCo"</string>
                            Node n2 = null;      // 例如：<boolean name="HiddenSSID" value="false" />

                            if ("SSID".equals(n.getTextContent()) ||
                                    "PreSharedKey".equals(n.getTextContent()) ||
                                    "HiddenSSID".equals(n.getTextContent()) ||
                                    "ConfigKey".equals(n.getTextContent())) {

                                if ("HiddenSSID".equals(n.getTextContent()) && nnm.getLength() >= 2) {
                                    n2 = nnm.item(1);
                                    Log.d("getWifiConfig", "##" + i + "## " + n.getTextContent() + "," + (n2 != null ? n2.getNodeValue() : ""));
                                    assert n2 != null;
                                    wifiXmlData.setHiddenSSID(strReplace(n2.getNodeValue()));
                                }

                                NodeList nodeList4 = node3.getChildNodes();
                                for (int i4 = 0; i4 < nodeList4.getLength(); i4++) {
                                    Node node4 = nodeList4.item(i4);
                                    if ("SSID".equals(n.getTextContent())) {
                                        wifiXmlData.setSSID(strReplace(node4.getNodeValue()));
                                    }

                                    if ("PreSharedKey".equals(n.getTextContent())) {
                                        wifiXmlData.setPreSharedKey(strReplace(node4.getNodeValue()));
                                    }

                                    if ("ConfigKey".equals(n.getTextContent())) {
                                        wifiXmlData.setType(strReplace(node4.getNodeValue()));
                                    }
                                    Log.d("getWifiConfig", "##" + i + "## " + n.getTextContent() + "," + node4.getNodeValue() + "," + (n2 != null ? n2.getNodeValue() : ""));
                                }

                            }
                        }

                        Log.d("getWifiConfig", "============");
                    }
                }
                wifiXmlDataList.add(wifiXmlData);
            }

            String currentSSID = getCurrentSSID();
            for (WifiXmlData wifiXmlData : wifiXmlDataList) {
                if (currentSSID.equals(wifiXmlData.getSSID())) {
                    myWifiXmlData = wifiXmlData;
                }
            }

            sharedWifiQrcode = "WIFI:S:" + myWifiXmlData.getSSID() + ";T:WPA;P:" + myWifiXmlData.getPreSharedKey() + ";H:" + myWifiXmlData.isHiddenSSID() + ";;";

        } catch (Exception e) {
            Log.d("getWifiConfig", "#######Exception: " + e.getLocalizedMessage());
            Log.d("getWifiConfig", "\n#######Exception:######### ");
        }
        return sharedWifiQrcode;
    }

    private String strReplace(String str) {
        return str.replace("\"", "");
    }

    // 將 WifiEntity 合併為內部靜態類別
    public static class WifiEntity {
        public String name;
        public String SSID;
        private boolean isEncrypt;
        private boolean isSaved;
        private boolean isConnected;
        private boolean isConnecting;
        public String encryption;
        public String description;
        public String capabilities;
        public String ip;
        public String state;
        public int level;

        public static WifiEntity create(ScanResult result, List<WifiConfiguration> configurations, String connectedSSID, int ipAddress) {
            if (TextUtils.isEmpty(result.SSID))
                return null;
            WifiEntity wifi = new WifiEntity();
            wifi.isConnected = false;
            wifi.isConnecting = false;
            wifi.isSaved = false;
            wifi.name = result.SSID;
            wifi.SSID = "\"" + result.SSID + "\"";
            wifi.isConnected = wifi.SSID.equals(connectedSSID);
            wifi.capabilities = result.capabilities;
            wifi.isEncrypt = true;
            wifi.encryption = "";
            wifi.level = result.level;
            wifi.ip = wifi.isConnected ? String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)) : "";

            if (wifi.capabilities.toUpperCase().contains("WPA2-PSK") && wifi.capabilities.toUpperCase().contains("WPA-PSK")) {
                wifi.encryption = "WPA/WPA2";
            } else if (wifi.capabilities.toUpperCase().contains("WPA-PSK")) {
                wifi.encryption = "WPA";
            } else if (wifi.capabilities.toUpperCase().contains("WPA2-PSK")) {
                wifi.encryption = "WPA2";
            } else {
                wifi.isEncrypt = false;
            }
            wifi.description = wifi.encryption;

            for (WifiConfiguration configuration : configurations) {
                if (configuration.SSID.equals(wifi.SSID)) {
                    if (configuration.status != WifiConfiguration.Status.DISABLED) {
                        wifi.isSaved = true;
                    }
                    break;
                }
            }

            if (wifi.isSaved) {
                wifi.description = "Saved";
            }
            if (wifi.isConnected) {
                wifi.description = "Connected";
            }
            return wifi;
        }

        public String getName() {
            return name;
        }

        public boolean isEncrypt() {
            return isEncrypt;
        }

        public boolean isSaved() {
            return isSaved;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public boolean isConnecting() {
            return isConnecting;
        }

        public void setConnecting(boolean connecting) {
            isConnecting = connecting;
        }

        public String getEncryption() {
            return encryption;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return state == null ? description : state;
        }

        public String getIp() {
            return ip;
        }

        public String getDescription2() {
            return isConnected ? String.format("%s (%s)", getDescription(), ip) : getDescription();
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getSSID() {
            return SSID;
        }

        public String getCapabilities() {
            return capabilities;
        }

        public String getState() {
            return state;
        }


        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WifiEntity)) return false;
            return ((WifiEntity) obj).SSID.equals(this.SSID);
        }

        @NonNull
        @Override
        public String toString() {
            return "WifiEntity{" +
                    "name='" + name + '\'' +
                    ", SSID='" + SSID + '\'' +
                    ", isEncrypt=" + isEncrypt +
                    ", isSaved=" + isSaved +
                    ", isConnected=" + isConnected +
                    ", isConnecting=" + isConnecting +
                    ", encryption='" + encryption + '\'' +
                    ", description='" + description + '\'' +
                    ", capabilities='" + capabilities + '\'' +
                    ", ip='" + ip + '\'' +
                    ", state='" + state + '\'' +
                    ", level=" + level +
                    '}';
        }
    }


    private WifiEventListener wifiEventListener;
    public void setWifiEventListener(WifiEventListener listener) {
        this.wifiEventListener = listener;
    }

    public interface WifiEventListener {
        void onWifiStateChanged(int state);
        void onNetworkStateChanged(NetworkInfo.DetailedState state);
        void onScanResultsAvailable(boolean isUpdated);
        void onSupplicantStateChanged(int state);
    }


    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiReceiver, filter);
    }

    public void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered or already unregistered.");
        }
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiStateChanged(intent);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                handleNetworkStateChanged(intent);
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                handleScanResults(intent);
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                handleSupplicantStateChanged(intent);
            }
        }

        private void handleWifiStateChanged(Intent intent) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiEventListener != null) {
                wifiEventListener.onWifiStateChanged(state);
            }
        }

        private void handleNetworkStateChanged(Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info == null || info.getDetailedState() == null) return;
            NetworkInfo.DetailedState state = info.getDetailedState();
            if (wifiEventListener != null) {
                wifiEventListener.onNetworkStateChanged(state);
            }
        }

        @SuppressLint("MissingPermission")
        private void handleScanResults(Intent intent) {
            boolean isUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            // 先由外部 listener 更新畫面（WifiWindow 會收到 onScanResultsAvailable）
            if (wifiEventListener != null) {
                wifiEventListener.onScanResultsAvailable(isUpdated);
            }

            // 接著自行檢查：如果掃描結果裡沒包含當前 SSID，就再重新掃描一次
            String connectedSSID = getCurrentSSID();
            if (TextUtils.isEmpty(connectedSSID) || "unknown".equals(connectedSSID)) {
                // 如果目前沒連到任何 SSID，就不必重掃
                return;
            }

            List<ScanResult> scanResults = mWifiManager.getScanResults();
            boolean isCurrentWifiInScanResults = false;
            for (ScanResult result : scanResults) {
                if (removeQuotes(result.SSID).equals(removeQuotes(connectedSSID))) {
                    isCurrentWifiInScanResults = true;
                    break;
                }
            }
            if (!isCurrentWifiInScanResults) {
                Log.e(TAG, "Current WiFi not found in scan results, rescanning...");
                // 再發一次掃描請求
                mWifiManager.startScan();
            }
        }


        private void handleSupplicantStateChanged(Intent intent) {
            int state = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            if (wifiEventListener != null) {
                wifiEventListener.onSupplicantStateChanged(state);
            }
        }
    };

    @SuppressLint("MissingPermission")
    public String getCurrentSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) return "unknown";

        String ssid = wifiInfo.getSSID();

        // 部分設備返回帶有引號的 SSID，例如："MyWiFi"，這裡去除引號
        if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        return (ssid == null || ssid.isEmpty() || ssid.equals("<unknown ssid>")) ? "unknown" : ssid;
    }

    /**
     * 這個方法會先掃描所有 ScanResult，根據 capabilities 判斷該 SSID 的「真實加密方式」。
     * 回傳字串："WPA/WPA2 PSK"、"WEP" 或 "None"
     */
    @SuppressLint("MissingPermission")
    private String getSecurityTypeFromScan(String targetSSID) {
        String security = "None";
        List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // ScanResult.SSID 就是不帶引號的 SSID
                if (TextUtils.equals(result.SSID, targetSSID)) {
                    String caps = result.capabilities.toUpperCase();
                    if (caps.contains("WEP")) {
                        security = "WEP";
                    } else if (caps.contains("PSK")) {
                        // PSK 會同時出現在 WPA2-PSK、WPA-PSK 裡面
                        security = "WPA/WPA2 PSK";
                    } else {
                        security = "None";
                    }
                    break;
                }
            }
        }
        return security;
    }

    /**
     * 改寫後的 addWifiNetwork：先檢查使用者傳入的 securityType，
     * 如果與掃描到的真實加密方式不符，就直接擋掉（不會儲存到系統）。
     */
    @SuppressLint("MissingPermission")
    public boolean addWifiNetwork(String ssid, String password, String securityType) {
        // 1. 先做一次掃描，拿到真實加密方式
        String realSecurity = getSecurityTypeFromScan(ssid);
        if (!realSecurity.equalsIgnoreCase(securityType)) {
            Log.e(TAG, "addWifiNetwork: 使用者選的 securityType [" + securityType + "] 與掃描到的 realSecurity [" + realSecurity + "] 不符，已擋掉");
            return false;
        }

        // 2. 如果與真實加密方式相符，繼續建立 WifiConfiguration
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";

        if ("WPA/WPA2 PSK".equalsIgnoreCase(securityType)) {
            wifiConfig.preSharedKey = "\"" + password + "\"";
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        } else if ("WEP".equalsIgnoreCase(securityType)) {
            wifiConfig.wepKeys = new String[] { "\"" + password + "\"" };
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        } else { // "None"
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        int netId = mWifiManager.addNetwork(wifiConfig);
        if (netId == -1) {
            Log.e(TAG, "addWifiNetwork: 新增失敗 netId = -1");
            return false;
        }

        // 斷線 → 啟用 → 連線
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
        return true;
    }



}





