package com.dyaco.spirit_commercial.support;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.view.MotionEvent.ACTION_UP;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.dyaco.spirit_commercial.App.MODE;
import static com.dyaco.spirit_commercial.App.UNIT_E;
import static com.dyaco.spirit_commercial.App.getApp;
import static com.dyaco.spirit_commercial.MainActivity.isSummary;
import static com.dyaco.spirit_commercial.MainActivity.isTreadmill;
import static com.dyaco.spirit_commercial.MainActivity.isUs;
import static com.dyaco.spirit_commercial.egym.EgymUtil.SYMBOL_DURATION;
import static com.dyaco.spirit_commercial.egym.EgymUtil.ZERO_DURATION_DURATION;
import static com.dyaco.spirit_commercial.support.FormulaUtil.E_BLANK;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_IDLE;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_LOGIN_PAGE;
import static com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef.STATUS_SUMMARY;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_EGYM;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.CONSOLE_SYSTEM_SPIRIT;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_ELLIPTICAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_RECUMBENT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_STEPPER;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_TREADMILL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UBE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.DEVICE_TYPE_UPRIGHT_BIKE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.NET_WORK_TYPE_NONE;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.OFF;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.ON;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.SCREEN_TIMEOUT_NEVER;
import static com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.TERRITORY_GLOBAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_AVG_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_AVG_POWER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_AVG_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_CADENCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_CALORIES;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_CURRENT_DISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_DISTANCE_LEFT;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_ELAPSED_TIME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_ELEVATION_GAIN;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_HEART_RATE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INCLINE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INTERVAL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_INTERVAL_DISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_LEVEL;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_METS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_PACE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_POWER;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_REMAINING_CALORIES;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_REMAINING_STEPS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_REMAINING_TIME;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_RESISTANCE;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_SPEED;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_TOTAL_REVOLUTIONS;
import static com.dyaco.spirit_commercial.support.intdef.GENERAL.STATS_TOTAL_STEPS;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MAX_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_IU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU_MAX;
import static com.dyaco.spirit_commercial.support.intdef.OPT_SETTINGS.MIN_SPD_MU_MIN;
import static com.dyaco.spirit_commercial.support.intdef.WorkoutIntDef.UNLIMITED;
import static com.dyaco.spirit_commercial.support.mediaapp.MediaAppUtils.CONSOLE_MEDIA_APP_LIST;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.dyaco.spirit_commercial.App;
import com.dyaco.spirit_commercial.MainActivity;
import com.dyaco.spirit_commercial.R;
import com.dyaco.spirit_commercial.product_flavor.InitProduct;
import com.dyaco.spirit_commercial.product_flavor.ModeEnum;
import com.dyaco.spirit_commercial.support.intdef.AppStatusIntDef;
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef;
import com.dyaco.spirit_commercial.support.intdef.GENERAL;
import com.dyaco.spirit_commercial.support.room.spirit.spirit_entity.MediaAppsEntity;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingBean;
import com.dyaco.spirit_commercial.viewmodel.DeviceSettingViewModel;
import com.dyaco.spirit_commercial.viewmodel.WorkoutViewModel;
import com.dyaco.spirit_commercial.work_task.InstallCallback;
import com.dyaco.spirit_commercial.workout.programs.ProgramsEnum;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CommonUtils {



    public static List<String> generateTimeOptions(int startMinute, int endMinute) {

        List<String> options = new ArrayList<>();

        for (int i = startMinute; i <= endMinute; i++) {
            String formattedTime = String.format(Locale.US, "%02d:00", i);
            options.add(formattedTime);
        }
        return options;
    }

    /**
     * 取得view的位置
     * <p>
     * Rect rect = locateView(getBinding().vTopIncline);
     * chartMsgWindow.showAtLocation(getWindow().getDecorView(), Gravity.START | Gravity.TOP, rect.left, rect.bottom);
     */
//    public static Rect locateView(View v) {
//        int[] loc_int = new int[2];
//        if (v == null) return null;
//        try {
//            v.getLocationOnScreen(loc_int);
//        } catch (NullPointerException npe) {
//            //Happens when the view doesn't exist on screen anymore.
//            return null;
//        }
//        Rect location = new Rect();
//        location.left = loc_int[0];
//        location.top = loc_int[1];
//        location.right = location.left + v.getWidth();
//        location.bottom = location.top + v.getHeight();
//        return location;
//    }
    public static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    public static float dp2px(int dp) {
//        return TypedValue.applyDimension(1, (float) dp, Resources.getSystem().getDisplayMetrics());
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, Resources.getSystem().getDisplayMetrics());
    }


    //只取得小數點第一位,如果小數點是0 就去除小數點
    public static String formatS(Double value) {
        if (value == null || value == 0) {
            return E_BLANK; // 預設回傳 "-"
        }

        // 直接截取小數點後一位，而不進行四捨五入
        double truncatedValue = Math.floor(value * 10) / 10;

        return (truncatedValue % 1 == 0) ? String.valueOf((int) truncatedValue) : String.format(Locale.getDefault(), "%.1f", truncatedValue);
    }


    /**
     * 小數點為0則移除
     *
     * @param s s
     * @return s
     */
    public static String subZeroAndDot(String s) {
        try {
            if (s.indexOf(".") > 0) {
                s = s.replaceAll("0 ?$", "");//去掉多餘的0
                s = s.replaceAll("[.]$", "");//如最後一位是.則去掉
            }
        } catch (Exception e) {
            showException(e);
        }
        return s;
    }

    public static String subZeroAndDot2(String s) {
        if (s == null) return s;
        s = s.trim(); // 移除輸入字串的前後空白
        if (!s.contains(".")) return s;
        String[] parts = s.split("\\.", 2);
        String intPart = parts[0];
        String decPart = parts[1].trim(); // 處理小數部分的空白

        // 如果小數部分為空或首位是 '0'，則僅回傳整數部分
        if (decPart.isEmpty() || decPart.charAt(0) == '0') {
            return intPart;
        } else {
            // 否則回傳整數部分 + "." + 小數部分的第一個數字
            return intPart + "." + decPart.charAt(0);
        }
    }


//    public static String addZero(String s) {
//
//        try {
//            if (s.indexOf(".") > 0) {
//                s = s.replaceAll("0 ?$", "");//去掉多餘的0
//                s = s.replaceAll("[.]$", "");//如最後一位是.則去掉
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return String.format(Locale.getDefault(), "%02d", Long.parseLong(s));
//    }
//
//    public static int null2Zero(String s) {
//
//        if (s == null || s.equals("")) {
//            s = "0";
//        }
//
//        return Integer.parseInt(s);
//    }


//    public static String textCheckNull(String str) {
//        if (str == null || "".equals(str) || str.length() == 0) {
//            str = "0";
//        }
//        return str;
//    }
//
//
//    public static int convertDpToPixel(float dpValue, Context context) {
//        float scale = getDensity(context);
//        return (int) (dpValue * scale + 0.5F);
//    }

    /**
     * Covert px to dp
     */
//    public static float convertPixelToDp(float px, Context context) {
//        return px / getDensity(context);
//    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     */
    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

//    public static boolean isInteger(String str) {
//        // Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
//        Pattern pattern = Pattern.compile("^[-]?[\\d]*$");
//        return pattern.matcher(str).matches();
//    }

    public static String formatSecondsToHHMMSS(int totalSeconds) {
        LocalTime time = LocalTime.ofSecondOfDay(totalSeconds);
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }


    public static String formatSecToM(long sec) {
        String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.SECONDS.toMinutes(sec));
        String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec)));
        return String.format("%s:%s", mm, ss);
    }

    public static String formatSecToHMS(long sec) {
        if (sec < 0) sec = 0; // 避免負數

        long hours = TimeUnit.SECONDS.toHours(sec);
        long minutes = TimeUnit.SECONDS.toMinutes(sec) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = sec - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec));

        return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
    }



    public static String getSecondsOnly(int totalSeconds) {
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d", seconds);
    }

    public static String getMinutesOnly(int totalSeconds) {
        int minutes = totalSeconds / 60;
        return String.format(Locale.getDefault(), "%02d", minutes);
    }


    public static String formatMsToM(long millis) {
        // 轉換為分鐘
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        // 計算剩餘的秒數
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        // 格式化為 mm:ss 格式
        String mm = String.format(Locale.getDefault(), "%02d", minutes);
        String ss = String.format(Locale.getDefault(), "%02d", seconds);
        return String.format("%s:%s", mm, ss);
    }

//    public String formatSecToM2(int sec) {
//        String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.SECONDS.toMinutes(sec));
//        String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec)));
//        return String.format("%s:%s", mm, ss);
//    }

    public int qrcodeIconShow(int currentStatus) {
        return isUs ? View.GONE : currentStatus == STATUS_IDLE || currentStatus == AppStatusIntDef.STATUS_LOGIN_PAGE || currentStatus == STATUS_SUMMARY ? View.VISIBLE : View.GONE;
    }

    public int settingIconShow(int currentStatus) {
        return currentStatus == STATUS_IDLE || currentStatus == AppStatusIntDef.STATUS_LOGIN_PAGE || isSummary ? View.VISIBLE : View.GONE;
    }

    public int memberProfileIconShow(int userType, int currentStatus) {
        return userType == GENERAL.USER_TYPE_GUEST ? View.GONE : (currentStatus == STATUS_IDLE || isSummary ? View.VISIBLE : View.GONE);
    }

    public int memberProfileIconShow(int userType, int currentStatus, int consoleSystem) {
        if (userType == GENERAL.USER_TYPE_GUEST && consoleSystem != CONSOLE_SYSTEM_EGYM) {
            return View.GONE;
        } else {
            return (currentStatus == STATUS_IDLE || isSummary ? View.VISIBLE : View.GONE);
        }
    }

    public float setGarminIconDimen(int currentStatus, int timUnit) {
        float dimen;
        switch (currentStatus) {
            case STATUS_LOGIN_PAGE:
                dimen = 300;
                break;
            case STATUS_IDLE:
            case STATUS_SUMMARY:
                if (isUs) {
                    dimen = 190;
                } else {
                    dimen = 380;
                }

                break;
            case AppStatusIntDef.STATUS_PAUSE:
                // dimen = 298;
                dimen = 110;
                break;
            case AppStatusIntDef.STATUS_RUNNING:
                dimen = 110;
                break;
            default:
                dimen = 0;
        }

        if (timUnit == DeviceIntDef.TF_AM_PM) {
            dimen += 40;
        }

        return dimen;
    }

    public float setSettingIconDimen(int currentStatus) {
        float dimen = 270;

        if (currentStatus == AppStatusIntDef.STATUS_PAUSE) {
            dimen = 182;
        }

        return dimen;
    }

    public float setPauseTimeDimen(int pauseMode) {
        float dimen;

        if (pauseMode == ON) {
            dimen = 760;
        } else {
            dimen = 890;
        }
        return dimen;
    }

//    public static String formatSec2H(long sec) {
//        String time;
//        if (sec >= 3600) {
//            LocalTime timeOfDay = LocalTime.ofSecondOfDay(sec);
//            time = timeOfDay.toString();
//        } else {
//            time = formatSecToM(sec);
//        }
//        return time;
//    }

    /**
     * 保留兩位小數，四捨五入
     *
     * @param value 數值
     * @return string
     */
//    public static String formatDouble(String value) {
//        DecimalFormat df = new DecimalFormat("#0.00");
//        return df.format(Double.valueOf(value));
//    }

    /**
     * 保留小數，不四捨五入
     *
     * @param value 數值
     * @return string
     */
    public String formatDecimal(double value) {
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(Double.valueOf(value));
    }

    public String formatDecimal(double value, int keep) {
        DecimalFormat df;
        if (keep == 1) {
            df = new DecimalFormat("#0.0");
        } else if (keep == 0) {
            df = new DecimalFormat("#0");
        } else {
            df = new DecimalFormat("#0.00");
        }
        return df.format(Double.valueOf(value));
    }


