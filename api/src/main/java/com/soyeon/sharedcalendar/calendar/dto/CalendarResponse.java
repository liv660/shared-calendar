package com.soyeon.sharedcalendar.calendar.dto;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

public record CalendarResponse(
        Long calendarId,
        Long ownerId,
        String calendarName,
        CalendarAccessLevel accessLevel,
        String profileImgUrl,
        List<CalendarEvent> events
) {
    public static CalendarResponse create(Long calendarId, Long ownerId, String calendarName, CalendarAccessLevel accessLevel, String profileImgUrl) {
        return new CalendarResponse(calendarId, ownerId, calendarName, accessLevel, profileImgUrl, new ArrayList<>());
    }
}

