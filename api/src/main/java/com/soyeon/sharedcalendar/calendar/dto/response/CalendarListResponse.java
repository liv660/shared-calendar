package com.soyeon.sharedcalendar.calendar.dto.response;

public record CalendarListResponse(Long calendarId, String calendarName, String profileImgKey) {

    public static CalendarListResponse create(Long calendarId, String calendarName, String profileImgKey) {
            return new CalendarListResponse(calendarId, calendarName, profileImgKey);
    }
}