//    public String formatDecimal(double value, int keep) {
//        final DecimalFormat format = new DecimalFormat();
//        format.setMaximumFractionDigits(keep);
//        format.setGroupingSize(0);
//        format.setRoundingMode(RoundingMode.FLOOR);
//        return format.format(value);
//    }


    public Bitmap createQRCode(String qrUrl, int size, int padding) {
        Bitmap bitmapQR = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix matrix = new QRCodeWriter().encode(qrUrl,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            //
            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        if (!isFirstBlackPoint) {
                            isFirstBlackPoint = true;
                            startX = x;
                            startY = y;
                        }
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            //剪切中間的二維碼區域，減少padding區域
            if (startX <= padding) {
                return bitmap;
            }

            int x1 = startX - padding;
            int y1 = startY - padding;
            if (x1 < 0 || y1 < 0) {
                return bitmap;
            }

            int w1 = width - x1 * 2;
            int h1 = height - y1 * 2;

            bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapQR;
    }

//    public Bitmap generateQRCode(String QRCodeContent, Context context) {
//
//        try {
//            // QR code 的內容
//            //QRCodeContent
//            Bitmap bitmap;
//            // QR code 寬度
//            //   int QRCodeWidth = 185;
//            int QRCodeWidth = 250;
//            // QR code 高度
//            //   int QRCodeHeight = 185;
//            int QRCodeHeight = 250;
//            // QR code 內容編碼
//            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
//
//            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//
//            MultiFormatWriter writer = new MultiFormatWriter();
//            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
//            // 設定 QR code 容錯率為 H
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
//
//            // 建立 QR code 的資料矩陣
//            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
//            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...
//
//            //建立點陣圖
//            bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
//            // 將 QR code 資料矩陣繪製到點陣圖上
//            for (int y = 0; y < QRCodeHeight; y++) {
//                for (int x = 0; x < QRCodeWidth; x++) {
//                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : ContextCompat.getColor(context, R.color.colorADB8C2));
//                }
//            }
//            return bitmap;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    public static boolean checkStr(String str) {
//        return !"".equals(str) && str != null;
//    }
//
//    public static boolean checkInt(Integer i) {
//        return i != null && i != 0;
//    }

    public static int findMaxInt(int[] array) {
        int max = 0;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

//    public File readFileFromAssets(Context ctx) {
//        File f = new File(ctx.getCacheDir() + "/fff");
//        try {
//            InputStream inputStream = ctx.getAssets().open("fff.txt");
//
//            FileOutputStream fos = new FileOutputStream(f);
//            byte[] buffer = new byte[1024];
//            inputStream.read(buffer);
//            inputStream.close();
//            fos.close();
//            inputStream.close();
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return f;
//    }

    public String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public long getLocalVersionCode() {
        long localVersion = 0;
        try {
            PackageInfo packageInfo = getApp()
                    .getPackageManager()
                    .getPackageInfo(getApp().getPackageName(), 0);
            localVersion = packageInfo.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


//    // 需要点击几次 就设置几
//    private static long[] mHits = null;
//    private static final int COUNTS = 5;//点击次数
//    private static final long DURATION = TimeUnit.SECONDS.toMillis(3);//规定有效时间
//
//    public static boolean onSwitchMonitor() {
//        if (mHits == null) {
//            mHits = new long[COUNTS];
//        }
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
//        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//记录一个时间
//        if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {//一秒内连续点击。
//            //进来以后需要还原状态，否则如果点击过快，第六次，第七次 都会不断进来触发该效果。重新开始计数即可
//            mHits = null;
//            return true;
//        }
//        return false;
//    }


//    /**
//     * 判斷網路是否連接
//     *
//     * @param context context
//     * @return 判斷網路是否連接
//     */
//    public static boolean isConnected(Context context) {
//
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager == null) {
//            return false;
//        }
//
//        @SuppressLint("MissingPermission")
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//        return networkInfo != null;
//    }

    public static boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        //  return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        return actNw != null && ((actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || (actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))));
    }


    public static int getNetWorkType() {
        int netWorkType = NET_WORK_TYPE_NONE;

        ConnectivityManager connectivityManager = (ConnectivityManager) getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return NET_WORK_TYPE_NONE;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);

        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            netWorkType = NetworkCapabilities.TRANSPORT_WIFI;
        }

        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            netWorkType = NetworkCapabilities.TRANSPORT_ETHERNET;
        }

        return netWorkType;
    }

    RxTimer longTimer;

    @SuppressLint("ClickableViewAccessibility")
    public void addAutoClick(View button, int speed) {

        if (button == null) {
            Log.e("CommonUtils", "Button is null in addAutoClick!");
            return;
        }

        button.setOnLongClickListener(v -> {
            if (longTimer != null) {
                longTimer.cancel();
                longTimer = null;
            }
            longTimer = new RxTimer();
            longTimer.interval3(speed, n -> button.callOnClick());
            return true;
        });

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == ACTION_UP) {
                if (longTimer != null) {
                    longTimer.cancel();
                    longTimer = null;
                }
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addAutoClick(ImageButton button) {

        button.setOnLongClickListener(v -> {
            if (longTimer != null) {
                longTimer.cancel();
                longTimer = null;
            }
            longTimer = new RxTimer();
            longTimer.interval3(200, n ->
                    button.callOnClick());
            return true;
        });

        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == ACTION_UP) {
                if (longTimer != null) {
                    longTimer.cancel();
                    longTimer = null;
                }
            }
            return false;
        });
    }

    // String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CoreStar/Dyaco/Spirit/default_settings.json";

    public String readFromFileInputStream(FileInputStream fileInputStream) {
        StringBuilder retBuf = new StringBuilder();
        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                }
            }
        } catch (IOException ex) {
        }
        return retBuf.toString();
    }

//    public String createSettingFile() {
//        try {
//            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/Dyaco/Spirit/");
//            if (!root.exists()) {
//                boolean b = root.mkdirs();
//            }
//            File file = new File(root, "default_settings.json");
//            FileWriter writer = new FileWriter(file);
//            ModeEnum modeEnum;
//            if (getInstance().getDeviceSettingBean().getModel_name() != null) {
//                modeEnum = ModeEnum.getMode(getInstance().getDeviceSettingBean().getModel_code());
//                Log.d("SETTING_FILE", "裝置資料庫的機型為: " + getInstance().getDeviceSettingBean().getModel_name());
//            } else {
//                modeEnum = ModeEnum.XE395ENT;
//            }
//
//            new InitProduct(getInstance()).setProductDefault(modeEnum);
//            String settingData = new Gson().toJson(getInstance().getDeviceSettingBean());
//            writer.append(settingData);
//            writer.flush();
//            writer.close();
//            Log.d("SETTING_FILE", "重新建立 " + modeEnum + " 設定檔 成功");
//            return settingData;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public static String getMacAddress() {
//        String macAddress = "";
//        try {
//            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//            while (interfaces.hasMoreElements()) {
//                NetworkInterface iF = interfaces.nextElement();
//
//                byte[] address = iF.getHardwareAddress();
//                if (address == null || address.length == 0) {
//                    continue;
//                }
//
//                StringBuilder buf = new StringBuilder();
//                for (byte b : address) {
//                    buf.append(String.format("%02X:", b));
//                }
//                if (buf.length() > 0) {
//                    buf.deleteCharAt(buf.length() - 1);
//                }
//                String mac = buf.toString();
//                //  Log.d("mac", "interfaceName="+iF.getName()+", mac="+mac);
//
//                if (TextUtils.equals(iF.getName(), "wlan0")) {
//                    return mac;
//                }
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//            return macAddress;
//        }
//
//        return macAddress;
//    }

    /**
     * Android 10 以上 需要 ACCESS_FINE_LOCATION 權限
     *
     * @param context c
     * @return s
     */
    public String getSSID(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo != null ? wifiInfo.getSSID() : "";
            return ssid.replace("\"", "").replace("<unknown ssid>", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "UnKnow";
        }
    }


//    public static void closePackage(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
//        Method method;
//        try {
//            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
//            //  method.setAccessible(true);
//
//            method.invoke(activityManager, PACKAGE_NAME_ESPN);
//            method.invoke(activityManager, PACKAGE_NAME_CNN);
//            method.invoke(activityManager, PACKAGE_NAME_BBC);
//            method.invoke(activityManager, PACKAGE_NAME_NETFLIX);
//            method.invoke(activityManager, PACKAGE_NAME_HULU);
//            method.invoke(activityManager, PACKAGE_NAME_FACEBOOK);
//            method.invoke(activityManager, PACKAGE_NAME_INSTAGRAM);
//            method.invoke(activityManager, PACKAGE_NAME_TWITTER);
//            method.invoke(activityManager, PACKAGE_NAME_SPOTIFY);
//            method.invoke(activityManager, PACKAGE_NAME_IHEART);
//            method.invoke(activityManager, PACKAGE_NAME_WEATHER);
//            method.invoke(activityManager, PACKAGE_NAME_STUDIO);
//
//
//            method.invoke(activityManager, "com.android.settings");
//            method.invoke(activityManager, "com.mediatek.factorymode");
//            method.invoke(activityManager, "com.android.browser");
//            method.invoke(activityManager, "com.redstone.ota.ui");
//
//            //  method.invoke(activityManager, "com.android.launcher3");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void closePackage(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            Method method = activityManager.getClass().getMethod("forceStopPackage", String.class);
            method.setAccessible(true);


            for (MediaAppsEntity mediaAppsEntity : CONSOLE_MEDIA_APP_LIST) {
                method.invoke(activityManager, mediaAppsEntity.getPackageName());
            }

//            method.invoke(activityManager, "com.android.settings");
            method.invoke(activityManager, "com.mediatek.factorymode");
            method.invoke(activityManager, "com.android.browser");
            method.invoke(activityManager, "com.redstone.ota.ui");


            //  method.invoke(activityManager, "com.android.launcher3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static void closePackage2(){
//        ActivityManager am = (ActivityManager) getApp().getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(20 + 1, ActivityManager.RECENT_IGNORE_UNAVAILABLE); //MAX_RECENT_TASKS=20
//        for (ActivityManager.RecentTaskInfo taskInfo : recentTasks) {
//            if (taskInfo.baseActivity.getPackageName().equals(PACKAGE_NAME_ESPN)) {//pkgName是要移除的应用的包名
//                try {
//                    ActivityManagerNative.getDefault().removeTask(taskInfo.id);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//    }

