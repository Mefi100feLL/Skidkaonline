package com.popcorp.parser.skidkaonline.util;

import com.popcorp.parser.skidkaonline.Application;
import com.popcorp.parser.skidkaonline.entity.Error;

public class ErrorManager {

    private static SenderTLS tlsSender = new SenderTLS("mestoskidki.parser.popsuenko@gmail.com", "popsuenkoae16mestoskidki");

    public static void sendError(String subject, String error){
        if (!Application.getErrorRepository().exist(new Error(subject, error))){
            Application.getErrorRepository().save(new Error(subject, error));
            //tlsSender.send(subject, error, "mestoskidki.parser.popsuenko@gmail.com", "alexpopsuenko@gmail.com");
        }
    }

    public static void sendError(String error){
        sendError("SkidkaOnline Error", error);
    }
}
