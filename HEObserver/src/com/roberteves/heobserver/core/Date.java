package com.roberteves.heobserver.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date {
    public static java.util.Date ParseDate(String date, String format) throws ParseException {
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.parse(date);
    }

    public static String FormatDate(java.util.Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public static Long GetTimeDifference(java.util.Date date1, java.util.Date date2) {
        return date1.getTime() - date2.getTime();
    }
}
