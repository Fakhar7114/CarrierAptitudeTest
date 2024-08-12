package com.alphacoder.carrieraptitudetest.helpers;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static String getTime(Long time) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
        Date date = new Date(time);
        return dateFormat.format(date);


    }

    @SuppressLint("DefaultLocale")
    public static String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String getExactTime(long timeInMilli) {

        // Convert milliseconds to Date
        Date testDate = new Date(timeInMilli);

        // Calendar instances for comparison
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.setTime(testDate);

        Calendar today = Calendar.getInstance();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        // Formatters
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:a");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        String displayTime;

        // Determine which format to use
        if (isSameDay(testCalendar, today)) {
            displayTime = "Today at " + timeFormatter.format(testDate);
        } else if (isSameDay(testCalendar, yesterday)) {
            displayTime = "Yesterday at " + timeFormatter.format(testDate);
        } else {
            displayTime = dateFormatter.format(testDate);
        }
        return displayTime;

    }
    public static String getExactTimeForInbox(long timeInMilli) {

        // Convert milliseconds to Date
        Date testDate = new Date(timeInMilli);

        // Calendar instances for comparison
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.setTime(testDate);

        Calendar today = Calendar.getInstance();

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        // Formatters
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:a");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        String displayTime;

        // Determine which format to use
        if (isSameDay(testCalendar, today)) {
            displayTime = timeFormatter.format(testDate);
        } else if (isSameDay(testCalendar, yesterday)) {
            displayTime = "Yesterday at " + timeFormatter.format(testDate);
        } else {
            displayTime = dateFormatter.format(testDate);
        }
        return displayTime;

    }
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDayAndDate(long time){

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd yyyy, hh:mm a", Locale.getDefault());
        Date date=new Date(time);
        return dateFormat.format(date);
    }

}