//    private void queryFilterAppInfo() {
//        PackageManager pm = getApp().getPackageManager();
//        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
//        List<ApplicationInfo> applicationInfoList = new ArrayList<>();
//
//        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//        resolveIntent.addCategory(Intent.GATEGORY_LAUNCHER);
//    }


    public static void uninstallApp(String pkgName) {
        String command = "pm uninstall ";
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command + pkgName);
        } catch (Exception e) {
            showException(e);
        }
    }


    public static boolean uninstallWithRoot(String pkgName) {
        try {
            // 構建 Shell 指令
            String command = "pm uninstall " + pkgName;
            // 使用 Runtime 執行命令
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            // 等待命令執行完成
            int resultCode = process.waitFor();
            return resultCode == 0; // 返回值為 0 表示成功
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean uninstallByPackageManager(Context context, String pkgName) {
        try {
            final int DELETE_ALL_USERS = 0x00000002;

            Class<?> iPackageDeleteObserverClass = Class.forName("android.content.pm.IPackageDeleteObserver");
            Class<?> appPackageManagerClass = Class.forName("android.app.ApplicationPackageManager");

            Method deletePackageMethod = appPackageManagerClass.getMethod(
                    "deletePackage",
                    String.class,
                    iPackageDeleteObserverClass,
                    int.class
            );

            Object observerProxy = Proxy.newProxyInstance(
                    context.getClassLoader(),
                    new Class[]{iPackageDeleteObserverClass},
                    (proxy, method, args) -> {
                        // 可以在這邊 log callback method，例如 onPackageDeleted()
                        return null;
                    });

            Object[] params = new Object[]{
                    pkgName,
                    observerProxy,
                    DELETE_ALL_USERS
            };

            deletePackageMethod.invoke(context.getPackageManager(), params);
            return true;

        } catch (ClassNotFoundException e) {
            Log.e("Uninstall", "找不到必要的類別", e);
        } catch (NoSuchMethodException e) {
            Log.e("Uninstall", "找不到 deletePackage 方法", e);
        } catch (Exception e) {
            Log.e("Uninstall", "執行卸載時出現例外", e);
        }

        return false;
    }



    public static void clearAppData() {
        //執行底層Linux下的程式或指令碼
        //Android的安全機制不允許一個App去刪除另一個App的數據。
        String command = "pm clear ";
        try {
            // clearing app data
            Runtime runtime = Runtime.getRuntime();

            for (MediaAppsEntity mediaAppsEntity : CONSOLE_MEDIA_APP_LIST) {
                runtime.exec(command + mediaAppsEntity.getPackageName());
            }
        } catch (Exception e) {
            showException(e);
        }
    }

//    private static void execCommand(String command) {
//        try {
//            //String keyCommand = "input keyevent " + KeyEvent.KEYCODE_BACK;
//            Runtime runtime = Runtime.getRuntime();
//            //              //  Process proc = runtime.exec(keyCommand);
//            runtime.exec(command);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 清除 WebView cookies
     */
    public static void clearCookies() {
        iExc(() -> {
            //For security reasons, WebView is not allowed in privileged processes
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        });
    }


    //刪除快取 ClearCache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children != null ? children : new String[0]) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


//    /**
//     * F215U.Q0.V4.69.PBJ-V30.8839.lcm.10.1 > 30
//     *
//     */
//    public static int checkSwVersion() {
//        int v;
//        try {
//            //F215U.Q0.V4.69.PBJ-V30.8839.lcm.10.1
//            //R51V1.0-userdebug-20220314:154946
//            String sw_version = SysProp.get("ro.build.display.id", "ro.build.display.id");
//            String[] x = sw_version.split("\\.");
//            String o = x[4];
//            App.SW_VERSION = Integer.parseInt(o.substring(5));
//            v = Integer.parseInt(o.substring(5));
//        } catch (Exception e) {
//            e.printStackTrace();
//            App.SW_VERSION = 0;
//            v = 0;
//        }
//        //  Log.d("更新", "設備SwVersion: " + App.SW_VERSION);
//        return v;
//    }

    /**
     * R51V1.0-userdebug-20220314:154946
     */
    public static String checkSwVersion() {
        String sw_version = "";
        try {
            //R51V1.0-userdebug-20220314:154946
            sw_version = SysProp.get("ro.build.display.id", "ro.build.display.id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sw_version;
    }


    /**
     * v30 > 30
     * 去掉V
     */
//    public static int convertSwVersion(String sv) {
//
//        int v = 0;
//        try {
//            v = Integer.parseInt(sv.substring(1));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        App.NEW_SW_VERSION = v;
//        //    Log.d("更新", "新SwVersion: " + App.NEW_SW_VERSION);
//        return v;
//    }
//
//
//    public static int toCeilInt(double num) {
//        int i = 0;
//        try {
//            i = (int) Math.ceil(num);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return i;
//    }

//    public void mmkvUserProfileToViewModel(UserProfileViewModel userProfileViewModel, UserProfileEntity mmkvData) {
//        userProfileViewModel.userDisplayName.set(mmkvData.getUserName());
//    }
    public void mmkvDeviceSettingToViewModel(DeviceSettingViewModel deviceSettingViewModel, DeviceSettingBean mmkvData) {

        deviceSettingViewModel.territoryCode.set(mmkvData.getTerritoryCode());
        deviceSettingViewModel.modelCode.set(mmkvData.getModel_code());
        deviceSettingViewModel.typeCode.set(mmkvData.getType());
        deviceSettingViewModel.unitCode.set(mmkvData.getUnit_code());
        deviceSettingViewModel.time_unit.set(mmkvData.getTime_unit());
        deviceSettingViewModel.gsMode.set(mmkvData.isGsMode()); //false = GS MODE
        deviceSettingViewModel.protocol.setValue(mmkvData.getProtocol());
        deviceSettingViewModel.beep.setValue(mmkvData.getBeep_sound() == ON);
        deviceSettingViewModel.video.setValue(mmkvData.getVideo());
        //   deviceSettingViewModel.tv.setValue(mmkvData.getTv());
        deviceSettingViewModel.nfc.setValue(mmkvData.getNfc());

        deviceSettingViewModel.pauseMode.set(mmkvData.getPauseMode());

        deviceSettingViewModel.sleepMode.setValue(mmkvData.getSleep_mode());
        deviceSettingViewModel.sleepAfter.set(mmkvData.getSleepAfter());


        deviceSettingViewModel.autoPause.setValue(mmkvData.getAutoPause());

        deviceSettingViewModel.pauseAfter.set(mmkvData.getPauseAfter());

//        deviceSettingViewModel.useTimeLimit.set(mmkvData.getUseTimeLimit());
//        deviceSettingViewModel.isUseTimeLimit.set(mmkvData.getIsUseTimeLimit());
//
//        deviceSettingViewModel.limitCode.set(mmkvData.getLimitCode());
////        deviceSettingViewModel.useDistanceLimitMI.set(mmkvData.getUseDistanceLimit());
//        deviceSettingViewModel.useDistanceLimitKM.set(mmkvData.getUseDistanceLimit());
//        deviceSettingViewModel.useDistanceLimitKM.set(mmkvData.getUseDistanceLimit());

        //  deviceSettingViewModel.antPlusDeviceId.setValue(mmkvData.getAntPlusDeviceId());

        deviceSettingViewModel.modelName.set(ModeEnum.getDeviceFromType(mmkvData.getType()).getModelName());
        deviceSettingViewModel.typeName.set(ModeEnum.getDeviceFromType(mmkvData.getType()).getTypeName());

        deviceSettingViewModel.minSpeedIu.set(mmkvData.getMinSpeedIu());
        deviceSettingViewModel.minSpeedMu.set(mmkvData.getMinSpeedMu());

        deviceSettingViewModel.consoleSystem.set(mmkvData.getConsoleSystem());

        App.UNIT_E = deviceSettingViewModel.unitCode.get();
        App.TIME_UNIT_E = deviceSettingViewModel.time_unit.get();

        if (isTreadmill) {
            MIN_SPD_IU = mmkvData.getMinSpeedIu() > MIN_SPD_IU_MAX || mmkvData.getMinSpeedIu() < MIN_SPD_IU_MIN ? MIN_SPD_IU_MIN : mmkvData.getMinSpeedIu();
            MIN_SPD_MU = mmkvData.getMinSpeedMu() > MIN_SPD_MU_MAX || mmkvData.getMinSpeedMu() < MIN_SPD_MU_MIN ? MIN_SPD_MU_MIN : mmkvData.getMinSpeedMu();


//            OPT_SETTINGS.MAX_SPD_MU_MAX = mmkvData.getMaxSpeedMu();
//            OPT_SETTINGS.MAX_SPD_IU_MAX = mmkvData.getMaxSpeedIu();

            int iu = (int) mmkvData.getMaxSpeedIu();
            MAX_SPD_IU_MAX = (iu >= 150 && iu <= 155) ? iu : 150;

            int mu = (int) mmkvData.getMaxSpeedMu();
            MAX_SPD_MU_MAX = (mu >= 240 && mu <= 250) ? mu : 240;


            Log.d("MAXXXXXXXXX", "最小速度是: " + (MIN_SPD_IU / 10f)  +", "+ (MIN_SPD_MU/ 10f));
            Log.d("MAXXXXXXXXX", "最大速度是: " + (MAX_SPD_IU_MAX  / 10f)  +", "+ (MAX_SPD_MU_MAX  / 10f));

        }

//
//        Log.d("MMMMMMMMIIIIII", "mmkvDeviceSettingToViewModel: " + OPT_SETTINGS.MIN_SPD_MU);
//        Log.d("MMMMMMMMIIIIII", "mmkvDeviceSettingToViewModel: " + OPT_SETTINGS.MIN_SPD_IU);

        //   OPT_SETTINGS.MAX_INC_MAX = (int) (mmkvData.getMaxIncline() * 2);
//        OPT_SETTINGS.MAX_SPD_IU_MAX = (int) (mmkvData.getMaxSpeedIu() * 10);
//        OPT_SETTINGS.MAX_SPD_MU_MAX = (int) (mmkvData.getMaxSpeedMu() * 10);

        //     Log.d("SETTING_FILE", "####$$$%%%%%: " + OPT_SETTINGS.MAX_INC_MAX +","+ OPT_SETTINGS.MAX_SPD_IU_MAX +","+OPT_SETTINGS.MAX_SPD_MU_MAX);
    }

    public void deviceSettingViewModelToMMKV(DeviceSettingViewModel deviceSettingViewModel) {

        DeviceSettingBean mmkvData = getApp().getDeviceSettingBean();
        mmkvData.setModel_code(deviceSettingViewModel.modelCode.get());
        mmkvData.setTerritoryCode(deviceSettingViewModel.territoryCode.get());
        mmkvData.setType(deviceSettingViewModel.typeCode.get());
        mmkvData.setUnit_code(deviceSettingViewModel.unitCode.get());
        mmkvData.setTime_unit(deviceSettingViewModel.time_unit.get());
        mmkvData.setGsMode(deviceSettingViewModel.gsMode.get());
        mmkvData.setProtocol(deviceSettingViewModel.protocol.getValue());
        mmkvData.setBeep_sound(deviceSettingViewModel.beep.getValue() ? ON : OFF);

        mmkvData.setVideo(deviceSettingViewModel.video.getValue());
        //  mmkvData.setTv(deviceSettingViewModel.tv.getValue());
        //    mmkvData.setNfc(deviceSettingViewModel.nfc.getValue());
        mmkvData.setSleep_mode(deviceSettingViewModel.sleepMode.getValue());
        mmkvData.setPauseMode(deviceSettingViewModel.pauseMode.get());

        mmkvData.setAutoPause(deviceSettingViewModel.autoPause.getValue());

        mmkvData.setSleepAfter(deviceSettingViewModel.sleepAfter.get());

        mmkvData.setPauseAfter(deviceSettingViewModel.pauseAfter.get());


//        mmkvData.setLimitCode(deviceSettingViewModel.limitCode.get());
////        mmkvData.setUseDistanceLimit(deviceSettingViewModel.useDistanceLimitMI.get());
//        mmkvData.setUseDistanceLimit(deviceSettingViewModel.useDistanceLimitKM.get());

        //  mmkvData.setAntPlusDeviceId(deviceSettingViewModel.antPlusDeviceId.getValue());

        getApp().setDeviceSettingBean(mmkvData);

        App.UNIT_E = deviceSettingViewModel.unitCode.get();
        App.TIME_UNIT_E = deviceSettingViewModel.time_unit.get();
    }


    /**
     * @param context context
     * @param num     num
     * @param type    0 incline,1 speed,2 hr
     * @return b
     */
    public Bitmap getDiagramBitmap(Context context, String num, int type) {

        //   int maxHeight = type == 1 ? (UNIT_E == DeviceIntDef.IMPERIAL ? 400 : 600) : 400;
        int maxHeight = type == 1 ? (UNIT_E == DeviceIntDef.IMPERIAL ? 500 : 600) : 500;

        int width = 480;//圖寬度
//        int barWidth = width / chartCount;
//        int space = barWidth + 5;
        Bitmap bitmap = null;
        try {

            int[] numArray = Arrays.stream(num.split("#"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            int count = numArray.length;//數量
            int barWidth = width / count; //bar寬度
            int space = barWidth + 5;//空隙

            int maxNum = 10;
            if (type == 0) {
                maxNum = 15;
            } else if (type == 1) {
                if (isTreadmill) {
                    if (UNIT_E == IMPERIAL) {
                        maxNum = 150;
                    } else {
                        maxNum = 240;
                    }
                } else {
                    maxNum = 40;
                }
            }


            //  int maxNum = findMaxInt(numArray);
//            if (maxNum <= 1) {
//                maxNum = 10;
//            }

            double present = maxNum * 0.01;

            StringBuilder newNum = new StringBuilder();
            for (int dNum : numArray) {
                if (dNum == 0) {
                    newNum.append(24).append("#");
                } else {
                    newNum.append(Math.round((dNum * 4.8) / present)).append("#");
                }
            }

            newNum.setLength(newNum.length() - 1);

            int[] newNumArray = Arrays.stream(newNum.toString().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            final List<Drawable> segmentImage = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                PaintDrawable drawable = new PaintDrawable();
                drawable.getPaint().setColor(context.getColor(R.color.color5a7085));
                segmentImage.add(drawable);
            }

            final LayerDrawable layerDrawable = new LayerDrawable(segmentImage.toArray(new Drawable[0]));

            int n = 0;
            for (int i = 0; i < count; i++) {
                layerDrawable.setLayerGravity(i, Gravity.START | Gravity.BOTTOM);
                layerDrawable.setLayerSize(i, barWidth, newNumArray[i]);
                layerDrawable.setLayerInsetLeft(i, n);
                n += space;
            }

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), maxHeight, Bitmap.Config.ARGB_8888);
            layerDrawable.setBounds(0, 0, layerDrawable.getIntrinsicWidth(), maxHeight);
            layerDrawable.draw(new Canvas(bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


//    static SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
//    static SimpleDateFormat dateFormat12 = new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String updateTime() {
        //    return dateFormat24.format(Calendar.getInstance().getTime());

        if (DeviceIntDef.TF_24HR == App.TIME_UNIT_E) {
            //    return new SimpleDateFormat("HH:mm", Locale.US).format(Calendar.getInstance().getTime());
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());
        } else {
            //    return new SimpleDateFormat("hh:mm a", Locale.US).format(Calendar.getInstance().getTime());
            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
        }
    }

    public static int getAvatarSelectedFromTag(String tag, boolean isSelected) {
        int icon = 0;

        switch (tag) {
            case "avatar17":
                icon = isSelected ? R.drawable.btn_avatar_selected_1 : R.drawable.avatar_male_1_default;
                break;
            case "avatar18":
                icon = isSelected ? R.drawable.btn_avatar_selected_2 : R.drawable.avatar_male_2_default;
                break;
            case "avatar19":
                icon = isSelected ? R.drawable.btn_avatar_selected_3 : R.drawable.avatar_male_3_default;
                break;
            case "avatar20":
                icon = isSelected ? R.drawable.btn_avatar_selected_4 : R.drawable.avatar_male_4_default;
                break;
            case "avatar21":
                icon = isSelected ? R.drawable.btn_avatar_selected_5 : R.drawable.avatar_male_5_default;
                break;
            case "avatar22":
                icon = isSelected ? R.drawable.btn_avatar_selected_6 : R.drawable.avatar_male_6_default;
                break;
            case "avatar23":
                icon = isSelected ? R.drawable.btn_avatar_selected_7 : R.drawable.avatar_male_7_default;
                break;
            case "avatar24":
                icon = isSelected ? R.drawable.btn_avatar_selected_8 : R.drawable.avatar_male_8_default;
                break;
            case "avatar25":
                icon = isSelected ? R.drawable.btn_avatar_selected_9 : R.drawable.avatar_male_9_default;
                break;
            case "avatar26":
                icon = isSelected ? R.drawable.btn_avatar_selected_10 : R.drawable.avatar_male_10_default;
                break;
            case "avatar27":
                icon = isSelected ? R.drawable.btn_avatar_selected_11 : R.drawable.avatar_male_11_default;
                break;
            case "avatar28":
                icon = isSelected ? R.drawable.btn_avatar_selected_12 : R.drawable.avatar_male_12_default;
                break;
            case "avatar29":
                icon = isSelected ? R.drawable.btn_avatar_selected_13 : R.drawable.avatar_male_13_default;
                break;
            case "avatar30":
                icon = isSelected ? R.drawable.btn_avatar_selected_14 : R.drawable.avatar_male_14_default;
                break;
            case "avatar31":
                icon = isSelected ? R.drawable.btn_avatar_selected_15 : R.drawable.avatar_male_15_default;
                break;
            case "avatar32":
                icon = isSelected ? R.drawable.btn_avatar_selected_16 : R.drawable.avatar_male_16_default;
                break;
            case "avatar01":
                icon = isSelected ? R.drawable.btn_avatar_selected_17 : R.drawable.avatar_female_1_default;
                break;
            case "avatar02":
                icon = isSelected ? R.drawable.btn_avatar_selected_18 : R.drawable.avatar_female_2_default;
                break;
            case "avatar03":
                icon = isSelected ? R.drawable.btn_avatar_selected_19 : R.drawable.avatar_female_3_default;
                break;
            case "avatar04":
                icon = isSelected ? R.drawable.btn_avatar_selected_20 : R.drawable.avatar_female_4_default;
                break;
            case "avatar05":
                icon = isSelected ? R.drawable.btn_avatar_selected_21 : R.drawable.avatar_female_5_default;
                break;
            case "avatar06":
                icon = isSelected ? R.drawable.btn_avatar_selected_22 : R.drawable.avatar_female_6_default;
                break;
            case "avatar07":
                icon = isSelected ? R.drawable.btn_avatar_selected_23 : R.drawable.avatar_female_7_default;
                break;
            case "avatar08":
                icon = isSelected ? R.drawable.btn_avatar_selected_24 : R.drawable.avatar_female_8_default;
                break;
            case "avatar09":
                icon = isSelected ? R.drawable.btn_avatar_selected_25 : R.drawable.avatar_female_9_default;
                break;
            case "avatar10":
                icon = isSelected ? R.drawable.btn_avatar_selected_26 : R.drawable.avatar_female_10_default;
                break;
            case "avatar11":
                icon = isSelected ? R.drawable.btn_avatar_selected_27 : R.drawable.avatar_female_11_default;
                break;
            case "avatar12":
                icon = isSelected ? R.drawable.btn_avatar_selected_28 : R.drawable.avatar_female_12_default;
                break;
            case "avatar13":
                icon = isSelected ? R.drawable.btn_avatar_selected_29 : R.drawable.avatar_female_13_default;
                break;
            case "avatar14":
                icon = isSelected ? R.drawable.btn_avatar_selected_30 : R.drawable.avatar_female_14_default;
                break;
            case "avatar15":
                icon = isSelected ? R.drawable.btn_avatar_selected_31 : R.drawable.avatar_female_15_default;
                break;
            case "avatar16":
                icon = isSelected ? R.drawable.btn_avatar_selected_32 : R.drawable.avatar_female_16_default;
                break;
            case "default_avatar":
                icon = isSelected ? R.drawable.btn_avatar_selected_tag_32 : R.drawable.avatar_normal_1_default;
                break;
            case "default_avatar2":
                icon = isSelected ? R.drawable.btn_avatar_selected_tag_33 : R.drawable.avatar_normal_2_default;
                break;
            case "default_avatar3":
                icon = isSelected ? R.drawable.btn_avatar_selected_tag_34 : R.drawable.avatar_normal_3_default;
                break;
        }

        return icon;
    }

//    public static int getAvatarIconFromTag(int tag) {
//        int icon = 0;
//
//        switch (tag) {
//            case 0:
//                icon = R.drawable.avatar_male_1_default;
//                break;
//            case 1:
//                icon = R.drawable.avatar_male_2_default;
//                break;
//            case 2:
//                icon = R.drawable.avatar_male_3_default;
//                break;
//            case 3:
//                icon = R.drawable.avatar_male_4_default;
//                break;
//            case 4:
//                icon = R.drawable.avatar_male_5_default;
//                break;
//            case 5:
//                icon = R.drawable.avatar_male_6_default;
//                break;
//            case 6:
//                icon = R.drawable.avatar_male_7_default;
//                break;
//            case 7:
//                icon = R.drawable.avatar_male_8_default;
//                break;
//            case 8:
//                icon = R.drawable.avatar_male_9_default;
//                break;
//            case 9:
//                icon = R.drawable.avatar_male_10_default;
//                break;
//            case 10:
//                icon = R.drawable.avatar_male_11_default;
//                break;
//            case 11:
//                icon = R.drawable.avatar_male_12_default;
//                break;
//            case 12:
//                icon = R.drawable.avatar_male_13_default;
//                break;
//            case 13:
//                icon = R.drawable.avatar_male_14_default;
//                break;
//            case 14:
//                icon = R.drawable.avatar_male_15_default;
//                break;
//            case 15:
//                icon = R.drawable.avatar_male_16_default;
//                break;
//            case 16:
//                icon = R.drawable.avatar_female_1_default;
//                break;
//            case 17:
//                icon = R.drawable.avatar_female_2_default;
//                break;
//            case 18:
//                icon = R.drawable.avatar_female_3_default;
//                break;
//            case 19:
//                icon = R.drawable.avatar_female_4_default;
//                break;
//            case 20:
//                icon = R.drawable.avatar_female_5_default;
//                break;
//            case 21:
//                icon = R.drawable.avatar_female_6_default;
//                break;
//            case 22:
//                icon = R.drawable.avatar_female_7_default;
//                break;
//            case 23:
//                icon = R.drawable.avatar_female_8_default;
//                break;
//            case 24:
//                icon = R.drawable.avatar_female_9_default;
//                break;
//            case 25:
//                icon = R.drawable.avatar_female_10_default;
//                break;
//            case 26:
//                icon = R.drawable.avatar_female_11_default;
//                break;
//            case 27:
//                icon = R.drawable.avatar_female_12_default;
//                break;
//            case 28:
//                icon = R.drawable.avatar_female_13_default;
//                break;
//            case 29:
//                icon = R.drawable.avatar_female_14_default;
//                break;
//            case 30:
//                icon = R.drawable.avatar_female_15_default;
//                break;
//            case 31:
//                icon = R.drawable.avatar_female_16_default;
//                break;
//            case 32:
//                icon = R.drawable.avatar_normal_1_default;
//                break;
//            case 33:
//                icon = R.drawable.avatar_normal_2_default;
//                break;
//            case 34:
//                icon = R.drawable.avatar_normal_3_default;
//                break;
//        }
//
//        return icon;
//    }

    public static Drawable bytes2Drawable(byte[] b) {
        Bitmap bitmap = bytes2Bitmap(b);
        return bitmap2Drawable(bitmap);
    }

    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        @SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }


    public static byte[] drawableToByteArray(int res) {
        Drawable d = AppCompatResources.getDrawable(App.getApp(), res);
        if (d == null) return null;
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

//    public static int getAgeFormBirth(String birthdayStr) {
//
//        if (birthdayStr == null || "".equals(birthdayStr)) return -1;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
//        Date birthday = null;
//        try {
//            birthday = sdf.parse(birthdayStr);
//        } catch (ParseException | NullPointerException e) {
//            e.printStackTrace();
//        }
//        //Calendar：日歷
//        /*從Calendar對象中或得一個Date對象*/
//        Calendar cal = Calendar.getInstance();
//        /*把出生日期放入Calendar類型的bir對象中，進行Calendar和Date類型進行轉換*/
//        Calendar bir = Calendar.getInstance();
//        if (birthday != null) {
//            bir.setTime(birthday);
//        } else {
//            return -1;
//        }
//
//        /*如果生日大於當前日期，則拋出異常：出生日期不能大於當前日期*/
//        if (cal.before(birthday)) {
//            throw new IllegalArgumentException("The birthday is before Now,It‘s unbelievable");
//        }
//
//        /*取出當前年月日*/
//        int yearNow = cal.get(Calendar.YEAR);
//        int monthNow = cal.get(Calendar.MONTH);
//        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
//        /*取出出生年月日*/
//        int yearBirth = bir.get(Calendar.YEAR);
//        int monthBirth = bir.get(Calendar.MONTH);
//        int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
//        /*大概年齡是當前年減去出生年*/
//        int age = yearNow - yearBirth;
//        /*如果出當前月小與出生月，或者當前月等於出生月但是當前日小於出生日，那麽年齡age就減一歲*/
//        if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
//            age--;
//        }
//        return age;
//    }

    /**
     * 判斷是否數字
     *
     * @param str 字串
     * @return 是否數字
     */
    public static boolean chkNum(String str) {
        return str.chars().allMatch(Character::isDigit);
    }


    public String getSettingFile(String path) {
        try {

//            File file = new File(path + "/default_settings.json");
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            String fileData = new CommonUtils().readFromFileInputStream(fileInputStream);
            if (fileData.length() > 0) {
                return fileData;
            } else {
                return createSettingFile(path);
            }
        } catch (FileNotFoundException ex) {
            return createSettingFile(path);
            // return null;
        }
    }


    /**
     * 找不到設定檔，由本機自行產生
     */
    public String createSettingFile(String path) {
        try {
//            File root = new File(path);
//            if (!root.exists()) {
//                boolean b = root.mkdirs();
//            }

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/Dyaco/Spirit/");
            if (!root.exists()) {
                boolean b = root.mkdirs();
            }

            File file = new File(root, "default_settings.json");
            FileWriter writer = new FileWriter(file);
            ModeEnum modeEnum;
            if (getApp().getDeviceSettingBean().getModel_name() != null) {
                modeEnum = ModeEnum.getMode(getApp().getDeviceSettingBean().getModel_code());
            } else {
                modeEnum = ModeEnum.CT1000ENT;
            }

            new InitProduct(getApp()).setProductDefault(modeEnum, TERRITORY_GLOBAL);
            String settingData = new Gson().toJson(getApp().getDeviceSettingBean());

            //   LogS.printJson("SSSSSSS", settingData, "");

            writer.append(settingData);
            writer.flush();
            writer.close();
            return settingData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int getRandomValue(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }


    //WorkoutStatsFragment 的顯示,每秒呼叫
    public String getStatsValue(int tag, WorkoutViewModel workoutViewModel, boolean isTreadmill, boolean isTarget) {
        String value;
        if (isTreadmill) {
            switch (tag) {
                case STATS_SPEED:
                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentSpeed.get());
                    } else {
                        value = " / " + workoutViewModel.egymTargetSpeed.get();
                    }
                    break;
                case STATS_INCLINE:
                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentInclineValue.get());
                    } else {
                        value = " / " + workoutViewModel.egymTargetIncline.get();
                    }

//                    value = String.valueOf(workoutViewModel.currentInclineValue.get());
                    break;
                case STATS_ELAPSED_TIME:

                    if (!isTarget) {
                        value = formatSecToM(workoutViewModel.elapsedTimeShow.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_CURRENT_DISTANCE:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentDistance.get(), 2);
                    } else {
                        value = "";
                    }
                    break;
                case STATS_PACE:
                    if (!isTarget) {
                        value = formatSecToM((long) workoutViewModel.currentPace.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_ELEVATION_GAIN:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentElevationGain.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_REMAINING_TIME:

                    if (!isTarget) {
                        if (workoutViewModel.selWorkoutTime.get() == UNLIMITED) { //上數
                            value = ("- : -");
                        } else {

                            value = formatSecToM(workoutViewModel.remainingTimeShow.get());
                        }
                    } else {
                        value = "";
                    }


                    break;
                case STATS_DISTANCE_LEFT:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.distanceLeft.get(), 2);
                    } else {
                        value = "";
                    }


                    break;
//                case STATS_AVG_PACE:
                case STATS_POWER:

                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentPower.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_CALORIES:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentCalories.get());
                    } else {
                        value = "";
                    }


                    break;
                case STATS_HEART_RATE:

                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentHeartRate.get());
                    } else {
                        value = " / " + workoutViewModel.egymTargetHeartRate.get();
                    }

                    //    value = String.valueOf(workoutViewModel.currentHeartRate.get());
                    break;
                case STATS_METS:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentMets.get());
                    } else {
                        value = "";
                    }

                    break;

                case STATS_INTERVAL:
                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.egymCurrentSet.get() + 1);
                    } else {
                        value = " / " + workoutViewModel.egymTotalInterval.get();
                    }


                    break;
                case STATS_INTERVAL_DISTANCE:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.egymIntervalDistance.get(), 2);
                    } else {
                        value = " / " + workoutViewModel.egymTargetDistance.get();
                    }

                    //   value = formatDecimal(workoutViewModel.currentDistance.get(), 2);


                    break;
                default:
                    value = "";
            }
        } else {
            switch (tag) {
                case STATS_POWER:

                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentPower.get());
                    } else {
                        value = "";
                    }
                    break;
                case STATS_ELAPSED_TIME:

                    if (!isTarget) {
                        value = formatSecToM(workoutViewModel.elapsedTimeShow.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_SPEED:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentSpeed.get());//rpm
                    } else {
                        value = "";
                    }

                    break;
                case STATS_LEVEL:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentLevel.get(), 0);
                    } else {
                        value = " / " + workoutViewModel.egymTargetSpeed.get();
                    }

