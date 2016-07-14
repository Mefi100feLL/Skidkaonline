package com.popcorp.parser.skidkaonline.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaleCommentDateTimeMapper {

    public static long getDateTimeInLong(String dateTime){
        long result = 0;
        dateTime = dateTime.toLowerCase();
        Calendar currentTime = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy HH:mm", new Locale("ru"));
        try {
            result = format.parse(dateTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (result == 0){
            Matcher hoursMatcher = Pattern.compile("[0-9]* час. назад").matcher(dateTime);
            if (hoursMatcher.find()){
                int hours = Integer.valueOf(hoursMatcher.group().replace(" час. назад", ""));
                currentTime.add(Calendar.HOUR_OF_DAY, -hours);
                result = currentTime.getTimeInMillis();
            }
        }

        if (result == 0){
            Matcher hoursMatcher = Pattern.compile("[0-9]* мин. назад").matcher(dateTime);
            if (hoursMatcher.find()){
                int hours = Integer.valueOf(hoursMatcher.group().replace(" мин. назад", ""));
                currentTime.add(Calendar.MINUTE, -hours);
                result = currentTime.getTimeInMillis();
            }
        }

        if (result == 0){
            Matcher hoursMatcher = Pattern.compile("[0-9]* сек. назад").matcher(dateTime);
            if (hoursMatcher.find()){
                int hours = Integer.valueOf(hoursMatcher.group().replace(" сек. назад", ""));
                currentTime.add(Calendar.SECOND, -hours);
                result = currentTime.getTimeInMillis();
            }
        }

        return result;
    }
}
