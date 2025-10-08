package com.dyaco.spirit_commercial.support.download;

public interface DownloadListener {
    void onStart();

    void onProgress(int currentLength , long count, long total);

    void onFinish(String localPath);

    void onFailure(String errorInfo);
}
