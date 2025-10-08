package com.dyaco.spirit_commercial.support.utils;

import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;

import com.dyaco.spirit_commercial.support.custom_view.banner.util.LogUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 *
 */
public class DateUtils {
    /**
     * yyyy格式
     */
    public static final String sdfyyyy = "yyyy";
    /**
     * yyyy-MM-dd格式
     */
    public static final String sdfyyyy_MM_dd = "yyyy-MM-dd";
    /**
     * yyyy年MM月dd日格式
     */
    public static final String sdfyyyyCMMCddC = "yyyy年MM月dd日";
    /**
     * yyyyMMdd格式
     */
    public static final String sdfyyyyMMdd = "yyyyMMdd";
    /**
     * yyyy-MM-dd HH:mm:ss格式
     */
    public static final String sdfyyyy_MM_ddHHmmss = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd HH:mm格式
     */
    public static final String sdfyyyy_MM_ddHHmm = "yyyy-MM-dd HH:mm";
    /**
     * yyyy年MM月dd日 HH:mm:ss格式
     */
    public static final String sdfyyyyCMMCddCHHmmss = "yyyy年MM月dd日 HH:mm:ss";
    /**
     * HH:mm:ss格式
     */
    public static final String sdfHHmmss = "HH:mm:ss";
    /**
     * yyyyMMddHHmmss格式
     */
    public static final String sdfyyyyMMddHHmmss = "yyyyMMddHHmmss";
    /**
     * yyyyMM格式
     */
    public static final String sdfyyyyMM = "yyyyMM";
    /**
     * yyyy-MM格式
     */
    public static final String sdfyyyy_MM = "yyyy-MM";
    /**
     * MMdd格式
     */
    public static final String sdfMMdd = "MMdd";
    /**
     * HHmm格式
     */
    public static final String sdfHHmm = "HHmm";

    /**
     * 獲取yyyyMMddHHmmss格式
     *
     * @return d
     */
    public static String getSdfTimes() {
        return new SimpleDateFormat(sdfyyyyMMddHHmmss, Locale.getDefault()).format(new Date());
    }

    /**
     * 獲取YYYYMM格式
     *
     * @return d
     */
    public static String getMonth() {
        return new SimpleDateFormat(sdfyyyy_MM, Locale.getDefault()).format(new Date());
    }


