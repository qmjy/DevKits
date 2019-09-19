package cn.devkits.client.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DKDateTimeUtil {
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
}
