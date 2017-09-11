package com.yuangee.flower.customer.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by developerLzh on 2017/9/4 0004.
 */

public class DateFormatUtils {
    /**
     * 把 timestamp 转换为字符串格式的日期
     */
    public static String format(long timestamp, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.CHINESE);
        return format.format(new Date(timestamp));
    }
}
