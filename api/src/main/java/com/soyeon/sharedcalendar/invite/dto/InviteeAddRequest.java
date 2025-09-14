package com.soyeon.sharedcalendar.invite.dto;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;

import java.time.LocalDateTime;

public record InviteeAddRequest(Long calendarId, String email, String inviteToken, Long inviteBy, CalendarAccessLevel accessLevel, LocalDateTime expiresAt) {

    public static InviteeAddRequest create(Long calendarId, String email, String inviteToken, Long inviteBy, CalendarAccessLevel accessLevel, LocalDateTime expiresAt) {
        return new InviteeAddRequest(calendarId, email, inviteToken, inviteBy, accessLevel, expiresAt);
    }
}
