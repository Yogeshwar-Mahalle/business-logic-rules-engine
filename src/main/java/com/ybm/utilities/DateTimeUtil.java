/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    /**
     * Get current time with specified format
     *
     * @return Current time with format yyyy-MM-dd
     */
    public static String now() {
        String result = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateObject = new Date();
            result = dateFormat.format(dateObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get current time with specified format
     *
     * @param precission Decimal precission
     * @return Current time with format yyyy-MM-dd
     */
    public static String now(int precission) {
        if (precission > 6) {
            precission = 6;
        }
        if (precission < 0) {
            precission = 0;
        }
        long decimal = 0;
        long nanoSecond = System.nanoTime();
        if (precission == 6) {
            decimal = nanoSecond % 1000000;
        } else if (precission == 5) {
            decimal = nanoSecond % 100000;
        } else if (precission == 4) {
            decimal = nanoSecond % 10000;
        } else if (precission == 3) {
            decimal = nanoSecond % 1000;
        } else if (precission == 2) {
            decimal = nanoSecond % 100;
        } else if (precission == 1) {
            decimal = nanoSecond % 10;
        }
        String result = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateObject = new Date();
            result = dateFormat.format(dateObject) + "." + decimal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String now3() {
        String result = "";
        try {
            long miliSecond = System.nanoTime() % 1000;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateObject = new Date();
            result = dateFormat.format(dateObject) + "." + miliSecond;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String now6() {
        String result = "";
        try {
            long microSecond = System.nanoTime() % 1000000;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateObject = new Date();
            result = dateFormat.format(dateObject) + "." + microSecond;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get current time with specified format
     *
     * @param format Time format
     * @return Current time with specified format
     */
    public static String now(String format) {
        String result = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            Date dateObject = new Date();
            result = dateFormat.format(dateObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get MySQL format of current time
     *
     * @return Current time with MySQL format
     */
    public static String MySQLDate() {
        return now("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Get PgSQL format of current time
     *
     * @return Current time with PgSQL format
     */
    public static String PgSQLDate() {
        return now("yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * Get current time with MMddHHmmss format
     *
     * @return Current time with MMddHHmmss format
     */
    public static String date10() {
        return now("MMddHHmmss");
    }

    /**
     * Get current time with MMdd format
     *
     * @return Current time with MMdd format
     */
    public static String date4() {
        return now("MMdd");
    }

    /**
     * Get current time with HHmmss format
     *
     * @return Current time with HHmmss format
     */
    public static String time6() {
        return now("HHmmss");
    }

    /**
     * Get current time with HHmm format
     *
     * @return Current time with HHmm format
     */
    public static String time4() {
        return now("HHmm");
    }

    /**
     * Convert Date10 to MySQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss)
     *
     * @param datetime Date10
     * @return MySQL date format
     */
    public static String date10ToMySQLDate(String datetime) {
        return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Convert Date10 to PgSQL date format (MMddHHmmss to yyyy-MM-dd HH:mm:ss.SSS)
     *
     * @param datetime Date10
     * @return PgSQL date format
     */
    public static String date10ToPgSQLDate(String datetime) {
        return date10ToFullDate(datetime, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * Convert Date10 to full date time
     *
     * @param datetime Date10
     * @param format   Expected format
     * @return Full date time format
     */
    public static String date10ToFullDate(String datetime, String format) {
        while (datetime.length() < 10) {
            datetime = "0" + datetime;
        }
        String yyyy = now("yyyy");
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = "";
        try {
            // debug year transition
            if (datetime.length() > 4) {
                String month1 = datetime.substring(0, 4);
                if (month1.equals("1231")) {
                    String month2 = now("MMdd");
                    if (!month2.equals("0101")) {
                        int YYYY = Integer.parseInt(yyyy) - 1;
                        yyyy = YYYY + "";
                    }
                }
            }
            Date dateObject = dateFormat.parse(yyyy + datetime);
            dateFormat = new SimpleDateFormat(format);
            result = dateFormat.format(dateObject);
        } catch (ParseException e) {
            e.printStackTrace();
            result = MySQLDate();
        }
        return result;
    }

    /**
     * Date time
     *
     * @param format Date time format
     * @return String contains current date time
     */
    public static String date(String format) {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date dateObject = new Date();
            result = dateFormat.format(dateObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Date time
     *
     * @param format Date time format
     * @param date   Date time
     * @return String contains current date time
     */
    public static String date(String format, Date date) {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            result = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Date time
     *
     * @param format Date time format
     * @param time   Unix Timestamp
     * @return String contains current date time
     */
    public static String date(String format, long time) {
        String result = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date dateObject = new Date(time);
            result = dateFormat.format(dateObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Date yesterday
     *
     * @return Date yesterday
     */
    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * Date tomorrow
     *
     * @return Date tomorrow
     */
    public static Date tomorrow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        return cal.getTime();
    }

}