//                    value = formatDecimal(workoutViewModel.currentLevel.get(), 0);

                    break;
                case STATS_AVG_POWER:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.avgPower.get());
                    } else {
                        value = "";
                    }
                    break;
                case STATS_REMAINING_TIME:
                    if (!isTarget) {
                        value = formatSecToM(workoutViewModel.remainingTimeShow.get());
                    } else {
                        value = "";
                    }
                    break;
                case STATS_AVG_SPEED:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.avgSpeed.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_AVG_LEVEL:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.avgLevel.get());
                    } else {
                        value = "";
                    }
                    break;
                case STATS_CURRENT_DISTANCE:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentDistance.get(), 2);
                    } else {
                        value = "";
                    }

                    break;
                case STATS_CALORIES:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentCalories.get());
                    } else {
                        value = "";
                    }
                    break;
                case STATS_HEART_RATE:
                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.currentHeartRate.get());
                    } else {
                        value = " / " + workoutViewModel.egymTargetHeartRate.get();
                    }
                    //  value = String.valueOf(workoutViewModel.currentHeartRate.get());
                    break;
                case STATS_METS:
                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.currentMets.get());
                    } else {
                        value = "";
                    }

                    break;
                case STATS_INTERVAL:
                    if (!isTarget) {
                        value = String.valueOf(workoutViewModel.egymCurrentSet.get() + 1);
                    } else {
                        value = " / " + workoutViewModel.egymTotalInterval.get();
                    }
                    break;
                case STATS_INTERVAL_DISTANCE:

                    if (!isTarget) {
                        value = formatDecimal(workoutViewModel.egymIntervalDistance.get(), 2);
                    } else {
                        value = " / " + workoutViewModel.egymTargetDistance.get();
                    }

