package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class CalendarResponse {
    private Long calendarId;
    private Long ownerId;
    private String calendarName;
    private CalendarAccessLevel accessLevel;
    private String profileImgKey;
    private List<CalendarEventResponse> events;

    private CalendarResponse(Long calendarId,
                             Long ownerId,
                             String calendarName,
                             CalendarAccessLevel accessLevel,
                             List<CalendarEventResponse> events) {
        this.calendarId = calendarId;
        this.ownerId = ownerId;
        this.calendarName = calendarName;
        this.accessLevel = accessLevel;
        this.events = events;
    }

    public static CalendarResponse of(Calendar c, List<CalendarEvent> events) {
        return new CalendarResponse(c.getCalendarId(),
                c.getOwnerId(),
                c.getCalendarName(),
                c.getDefaultAccessLevel(),
                events.stream().map(CalendarEventResponse::from).toList());
    }

    public void updateProfileImgKey(String profileImgKey) {
        this.profileImgKey = profileImgKey;
    }
}