    /**
     * 獲取上一個月
     *
     * @return d
     */
    public static String getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        return dft.format(cal.getTime());
    }

    /**
     * 描述:獲取下一個月.
     *
     * @return d
     */
    public static String getPreMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        return dft.format(cal.getTime());
    }

    /**
     * 獲取YYYY格式
     *
     * @return d
     */
    public static String getYear() {
        return new SimpleDateFormat(sdfyyyy, Locale.getDefault()).format(new Date());
    }

    /**
     * 獲取YYYY-MM-DD格式
     *
     * @return d
     */
    public static String getDay() {
        return new SimpleDateFormat(sdfyyyy_MM_dd, Locale.getDefault()).format(new Date());
    }

    /**
     * 獲取YYYYMMDD格式
     *
     * @return d
     */
    public static String getDays() {
        return new SimpleDateFormat(sdfyyyyMMddHHmmss, Locale.getDefault()).format(new Date());
    }


    /**
     * 獲取YYYY-MM-DD HH:mm:ss格式
     *
     * @return d
     */
    public static String getTime() {
        return new SimpleDateFormat(sdfyyyy_MM_ddHHmmss, Locale.getDefault()).format(new Date());
    }

    /**
     * @param s d
     * @param e d
     * @return boolean
     * @Description: 日期比較 ， 如果s > = e 返回true 否則返回false)
     * @author fh
     */
    public static boolean compareDate(String s, String e) {
        if (formatDate(s) == null || formatDate(e) == null) {
            return false;
        }
        return Objects.requireNonNull(formatDate(s)).getTime() >= Objects.requireNonNull(formatDate(e)).getTime();
    }

    /**
     * 格式化日期
     *
     * @return d
     */
    public static Date formatDate(String date) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (date == null || "".equals(date) || "null".equals(date))
                return null;

            return fmt.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            LogUtils.d("DateUtils -> formatDate() e:" + e.toString());
            return null;
        }
    }


    /**
     * 格式化日期
     *
     * @return d
     */
    public static Date formatDate(String date, String sdf) {
        try {
            if (date == null || "".equals(date) || "null".equals(date))
                return null;

            return new SimpleDateFormat(sdf, Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 格式化日期
     *
     * @return d
     */
    public static String formatDate(Date date, String sdf) {

        try {
            if (date == null)
                return null;

            return new SimpleDateFormat(sdf, Locale.getDefault()).format(date);
        } catch (Exception e) {
            //e.printStackTrace();
            LogUtils.d("DateUtils -> formatDate() e:" + e.toString());
            return null;
        }
    }

    /**
     * 格式化日期
     *
     * @return d
     */
    public static Date formatDateToDate(Date date, String sdf) {
        return formatDate(formatDate(date, sdf), sdf);
    }

    /**
     * 校驗日期是否合法
     *
     * @return d
     */
    public static boolean isValidDate(String s) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            fmt.parse(s);
            return true;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就說明格式不對
            return false;
        }
    }

    /**
     * @param startTime d
     * @param endTime d
     * @return d
     */
    public static int getDiffYear(String startTime, String endTime) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {

            return (int) (((Objects.requireNonNull(fmt.parse(endTime)).getTime() - Objects.requireNonNull(fmt.parse(startTime)).getTime()) / (1000 * 60 * 60 * 24)) / 365);

        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就說明格式不對
          //  System.out.println(e);
            return 0;
        }
    }

    /**
     * <li>功能描述：時間相減得到天數
     *
     * @param beginDateStr d
     * @param endDateStr d
     * @return long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.util.Date beginDate = null;
        java.util.Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (beginDate != null && endDate != null)
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        //System.out.println("相隔的天數="+day);

        return day;
    }

    /**
     * <li>功能描述：時間相減得到秒
     *
     * @param beginDateStr d
     * @param endDateStr d
     * @return long
     * @author Administrator
     */
    public static long getDaySubTime(String beginDateStr, String endDateStr) {
        long second = 0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date beginDate = null;
        java.util.Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (beginDate != null && endDate != null)
            second = (endDate.getTime() - beginDate.getTime()) / (1000);

        return second;
    }


    /**
     * 得到n天之後的日期
     *
     * @param days d
     * @return d
     */
    public static String getAfterDayDate(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar calendar = Calendar.getInstance(); // java.util包
        calendar.add(Calendar.DATE, daysInt); // 日期減 如果不夠減會將月變動
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 得到n天之後的日期
     *
     * @param days d
     * @return d
     */
    public static String getAfterDayDate(int days) {

        Calendar calendar = Calendar.getInstance(); // java.util包
        calendar.add(Calendar.DATE, days); // 日期減 如果不夠減會將月變動
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 得到n天之後的日期
     *
     * @param days d
     * @return d
     */
    public static Date getAfterDay(int days) {
        Calendar calendar = Calendar.getInstance(); // java.util包
        calendar.add(Calendar.DATE, days); // 日期減 如果不夠減會將月變動
        return calendar.getTime();
    }

    /**
     * 得到n天之後的日期
     *
     * @param days d
     * @return d
     */
    public static Date getAfterDay(int days, String sdf) {
        return formatDate(formatDate(getAfterDay(days), sdf), sdf);
    }

    /**
     * 獲取當前月的天數
     *
     * @return d
     */
    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DATE);
    }

    /**
     * 根據指定時間得到n小時之後的時間
     *
     * @param date d
     * @param n d
     * @return d
     */
    public static Date getAfterHour(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, n);
        return calendar.getTime();
    }

    /**
     * 得到n天之後是周幾
     *
     * @param days d
     * @return d
     */
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);
        Calendar calendar = Calendar.getInstance(); // java.util包
        calendar.add(Calendar.DATE, daysInt); // 日期減 如果不夠減會將月變動
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 返回第一個引數加上第二個引數（天）之後的日期
     *
     * @param date d
     * @param day d
     * @return d
     */
    public static Date getDateNext(Date date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 根據日期字串,傳入幾天,得到向後幾天日期
     *
     * @param date 日期
     * @param day  幾天
     * @return d
     */
    public static Date getDateNext(String date, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(formatDate(date));
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 取得上月日期
     *
     * @param monthNum 月數
     * @return d
     */
    public static String getLastMonth(int monthNum) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1 * monthNum);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        return dateFormat.format(c.getTime());
    }

    /**
     * 下月第一天
     *
     * @return d
     */
    public static Date nextMonthFirstDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 上月第一天
     *
     * @return d
     */
    public static Date lastMonthFirstDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 將日期中的時分秒清零
     *
     * @param date d
     * @return d
     */
    public static Date getDayStart(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * 1970/01/01至今的秒數轉換成Date
     *
     * @param timeStamp d
     * @return d
     */
    public static Date getDateByTimeStamp(Long timeStamp) {
        return new Date(timeStamp * 1000);
    }

    /**
     * 1970/01/01至今的豪秒數轉換成Date
     *
     * @param timeStampMs d
     * @return d
     */
    public static Date getDateByTimeStampMs(Long timeStampMs) {
        return new Date(timeStampMs);
    }

    /**
     * 時間轉換成秒 1970/01/01至今的秒數（Date轉long），等於new Date().getTime()/1000
     *
     * @param date d
     * @return d
     */
    public static long getTimeStampByDate(Date date) {
        long stamp;
        Date date2;
        try {
            date2 = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())).parse("1970/01/01 08:00:00");
            stamp = (date.getTime() - Objects.requireNonNull(date2).getTime()) / 1000L;
        } catch (Exception e) {
            stamp = 0L;
        }

        return stamp;
    }

    /**
     * 時間轉換成秒 1970/01/01至今的豪秒數（Date轉long）
     *
     * @param date d
     * @return d
     */
    public static long getTimeStampMsByDate(Date date) {
        long stamp;
        Date date2 ;
        try {
            date2 = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())).parse("1970/01/01 08:00:00");
            stamp = (date.getTime() - Objects.requireNonNull(date2).getTime());
        } catch (Exception e) {
            stamp = 0L;
        }

        return stamp;
    }

    /**
     * 獲取當前時間之前或之後幾小時 hour
     *
     * @param hour d
     * @return d
     */
    public static Date getTimeByHour(int hour) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

        //return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        return calendar.getTime();
    }


    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat(日期格式 例如:MM/ dd/yyyy HH:mm:ss)
     * @param millSec(毫秒数)    d
     * @return d
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        if (millSec < 1)
            return "00:00";

