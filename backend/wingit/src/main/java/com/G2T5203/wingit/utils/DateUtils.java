package com.G2T5203.wingit.utils;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static LocalDateTime parseDateTime(String datetimeString) throws ParseException {
        if (datetimeString.contains("T")) {
            datetimeString = datetimeString.replace("T", " ");
        }
        if (datetimeString.contains("_")) {
            datetimeString = datetimeString.replace("_", "-");
        }
        if (datetimeString.contains("x")) {
            datetimeString = datetimeString.replace("x", ":");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);
        return LocalDateTime.parse(datetimeString, formatter);
    }

    public static LocalDateTime handledParseDateTime(String datetimeString) {
        try {
            return parseDateTime(datetimeString);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public static LocalDate parseDate(String dateString) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDate handledParseDate(String dateString) {
        try {
            return parseDate(dateString);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String datetimeString = "2023-09-15 14:30:00";

        try {
            LocalDateTime date = parseDateTime(datetimeString);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
