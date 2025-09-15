package com.soyeon.sharedcalendar.calendar.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class CalendarResponse {
    private String calendarId;
    private String calendarName;
    private CalendarAccessLevel defaultAccessLevel;
    private CalendarAccessLevel myAccessLevel;
    private boolean isOwner;
    private List<CalendarEventResponse> events;

    private CalendarResponse(String calendarId,
                             String calendarName,
                             CalendarAccessLevel defaultAccessLevel,
                             CalendarAccessLevel myAccessLevel,
                             boolean isOwner,
                             List<CalendarEventResponse> events) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.defaultAccessLevel = defaultAccessLevel;
        this.isOwner = isOwner;
        this.myAccessLevel = myAccessLevel;
        this.events = events;
    }

    public static CalendarResponse of(Calendar c,
                                      CalendarAccessLevel myAccessLevel,
                                      boolean isOwner,
                                      List<CalendarEvent> events) {
        return new CalendarResponse(String.valueOf(c.getCalendarId()),
                c.getCalendarName(),
                c.getDefaultAccessLevel(),
                myAccessLevel,
                isOwner,
                events.stream().map(CalendarEventResponse::from).toList());
    }

    @JsonProperty("isOwner")
    public boolean isOwner() {
        return isOwner;
    }
}

