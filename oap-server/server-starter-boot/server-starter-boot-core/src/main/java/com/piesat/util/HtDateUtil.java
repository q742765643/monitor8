package com.piesat.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName : DateUtil
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-27 15:54
 */
public class HtDateUtil {
    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_SHORT = "yyyy-MM-dd";
    /**
     * 英文全称  如：2010-12-01 23:15:06
     */
    public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到毫秒的完整时间    如：yyyy-MM-dd HH:mm:ss.S
     */
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";

    public static String FORMAT_FULL_ = "yyyyMMddHHmmss";

    /**
     * 中文简写  如：2010年12月01日
     */
    public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
    /**
     * 中文全称  如：2010年12月01日  23时15分06秒
     */
    public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    /**
     * 精确到毫秒的完整中文时间
     */
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";


    public static void main(String[] args) {
        System.out.println(getTimeString());
        System.out.println("返回日期年份:" + getYear(new Date()));
        System.out.println("返回月份：" + getMonth(new Date()));
        System.out.println("返回当天日份" + getDay(new Date()));
        System.out.println("返回当天小时" + getHour(new Date()));
        System.out.println("返回当天分" + getMinute(new Date()));
        System.out.println("返回当天秒" + getSecond(new Date()));
        System.out.println("返回当天毫秒" + getMillis(new Date()));
        getTime(new Date());

    }

    public static Map<String, String> getTime(Date date) {
        Map<String, String> map = new HashMap<>();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL_);
        String str = df.format(date);
        map.put("year", str.substring(0, 4));
        map.put("month", str.substring(4, 6));
        map.put("day", str.substring(6, 8));
        map.put("hour", str.substring(8, 10));
        //map.put("minute", str.substring(10, 12));
        map.put("minute", "00");
        map.put("second", "00");
        return map;
    }

    public static long matchingTime(Date date, String format, Map<String, String> map) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL_);
        String str = df.format(date);
        //format = format.toUpperCase();
        //yyyyMMddHHmmss
        if (format.indexOf("y") != -1) {
            map.put("year", str.substring(0, 4));
        }
        if (format.indexOf("M") != -1) {
            map.put("month", str.substring(4, 6));
        }
        if (format.indexOf("d") != -1) {
            map.put("day", str.substring(6, 8));
        }
        if (format.indexOf("H") != -1) {
            map.put("hour", str.substring(8, 10));
        }
        if (format.indexOf("m") != -1) {
            map.put("minute", str.substring(10, 12));
        }
        if (format.indexOf("s") != -1) {
            map.put("second", str.substring(12, 14));
        }
        String realStr = map.get("year") + map.get("month") + map.get("day") + map.get("hour") + map.get("minute") + map.get("second");
        long time = df.parse(realStr).getTime();
        return time;
    }


    /**
     * 获取当前时间
     */
    public static String getTimeString() {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    /**
     * 获取日期年份
     *
     * @param date 日期
     * @return
     */
    public static String getYear(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
        return df.format(date).substring(0, 4);
    }

    /**
     * 功能描述：返回月
     *
     * @param date Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述：返回日期
     *
     * @param date Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回小时
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回分
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 功能描述：返回毫
     *
     * @param date 日期
     * @return 返回毫
     */
    public static long getMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static String millisToDayHrMinSec(long milliseconds){
        final long day = TimeUnit.MILLISECONDS.toDays(milliseconds);

        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));


        return String.format("%d 天 %d 小时 %d 分", day, hours, minutes);
    }

}