//                    value = formatDecimal(workoutViewModel.currentDistance.get(), 2);
                    break;


                case STATS_CADENCE:
                    if (!isTarget) {
                        if (MODE.isStepperType()) {
                            // TODO: PF  SPM
                            value = String.valueOf((workoutViewModel.currentRpm.get() * 2));
                        } else {
                            value = String.valueOf(workoutViewModel.currentRpm.get());
                        }

                    } else {
                        value = " / " + workoutViewModel.egymTargetIncline.get();
                    }
                    break;
                // TODO: PF
                case STATS_RESISTANCE:
                    value = String.valueOf(workoutViewModel.currentLevel.get());
                    break;

                case STATS_REMAINING_CALORIES:
                    value = formatDecimal(workoutViewModel.caloriesLeft.get());
                    break;

                case STATS_TOTAL_REVOLUTIONS:
                    value = String.valueOf(workoutViewModel.rpmCounter.get());
                    break;

                case STATS_TOTAL_STEPS:
                    value = formatDecimal(workoutViewModel.currentStep.get());
                    break;

                case STATS_REMAINING_STEPS:
                    value = formatDecimal(workoutViewModel.stepLeft.get());
                    break;


                default:
                    value = "";
            }
        }
        return value;
    }

    //點擊時呼叫
    //EGYM時 要判斷 TARGET 是否為 - , 設定成 灰色
    public void setStats(TextView view1, TextView view2, TextView view3, TextView view4, int tag, WorkoutViewModel workoutViewModel, Context context) {
        String view1Text = "";
        String view2Text = "";
        String view3Text = "";
        String view4Text = "";
        int gravity = Gravity.CENTER_HORIZONTAL;
        int width = WRAP_CONTENT;
        int marginStart = 0;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view1.getLayoutParams();
//        ViewGroup.LayoutParams params = view1.getLayoutParams();
        if (isTreadmill) {
            switch (tag) {
                case STATS_SPEED:
                    view1Text = String.valueOf(workoutViewModel.currentSpeed.get());
                    view2Text = context.getString(R.string.Speed);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mph) : context.getString(R.string.kph);

                    view4Text = " / " + workoutViewModel.egymTargetSpeed.get();
                    break;
                case STATS_INCLINE:
                    view1Text = String.valueOf(workoutViewModel.currentInclineValue.get());
                    view2Text = context.getString(R.string.Incline);
                    view3Text = "%";
                    view4Text = " / " + workoutViewModel.egymTargetIncline.get();
                    break;
                case STATS_ELAPSED_TIME:
                    view1Text = formatSecToM(workoutViewModel.elapsedTimeShow.get());
                    view2Text = context.getString(R.string.Time);
                    view3Text = "";
                    //不加的話，文字會左右擺動
                    gravity = Gravity.CENTER_VERTICAL;
                    width = 301;
                    if (view4 != null) {
                        marginStart = 100;
                    }
                    break;
                case STATS_CURRENT_DISTANCE:
                    view1Text = formatDecimal(workoutViewModel.currentDistance.get(), 2);
                    view2Text = context.getString(R.string.Distance);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);
                    break;
                case STATS_PACE:
                    view1Text = formatSecToM((long) workoutViewModel.currentPace.get());
                    view2Text = context.getString(R.string.Pace);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.min_per_mi) : context.getString(R.string.min_per_km);
                    //    width = 301;
                    //    gravity = Gravity.CENTER_VERTICAL;
                    break;
                case STATS_ELEVATION_GAIN:
                    view1Text = formatDecimal(workoutViewModel.currentElevationGain.get());
                    view2Text = context.getString(R.string.Elevation_Gain);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.ft) : context.getString(R.string.m);
                    break;
                case STATS_REMAINING_TIME: //time left
                    if (workoutViewModel.selWorkoutTime.get() == UNLIMITED) {
                        view1Text = ("- : -");
                        gravity = Gravity.CENTER_HORIZONTAL;
                    } else {
                        view1Text = formatSecToM(workoutViewModel.remainingTimeShow.get());
                        gravity = Gravity.CENTER_VERTICAL;
                    }


                    view2Text = context.getString(R.string.Time_Left);
                    view3Text = "";

                    if (view1Text.length() >= 6) {
                        width = 350;
                    } else {
                        width = 301;
                        if (view4 != null) {
                            marginStart = 100;
                        }
                    }
                    //    Log.d("KKKKEEEE", "setStats: " + view1Text.length());
                    break;
                case STATS_DISTANCE_LEFT:
                    view1Text = formatDecimal(workoutViewModel.distanceLeft.get(), 2);
                    view2Text = context.getString(R.string.Distance_Left);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);
                    break;
//                case STATS_AVG_PACE:
                case STATS_POWER:
                    view1Text = String.valueOf(workoutViewModel.currentPower.get());
                    view2Text = context.getString(R.string.Power);
                    view3Text = context.getString(R.string.W);
                    break;
                case STATS_CALORIES:
                    view1Text = formatDecimal(workoutViewModel.currentCalories.get());
                    view2Text = context.getString(R.string.Calories);
                    view3Text = context.getString(R.string.kcal);
                    break;
                case STATS_HEART_RATE:

                    view1Text = String.valueOf(workoutViewModel.currentHeartRate.get());

                    view4Text = " / " + workoutViewModel.egymTargetHeartRate.get();

                    //    view1Text = String.valueOf(workoutViewModel.currentHeartRate.get());
                    view2Text = context.getString(R.string.Heart_Rate);
                    view3Text = context.getString(R.string.BPM);
                    break;
                case STATS_METS:
                    view1Text = formatDecimal(workoutViewModel.currentMets.get());
                    view2Text = context.getString(R.string.METs);
                    view3Text = "";
                    break;
                case STATS_INTERVAL:

                    view1Text = String.valueOf(workoutViewModel.egymCurrentSet.get() + 1);

                    view2Text = context.getString(R.string.Interval) + " #";
                    view3Text = "";

                    view4Text = " / " + workoutViewModel.egymTotalInterval.get();
                    break;
                case STATS_INTERVAL_DISTANCE:
                    view1Text = formatDecimal(workoutViewModel.egymIntervalDistance.get(), 2);
                    view2Text = context.getString(R.string.interval_distance);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);

                    view4Text = " / " + workoutViewModel.egymTargetDistance.get();
                    break;
            }

        } else {
            switch (tag) {
                case STATS_POWER:
                    view1Text = String.valueOf(workoutViewModel.currentPower.get());
                    view2Text = context.getString(R.string.Power);
                    view3Text = context.getString(R.string.W);
                    break;
                case STATS_ELAPSED_TIME:
                    view1Text = formatSecToM(workoutViewModel.elapsedTimeShow.get());
                    view2Text = context.getString(R.string.Time);
                    view3Text = "";
                    gravity = Gravity.CENTER_VERTICAL;
                    width = 301;
                    if (view4 != null) {
                        marginStart = 100;
                    }
                    break;
                case STATS_SPEED:
                    view1Text = formatDecimal(workoutViewModel.currentSpeed.get());
                    view2Text = context.getString(R.string.Speed);
                    //    view3Text = context.getString(R.string.rpm);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mph) : context.getString(R.string.kph);
                    break;
                case STATS_LEVEL:
                    view1Text = formatDecimal(workoutViewModel.currentLevel.get(), 0);
                    view2Text = (workoutViewModel.selProgram != ProgramsEnum.EGYM) ? context.getString(R.string.Level) : context.getString(R.string.Resistance);
                    view3Text = "";
                    view4Text = " / " + workoutViewModel.egymTargetSpeed.get();
                    break;
                case STATS_AVG_POWER:
                    view1Text = formatDecimal(workoutViewModel.avgPower.get());
                    view2Text = context.getString(R.string.Avg_Power);
                    view3Text = "W";
                    break;
                case STATS_REMAINING_TIME: //time left
                    view1Text = formatSecToM(workoutViewModel.remainingTimeShow.get());
                    view2Text = context.getString(R.string.Remaining_Time);
                    view3Text = "";
                    gravity = Gravity.CENTER_VERTICAL;
                    width = 301;
                    if (view4 != null) {
                        marginStart = 100;
                    }
                    break;
                case STATS_AVG_SPEED:
                    view1Text = formatDecimal(workoutViewModel.avgSpeed.get());
                    view2Text = context.getString(R.string.Avg_Speed);
//                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mph) : context.getString(R.string.km_h);
                    break;
                case STATS_AVG_LEVEL:
                    view1Text = String.valueOf(workoutViewModel.avgLevel.get());
                    view2Text = context.getString(R.string.Avg_Level);
                    view3Text = "";
                    break;
                case STATS_CURRENT_DISTANCE:
                    view1Text = formatDecimal(workoutViewModel.currentDistance.get(), 2);
                    view2Text = context.getString(R.string.Distance);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);
                    break;
                case STATS_CALORIES:
                    view1Text = formatDecimal(workoutViewModel.currentCalories.get());
                    view2Text = context.getString(R.string.Calories);
                    view3Text = context.getString(R.string.kcal);
                    break;
                case STATS_HEART_RATE:
                    view1Text = String.valueOf(workoutViewModel.currentHeartRate.get());
                    view2Text = context.getString(R.string.Heart_Rate);
                    view3Text = context.getString(R.string.BPM);

                    view4Text = " / " + workoutViewModel.egymTargetHeartRate.get();
                    break;
                case STATS_METS:
                    view1Text = formatDecimal(workoutViewModel.currentMets.get());
                    view2Text = context.getString(R.string.METs);
                    view3Text = "";
                    break;
                case STATS_INTERVAL:
                    view1Text = String.valueOf(workoutViewModel.egymCurrentSet.get() + 1);
                    view2Text = context.getString(R.string.Interval) + " #";
                    view3Text = "";
                    view4Text = " / " + workoutViewModel.egymTotalInterval.get();
                    break;
                case STATS_INTERVAL_DISTANCE:
                    view1Text = formatDecimal(workoutViewModel.egymIntervalDistance.get(), 2);
                    view2Text = context.getString(R.string.interval_distance);
                    view3Text = UNIT_E == DeviceIntDef.IMPERIAL ? context.getString(R.string.mi) : context.getString(R.string.km);
                    view4Text = " / " + workoutViewModel.egymTargetDistance.get();
                    break;
                case STATS_CADENCE:
//                    view1Text = String.valueOf(workoutViewModel.currentRpm.get());
                    if (MODE.isStepperType()) {
                        view1Text = String.valueOf(workoutViewModel.currentRpm.get() * 2);
                        view2Text = context.getString(R.string.Cadence_SPM);
                    } else {
                        view1Text = String.valueOf(workoutViewModel.currentRpm.get());
                        view2Text = context.getString(R.string.Cadence_RPM);
                    }
                   // view3Text 單位
                    view4Text = " / " + workoutViewModel.egymTargetIncline.get();
                    break;
                // TODO: PF
                case STATS_RESISTANCE:
                    view1Text = String.valueOf(workoutViewModel.currentLevel.get());
                    view2Text = context.getString(R.string.Resistance);
                    view3Text = "";
                    break;

                case STATS_REMAINING_CALORIES:
                    view1Text = formatDecimal(workoutViewModel.caloriesLeft.get());
                    view2Text = context.getString(R.string.Remaining_Calories);
                    view3Text = "";
                    break;

                case STATS_TOTAL_REVOLUTIONS:
                    view1Text = String.valueOf(workoutViewModel.rpmCounter.get());
                    view2Text = context.getString(R.string.Total_Revolutions);
                    view3Text = "";
                    break;

                case STATS_TOTAL_STEPS:
                    view1Text = formatDecimal(workoutViewModel.currentStep.get());
                    view2Text = context.getString(R.string.Total_Steps);
                    view3Text = "";
                    break;

                case STATS_REMAINING_STEPS:
                    view1Text = formatDecimal(workoutViewModel.stepLeft.get());
                    view2Text = context.getString(R.string.Remaining_Steps);
                    view3Text = "";
                    break;

            }
        }
        view1.setText(view1Text);
        view2.setText(view2Text);
        view3.setText(view3Text);

        if (view4 != null) {
            //EGYM 才有
            view4.setText(view4Text);
        }

        view1.setGravity(gravity);
        params.setMarginStart(marginStart);
        params.width = width;
        view1.setLayoutParams(params);
    }


