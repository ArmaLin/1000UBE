package com.dyaco.spirit_commercial.support.download;

import static com.dyaco.spirit_commercial.MainActivity.UPDATE_FILE_PATH;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    //  public static final String PATH_CHALLENGE_VIDEO = Environment.getExternalStorageDirectory() + "/SpiritDownloadFile";

    protected ApiInterface mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    private String mFilePath;
    private String fileExtension;

    public DownloadUtil(String fileExtension) {
        this.fileExtension = fileExtension;
        if (mApi == null) {
            mApi = ApiHelper.getInstance().buildRetrofit("https://github.com/")
                    .createService(ApiInterface.class);
        }
    }

    public void downloadFile(String url, final DownloadListener downloadListener) {
//        if (FileUtils.createOrExistsDir(PATH_CHALLENGE_VIDEO)) {
//            mFilePath = PATH_CHALLENGE_VIDEO + File.separator + "Spirit.apk";
//            Log.d("UPDATE@@@", "資料夾創建完成: " + mFilePath);
//        }
//        if (TextUtils.isEmpty(mFilePath)) {
//            //資料夾創建失敗
//            downloadListener.onFailure("PATH NULL");
//            return;
//        }

        // TODO: XAPK
        mFilePath = UPDATE_FILE_PATH + File.separator + "Spirit." + fileExtension;
//        mFilePath = UPDATE_FILE_PATH + File.separator + "Spirit.apk";
        mFile = new File(mFilePath);
//        Log.d("UPDATE@@@", "檔案位置: " + mFilePath);
        if (!FileUtils.isFileExists(mFile) && FileUtils.createOrExistsFile(mFile)) {
            if (mApi == null) {
                downloadListener.onFailure("mApi == null");
                return;
            }
            mCall = mApi.downloadFile(url);
            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            //保存到本地
                            writeFile2Disk(response, mFile, downloadListener);
                        }
                    };
                    mThread.start();
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                    downloadListener.onFailure("網路錯誤！");
                    downloadListener.onFailure("");
                }
            });
        } else {
            Log.d("UPDATE@@@", "檔案創建失敗: " + mFilePath);
            downloadListener.onFailure("file error");
        }
    }

    public void stop() {
        if (mCall != null) mCall.cancel();
    }

    private void writeFile2Disk(Response<ResponseBody> response, File file, DownloadListener downloadListener) {
        downloadListener.onStart();
        long currentLength = 0;
        OutputStream os = null;

        if (response.body() == null) {
            downloadListener.onFailure("source error！");
            return;
        }
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                downloadListener.onProgress((int) (100 * currentLength / totalLength), currentLength, totalLength);
                if ((int) (100 * currentLength / totalLength) == 100) {
                    downloadListener.onFinish(mFilePath);
                }
            }
        } catch (FileNotFoundException e) {
            downloadListener.onFailure("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            downloadListener.onFailure("");
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void deleteFile() {

        File deleteFile = new File(UPDATE_FILE_PATH);
        RecursionDeleteFile(deleteFile);
        Log.d("UPDATE@@@", "刪除檔案 ");
//        if (!deleteFile.exists()) {
//            boolean d = deleteFile.mkdirs();
//            Log.d("UPDATE@@@", "刪除檔案: " + d); //true
//        }
    }

    public void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    public void installAPK(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 廣播裡面操作需要加上這句，存在於一個獨立的棧裡

        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        context.startActivity(intent);
        Log.d("安裝", "installAPK: ");

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            /* Android N 写法*/
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(filePath));
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            /* Android N之前的老版本写法*/
//            intent.setDataAndType(Uri.fromFile(new File("apk地址")), "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        startActivity(intent);

    }

    public String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getDownloadTime(NetworkCapabilities networkCapabilities, long total, long current) {

        String time;

//        long size = total - current; // byte
        //8 kbps = 1 kb, 1000 kb = 1 mbps, 8 mbps = 1 Mb/s
//        double downSpeed = (networkCapabilities.getLinkDownstreamBandwidthKbps() / 8d); //kb

        double sizeMb = (total - current) / 1024d / 1024d;
        double downSpeedMb = (((double) networkCapabilities.getLinkDownstreamBandwidthKbps() / 8) / 1000) / 8; //MB
//        Log.d("LLLFFFFF", "getDownloadTime: " + sizeMb + "," + ((sizeMb / downSpeedMb)));//秒
        time = String.format(Locale.getDefault(), "%.1f", (sizeMb / downSpeedMb) / 60); //分

        return time;
    }

    private String copyFileToInternalStorage(Context mContext, Uri uri, String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = mContext.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if (!newDirName.equals("")) {
            File dir = new File(mContext.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(mContext.getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(mContext.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }
}