package com.venegas.moneytracker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final SimpleDateFormat monthFormat =
            new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private static final SimpleDateFormat shortDateFormat =
            new SimpleDateFormat("dd/MM", Locale.getDefault());

    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }

    public static String formatMonth(long timestamp) {
        return monthFormat.format(new Date(timestamp));
    }

    public static String formatShortDate(long timestamp) {
        return shortDateFormat.format(new Date(timestamp));
    }

    public static long[] getCurrentMonthRange(int startDay) {
        Calendar calendar = Calendar.getInstance();

        // Inicio del período
        calendar.set(Calendar.DAY_OF_MONTH, startDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.DAY_OF_MONTH) < startDay) {
            calendar.add(Calendar.MONTH, -1);
        }

        long startDate = calendar.getTimeInMillis();

        // Fin del período (un mes después, día anterior)
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endDate = calendar.getTimeInMillis();

        return new long[]{startDate, endDate};
    }

    public static int getRemainingDays(int startDay) {
        long[] range = getCurrentMonthRange(startDay);
        long endDate = range[1];
        long today = Calendar.getInstance().getTimeInMillis();
        long diff = endDate - today;
        return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
    }

    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getMonthName(long timestamp) {
        SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        return monthNameFormat.format(new Date(timestamp));
    }
}