package com.homemoney.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter BRAZILIAN_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String formatToBrazilianDate(LocalDate date) {
        return date != null ? date.format(BRAZILIAN_FORMATTER) : "";
    }
} 