//    public static String getFormatTag() {
//        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
//        String className = stackTraceElement.getClassName();
//        return String.format(Locale.getDefault(), "%s.%s(L:%d)", className.substring(className.lastIndexOf(".") + 1),
//                stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
//    }

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }


    public static void restartApp(MainActivity mainActivity) {
        closePackage(getApp());

        ProcessPhoenix.triggerRebirth(getApp());
    }


//    public static ActivityManager activityManager = (ActivityManager) getApp().getSystemService(ACTIVITY_SERVICE);
//    //查看記憶體使用
//    public static void getMemory() {
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(memoryInfo);
//        long availMem = memoryInfo.availMem / (1024 * 1024); //可用內存
//        boolean isLowMem = memoryInfo.lowMemory; //是否達到最低內存
//        long threshold = memoryInfo.threshold / (1024 * 1024);//臨界值，到達這個值，進程就會被殺死
//        long totalMem = memoryInfo.totalMem / (1024 * 1024); //總內存
//        int size1 = (int) (memoryInfo.availMem >> 20);
//
////        Log.d("GET_MEMORY", "可用Memory：" + availMem + "MB ,是否達到最低Memory:" + isLowMem + " ,臨界值:" + threshold + "MB" + " ,總Memory:" + totalMem + "MB" + ",#已使用:" + (totalMem - availMem));
//        Log.d("GET_MEMORY", "總Memory：" + totalMem + "MB ,可用Memory:" + availMem + "MB" + ",#已使用:" + (totalMem - availMem));
//    }

    public static String getJson(Map<String, Object> map) {
        JSONObject param = new JSONObject();
        map.forEach((k, v) -> {
            try {
                param.put(k, v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return param.toString();
    }

//    public static String formatTime(double millis) {
//        long secs = (long) (millis / 1000);
//        long hour = secs / 3600;
//        if (hour >= 1) {
//            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, (secs % 3600) / 60, secs % 60);
//        } else {
//            return String.format(Locale.getDefault(), "%02d:%02d", (secs % 3600) / 60, secs % 60);
//        }
//    }
//
//    public void reBootDevice() {
//        PowerManager pManager = (PowerManager) getApp().getSystemService(Context.POWER_SERVICE); //重啟到fastboot模式
//        pManager.reboot("reason");
//    }

    /**
     * @param value 0:開啟狀態列、導航列，1:關閉狀態列、導航列
     */
    public void hideStatusBar(int value) {
        //1關閉狀態列
        try {
            Settings.System.putInt(getApp().getContentResolver(), "always_hide_bar", value);
            Intent intent = new Intent("action.ALWAYS_HIDE_STATUSBAR_CHENAGE");
            getApp().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setSleepMode(int time) {
        try {
            String t = (time == SCREEN_TIMEOUT_NEVER ? "不休眠" : String.valueOf(time));
            Settings.System.putInt(getApp().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
        } catch (Exception e) {
            showException(e);
        }
    }


    public static int getSleepTime() {
        //取得Console設定的休眠時間
        try {
            int timeout = Settings.System.getInt(getApp().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            Log.d("getSleepTime", "getSleepTime: " + timeout + ", " + (timeout / 1000f));
            return timeout;
        } catch (Settings.SettingNotFoundException e) {
            showException(e);
            return SCREEN_TIMEOUT_NEVER;
        }
    }

    public static boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getApp().getSystemService(Context.POWER_SERVICE);
        //true為開啟，false為關閉
        return powerManager.isInteractive();
    }


    public static void wakeUpScreen(MainActivity m) {
        Log.d("休眠", "wakeUpScreen 開啟螢幕");
        PowerManager pm = (PowerManager) getApp().getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock mWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, MainActivity.class.getSimpleName());

        mWakeLock.acquire(5000);

        //   new RxTimer().timer(500, number -> m.mRestartApp());
    }


    /**
     * 設置系統時間
     */
//    public static void setSystemTime(int hour, int minute) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        long timeMills = calendar.getTimeInMillis();
//        SystemClock.setCurrentTimeMillis(timeMills);
//    }

    /**
     * 設置系統日期
     */
    public static boolean setSystemDateAndTime(Calendar calendar) {
        try {
            long timeMills = calendar.getTimeInMillis();
            SystemClock.setCurrentTimeMillis(timeMills);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


//    /**
//     * 設置系統是否自動獲取時間
//     *
//     * @param context Activity's context.
//     * @param checked If checked > 0, it will auto set date.
//     */
//    public static void setAutoDateTime(Context context, int checked) {
//        Settings.Global.putInt(
//                context.getContentResolver(),
//                Settings.Global.AUTO_TIME, checked
//        );
//    }


//    /**
//     * 判斷系統是否自動獲取時間
//     *
//     * @param context Activity's context.
//     * @return If date is auto setting.
//     */
//    public static boolean checkDateAutoSet(Context context) {
//        boolean isAuto;
//        try {
//            isAuto = Settings.Global.getInt(
//                    context.getContentResolver(),
//                    Settings.Global.AUTO_TIME
//            ) > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            isAuto = false;
//        }
//
//        return isAuto;
//    }

//    private void setTimeZone(String timeZone) {
////        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
////        am.setTimeZone(timeZone);
////        Log.d("TIME_ZONE", "TIME_ZONE: " + TimeZone.getDefault().getID());
//    }


    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.setEnabled(true);
            view.getHitRect(bounds);

            bounds.top -= top;
            bounds.bottom += bottom;
            bounds.left -= left;
            bounds.right += right;

            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (view.getParent() instanceof View) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }


    /**
     * 獲取螢幕的亮度
     */
    public static int getScreenBrightness(Context context) {
        int currentBrightnessValue = 0;
        try {
            currentBrightnessValue = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentBrightnessValue;
    }


    /**
     * 設定當前Activity顯示時的亮度
     * 螢幕亮度最大數值一般為255，各款手機有所不同
     * screenBrightness 的取值範圍在[0,1]之間
     */
    public static void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness; //範圍 0 ~ 1.0
        activity.getWindow().setAttributes(lp);
        //     Log.d("亮度", "設定亮度: " + lp.screenBrightness);
    }


    /**
     * 儲存亮度設定狀態，退出app也能保持設定狀態
     */
    public static void saveBrightness(Context context, int brightness) {

        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
        //   Log.d("亮度", "儲存亮度: " + brightness);
    }

    //255
    public static int getMaxBrightness(Context context) {
        int brightnessSettingMaximumId = context.getResources().getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
        int brightnessSettingMaximum = context.getResources().getInteger(brightnessSettingMaximumId);

        //    Log.d("亮度", "initDisplayBrightnessBar: " + brightnessSettingMaximumId +","+ brightnessSettingMaximum);
        return brightnessSettingMaximum;
    }

    //10
    public static int getMinBrightness(Context context) {
        int brightnessSettingMinimumId = context.getResources().getIdentifier("config_screenBrightnessSettingMinimum", "integer", "android");
        int brightnessSettingMinimum = context.getResources().getInteger(brightnessSettingMinimumId);
        //   Log.d("亮度", "initDisplayBrightnessBar: " + brightnessSettingMinimumId +","+ brightnessSettingMinimum);
        return brightnessSettingMinimum;
    }

//    public static float getBrightness255(float value) {
//        return (value * 0.01f) * 255f;
//    }

    public static int getBrightnessPresent(float value) {
        return (Math.round((value / 255) * 100));
    }

    public static String loadAssetFile(Context context, String fileName, String defaultValue) {
        String result = defaultValue;
        InputStreamReader inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new InputStreamReader(context.getAssets().open(fileName));
            bufferedReader = new BufferedReader(inputStream);
            StringBuilder out = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                out.append(line);
                line = bufferedReader.readLine();
            }
            result = out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(inputStream).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Objects.requireNonNull(bufferedReader).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


//    public BadgeView getBadgerView(Context context, int badgeCount) {
//        return BadgeFactory.create(context)
//                .setTextColor(Color.WHITE)
//                .setWidthAndHeight(25, 25)
//                .setBadgeBackground(Color.RED)
//                .setTextSize(10)
//                .setBadgeGravity(Gravity.END | Gravity.TOP)
////                .setMargin(0, -15, -15, 0)
//                //    .setBadgeCount(badgeCount)
//                .setShape(BadgeView.SHAPE_CIRCLE);
//    }

    //1.2 > 2
    public static int getFloat(float value) {
        int tValue = 0;
        try {
            String s2 = String.valueOf(value);
            String[] a = s2.split("\\.");
            tValue = Integer.parseInt(a[1]);//取小數位
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tValue;
    }


    public static int secToTimeHour(long sec) {

        return (int) (TimeUnit.SECONDS.toHours(sec));
    }

    public static int secToTimeMin(long sec) {
        int timeLimitSec = (int) (TimeUnit.SECONDS.toSeconds(sec) - (TimeUnit.SECONDS.toMinutes(sec) * 60));

        return (int) (TimeUnit.SECONDS.toMinutes(sec) - (TimeUnit.SECONDS.toHours(sec) * 60));
    }

    public static int secToTimeSec(long sec) {

        return (int) (TimeUnit.SECONDS.toSeconds(sec) - (TimeUnit.SECONDS.toMinutes(sec) * 60));
    }


    //Current Android version data
//    public static String currentVersion() {
//        double release = Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)", "$1"));
//        String codeName = "Unsupported";//below Jelly Bean
//        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
//        else if (release < 5) codeName = "Kit Kat";
//        else if (release < 6) codeName = "Lollipop";
//        else if (release < 7) codeName = "Marshmallow";
//        else if (release < 8) codeName = "Nougat";
//        else if (release < 9) codeName = "Oreo";
//        else if (release < 10) codeName = "Pie";
//        else if (release >= 10)
//            codeName = "Android " + ((int) release);//since API 29 no more candy code names
//        //   return codeName + " v" + release + ", API Level: " + Build.VERSION.SDK_INT;
//        return codeName;
//    }


//    public void getAppUpdateJson(Context context) {
//        PackageManagerUtils packageManagerUtils = new PackageManagerUtils();
//        AppUpdateManager appUpdateManager = new AppUpdateManager(context, packageManagerUtils);
//        AppUpdateData appUpdateData = new AppUpdateData();
//        List<AppUpdateData.AppUpdateBean> appUpdateBeanList = new ArrayList<>();
//
//        String pathNetflix = "com.netflix.mediaclient_8.23.0_build_12_40200-40200_minAPI24(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathHulu = "com.hulu.plus_4.44.0_10029-google-4010029_minAPI21(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathESPN = "com.espn.score_center_6.16.0-8302_minAPI21(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathCNN = "com.cnn.mobile.android.phone_7.0.1-3185172_minAPI24(arm64-v8a,armeabi-v7a)(nodpi)_apkmirror.com.apk";
//        String pathBBC = "bbc.mobile.news.uk_6.2.24-6022402_minAPI21(arm64-v8a,armeabi,armeabi-v7a,mips,mips64,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathFacebook = "com.facebook.katana_361.0.0.0.66-312207028_minAPI29(arm64-v8a)(420,480,360,400dpi)_apkmirror.com.apk";
//        String pathInstagram = "com.instagram.android_229.0.0.0.43-362901460_minAPI23(arm64-v8a)(nodpi)_apkmirror.com.apk";
//        String pathTwitter = "com.twitter.android_9.37.0-release.0-29370000_minAPI21(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathSpotify = "Spotify_8.7.16.1354_apkcombo.com.apk";
//        String pathIHeart = "com.clearchannel.iheartradio.controller_10.13.0-710130002_minAPI21(arm64-v8a,armeabi-v7a,mips,x86,x86_64)(nodpi)_apkmirror.com.apk";
//        String pathWeather = "Weather app_5.9_apkcombo.com.apk";
//        String pathStudio = "Studio-1.0.0-release .apk";
//
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_NETFLIX), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_NETFLIX), PACKAGE_NAME_NETFLIX, "", BuildConfig.UPDATE_URL + "APPs/" + pathNetflix, "NO", pathNetflix));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_HULU), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_HULU), PACKAGE_NAME_HULU, "", BuildConfig.UPDATE_URL + "APPs/" + pathHulu, "NO", pathHulu));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_ESPN), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_ESPN), PACKAGE_NAME_ESPN, "", BuildConfig.UPDATE_URL + "APPs/" + pathESPN, "NO", pathESPN));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_CNN), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_CNN), PACKAGE_NAME_CNN, "", BuildConfig.UPDATE_URL + "APPs/" + pathCNN, "NO", pathCNN));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_BBC), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_BBC), PACKAGE_NAME_BBC, "", BuildConfig.UPDATE_URL + "APPs/" + pathBBC, "NO", pathBBC));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_FACEBOOK), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_FACEBOOK), PACKAGE_NAME_FACEBOOK, "", BuildConfig.UPDATE_URL + "APPs/" + pathFacebook, "NO", pathFacebook));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_INSTAGRAM), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_INSTAGRAM), PACKAGE_NAME_INSTAGRAM, "", BuildConfig.UPDATE_URL + "APPs/" + pathInstagram, "NO", pathInstagram));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_TWITTER), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_TWITTER), PACKAGE_NAME_TWITTER, "", BuildConfig.UPDATE_URL + "APPs/" + pathTwitter, "NO", pathTwitter));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_SPOTIFY), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_SPOTIFY), PACKAGE_NAME_SPOTIFY, "", BuildConfig.UPDATE_URL + "APPs/" + pathSpotify, "NO", pathSpotify));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_IHEART), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_IHEART), PACKAGE_NAME_IHEART, "", BuildConfig.UPDATE_URL + "APPs/" + pathIHeart, "NO", pathIHeart));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_WEATHER), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_WEATHER), PACKAGE_NAME_WEATHER, "", BuildConfig.UPDATE_URL + "APPs/" + pathWeather, "NO", pathWeather));
//        appUpdateBeanList.add(new AppUpdateData.AppUpdateBean(appUpdateManager.getPackageInfoVersionName(PACKAGE_NAME_STUDIO), appUpdateManager.getPackageInfoVersionCode(PACKAGE_NAME_STUDIO), PACKAGE_NAME_STUDIO, "", BuildConfig.UPDATE_URL + "APPs/" + pathStudio, "NO", pathStudio));
//
//        appUpdateData.setAppUpdateBeans(appUpdateBeanList);
//        String updateAppJson = new Gson().toJson(appUpdateData);
//        Log.d("APP_UPDATE_JSON", updateAppJson);
//
//        try {
//            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CoreStar/");
//            boolean isMkDirs = true;
//            if (!root.exists()) {
//                isMkDirs = root.mkdirs();
//            }
//
//            if (isMkDirs) {
//                File file = new File(root, "app_update.json");
//                FileWriter writer = new FileWriter(file);
//
//                writer.append(updateAppJson);
//                writer.flush();
//                writer.close();
//            } else {
//                Log.d("APP_UPDATE_JSON", "1建立 APP_UPDATE_JSON 失敗: ");
//            }
//        } catch (IOException e) {
//            Log.d("APP_UPDATE_JSON", "2建立 APP_UPDATE_JSON 失敗: " + e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

    /**
     * 獲取apk的包名
     */
    public String getApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, 0);
        if (info != null) {
            return info.packageName;
        } else {
            return null;
        }
    }

