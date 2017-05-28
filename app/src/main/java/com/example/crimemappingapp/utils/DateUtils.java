package com.example.crimemappingapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtils {
    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private static final Map<Integer, String> monthStringMap;
    static {
        monthStringMap = new HashMap<>();
        monthStringMap.put(0, "Jan");
        monthStringMap.put(1, "Feb");
        monthStringMap.put(2, "Mar");
        monthStringMap.put(3, "Apr");
        monthStringMap.put(4, "May");
        monthStringMap.put(5, "Jun");
        monthStringMap.put(6, "Jul");
        monthStringMap.put(7, "Aug");
        monthStringMap.put(8, "Sep");
        monthStringMap.put(9, "Oct");
        monthStringMap.put(10, "Nov");
        monthStringMap.put(11, "Dec");
    };

    public static String buildDateDisplay(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static String buildDateDisplay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public static long convertToMillis(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("Invalid date format", e.getMessage());
        }
        return 0;
    }

    public static int getYear(long dateMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);
        return calendar.get(Calendar.YEAR);
    }

    public static String getMonthString(int month) {
        return monthStringMap.get(month);
    }

    public static String getMonthString(long dateMillis) {
        return monthStringMap.get(getMonth(dateMillis));
    }

    public static int getMonth(long dateMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);
        return calendar.get(Calendar.MONTH);
    }
}