package com.dyaco.spirit_commercial.maintenance_mode;

import static com.dyaco.spirit_commercial.support.CommonUtils.getMimeType;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dyaco.spirit_commercial.support.CommonUtils;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.github.mjdev.libaums.partition.Partition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UsbReader {
    private static final String TAG = "USB_UPDATE";
    public static byte[] end = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, -70, 10};
    private static final String ACTION_USB_PERMISSION = "corestar.usb.permission";
    private static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String STATUS = "status";
    private static final String RESULT = "result";
    private static final String KIND = "kind";
    private final Context context;
    private final UsbManager usbManager;
    private final IntentFilter filter;
    private BroadcastReceiver broadcastReceiver;
    private final Map<String, UsbReader.CSUsbDevice> mCSUsbDevices = new ArrayMap<>();
    private UsbReader.UsbReaderListener mListener;
    private boolean isFileFinding = false;
    private File dir;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            UsbReader.USB_EVENT event = UsbReader.USB_EVENT.find(msg.what);
            switch (event) {
                case REQUEST_PERMISSION:
                    UsbReader.this.doRequestPermission(msg);
                    break;
                case FIND_FILE:
                    UsbReader.this.doFindFile(msg);
                    break;
                case DEVICE_ATTACHED:
                    UsbReader.this.doDeviceAttached(msg);
                    break;
                case DEVICE_DETACHED:
                    UsbReader.this.doDeviceDetached(msg);
            }

        }
    };

    public UsbReader(Context context) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.filter = this.getFilter();
        this.broadcastReceiver = this.getBroadcastReceiver();
        this.dir = context.getCacheDir();
    }

    public boolean isFileFinding() {
        return this.isFileFinding;
    }

    public void setFilePath(File path) {
        this.dir = path;
    }

    public String getFilePath() {
        return this.dir.getPath();
    }

    public void findFile(final UsbReader.CSUsbDevice device, final String fileName, final UsbReader.FILE_TYPE type, final UsbReader.FILE_KIND kind) {
        if (device != null && fileName != null && type != null) {
            if (!this.isFileFinding) {
                this.isFileFinding = true;
                (new Thread(() -> UsbReader.this.findFileInBackground(device, fileName, type, kind))).start();
            }

        }
    }

    private void findFileInBackground(UsbReader.CSUsbDevice device, String fileName, UsbReader.FILE_TYPE type, UsbReader.FILE_KIND kind) {
        if (!device.isPermissionGranted) {
            Log.w(TAG, "usb device permission not granted.");
            this.isFileFinding = false;
        } else {
            UsbReader.FILE_STATUS file_status = UsbReader.FILE_STATUS.FILE_NOT_FOUND;
            Object copyResult = null;
            HashMap<String, Object> dataObj = new HashMap<>();
            boolean var21 = false;

            Message msg;
            label95:
            {
                try {
                    var21 = true;
                    device.getDevice().init();
                    Partition partition = device.getDevice().getPartitions().get(0);
                    FileSystem fileSystem = partition.getFileSystem();
                    UsbFile root = fileSystem.getRootDirectory();
                    UsbFile[] files = root.listFiles();

                    for (UsbFile file : files) {
                        if (!file.isDirectory() && file.getName().equals(fileName)) {
                            File targetFile = new File(this.dir, fileName);
                            file_status = FILE_STATUS.FILE_FOUND;


                            if (type == FILE_TYPE.MP4) {
                                if (!checkVideo(file)) {
                                    file_status = FILE_STATUS.FILE_NOT_FOUND;
                                    if (this.mListener != null) {
                                        String xx = "#" + w1 + "#" + w2 + "#" + w3 + "#" + w4;
                                        this.mListener.onCopyFileError("SIZE_ERROR" + xx);
                                        return;
                                    }

                                }
                            }

                            copyResult = this.copyAndReadFile(fileSystem, file, targetFile, fileSystem.getChunkSize(), type);
                            if (copyResult == null) {
                                file_status = FILE_STATUS.WRITE_FAIL;
                            }
                            var21 = false;
                            break label95;
                        }
                    }

                    var21 = false;
                    break label95;
                } catch (Exception var22) {
                    var22.printStackTrace();
                    var21 = false;
                } finally {
                    if (var21) {
                        device.getDevice().close();
                        this.isFileFinding = false;
                        dataObj.put(NAME, fileName);
                        dataObj.put(STATUS, file_status);
                        dataObj.put(RESULT, copyResult);
                        dataObj.put(TYPE, type);
                        dataObj.put(KIND, kind);
                        msg = new Message();
                        msg.what = UsbReader.USB_EVENT.FIND_FILE.getCode();
                        msg.obj = dataObj;
                        this.mHandler.sendMessage(msg);
                    }
                }
                try {
                    device.getDevice().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.isFileFinding = false;
                dataObj.put(NAME, fileName);
                dataObj.put(STATUS, file_status);
                dataObj.put(RESULT, copyResult);
                dataObj.put(TYPE, type);
                dataObj.put(KIND, kind);
                msg = new Message();
                msg.what = UsbReader.USB_EVENT.FIND_FILE.getCode();
                msg.obj = dataObj;
                this.mHandler.sendMessage(msg);
                return;
            }

            try {
                device.getDevice().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.isFileFinding = false;
            dataObj.put(NAME, fileName);
            dataObj.put(STATUS, file_status);
            dataObj.put(RESULT, copyResult);
            dataObj.put(TYPE, type);
            dataObj.put(KIND, kind);
            msg = new Message();
            msg.what = UsbReader.USB_EVENT.FIND_FILE.getCode();
            msg.obj = dataObj;
            this.mHandler.sendMessage(msg);
        }
    }

    private Object copyAndReadFile(FileSystem fileSystem, UsbFile fromFile, File toFile, int chunksize, UsbReader.FILE_TYPE type) {
        if (toFile == null) {
            Log.w(TAG, "target file is null.");
            return null;
        } else {
            String result = null;
            byte[] content = new byte[(int) fromFile.getLength()];

            long totalSize = (int) fromFile.getLength();
            try {
                BufferedInputStream fosfrom = UsbFileStreamFactory.createBufferedInputStream(fromFile, fileSystem);
                BufferedOutputStream fosto = new BufferedOutputStream(Files.newOutputStream(toFile.toPath()), chunksize);
                byte[] buffer = new byte[chunksize];

                int c;
                for (int current = 0; (c = fosfrom.read(buffer)) > 0; current += c) {
                    fosto.write(buffer, 0, c);
                    System.arraycopy(buffer, 0, content, current, c);
                    if (this.mListener != null) {
                        this.mListener.onProgress(current, totalSize);
                    }
                }

                fosfrom.close();
                fosto.close();
                if (type == UsbReader.FILE_TYPE.JSON) {
                    result = new String(content);
                }

                if (type == UsbReader.FILE_TYPE.APK || type == UsbReader.FILE_TYPE.MP4 || type == UsbReader.FILE_TYPE.IMAGE) {
                    result = toFile.getAbsolutePath();
                }

                return type == UsbReader.FILE_TYPE.BIN ? content : result;
            } catch (Exception var13) {
                var13.printStackTrace();
                if (this.mListener != null) {
                    this.mListener.onCopyFileError(var13.getLocalizedMessage());
                }
                return null;
            }
        }
    }

    public void setListener(UsbReader.UsbReaderListener listener) {
        this.mListener = listener;
        if (this.mListener != null) {
            this.context.registerReceiver(this.broadcastReceiver, this.filter);
        }

    }

    public void unregisterListener() {
        this.mListener = null;
        this.context.unregisterReceiver(this.broadcastReceiver);
        broadcastReceiver = null;

    }

    public void requestPermission(UsbReader.CSUsbDevice csUsbDevice) {
        if (csUsbDevice != null) {
            if (this.mCSUsbDevices.containsKey(csUsbDevice.getName())) {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this.context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
                this.usbManager.requestPermission(csUsbDevice.getDevice().getUsbDevice(), permissionIntent);
            } else {
                Log.w(TAG, "device not exists or unmounted.");
            }

        }
    }

    public List<UsbReader.CSUsbDevice> getUsbDevice() {
        this.mCSUsbDevices.clear();
        UsbMassStorageDevice[] usbMassStorageDevices = UsbMassStorageDevice.getMassStorageDevices(this.context);
        int var3 = usbMassStorageDevices.length;

        for (UsbMassStorageDevice device : usbMassStorageDevices) {
            this.mCSUsbDevices.put(device.getUsbDevice().getDeviceName(), new CSUsbDevice(device));
        }
        return new ArrayList<>(this.mCSUsbDevices.values());
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        return filter;
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //  Log.d(UsbReader.TAG, "action received. " + action);
                Message msg = new Message();
                if (action.equals(ACTION_USB_PERMISSION)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra("device");
                        boolean isPermissionGranted = intent.getBooleanExtra("permission", false);
                        UsbReader.CSUsbDevice csUsbDevice = UsbReader.this.mCSUsbDevices.get(device.getDeviceName());
                        if (csUsbDevice != null) {
                            csUsbDevice.setPermissionGranted(isPermissionGranted);
                            msg.what = UsbReader.USB_EVENT.REQUEST_PERMISSION.getCode();
                            msg.obj = csUsbDevice;
                            UsbReader.this.mHandler.sendMessage(msg);
                        }
                    }
                }

                UsbDevice devicex;
                if (action.equals(ACTION_USB_DEVICE_ATTACHED)) {
                    devicex = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    msg.what = UsbReader.USB_EVENT.DEVICE_ATTACHED.getCode();
                    msg.obj = devicex.getDeviceName();
                    UsbReader.this.mHandler.sendMessage(msg);
                }

                if (action.equals(ACTION_USB_DEVICE_DETACHED)) {
                    devicex = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    msg.what = UsbReader.USB_EVENT.DEVICE_DETACHED.getCode();
                    msg.obj = devicex.getDeviceName();
                    UsbReader.this.mHandler.sendMessage(msg);
                }

            }
        };
    }

    public byte[][] packingData(byte[] data) {
        int packSize = 32;
        int checkSize = 2;
        int currentRemainder = data.length % packSize;
        int currentRow = currentRemainder > 0 ? data.length / packSize + 1 : data.length / packSize;
        byte[][] pack = new byte[currentRow + 1][packSize + checkSize];
        byte sum = 0;
        byte progression = 0;
        currentRow = 0;
        currentRemainder = 0;
        int i = 0;
        int var11 = data.length;

        for (byte b : data) {
            currentRow = i / packSize;
            currentRemainder = i % packSize;
            sum += data[i];
            progression += sum;
            pack[currentRow][currentRemainder] = b;
            if (currentRemainder == packSize - 1) {
                pack[currentRow][packSize] = sum;
                pack[currentRow][packSize + 1] = progression;
                sum = 0;
                progression = 0;
            }

            ++i;
        }

        if (currentRemainder < packSize - 1) {
            int size = currentRemainder + 1;
            pack[currentRow][packSize - 1] = (byte) size;
            sum = (byte) (sum + size);
            progression += sum;
            sum = (byte) (sum + 85);
            progression = (byte) (progression + 85);
            pack[currentRow][packSize] = sum;
            pack[currentRow][packSize + 1] = progression;
        }

        pack[currentRow + 1] = end;
        return pack;
    }

    private void doRequestPermission(Message msg) {
        UsbReader.CSUsbDevice device = (UsbReader.CSUsbDevice) msg.obj;
        if (this.mListener != null) {
            this.mListener.onRequestPermission(device);
        }

    }

    private void doFindFile(Message msg) {
        Object obj = msg.obj;
        Map<String, Object> dataObj = (HashMap) obj;
        if (dataObj != null) {
            String fileName = (String) dataObj.get(NAME);
            UsbReader.FILE_TYPE type = (UsbReader.FILE_TYPE) dataObj.get(TYPE);
            UsbReader.FILE_STATUS status = (UsbReader.FILE_STATUS) dataObj.get(STATUS);
            UsbReader.FILE_KIND kind = (UsbReader.FILE_KIND) dataObj.get(KIND);
            String data = null;
            byte[] raw = null;
            if (type != null) {
                switch (type) {
                    case APK:
                    case MP4:
                    case IMAGE:
                    case JSON:
                        data = (String) dataObj.get(RESULT);
                        break;
                    case BIN:
                        raw = (byte[]) (dataObj.get(RESULT));
                }
            }

            if (this.mListener != null) {
                this.mListener.onFindFile(fileName, status, type, data, raw, kind);
            }
        }

    }

    String d;

    private void doDeviceAttached(Message msg) {
        String deviceName = (String) msg.obj;
        d = deviceName;
        if (this.mListener != null) {
            this.mListener.onDeviceAttached(deviceName);
        }

    }

    private void doDeviceDetached(Message msg) {
        String deviceName = (String) msg.obj;
        if (this.mListener != null) {
            this.mListener.onDeviceDetached(deviceName);
        }

    }

    public static class CSUsbDevice {
        private boolean isPermissionGranted = false;
        private final UsbMassStorageDevice usbMassStorageDevice;
        private final String name;
        private boolean isMounted = false;

        public CSUsbDevice(UsbMassStorageDevice usbMassStorageDevice) {
            this.usbMassStorageDevice = usbMassStorageDevice;
            this.name = usbMassStorageDevice.getUsbDevice().getDeviceName();
        }

        public boolean isPermissionGranted() {
            return this.isPermissionGranted;
        }

        public void setPermissionGranted(boolean permissionGranted) {
            this.isPermissionGranted = permissionGranted;
        }

        public boolean isMounted() {
            return this.isMounted;
        }

        public void setMounted(boolean mounted) {
            this.isMounted = mounted;
        }

        public String getName() {
            return this.name;
        }

        public UsbMassStorageDevice getDevice() {
            return this.usbMassStorageDevice;
        }
    }

    public interface UsbReaderListener {
        void onRequestPermission(UsbReader.CSUsbDevice var1);

        void onFindFile(String var1, UsbReader.FILE_STATUS var2, UsbReader.FILE_TYPE var3, String var4, byte[] var5, UsbReader.FILE_KIND kind);

        void onDeviceAttached(String var1);

        void onDeviceDetached(String var1);

        void onProgress(long current, long total);

        void onCopyFileError(String errorMsg);
    }

    public enum FILE_STATUS {
        FILE_NOT_FOUND,
        FILE_FOUND,
        WRITE_FAIL;

        FILE_STATUS() {
        }
    }

    public enum FILE_TYPE {
        MP4,
        IMAGE,
        JSON,
        APK,
        BIN;

        FILE_TYPE() {
        }
    }

    public enum FILE_KIND {
        SUB_MCU,
        LWR,
        NORMAL;

        FILE_KIND() {
        }
    }

    public enum USB_EVENT {
        REQUEST_PERMISSION(0),
        FIND_FILE(1),
        DEVICE_ATTACHED(2),
        DEVICE_DETACHED(3);

        private final int code;
        private static final Map<Integer, UsbReader.USB_EVENT> lookup = new HashMap<>();

        private USB_EVENT(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }

        public static UsbReader.USB_EVENT find(int code) {
            return (UsbReader.USB_EVENT) lookup.get(code);
        }

        static {
            UsbReader.USB_EVENT[] var0 = values();
            int var1 = var0.length;

            for (USB_EVENT event : var0) {
                lookup.put(event.getCode(), event);
            }

        }
    }


    //mnt/usb_storage
    //content://com.android.externalstorage.documents
//    Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/82D5-1513:retail.mp4");
    int w1, w2 = 0, w3, w4 = 1;
    //0 沒檢查, 1ok, 2fail
    private boolean checkVideo(UsbFile usbFile) {
        boolean result;
        String path = usbFile.getAbsolutePath();

        if ("video/mp4".equals(getMimeType(path))) {
            w1 = 1;
        } else {
            w1 = 2;
        }

        //解析度
//        if (width == 1920 && height == 1080) {
//            w2 = true;
//        }

        float fileSize = CommonUtils.byte2Mb(usbFile.getLength());
        if (fileSize <= 120) { //6980598   6.980598
            w3 = 1;
        } else {
            w3 = 2;
        }


        Log.d(TAG, "FILE_SIZE:" + usbFile.getLength());
        Log.d(TAG, "getMimeType:" + getMimeType(usbFile.getAbsolutePath()));

        result = w1 < 2 && w2 < 2 && w3 < 2 && w4 < 2;

        return result;

    }
}