//    public boolean install(Context context, String apkPath) {
//
////        if (!isEmulator && isTreadmill) {
////          //  ((MainActivity)context).uartConsole.setDevMainMode(RESET);
////            getDeviceSpiritC().setMainModeTreadmill(RESET); //停止LWR計數, 以免發time out錯誤
////        }
//
//        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
//        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
//        String pkgName = getApkPackageName(context, apkPath);
//
//        if (pkgName == null) {
//            return false;
//        }
//        params.setAppPackageName(pkgName);
//        try {
//            Method allowDowngrade = PackageInstaller.SessionParams.class.getMethod("setAllowDowngrade", boolean.class);
//            allowDowngrade.setAccessible(true);
//            allowDowngrade.invoke(params, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        OutputStream os = null;
//        InputStream is = null;
//        try {
//            int sessionId = packageInstaller.createSession(params);
//            PackageInstaller.Session session = packageInstaller.openSession(sessionId);
//            os = session.openWrite(pkgName, 0, -1);
//            is = new FileInputStream(apkPath);
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = is.read(buffer)) != -1) {
//                os.write(buffer, 0, len);
//            }
//            //根據需要調用，用來確保字節已保留到磁盤
//            session.fsync(os);
//            os.close();
//            os = null;
//            is.close();
//            is = null;
//
//            //     session.commit(PendingIntent.getBroadcast(context, sessionId, new Intent(Intent.ACTION_MAIN), 0).getIntentSender());
//
//            String ACTION_INSTALL_COMPLETE = "cm.android.intent.action.INSTALL_COMPLETE";
//            PendingIntent broadCastTest = PendingIntent.getBroadcast(
//                    context,
//                    sessionId,
//                    new Intent(ACTION_INSTALL_COMPLETE),
//                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
//            session.commit(broadCastTest.getIntentSender());
//            session.close();
//        } catch (Exception e) {
//            return false;
//        } finally {
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return true;
//    }


    public void install2(Context context, String apkPath, InstallCallback installCallback) {
        new Thread(() -> {
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            String pkgName = getApkPackageName(context, apkPath);

            if (pkgName == null) {
                installCallback.onFail("APK Not Found");
                return;
            }
            params.setAppPackageName(pkgName);
            try {
                Method allowDowngrade = PackageInstaller.SessionParams.class.getMethod("setAllowDowngrade", boolean.class);
                allowDowngrade.setAccessible(true);
                allowDowngrade.invoke(params, true);
            } catch (Exception e) {
                installCallback.onFail("1:" + e.getLocalizedMessage());
                return;
            }
            OutputStream os = null;
            InputStream is = null;
            try {
                int sessionId = packageInstaller.createSession(params);
                PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                os = session.openWrite(pkgName, 0, -1);
                is = new FileInputStream(apkPath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                //根據需要調用，用來確保字節已保留到磁盤
                session.fsync(os);
                os.close();
                os = null;
                is.close();
                is = null;

                //     session.commit(PendingIntent.getBroadcast(context, sessionId, new Intent(Intent.ACTION_MAIN), 0).getIntentSender());

                String ACTION_INSTALL_COMPLETE = "cm.android.intent.action.INSTALL_COMPLETE";
                PendingIntent broadCastTest = PendingIntent.getBroadcast(
                        context,
                        sessionId,
                        new Intent(ACTION_INSTALL_COMPLETE),
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                session.commit(broadCastTest.getIntentSender());
                session.close();
                installCallback.onSuccess();
            } catch (Exception e) {
                installCallback.onFail("2:" + e.getLocalizedMessage());
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.getLocalizedMessage();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.getLocalizedMessage();
                    }
                }
            }
        }).start();

    }


    public static long strDateTimeToMillis(String dateString) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date mDate = sdf.parse(dateString);
            if (mDate != null) {
                timeInMilliseconds = mDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    public static void ignoringExc(String tag, RunnableExc r) {
        try {
            r.run();
        } catch (Exception e) {
            Log.d(tag, "ignoringExc: " + e.getLocalizedMessage());
        }
    }

    public static void iExc(RunnableExc r) {
        try {
            r.run();
        } catch (Exception e) {
            Log.d("iExc", "ignoringExc: " + e.getLocalizedMessage());
        }
    }


    // 本次開機經過的時間
//    public static long getUptime() {
//        return SystemClock.uptimeMillis() / 1000;
//    }


//    public static void getTTT() {
//        long startTime = System.currentTimeMillis();
//        MMKV.defaultMMKV().encode("startTime", startTime);
//    }
//
//    public static void checkBootTime(long netTime) {
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
//        // 获取开机时长
//        long runTime = SystemClock.elapsedRealtime();
//        // 获取开机时间戳
//        //  long bootTime = SharePreferenceUtils.newInstance(getApp()).getLong("startTime", 0);
//        long bootTime = MMKV.defaultMMKV().decodeLong("startTime", 0);
//        // 获取新的开机时间戳
//        long newBootTime = netTime - runTime;
//        if (newBootTime - bootTime > 60 * 1000) {
//            bootTime = newBootTime;
//        }
//
//        Log.d("@@@@@@@DDDDD", "222222checkBootTime: " + sdf.format(bootTime));
//        // 此处可以上报校验后的开机时间戳bootTime
//    }
//
//    public static void getNetTime() {
//        new Thread(() -> {
//            long standardTime;
//            try {
//                URL url = new URL("https://github.com/");
//                URLConnection uc = url.openConnection();
//                uc.connect();
//                standardTime = uc.getDate();
//                checkBootTime(standardTime);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }


//    private void getTopApp(Context context) {
//        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        if (m != null) {
//            long now = System.currentTimeMillis();
//            //获取10分钟之内的应用数据
//            List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 600 * 1000, now);
//            String topActivity = "";
//            //取得最近运行的一个app，即当前运行的app
//            if ((stats != null) && (!stats.isEmpty())) {
//                int j = 0;
//                for (int i = 0; i < stats.size(); i++) {
//                    if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
//                        j = i;
//                    }
//                }
//                topActivity = stats.get(j).getPackageName();
//            }
//            Log.d("PPPPPPP", "getTopApp: " + topActivity);
//        }
//    }
//
//
//    public static void getUsage(Context context) {
//        new Thread(() -> {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
//            long startTime = new GregorianCalendar(2022, 4, 8, 9, 1, 1).getTimeInMillis();
//            //   long endTime = new GregorianCalendar(2022, 10, 17,17,1,1).getTimeInMillis();
//            long endTime = new GregorianCalendar().getTimeInMillis();
//
////        Calendar beginCal = Calendar.getInstance();
////        beginCal.add(Calendar.DATE, -3); // get yesterdays data
////        Calendar  endCal = Calendar.getInstance();
//
////        Log.d("@@##@@##", "xxxxx: " + sdf.format(startTime));
////        Log.d("@@##@@##", "xxxxx: " + sdf.format(endTime));
//
//            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
//            Log.d("@@##@@##", "size: " + queryUsageStats.size());
//            for (UsageStats us : queryUsageStats) {
//                if (us.getPackageName().equalsIgnoreCase(getApp().getPackageName())) {
////                Log.d("@@##@@## 使用秒數", us.getPackageName() + " = " + (us.getTotalTimeInForeground() / 1000));
//                    Log.d("@@##@@## 使用時間", us.getPackageName() + " = " + DateUtils.formatElapsedTime(us.getTotalTimeInForeground() / 1000));
//                    Log.d("@@##@@##", us.getPackageName() + " = " + (sdf.format(us.getFirstTimeStamp())));
//                    Log.d("@@##@@##", us.getPackageName() + " = " + (sdf.format(us.getLastTimeStamp())));
////                Log.d("@@##@@##", us.getPackageName() + " = #" + (sdf.format(us.getLastTimeUsed())));
////                Log.d("@@##@@##", us.getPackageName() + " =@ " + (sdf.format(us.getLastTimeVisible())));
//                }
//            }
//        }).start();
//    }



    public static int getDeviceStrName(int deviceTypeCode) {
        int s;
        switch (deviceTypeCode) {
            case DEVICE_TYPE_TREADMILL:
                s = R.string.treadmill;
                break;
            case DEVICE_TYPE_ELLIPTICAL:
                s = R.string.elliptical;
                break;
            case DEVICE_TYPE_UPRIGHT_BIKE:
                s = R.string.upright_bike;
                break;
            case DEVICE_TYPE_RECUMBENT_BIKE:
                s = R.string.recumbent_bike;
                break;
            case DEVICE_TYPE_UBE:
                s = R.string.UBE;
                break;
            case DEVICE_TYPE_STEPPER:
                s = R.string.Stepper;
                break;
            default:
                s = R.string.none;
        }

        return s;
    }


//    private int[] reverseArray(int[] array) {
//        int[] new_array = new int[array.length];
//        for (int i = 0; i < array.length; i++) {
//// 反轉後陣列的第一個元素等於源陣列的最後一個元素：
//            new_array[i] = array[array.length - i - 1];
//        }
//        return new_array;
//    }


//    public static void getMenuWidth() {
//
////        String a1 = getApp().getString(R.string.Hide_Panels);//149 //152
////        String a2 = getApp().getString(R.string.Go_Back);//103 //108
//
//        Paint tPaint = new Paint();
//        Typeface typeface = ResourcesCompat.getFont(getApp(), R.font.inter_bold);
//        tPaint.setTypeface(typeface);
//        tPaint.setTextSize(24);
//        int width1 = Math.round(tPaint.measureText(getApp().getString(R.string.Hide_Panels)));
//        int width2 = Math.round(tPaint.measureText(getApp().getString(R.string.Pause_Workout)));
//        int width3 = Math.round(tPaint.measureText(getApp().getString(R.string.Resume_Workout)));
//
//        int length = Math.max((Math.max(width1, width2)), width3);
////        int length = Math.max(width1, width2);
//        MainActivity.MENU_MAX_WIDTH = Math.round(length) + 160;
//
//        Log.d("FFFFFFFFEEEE", "Menu最大寬度: " + MainActivity.MENU_MAX_WIDTH);
//    }

//    public static void getUsage2(Context context) {
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
//        long startTime = new GregorianCalendar(2022, 4, 8,9,1,1).getTimeInMillis();
//           long endTime = new GregorianCalendar(2022, 10, 17,17,1,1).getTimeInMillis();
//
//        UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
//        Map<String,UsageStats> queryUsageStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
//
//
//           for (String key : queryUsageStats.keySet()) {
//
//               UsageStats value = queryUsageStats.get(key);
//               Log.d("BBBBBBB", "Key = " + key + ", Value = " + sdf.format(value.getFirstTimeStamp()));
//
//             }
//
//    }

    /**
     * 是否真的移動過
     */
    public static boolean isRealMove(PointF downPoint, MotionEvent event) {
        float dx = event.getRawX() - downPoint.x;
        float dy = event.getRawY() - downPoint.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance > 40; // 閾值根據你的需求調整
    }


//    public static boolean isRealMove(PointF downPoint, MotionEvent event) {
//        float dx = Math.abs(event.getX() - downPoint.x);
//        float dy = Math.abs(event.getY() - downPoint.y);
//        return dx > 15 || dy > 15; // 可視需求調整閾值
//    }



    /**
     * 0.85 ~ 1.3
     */
    public static void changeSystemFontSize(Context context, float size) {
        Settings.System.putFloat(context.getContentResolver(), Settings.System.FONT_SCALE, size);
    }

    public static float getSystemFontSize(Context context) {
        return android.provider.Settings.System.getFloat(context.getContentResolver(),
                android.provider.Settings.System.FONT_SCALE, 1f);
    }


    //        XE395ENT-2233,2
//        FNT-D3,1
//        DS-715,1
//        JLab GO Air Pop,1
//        vívosmart 4,2

    /**
     * Console 藍芽已配對裝置
     */
    @SuppressLint("MissingPermission")
    public void consolePairedDevices(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) return;
        Set<BluetoothDevice> consolePairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : consolePairedDevices) {
            unpairDevice(device);
            //      dL.add(device);
        }
    }

    /**
     * 解除 Console 藍芽已配對裝置
     */
    public void unpairDevice(BluetoothDevice device) {

        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 維持住app font size 不要被系統font size影響
     *
     * @param context
     * @return
     */
    public static Context updateResourceFontSize(Context context) {

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = 1.0f;

        return context.createConfigurationContext(configuration);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        Resources resources = newBase.getResources();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(Locale.GERMAN);
//        Context context =  newBase.createConfigurationContext(configuration);
//        super.attachBaseContext(context);
//    }


    /**
     * 判斷Custom的20個level是否都不為0,都不為0代表都有選到
     *
     * @param selectTextViews ex. 20個bar的level值
     * @return 如果level都有選到不為0回傳true
     */
    public static boolean isCustomAllBarSetValue(List<String> selectTextViews) {

        boolean[] isSelects = new boolean[selectTextViews.size()];

        for (int i = 0; i < selectTextViews.size(); i++) {
            String text = selectTextViews.get(i);
            int value = Integer.parseInt(text);
            isSelects[i] = value != 0;
        }

        return isBooleanArrayAllTrue(isSelects);
    }


    /**
     * 判斷Boolean陣列裡面的值是否都為ture
     */
    public static boolean isBooleanArrayAllTrue(boolean[] booleans) {
        boolean isTrue = true;
        for (boolean b : booleans) {
            if (b) {
                continue;
            }
            isTrue = false;
            break;
        }
        return isTrue;
    }


    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static float byte2Mb(long size) {
        float MEGABYTE = 1024f * 1024f;
        return size / MEGABYTE;
    }


    public Bitmap addWatermark(Bitmap source, Bitmap watermark, float ratio) {
        Canvas canvas;
        Paint paint;
        Bitmap bmp;
        Matrix matrix;
        RectF r;

        int width, height;
        float scale;

        width = source.getWidth();
        height = source.getHeight();

        // Create the new bitmap
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Copy the original bitmap into the new one
        canvas = new Canvas(bmp);
        canvas.drawBitmap(source, 0, 0, paint);

        // Scale the watermark to be approximately to the ratio given of the source image height
        scale = (float) (((float) height * ratio) / (float) watermark.getHeight());

        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);

        // Move the watermark to the bottom right corner
        matrix.postTranslate(width - r.width(), height - r.height());

        // Draw the watermark
        canvas.drawBitmap(watermark, matrix, paint);

        return bmp;
    }


    //副檔名
    public static String getFileExtension(String fileName) {

        String fe = "";
        final Pattern PATTERN = Pattern.compile("(.*)\\.(.*)");
        Matcher m = PATTERN.matcher(fileName);
        if (m.find()) {
            fe = m.group(2);
        }
        return fe;
    }

    public static boolean isJPGs(String fileName) {
        return ("jpg".equalsIgnoreCase(CommonUtils.getFileExtension(fileName)) || "jpeg".equalsIgnoreCase(CommonUtils.getFileExtension(fileName)));
    }

    public static boolean isPNGs(String fileName) {
        return ("png".equalsIgnoreCase(CommonUtils.getFileExtension(fileName)));
    }

    public static void showException(Exception e) {
        Log.d("Exception", "showException: " + e.getLocalizedMessage());
    }

    public static Drawable resToDrawable(int res) {

        return ContextCompat.getDrawable(getApp(), res);
    }


    public static void hideSoftKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception x) {
            return false;
        }
        return true;
    }


    public static Observable<byte[]> downloadImage(String urlString) {
        return Observable.fromCallable(() -> {
                    byte[] imageBytes = null;

                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream input = connection.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        imageBytes = outputStream.toByteArray();
                        outputStream.close();
                        input.close();

                    } catch (Exception e) {
                        // Handle the exception appropriately (e.g., throw an error or log the error)
                        throw new Exception("Error downloading image: " + e.getMessage());
                    }

                    return imageBytes;
                })
                .subscribeOn(Schedulers.io()) // Perform the download on a background thread
                .observeOn(AndroidSchedulers.mainThread()); // Switch to the main thread for UI updates
    }


    // dp 轉 px
    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public static int getAgeFormBirth(String birthdayStr) {

        if (birthdayStr == null || birthdayStr.isEmpty()) return -1;

        // 定義日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 解析生日字符串
        LocalDate birthDate = LocalDate.parse(birthdayStr, formatter);

        // 獲取當前日期
        LocalDate currentDate = LocalDate.now();

        // 計算年齡
        return Period.between(birthDate, currentDate).getYears();
    }


    public static byte[] saveImageToMemory(ResponseBody responseBody) {
        try (InputStream inputStream = responseBody.byteStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray(); // 返回字節數組
        } catch (Exception e) {
            showException(e);
            return null;
        }
    }

    // 調整顏色透明度
    public static int adjustAlpha(float alpha) {
        int alphaValue = Math.round(Color.alpha(Color.WHITE) * alpha);
        return Color.argb(alphaValue, Color.red(Color.WHITE), Color.green(Color.WHITE), Color.blue(Color.WHITE));
    }


    //list 有無東西
    public static boolean hasValueAt(List<byte[]> list, int position) {
        return position >= 0 && position < list.size() && list.get(position) != null;
    }


    public int logoutViewChk(boolean isUs, int appStatus, int consoleSystem) {
        int visibility = View.GONE;
        if (isUs) {
            if (consoleSystem == CONSOLE_SYSTEM_EGYM) {
                if (appStatus == STATUS_IDLE || appStatus == STATUS_SUMMARY) {
                    visibility = View.VISIBLE;
                }
            }
        } else {
            if (appStatus == STATUS_IDLE || appStatus == STATUS_SUMMARY) {
                visibility = View.VISIBLE;
            }
        }

        return visibility;
    }

    public float settingMarginChk(boolean isUs, int appStatus, int consoleSystem) {
        float x = 4f;
        if (appStatus == STATUS_LOGIN_PAGE) {
            x = 20f;
        } else {
            if (isUs) {
                if (consoleSystem == CONSOLE_SYSTEM_SPIRIT) {
                    x = 20f;
                }
            }
        }
        return x;
    }


    public static void setTextWithFade(TextView textView, String newText) {
        // 创建渐隐动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        fadeOut.setDuration(200); // 渐隐动画时间

        // 添加动画监听
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 动画结束后更新文字
                textView.setText(newText);

                // 创建渐显动画
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
                fadeIn.setDuration(200); // 渐显动画时间
                fadeIn.start();
            }
        });

        fadeOut.start();
    }


    public static void animateMarginStart(ConstraintLayout layout, MaterialButton view, int fromMargin, int toMargin, long duration) {
        // 將 dp 轉為 px（設備像素）
        float density = getApp().getResources().getDisplayMetrics().density;
        int fromMarginPx = (int) (fromMargin * density);
        int toMarginPx = (int) (toMargin * density);

        // 設定初始狀態：先將按鈕設為透明，再設定可見，這樣才能看見漸變效果
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        // 建立 ValueAnimator 處理 margin 動畫
        ValueAnimator marginAnimator = ValueAnimator.ofInt(fromMarginPx, toMarginPx);
        marginAnimator.setDuration(duration);
        marginAnimator.addUpdateListener(animation -> {
            int currentMargin = (int) animation.getAnimatedValue();
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            constraintSet.setMargin(view.getId(), ConstraintSet.START, currentMargin);
            constraintSet.applyTo(layout);
        });
        marginAnimator.start();

        // 使用 ViewPropertyAnimator 來執行 alpha 動畫，由 0 漸變至 1
        view.animate().alpha(1f).setDuration(600).start();
    }


