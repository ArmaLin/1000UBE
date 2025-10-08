package com.dyaco.spirit_commercial.garmin;

import com.corestar.libs.ftms.DeviceFilter;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;

import java.util.HashSet;
import java.util.Set;

public class GarminDeviceFilter implements DeviceFilter {
    private final DeviceManager mDeviceManager;
    private final Set<String> garmins = new HashSet<>();

    public GarminDeviceFilter(DeviceManager deviceManager) {
        this.mDeviceManager = deviceManager;
    }

    public void addDevice(String mac) {
        this.garmins.add(mac);
    }

    public void clear() {
        this.garmins.clear();
    }

    @Override
    public boolean isIllegalDevice(String s) {
        Set<Device> devices = mDeviceManager.getPairedDevices();
        for (Device device : devices) {
            garmins.add(device.address());
        }
        return garmins.contains(s);
    }
}