//参数一是格式，参数二是转换语言标准（Locale.ENGLISH，英文大写）
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    //常用转换
    public static String transferLongToDate(Long millSec) {
        if (millSec < 1) {
            return "00:00";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = new Date(millSec);
            return sdf.format(date);
        }
    }


    /**
     * 將dateString 轉成"yyyy-MM-dd HH:mm:ss";
     *
     * @param date d
     * @return d
     */
    public static Date formatDateSdfyyyy_MM_ddHHmmss(String date) {
        DateFormat fmt = new SimpleDateFormat(sdfyyyy_MM_ddHHmmss, Locale.getDefault());
        try {
            if (date == null || "".equals(date) || "null".equals(date))
                return null;

            return fmt.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            System.out.println(e);
            LogUtils.d("DateUtils -> formatDate() e:" + e.toString());
            return null;
        }
    }


    /**
     * 將dateString 轉成"yyyy-MM-dd HH:mm:ss";
     *
     * @param date d
     * @return d
     */
    public static Date formatDates(String date, String format) {
        DateFormat fmt = new SimpleDateFormat(format, Locale.getDefault());
        try {
            if (date == null || "".equals(date) || "null".equals(date))
                return null;

            return fmt.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            LogUtils.d("DateUtils -> formatDate() e:" + e.toString());
            return null;
        }
    }

    /**
     * @param dateTime   時間戳
     * @param dateFormat (日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @return string Date
     */
    public static String dateTimeToDateText(String dateTime, String dateFormat) {
        return formatDate(formatDateSdfyyyy_MM_ddHHmmss(dateTime), dateFormat);
    }


    public static int get24HourByAmPm(int amPmType, int hour) {
        int newHour = hour;
        if (amPmType == 0) {
            //am
            if (newHour == 12)
                newHour = 0;
        } else {
            //pm
            if (newHour != 12) {
                newHour = newHour + 12;
            }
        }
        LogUtils.d("get24HourByAmPm -> newHour:" + newHour + ", amPmType:" + amPmType + ", hour");
        return newHour;
    }

    public static int getHourByAmPm(int amPmType, int hour) {
        int newHour = hour;
        if (amPmType == 0) {
            //am
            if (newHour == 0)
                newHour = 12;
        } else {
            //pm
            if (newHour != 12) {
                newHour = newHour - 12;
            }
        }
        LogUtils.d("getHourByAmPm -> newHour:" + newHour + ", amPmType:" + amPmType + ", hour:" + hour);
        return newHour;
    }
}