//    public static void animateMarginStart(ConstraintLayout layout, MaterialButton view, int fromMargin, int toMargin, long duration) {
//        // 将 dp 转为 px（设备像素）
//        float density = getApp().getResources().getDisplayMetrics().density;
//        int fromMarginPx = (int) (fromMargin * density);
//        int toMarginPx = (int) (toMargin * density);
//
//        // 创建 ValueAnimator 从起始值到结束值
//        ValueAnimator animator = ValueAnimator.ofInt(fromMarginPx, toMarginPx);
//        animator.setDuration(duration);
//
//        // 动画更新监听
//        animator.addUpdateListener(animation -> {
//            int currentMargin = (int) animation.getAnimatedValue();
//
//            // 更新 ConstraintSet 的 Margin 值
//            ConstraintSet constraintSet = new ConstraintSet();
//            constraintSet.clone(layout);
//            constraintSet.setMargin(view.getId(), ConstraintSet.START, currentMargin);
//            constraintSet.applyTo(layout);
//        });
//
//        // 启动动画
//        animator.start();
//    }


    public static boolean isValidIdL(Long planId) {
        return planId != null && planId != 0;
    }

    public static boolean isValidIdI(Integer planId) {
        return planId != null && planId != 0;
    }


    public static void setAutoSizeText(final TextView textView, int maxSize, int minSize) {
//        final int minSize = 12;
//        final int maxSize = 23;
        TextViewCompat.setAutoSizeTextTypeWithDefaults(textView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, maxSize);
//        textView.setText("");
//        textView.setText(content);
        textView.post(new Runnable() {
            @Override
            public void run() {
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, minSize, maxSize, 1, TypedValue.COMPLEX_UNIT_SP);
            }
        });
    }


    // 如果Duration是0 就給 30分鐘
    public static Integer chkDuration(Integer duration) {
        return duration == SYMBOL_DURATION ? ZERO_DURATION_DURATION : duration;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("-?\\d+(\\.\\d+)?");  // 支援正負數及小數
    }

    public static double formatNum(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.DOWN); // 截斷多餘小數，不進位
        return bd.doubleValue();
    }

    public static String reverseUidHex(String uid) {
        if (uid == null || uid.length() % 2 != 0) {
            return "";
        }

        StringBuilder reversedUid = new StringBuilder();

        for (int i = uid.length(); i > 0; i -= 2) {
            reversedUid.append(uid, i - 2, i);
        }

        return reversedUid.toString().toUpperCase();
    }
}