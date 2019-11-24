/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com> WebSite
 * https://github.com/MiracleTimes-Dev Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package cn.devkits.client.util.calendar;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一些日期辅助计算工具
 */
final class CalendarUtil {

    private static final long ONE_DAY = 1000 * 3600 * 24;

    static int getDate(String formatStr, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return Integer.parseInt(format.format(date));
    }

    /**
     * 判断一个日期是否是周末，即周六日
     *
     * @param calendar calendar
     * @return 判断一个日期是否是周末，即周六日
     */
    static boolean isWeekend(Calendar calendar) {
        int week = getWeekFormCalendar(calendar);
        return week == 0 || week == 6;
    }

    /**
     * 获取某月的天数
     *
     * @param year 年
     * @param month 月
     * @return 某月的天数
     */
    static int getMonthDaysCount(int year, int month) {
        int count = 0;
        // 判断大月份
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            count = 31;
        }

        // 判断小月
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            count = 30;
        }

        // 判断平年与闰年
        if (month == 2) {
            if (isLeapYear(year)) {
                count = 29;
            } else {
                count = 28;
            }
        }
        return count;
    }


    /**
     * 是否是闰年
     *
     * @param year year
     * @return 是否是闰年
     */
    static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }



    /**
     * 获取某个日期是星期几 测试通过
     *
     * @param calendar 某个日期
     * @return 返回某个日期是星期几
     */
    static int getWeekFormCalendar(Calendar calendar) {
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        return date.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }



    /**
     * 运算 calendar1 - calendar2 test Pass
     *
     * @param calendar1 calendar1
     * @param calendar2 calendar2
     * @return calendar1 - calendar2
     */
    static int differ(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null) {
            return Integer.MIN_VALUE;
        }
        if (calendar2 == null) {
            return Integer.MAX_VALUE;
        }
        java.util.Calendar date = java.util.Calendar.getInstance();

        date.set(calendar1.getYear(), calendar1.getMonth() - 1, calendar1.getDay());//

        long startTimeMills = date.getTimeInMillis();// 获得起始时间戳

        date.set(calendar2.getYear(), calendar2.getMonth() - 1, calendar2.getDay());//

        long endTimeMills = date.getTimeInMillis();// 获得结束时间戳

        return (int) ((startTimeMills - endTimeMills) / ONE_DAY);
    }

    /**
     * 比较日期大小
     *
     * @param minYear minYear
     * @param minYearMonth minYearMonth
     * @param minYearDay minYearDay
     * @param maxYear maxYear
     * @param maxYearMonth maxYearMonth
     * @param maxYearDay maxYearDay
     * @return -1 0 1
     */
    static int compareTo(int minYear, int minYearMonth, int minYearDay, int maxYear, int maxYearMonth, int maxYearDay) {
        Calendar first = new Calendar();
        first.setYear(minYear);
        first.setMonth(minYearMonth);
        first.setDay(minYearDay);

        Calendar second = new Calendar();
        second.setYear(maxYear);
        second.setMonth(maxYearMonth);
        second.setDay(maxYearDay);
        return first.compareTo(second);
    }


}


