package com.dyaco.spirit_commercial.support;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UsbFileCopier {

    private static final String TAG = "UsbFileCopier";
    private static final String MOUNT_POINT = "/mnt/usb";
    private static final String DEST_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit";
    private static final String[] FILESYSTEMS = {"vfat", "exfat", "ntfs", "ext4"};

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface CopyCallback {
        void onFileCopied(String fileName); // 🔔 每檔複製成功即回呼
        void onCompleted(int successCount, int failCount, long durationMs);
        void onError(String errorMessage);
    }


    public void copyRetailVideosFromUsb(CopyCallback callback) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            int copied = 0, failed = 0;

            try {
                String usbBlock = findUsbDevice();
                if (usbBlock == null) {
                    postError(callback, "USB device not found.");
                    return;
                }

                Log.d(TAG, "Found USB device: " + usbBlock);
                boolean mounted = tryMountWithSupportedFs(usbBlock);
                if (!mounted) {
                    postError(callback, "Failed to mount USB device.");
                    return;
                }

                File destFolder = new File(DEST_DIR);
                if (!destFolder.exists()) destFolder.mkdirs();

                Pattern pattern = Pattern.compile(".*retail_\\d{2}\\.mp4$", Pattern.CASE_INSENSITIVE);
                List<String> matchedFiles = new ArrayList<>();

                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "find " + MOUNT_POINT + " -type f"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String fileName = new File(line).getName();
                    if (pattern.matcher(fileName).matches()
                            && !line.contains("/.Trashes/")
                            && !fileName.startsWith("._")) {
                        matchedFiles.add(line);
                        Log.d(TAG, "Matched file: " + line);
                    } else {
                        Log.d(TAG, "Skipped: " + line);
                    }
                }
                proc.waitFor();

                Log.d(TAG, "Total matched files: " + matchedFiles.size());

                for (String src : matchedFiles) {
                    String fileName = new File(src).getName();
                    String dst = DEST_DIR + "/" + fileName;

                    try {
                        String cmd = "cp \"" + src + "\" \"" + dst + "\"";
                        Process copyProc = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
                        int result = copyProc.waitFor();
                        if (result == 0) {
                            copied++;
                            mainHandler.post(() -> callback.onFileCopied(fileName)); // ✅ 即時通知複製成功
                        } else {
                            throw new IOException("cp failed, code=" + result);
                        }
                    } catch (Exception e) {
                        failed++;
                        Log.e(TAG, "Failed to copy: " + fileName + " → " + e.getMessage());
                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                Log.i(TAG, "Copy finished. Success: " + copied + ", Failed: " + failed + ", Duration: " + duration + "ms");

                final int finalCopied = copied;
                final int finalFailed = failed;
                mainHandler.post(() -> callback.onCompleted(finalCopied, finalFailed, duration));

            } catch (Exception e) {
                Log.e(TAG, "Error during copy process: " + e.getMessage(), e);
                postError(callback, "Unexpected error: " + e.getMessage());
            }
        }).start();
    }

    private void postError(CopyCallback callback, String message) {
        Log.e(TAG, message);
        mainHandler.post(() -> callback.onError(message));
    }

    private boolean tryMountWithSupportedFs(String blockDevice) throws IOException, InterruptedException {
        for (String fs : FILESYSTEMS) {
            String cmd = "mkdir -p " + MOUNT_POINT + " && mount -o rw -t " + fs + " " + blockDevice + " " + MOUNT_POINT;
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            int result = proc.waitFor();
            if (result == 0) {
                Log.d(TAG, "Mounted with fs: " + fs);
                return true;
            } else {
                Log.w(TAG, "Mount failed for fs: " + fs);
            }
        }
        return false;
    }

    private String findUsbDevice() {
        File blockDir = new File("/dev/block");
        File[] blocks = blockDir.listFiles();
        if (blocks == null) return null;

        for (File file : blocks) {
            String name = file.getName();
            if (name.startsWith("sd")) {
                Log.d(TAG, "Found block: " + file.getAbsolutePath());
            }

            if (name.matches("^sd[a-z][0-9]*$")) {
                Log.d(TAG, "Confirmed USB block: " + name);
                return file.getAbsolutePath();
            }
        }

        return null;
    }
}
