package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;

import java.util.List;

public record CalendarResponse(
        Long calendarId,
        Long ownerId,
        String calendarName,
        CalendarAccessLevel accessLevel,
        String profileImgKey,
        List<CalendarEventResponse> events
) {
    public static CalendarResponse of(Calendar c, List<CalendarEvent> events) {
        return new CalendarResponse(c.getCalendarId(),
                c.getOwnerId(),
                c.getCalendarName(),
                c.getDefaultAccessLevel(),
                c.getProfileImgKey(),
                events.stream().map(CalendarEventResponse::from).toList());
    }
}

