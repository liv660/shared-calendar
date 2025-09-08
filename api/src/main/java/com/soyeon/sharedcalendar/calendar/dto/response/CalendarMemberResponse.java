package com.soyeon.sharedcalendar.calendar.dto.response;

public record CalendarMemberResponse(String name, String email, String presignUrl) {
    public static CalendarMemberResponse create(String name, String email, String presignUrl) {
        return new CalendarMemberResponse(name, email, presignUrl);
    }
}
