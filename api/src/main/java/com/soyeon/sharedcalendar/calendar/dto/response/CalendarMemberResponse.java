package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;

public record CalendarMemberResponse(String memberId, String name, String email, String presignedUrl, CalendarAccessLevel accessLevel, boolean isOwner) {
    public static CalendarMemberResponse create(Long memberId, String name, String email, String presignedUrl, CalendarAccessLevel accessLevel, boolean isOwner) {
        return new CalendarMemberResponse(String.valueOf(memberId), name, email, presignedUrl, accessLevel, isOwner);
    }
}
