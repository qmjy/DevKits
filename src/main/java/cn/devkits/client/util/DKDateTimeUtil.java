/*
 * Copyright (c) 2019-2020 QMJY.CN All rights reserved.
 */

package cn.devkits.client.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public final class DKDateTimeUtil {

    public static final String DATE_TIME_PATTERN_DEFAULT = "yyyy-MM-dd mm:HH:ss";
    public static final String DATE_TIME_PATTERN_FULL = "yyyyMMddmmHHss";

    public static void main(String[] args) throws ParseException {
        System.out.println(DKDateTimeUtil.utc2CHNTime("2019-09-08T14:54:13Z"));
    }

    /**
     * convert UTC time to CST, like: "2019-09-08T14:54:13Z"
     *
     * @param utcTimeStr UTC time string
     * @return CST time string
     * @throws ParseException
     */
    public static String utc2CHNTime(String utcTimeStr) throws ParseException {
        if (utcTimeStr.contains("T") && utcTimeStr.contains("Z")) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date parse = format.parse(utcTimeStr);

            Calendar instance = Calendar.getInstance();
            instance.setTime(parse);

            instance.add(Calendar.HOUR_OF_DAY, 8);

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sf.format(instance.getTime());
        }
        return null;
    }

    /**
     * 按照指定格式返回当前时间字符串
     *
     * @param pattern 时间格式
     * @return 当前时间字符串
     */
    public static String currentTimeStrWithPattern(String pattern) {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(instance.getTime());
    }

    /**
     * 当前时间，默认格式：yyyyMMddmmHHss
     *
     * @return “yyyyMMddmmHHss”格式的当前时间
     */
    public static String currentTimeStr() {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN_FULL);
        return simpleDateFormat.format(instance.getTime());
    }


    /**
     * convert long time to string time with pattern "yyyy-MM-dd mm:HH:ss"
     *
     * @param longTime long time
     * @return string time
     */
    public static String long2Str(long longTime) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(longTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN_DEFAULT);
        return simpleDateFormat.format(instance.getTime());
    }

    /**
     * 将long格式时间转换成字符串格式
     *
     * @param time    long time
     * @param pattern format pattern
     * @return formatted string time
     */
    public static String getDatetimeStrOfLong(long time, String pattern) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }
}
