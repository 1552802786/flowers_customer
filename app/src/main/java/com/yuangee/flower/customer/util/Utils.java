package com.yuangee.flower.customer.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;

import com.yuangee.flower.customer.App;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by developerLzh on 2017/6/13 0013.
 */

public class Utils {
    public static int getAppVersionCode(Context paramContext) {
        try {
            if (null == paramContext) {
                return 23;
            }
            int verionCode = paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0).versionCode;
            return verionCode;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {

        }
        return 1;
    }

    public static String getAppVersion(Context paramContext) {
        try {
            if (null == paramContext) {
                return "2.4.3";
            }
            String str = paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0).versionName;
            return str;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
//            printExceptionStackTrace(localNameNotFoundException);
        }
        return null;
    }

    /**
     * 把 timestamp 转换为字符串格式的日期
     */
    public static String format(long timestamp, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.CHINESE);
        return format.format(new Date(timestamp));
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str, String pattern) {

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static double[] bd_decrypt(double bd_lat, double bd_lon) {
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        double[] latlon = new double[]{gg_lat, gg_lon};
        return latlon;
    }

    public static String timestampToStr(long timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINESE);
        Date dateCur = new Date(timestamp);
        Calendar calendarCur = Calendar.getInstance();
        calendarCur.setTime(dateCur);

        int yearCur = calendarCur.get(Calendar.YEAR);
        int dayCurOfYear = calendarCur.get(Calendar.DAY_OF_YEAR);


        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        String dateStr = "";

        if (yearCur == year) {
            if (dayCurOfYear == dayOfYear) {
                dateStr += "今天，";
            } else if (dayOfYear - 1 == dayCurOfYear) {
                dateStr += "明天，";
            } else if (dayOfYear - 2 == dayCurOfYear) {
                dateStr += "后天，";
            } else {
                dateStr += month + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日，";
            }
        } else {
            dateStr += year + "年" + month + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日，";
        }

        dateStr += calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";

        return dateStr;
    }

    private static Vibrator vib;
    private static final long[] VIBRATOR_PATTERN = {0, 500, 100, 500, 1000, 500, 100, 500};

    public static void vibrate(Context paramContext, boolean isRepeat) {

        App.me();
        if (vib == null) {
            vib = (Vibrator) paramContext
                    .getSystemService(Context.VIBRATOR_SERVICE);
        }
        vib.vibrate(VIBRATOR_PATTERN, isRepeat ? 0x0 : -0x1);
    }

    public static String getTimeStr(String timeStr) {
        String day;
        long time = date2TimeStamp(timeStr, "yyyy-MM-dd HH:mm");

        String currentDay = Utils.timeFormat(System.currentTimeMillis(), "yyyy-MM-dd");

        if (Utils.timeFormat(time - 86400000, "yyyy-MM-dd").equals(currentDay)) {
            day = "明天";
        } else if (Utils.timeFormat(time - 86400000 * 2, "yyyy-MM-dd").equals(currentDay)) {
            day = "后天";
        } else if (Utils.timeFormat(time, "yyyy-MM-dd").equals(currentDay)) {
            day = "今天";
        } else {
            day = Utils.timeFormat(time, "MM-dd");
        }

        String houMin = timeStr.split(" ")[1];

        timeStr = day + " " + houMin;

        return timeStr;
    }

    public static String timeFormat(long timestamp, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.CHINESE);
        return format.format(new Date(timestamp));
    }

    public static Long date2TimeStamp(String date_str, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }
}
