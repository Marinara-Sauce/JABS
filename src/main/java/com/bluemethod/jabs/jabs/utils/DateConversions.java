package com.bluemethod.jabs.jabs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Handles converting string dates to Date classes
 */
public class DateConversions {

    public static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");

    /**
     * Converts a string date into a date object
     *
     * @param str the string to convert
     * @return the java date
     */
    public static Date convertStrToDate(String str) {
        Date exp;

        try {
            exp = SDF.parse(str);
        } catch (ParseException e) {
            return null;
        }

        return exp;
    }
}
