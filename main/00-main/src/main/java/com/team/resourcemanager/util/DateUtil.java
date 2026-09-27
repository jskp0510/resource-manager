package com.team.resourcemanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // 현재 날짜
    public static LocalDate today() {
        return LocalDate.now();
    }

    // 두 날짜 사이의 일수
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // 날짜 → 문자열
    public static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 문자열 → 날짜
    public static LocalDate parse(String date) {
        return LocalDate.parse(
                date,
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );
    }

    private DateUtil() {
    }
}