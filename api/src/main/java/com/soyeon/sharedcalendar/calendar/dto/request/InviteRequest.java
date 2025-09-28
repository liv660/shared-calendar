package com.soyeon.sharedcalendar.calendar.dto.request;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;

public record InviteRequest(String email, CalendarAccessLevel accessLevel) {
}
