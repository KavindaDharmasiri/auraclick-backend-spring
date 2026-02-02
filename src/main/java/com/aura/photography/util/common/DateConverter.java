package com.aura.photography.util.common;

import java.time.LocalDate;

/**
 * @Author: kasun
 * @Package: com.aura.photography.util.common
 * @Class: DateConverter
 * @Created on: 2/1/2026 at 10:28 PM
 */
public final class DateConverter {

    private DateConverter() {
        // Private constructor to prevent instantiation
    }

    public static LocalDate convertToLocalDate(String date) {
        return LocalDate.parse(date);
    }
}
