package com.dyaco.spirit_commercial.support.download;

import static com.dyaco.spirit_commercial.MainActivity.UPDATE_FILE_PATH;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class FileUtils {
    /**
     * 判斷文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在{@code false}: 不存在
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    public static boolean isFileExists2(final File file) {
        return file == null || !file.exists();
    }

    /**
     * 判斷文件是否存在，不存在則判斷是否創建成功
     *
     * @param file 文件
     * @return true:存在或創建成功，false:不存在或創建失敗
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        // 如果存在，是文件則返回 true，是目錄則返回 false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判斷目錄是否存在，不存在則判斷是否創建成功
     *
     * @param file 文件
     * @return true:存在或創建成功，false:不存在或創建失敗
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目錄則返回 true，是文件則返回 false，不存在則返回是否創建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判斷目錄是否存在，不存在則判斷是否創建成功
     *
     * @param dirPath 目錄路徑
     * @return true:存在或創建成功，false:不存在或創建失敗
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 根據文件路徑獲取文件
     *
     * @param filePath 文件路徑
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        Log.d("UPDATE@@@", "@@@getFileByPath isSpace: " + isSpace(filePath));
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 檢查有無空白字
     * @param s s
     * @return should false
     */
    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void persistImage(Bitmap bitmap, String name) {
        String filePath = "";
        if (FileUtils.createOrExistsDir(UPDATE_FILE_PATH)) {
            filePath = UPDATE_FILE_PATH + File.separator + name;
        }
        if (TextUtils.isEmpty(filePath)) {
            Log.e("UPDATE@@@", "downloadVideo: 存儲路徑為空");
            return;
        }
        File file = new File(filePath);
        if (!FileUtils.isFileExists(file) && FileUtils.createOrExistsFile(file)) {
            OutputStream os;
            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e("File", "Error writing bitmap", e);
            }
        }
    }

    /**
     * 取得文件列表
     */
    public static void getAllFile(File fileInput, List<File> allFileList) {
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 如果不想統計子資料夾則可以將下一行註解掉
            //    getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }
}