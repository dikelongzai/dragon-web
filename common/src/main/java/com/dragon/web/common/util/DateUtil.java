package com.dragon.web.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:04
 */
public class DateUtil {
    private static ThreadLocal<SimpleDateFormat> dateFormatter1 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter2 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter3 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter4 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter5 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter6 = new ThreadLocal();
    private static ThreadLocal<SimpleDateFormat> dateFormatter7 = new ThreadLocal();

    public DateUtil() {
    }

    public static SimpleDateFormat getDateFormatter1() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter1.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy-MM-dd");
            dateFormatter1.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter2() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter2.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatter2.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter3() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter3.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy/MM/dd");
            dateFormatter3.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter4() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter4.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            dateFormatter4.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter5() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter5.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy.MM.dd");
            dateFormatter5.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter6() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter6.get();
        if (o == null) {
            o = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            dateFormatter6.set(o);
        }

        return o;
    }

    public static SimpleDateFormat getDateFormatter7() {
        SimpleDateFormat o = (SimpleDateFormat)dateFormatter7.get();
        if (o == null) {
            o = new SimpleDateFormat("HH:mm:ss");
            dateFormatter7.set(o);
        }

        return o;
    }

    /**
     * string->date
     * @param time
     * @return
     */
    public static Date toDate(String time) {
        SimpleDateFormat sdf = getDateFormatter1();
        if (time.indexOf("-") > 0 && time.indexOf(" ") > 0) {
            sdf = getDateFormatter2();
        } else if (time.indexOf("/") > 0 && time.indexOf(" ") < 0) {
            sdf = getDateFormatter3();
        } else if (time.indexOf("/") > 0 && time.indexOf(" ") > 0) {
            sdf = getDateFormatter4();
        } else if (time.indexOf(".") > 0 && time.indexOf(" ") < 0) {
            sdf = getDateFormatter5();
        } else if (time.indexOf(".") > 0 && time.indexOf(" ") > 0) {
            sdf = getDateFormatter6();
        } else if (time.indexOf("-") < 0) {
            sdf = getDateFormatter7();
        }

        try {
            return sdf.parse(time);
        } catch (ParseException var3) {
            return null;
        }
    }

    /**
     * datetime->yyyy-MM-dd
     * @param time
     * @return
     */
    public static String dateToString(Date time) {
        String res = getDateFormatter2().format(time);
        if (res.indexOf("00:00:00") > 0) {
            res = res.substring(0, 10);
        }

        return res;
    }

    /**
     *获取当前日期yyyy-MM-dd
     * @return
     */
    public static String getSysdate() {
        String res = getDateFormatter1().format(new Date());
        if (res.indexOf("00:00:00") > 0) {
            res = res.substring(0, 10);
        }

        return res;
    }

    public static String getSysdatetime() {
        String res = getDateFormatter2().format(new Date());
        if (res.indexOf("00:00:00") > 0) {
            res = res.substring(0, 10);
        }

        return res;
    }

    public static Date addDays(Date dt, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(5, days);
        return cal.getTime();
    }

    public static int getYear(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(1);
    }

    public static int getMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(2) + 1;
    }

    public static int getDay(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(5);
    }
}
