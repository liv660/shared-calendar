package com.soyeon.sharedcalendar.calendar.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CalendarAccessLevel {
    READ_ONLY,
    READ_WRITE,
    FULL_ACCESS;

    @JsonCreator
    public static CalendarAccessLevel from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return  CalendarAccessLevel.valueOf(value.toUpperCase());
    }
}
