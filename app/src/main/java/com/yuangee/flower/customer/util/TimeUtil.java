package com.yuangee.flower.customer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.yuangee.flower.customer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static final String HM = "HH:mm";
    public static final String HMS = "HH:mm:ss";
    public static final String MD_HM = "MM-dd HH:mm";
    public static final String MD_HMS = "MM-dd HH:mm:ss";
    static final int NEXT_CLOCK = 0x7;
    static final int NEXT_MINUTE = 0x0;
    public static final String SERVER_FORMAT = "yyyyMMddHHmmss";
    public static final String YM = "yyyy-MM";
    public static final String YM2 = "yyyyMM";
    public static final String YMD = "yyyyMMdd";
    public static final String YMD_2 = "yyyy-MM-dd";
    public static final String YMD_3 = "yyyy_MM_dd";
    public static final String YMD_4_CN = "yyyy年MM月dd日";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_HM_LOG = "yyyyMMdd_HHmm";

    @SuppressLint("SimpleDateFormat")
    public static long parseTime(String format, String time) {
        Date date = null;
        try {
            if (time != null) {

                date = new SimpleDateFormat(format).parse(time);
            }

        } catch (ParseException e) {
        }
        return date != null ? date.getTime() : System.currentTimeMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String format, long time) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    public static boolean isExpired(long savedExprieTime) {
        if (savedExprieTime == 0x0) {
            return true;
        }
        long curTime = parseTime("yyyyMMdd", getTime("yyyyMMdd", System.currentTimeMillis()));
        if (savedExprieTime <= curTime) {
            return false;
        }
        return true;
    }

    public static boolean isExpired(String checkTime) {
        if (TextUtils.isEmpty(checkTime)) {
            return true;
        }
        long time = parseTime("yyyyMMdd", checkTime);
        String now = getTime("yyyyMMdd", System.currentTimeMillis());
        long curTime = parseTime("yyyyMMdd", now);
        if (time <= curTime) {
            return false;
        }
        return true;
    }

    public static String getTimeSpan(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "\u5929");
        }
        if (hours > 0) {
            buffer.append(hours + "\u5c0f\u65f6");
        }
        if (min > 0) {
            buffer.append(min + "\u5206\u949f");
        }
        return buffer.toString();
    }

    public static String getTimeFromMinutes(int minutes) {

        StringBuilder buffer = new StringBuilder();

        int min = minutes % 0x3c;
        int hours = (minutes / 0x3c) % 0x3c;
        if (hours > 0) {
            buffer.append(hours + "\u5c0f\u65f6");
        }
        if (min >= 0) {
            buffer.append(min + "\u5206\u949f");
        }

        return buffer.toString();
    }

    public static String getTimeSpanSeconds(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int sec = seconds % 0x3c;
        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "D");
        }
        if (hours > 0) {
            buffer.append(hours + "h");
        }
        if (min > 0) {
            buffer.append(min + "m");
        }
        if (sec >= 0) {
            buffer.append(sec + "s");
        }
        return buffer.toString();
    }

    public static String getTimeSpanMinutes(int seconds) {

        StringBuilder buffer = new StringBuilder();

        int min = (seconds / 0x3c) % 0x3c;
        int hours = ((seconds / 0x3c) / 0x3c) % 0x3c;
        int day = (((seconds / 0x3c) / 0x3c) / 0x18) % 0x18;
        if (day > 0) {
            buffer.append(day + "D");
        }
        if (hours > 0) {
            buffer.append(hours + "h");
        }
        if (min >= 0) {
            buffer.append(min + "m");
        }
        return buffer.toString();
    }

    public static long getTime(String date, int hour, int minute) {
        return parseTime("yyyy-MM-dd HH:mm", date + " " + String.format("%1$,02d:%2$,02d", hour, minute));
    }

    public static boolean isHourValid(String hour) {
        if (null == hour) {
            return false;
        }
        try {
            Integer h = Integer.parseInt(hour);
            if (h < 0 || h > 23) {
                return false;
            }

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean isMinuteValid(String minute) {
        if (null == minute) {
            return false;
        }
        try {
            Integer m = Integer.parseInt(minute);
            if (m < 0 || m > 59) {
                return false;
            }

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    @SuppressLint("DefaultLocale")
    public static float convertMinutesToHours(float minutes) {
        float hours = minutes / 60.0f;
        try {
            return Float.parseFloat(String.format("%.1f", Float.valueOf(hours)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0x0;
    }

    public static Calendar getNextSevenHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(0xb);
        if (hourOfDay >= 0x7) {
            calendar.set(0x6, (calendar.get(0x6) + 0x1));
        }
        calendar.set(0xb, 0x7);
        calendar.set(0xc, 0x0);
        calendar.set(0xd, 0x0);
        return calendar;
    }

    public static Calendar getPreviewSevenHourCalendar() {
        Calendar sevenClockCal = Calendar.getInstance();
        int hourOfDay = sevenClockCal.get(0xb);
        if (hourOfDay < 0x7) {
            sevenClockCal.set(0x6, (sevenClockCal.get(0x6) - 0x1));
        }
        sevenClockCal.set(0xb, 0x7);
        sevenClockCal.set(0xc, 0x0);
        sevenClockCal.set(0xd, 0x0);
        return sevenClockCal;
    }

    public static long getNextSevenHourOfDayDelayMillis() {
        Calendar calendar = getNextSevenHourCalendar();
        return (calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public static String convertMillisTime(long millisSeconds) {
        return convertSecondsTime((millisSeconds / 0x3e8));
    }

    @SuppressLint("DefaultLocale")
    public static String convertSecondsTime(long seconds) {
        int minute = (int) (seconds / 0x3c);
        int hour = minute / 0x3c;
        int second = (int) (seconds % 0x3c);
        minute = minute % 0x3c;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static int converSeconds2Minutes(int seconds) {
        return (seconds / 0x3c);
    }

    public static int convertMinutes2Seconds(int minutes) {
        return (minutes * 0x3c);
    }

    public static String getTimeSpanMinuteSeconds(int seconds) {
        int sec = seconds % 0x3c;
        int min = seconds / 0x3c;
        StringBuffer buffer = new StringBuffer();
        buffer.append(min).append("\u5206").append(sec).append("\u79d2");
        return buffer.toString();
    }

    public static int convertSeconds2MinutesRound(int seconds) {
        int min = seconds / 0x3c;
        int sec = seconds % 0x3c;
        if (sec >= 0x1e) {
            min = min + 0x1;
        }
        return min;
    }

    /**
     * 传入一个时间戳，返回相应的字符串
     *
     * @param time ：时间戳  当前时间 之前的时间
     * @return ：xx秒 xx分 xx...  之前的字符串
     */
    public static String getBeforeTime(Context context, long time) {

        long currentTime = System.currentTimeMillis();  //当前时间

        if (time > currentTime) {
            return "time error";
        }

        long dt = currentTime - time;   //传入的时间和当前时间之差

        if (dt > (7L * 24 * 60 * 60 * 1000)) {
            //7天外返回具体时间
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(time);
            return format.format(date);
        } else if (dt > (24L * 60 * 60 * 1000)) {
            //1天到7天
            int days = (int) (dt / (24L * 60 * 60 * 1000));
            return String.valueOf(days) + context.getString(R.string.tianqian);
        } else if (dt > (60L * 60 * 1000)) {
            int hours = (int) (dt / (60L * 60 * 1000));
            return String.valueOf(hours) + context.getString(R.string.xiaoshiqian);
        } else if (dt > (60L * 1000)) {
            int m = (int) (dt / (60L * 1000));
            return String.valueOf(m) + context.getString(R.string.fenzhongqian);
        } else {
            return String.valueOf(dt / 1000) + context.getString(R.string.miaoqian);
        }
    }

    public static String grabTimeStr(long timestamp) {

        String finalStr = "";

        String nowStr = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd hh:MM");
        String appointStr = DateFormatUtils.format(timestamp, "yyyy-MM-dd hh:MM");

        String today = nowStr.split(" ")[0];
        String appointDay = appointStr.split(" ")[0];

        String[] todayArray = today.split("-");
        String[] appointArray = appointDay.split("-");

        if (todayArray[0].equals(appointArray[0]) && todayArray[1].equals(appointArray[1]) && todayArray[2].equals(appointArray[2])) {
            finalStr = "今天" + appointStr.split(" ")[1];
        } else {
            finalStr = appointStr;
        }

        return finalStr;
    }


    /**
     * @return 今天17点  明天23点 ..
     */
    public static String getAfterTime(long time) {

        //获取当前系统时间
        Date date = new Date(System.currentTimeMillis());
        Calendar current = Calendar.getInstance();
        current.setTime(date);

        //今天0:00
        Calendar today = Calendar.getInstance();
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        //明天0：00
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.YEAR, current.get(Calendar.YEAR));
        tomorrow.set(Calendar.MONTH, current.get(Calendar.MONTH));
        tomorrow.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);

        //后天0:00
        Calendar next = Calendar.getInstance();
        next.set(Calendar.YEAR, current.get(Calendar.YEAR));
        next.set(Calendar.MONTH, current.get(Calendar.MONTH));
        next.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) + 2);
        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);

        //下月1号
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.set(Calendar.YEAR, current.get(Calendar.YEAR));
        nextMonth.set(Calendar.MONTH, current.get(Calendar.MONTH) + 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, 1);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.MINUTE, 0);
        nextMonth.set(Calendar.SECOND, 0);

        //明年
        Calendar nextYear = Calendar.getInstance();
        nextYear.set(Calendar.YEAR, current.get(Calendar.YEAR) + 1);
        nextYear.set(Calendar.MONTH, 0);
        nextYear.set(Calendar.DAY_OF_MONTH, 1);
        nextYear.set(Calendar.HOUR_OF_DAY, 0);
        nextYear.set(Calendar.MINUTE, 0);
        nextYear.set(Calendar.SECOND, 0);

        //订单时间
        Date orderDate = new Date(time);
        Calendar orderTime = Calendar.getInstance();
        orderTime.setTime(orderDate);

        SimpleDateFormat showFormat;

        if (orderTime.after(nextYear)) {
            //订单垮年了
            showFormat = new SimpleDateFormat("MM月dd日HH:mm");
            return showFormat.format(orderDate);
        } else if (orderTime.after(nextMonth)) {
            //订单在下一个月
            showFormat = new SimpleDateFormat("MM月dd日HH:mm");
            return showFormat.format(orderDate);
        } else if (orderTime.after(next)) {
            //后天或者后天之后
            showFormat = new SimpleDateFormat("dd日HH:mm");
            return showFormat.format(orderDate);
        } else if (orderTime.after(tomorrow)) {
            //订单在明天
            showFormat = new SimpleDateFormat("明天HH:mm");
            return showFormat.format(orderDate);
        } else if (orderTime.after(today)) {
            //订单在今天
            showFormat = new SimpleDateFormat("今天HH:mm");
            return showFormat.format(orderDate);
        } else {
            //今天之前
            showFormat = new SimpleDateFormat("MM月dd日HH:mm");
            return showFormat.format(orderDate);
        }
    }

    public static String getTimeStr(long time, Context context) {
        String day;
        String timeStr = DateFormatUtils.format(time, "yyyy-MM-dd HH:mm");

        String currentDay = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd");

        if (DateFormatUtils.format(time - 86400000, "yyyy-MM-dd").equals(currentDay)) {
            day = context.getString(R.string.tomorrow);
        } else if (DateFormatUtils.format(time - 86400000 * 2, "yyyy-MM-dd").equals(currentDay)) {
            day = context.getString(R.string.bermorgen);
        } else if (DateFormatUtils.format(time, "yyyy-MM-dd").equals(currentDay)) {
            day = context.getString(R.string.today);
        } else {
            day = DateFormatUtils.format(time, "MM-dd");
        }

        String houMin = timeStr.split(" ")[1];

        timeStr = day + " " + houMin;

        return timeStr;
    }

}