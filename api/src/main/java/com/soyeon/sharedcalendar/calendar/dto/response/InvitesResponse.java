package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.invite.domain.InviteStatus;
import com.soyeon.sharedcalendar.invite.domain.Invitee;

import java.time.LocalDateTime;

public record InvitesResponse(String inviteeId,
                                  String email,
                                  InviteStatus status,
                                  int resendCount,
                                  CalendarAccessLevel accessLevel,
                                  LocalDateTime expiresAt,
                                  LocalDateTime lastSendAt) {
    public static InvitesResponse from (Invitee i) {
        return new InvitesResponse(String.valueOf(i.getInviteeId()),
                i.getEmail(),
                i.getStatus(),
                i.getResendCount(),
                i.getAccessLevel(),
                i.getExpiresAt(),
                i.getLastSentAt());
    }
}
