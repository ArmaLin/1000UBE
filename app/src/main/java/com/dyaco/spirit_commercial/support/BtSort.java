package com.dyaco.spirit_commercial.support;

import android.annotation.SuppressLint;

import com.corestar.libs.audio.AudioDeviceWatcher;

import java.util.Comparator;

public class BtSort implements Comparator<AudioDeviceWatcher.AudioDevice> {
    @SuppressLint("MissingPermission")
    public int compare(AudioDeviceWatcher.AudioDevice a, AudioDeviceWatcher.AudioDevice b) {
        return b.getDevice().getBondState() - a.getDevice().getBondState();
    }
}
