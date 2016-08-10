package com.popcorp.parser.skidkaonline.util;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.Error;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ErrorManager {

    private static SenderTLS tlsSender = new SenderTLS("mestoskidki.parser.popsuenko@gmail.com", "popsuenkoae16mestoskidki");

    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));

    public static void sendError(String subject, String error) {
        Calendar datetime = Calendar.getInstance();
        Application.getErrorRepository().save(new Error(subject, error, format.format(datetime.getTime())));
        //tlsSender.send(subject, error, "mestoskidki.parser.popsuenko@gmail.com", "alexpopsuenko@gmail.com");
    }

    public static void sendError(String error) {
        sendError("SkidkaOnline Error", error);
    }
}
