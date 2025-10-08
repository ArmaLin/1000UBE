//package com.dyaco.spirit_commercial.support.wifi;
//
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.text.TextUtils;
//
//import androidx.annotation.NonNull;
//
//import java.util.List;
//import java.util.Locale;
//
//public class WifiEntity {
//    public String name;
//    public String SSID;
//    public boolean isEncrypt;
//    public boolean isSaved;
//    public boolean isConnected;
//    public String encryption;
//    public String description;
//    public String capabilities;
//    public String ip;
//    public String state;
//    public int level;
//
//    public static WifiEntity create(ScanResult result, List<WifiConfiguration> configurations, String connectedSSID, int ipAddress) {
//        if (TextUtils.isEmpty(result.SSID))
//            return null;
//        WifiEntity wifi = new WifiEntity();
//        wifi.isConnected = false;
//        wifi.isSaved = false;
//        wifi.name = result.SSID;
//        wifi.SSID = "\"" + result.SSID + "\"";
//        wifi.isConnected = wifi.SSID.equals(connectedSSID);
//        wifi.capabilities = result.capabilities;
//        wifi.isEncrypt = true;
//        wifi.encryption = "";
//        wifi.level = result.level;
//        wifi.ip = wifi.isConnected ? String.format(Locale.getDefault(), "%d.%d.%d.%d",
//                (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)) : "";
//
//        if (wifi.capabilities.toUpperCase().contains("WPA2-PSK") && wifi.capabilities.toUpperCase().contains("WPA-PSK")) {
//            wifi.encryption = "WPA/WPA2";
//        } else if (wifi.capabilities.toUpperCase().contains("WPA-PSK")) {
//            wifi.encryption = "WPA";
//        } else if (wifi.capabilities.toUpperCase().contains("WPA2-PSK")) {
//            wifi.encryption = "WPA2";
//        } else {
//            wifi.isEncrypt = false;
//        }
//        wifi.description = wifi.encryption;
//
//        for (WifiConfiguration configuration : configurations) {
//            if (configuration.SSID.equals(wifi.SSID)) {
//                if (configuration.status != WifiConfiguration.Status.DISABLED) {
//                    wifi.isSaved = true;
//                }
//                break;
//            }
//        }
//
//        if (wifi.isSaved) {
//            wifi.description = "Saved";
//        }
//        if (wifi.isConnected) {
//            wifi.description = "Connected";
//        }
//        return wifi;
//    }
//
//    /** 🔹 Getter / Setter 替代原來的 `IWifi` 方法 */
//    public String getName() {
//        return name;
//    }
//
//    public boolean isEncrypt() {
//        return isEncrypt;
//    }
//
//    public boolean isSaved() {
//        return isSaved;
//    }
//
//    public boolean isConnected() {
//        return isConnected;
//    }
//
//    public String getEncryption() {
//        return encryption;
//    }
//
//    public int getLevel() {
//        return level;
//    }
//
//    public String getDescription() {
//        return state == null ? description : state;
//    }
//
//    public String getIp() {
//        return ip;
//    }
//
//    public String getDescription2() {
//        return isConnected ? String.format("%s (%s)", getDescription(), ip) : getDescription();
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getSSID() {
//        return SSID;
//    }
//
//    public String getCapabilities() {
//        return capabilities;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof WifiEntity)) return false;
//        return ((WifiEntity) obj).SSID.equals(this.SSID);
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return "{" +
//                "\"name\":\'" + name + "\'" +
//                ", \"SSID\":\'" + SSID + "\'" +
//                ", \"isEncrypt\":" + isEncrypt +
//                ", \"isSaved\":" + isSaved +
//                ", \"isConnected\":" + isConnected +
//                ", \"encryption\":\'" + encryption + "\'" +
//                ", \"description\":\'" + description + "\'" +
//                ", \"capabilities\":\'" + capabilities + "\'" +
//                ", \"ip\":\'" + ip + "\'" +
//                ", \"state\":\'" + state + "\'" +
//                ", \"level\":" + level +
//                '}';
//    }
//}
