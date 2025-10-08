package com.dyaco.spirit_commercial.support;

public class ProgressPayload {
    private String packageName;
    private int progress;
    private String payload;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public ProgressPayload(String packageName, int progress, String payload) {
        this.packageName = packageName;
        this.progress = progress;
        this.payload = payload;
    }

    public ProgressPayload() {
    }
}
