package com.soyeon.sharedcalendar.calendar.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public final class CalendarUtils {
    public static LocalDate getFirstWeekDateOfMonth(LocalDate date) {
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public static LocalDate getLastWeekDateOfMonth(LocalDate date) {
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        DayOfWeek weekEndsOn = DayOfWeek.of((DayOfWeek.SUNDAY.getValue() + 5) % 7 + 1);
        return lastDay.with(TemporalAdjusters.nextOrSame(weekEndsOn));
    }

    public static LocalDateTime getDefaultStartDate() {
        LocalDate starDate = CalendarUtils.getFirstWeekDateOfMonth(LocalDate.now());
        return LocalDateTime.of(starDate, LocalTime.of(0, 0, 0));
    }

    public static LocalDateTime getDefaultEndDate() {
        LocalDate endDate = CalendarUtils.getLastWeekDateOfMonth(LocalDate.now());
        return LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
    }
}
