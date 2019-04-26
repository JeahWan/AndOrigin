package com.base.and.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtils {

    public static void main(String[] args) {
        System.out.println(getFormatDate("HH:mm:ss"));
    }

    /**
     * @param format 指定的日期格式<br>
     *               eg:<br>
     *               "yyyy-MM-dd HH:mm:ss"<br>
     *               "yyyy-MM-dd"<br>
     *               "yyyyMMddHHmmss"<br>
     *               "HH:mm:ss"<br>
     * @return
     */
    public static String getFormatDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 将指定日期按指定格式输出，日期为空则默认当前日期
     *
     * @param format
     * @return
     */
    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (date == null) {
            date = new Date();
        }
        return simpleDateFormat.format(date);
    }

    /**
     * 获得下个月
     *
     * @param date
     * @return
     */
    public static Date getNextMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, +1); // 得到下一月
        date = calendar.getTime();
        return date;
    }

    /**
     * 获得上个月
     *
     * @param date
     * @return
     */
    public static Date getPreMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1); // 得到上一月
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        long timeMilllis = System.currentTimeMillis();
        return simpleDateFormat.format(timeMilllis);
    }

    /**
     * 获得当前日期时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatDate1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 获得当前日期时间
     *
     * @return yyyyMMddHHmmss
     */
    public static String getFormatDate2() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 将日期格式 yyyy-M-d 转化为yyyyMMdd
     *
     * @param date yyyy-M-d
     * @return yyyyMMdd
     */
    public static String getFormatDate3(String date) {

        String year = date.split("-")[0];
        String month = date.split("-")[1];
        String day = date.split("-")[2];
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        return year + month + day;

    }

    /**
     * 将日期格式 yyyyMMdd 转化为 yyyy-MM-dd
     *
     * @param date yyyyMMdd
     * @return yyyy-MM-dd
     */
    public static String getFormatDate4(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return year + "-" + month + "-" + day;
    }

    /**
     * 将日期格式 YYYYMMDDHHMMSS 转化为 YYYY-MM-DD HH:MM:SS
     *
     * @param date YYYYMMDDHHMMSS
     * @return YYYY-MM-DD HH:MM:SS
     */
    public static String getFormatDate5(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String hour = date.substring(8, 10);
        String min = date.substring(10, 12);
        String sec = date.substring(12, 14);
        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
    }

    /**
     * 获得当前时间
     *
     * @return HH:mm
     */
    public static String getFormatTime1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 将HH：ss 格式的时间转化成分钟数
     *
     * @param time
     * @return
     */
    public static int timeToMinute(String time) {
        if (StringUtils.isEmpty(time)) {
            return 0;
        }
        String[] split = time.split(":");
        try {
            return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获得当前时间
     *
     * @return HH:mm:ss
     */
    public static String getFormatTime2() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 将时间格式 HH:mm 转化为 HHmm
     *
     * @param date HH:mm
     * @return HHmm
     */
    public static String getFormatTime3(String time) {
        return time.replaceAll(":", "");
    }

    /**
     * 将时间格式 HHmm 转化为 HH:mm
     *
     * @param time HHmm
     * @return HH:mm
     */
    public static String getFormatTime4(String time) {
        String hour = "00";
        String min = "00";
        if (time.length() == 4) {
            hour = time.substring(0, 2);
            min = time.substring(2, 4);
        }
        return hour + ":" + min;
    }

    /**
     * 根据指定的时间戳，返回指定格式的日期时间
     * <p>
     * 指定的日期格式<br>
     * eg:<br>
     * "yyyy-MM-dd HH:mm:ss"<br>
     * "yyyy-MM-dd"<br>
     * "yyyyMMddHHmmss"<br>
     * "HH:mm:ss"<br>
     *
     * @return
     */
    public static String getFormatTime(long time, String format) {
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            strs = sdf.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * @param day 输入日期格式 yyyy-MM-dd
     * @return 获取指定日期往前或者往后几天的日期
     * @leaveDay 差距的天数 -1 or +1
     */
    public static String getFormatLeaveDay(String day, int leaveDay) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ;
        Date date = null;

        try {
            date = simpleDateFormat.parse(day);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, leaveDay);
        date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    /**
     * @param day 输入日期格式 yyyy-MM-dd
     * @return 获得前一天的日期
     */
    public static String getFormatBeforeDay(String day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(day);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1); // 得到前一天
        date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    /**
     * @param day 输入日期格式 yyyy-MM-dd
     * @return 获得后一天的日期
     */
    public static String getFormatNextDay(String day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(day);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +1); // 得到后一天
        date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    /**
     * 获得输入日期的星期
     *
     * @param inputDate 需要转换的日期 yyyy-MM-dd
     * @return 星期×
     */
    public static String getWeekDay(String inputDate) {
        // String weekStrArr1[] = {"周日","周一","周二","周三","周四","周五","周六"};
        String weekStrArr1[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int outWeek = calendar.get(Calendar.DAY_OF_WEEK);// 返回的是1-7的整数，1为周日，2为周一，以此类推。
        return weekStrArr1[outWeek - 1];
    }

    /**
     * 检测时间是否在某个时间段内
     *
     * @param timeSlot 时间段 00：00--24：00
     * @param time     需要检测的时间 00：23
     * @return
     */
    public static boolean isInsideTime(String timeSlot, String time) {
        String startTime = timeSlot.split("--")[0];
        String endTime = timeSlot.split("--")[1];
        boolean isGreaterStart = compareTime(time, startTime);
        boolean isLessEnd = compareTime(endTime, time);
        if (isGreaterStart && isLessEnd) {
            return true;
        }
        return false;
    }

    /**
     * 比较两个时间的大小
     *
     * @param time1 00：23
     * @param time2 00：25
     * @return time1大于等于time2 为 true,time1小于time2 为 false
     */
    public static boolean compareTime(String time1, String time2) {
        if (time1.equals("24:00") || time2.equals("00:00")) {
            return true;
        }
        if (time2.equals("24:00") || time1.equals("00:00")) {
            return false;
        }
        // DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int result = c1.compareTo(c2);

        if (result < 0) {
            return false;
        } else if (result >= 0) {
            return true;
        }
        return true;
    }

    /**
     * 比较两个日期的大小
     *
     * @param date1 2012-5-11
     * @param date2 2012-5-11
     * @return date1大于等于date2 为 true,date1小于date2 为 false
     */
    public static int compareDate(String date1, String date2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        int result = c1.compareTo(c2);

        if (result < 0) {
            return -1;
        } else if (result >= 0) {
            return 1;
        } else {
            return 0;
        }
    }


    /**
     * 比较两个日期的大小
     *
     * @param date1 2012-5-11
     * @param date2 2012-5-11
     * @return result = 1 : date1大于date2;  result = -1 : date1小于date2 ;  result = 0 : date1等于date2
     */
    public static int compareDate(String date1, String date2, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        int result = c1.compareTo(c2);

        return result;
    }

    /**
     * 比较两个日期的月份大小
     *
     * @param date1
     * @param date2
     * @param format format = "yyyyMM",则为按月份比较大小！！
     * @return result = 1 : date1大于date2;  result = -1 : date1小于date2 ;  result = 0 : date1等于date2
     */
    public static int compareDate(Date date1, Date date2, String format) {

        String d1 = getFormatDate(date1, format);
        String d2 = getFormatDate(date2, format);


        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        try {
            c1.setTime(sdf.parse(d1));
            c2.setTime(sdf.parse(d2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int result = c1.compareTo(c2);

        return result;  //大于0，c1大于c2；等于0，c1=c2；小于0，c1小于c2
    }

}
