package com.soyeon.sharedcalendar.calendar.dto.response;

public record CalendarListResponse(String calendarId,
                                   String calendarName,
                                   String presignedUrl)
{
    public static CalendarListResponse create(
            Long calendarId,
            String calendarName,
            String presignedUrl) {
        return new CalendarListResponse(String.valueOf(calendarId),
                    calendarName,
                    presignedUrl);
    }
}
