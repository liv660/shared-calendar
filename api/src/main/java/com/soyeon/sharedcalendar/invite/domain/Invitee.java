package com.soyeon.sharedcalendar.invite.domain;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.MemberRole;
import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.invite.dto.InviteeAddRequest;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "invitee")
public class Invitee {
    @Id
    @GeneratedValue @SnowflakeId
    private Long inviteeId;

    private Long calendarId; //Calendar.calendarId
    private String email;
    private String inviteToken;
    private Long inviteBy;

    @Column(insertable = false)
    private Long acceptMemberId; //Member.memberId

    @Column(insertable = false)
    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @Column(insertable = false)
    private int resendCount;

    @Enumerated(EnumType.STRING)
    private MemberRole roleCode;

    @Enumerated(EnumType.STRING)
    private CalendarAccessLevel accessLevel;

    private LocalDateTime expiresAt;

    @Column(insertable = false)
    private LocalDateTime lastSentAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(insertable = false)
    private LocalDateTime deletedAt;

    public static Invitee init(InviteeAddRequest request) {
        Invitee invitee = new Invitee();
        invitee.calendarId = request.calendarId();
        invitee.email = request.email();
        invitee.inviteToken = request.inviteToken();
        invitee.inviteBy = request.inviteBy();
        invitee.status = InviteStatus.WAIT;
        invitee.roleCode = MemberRole.USER;
        invitee.accessLevel = request.accessLevel();
        invitee.expiresAt = request.expiresAt();
        return invitee;
    }

    public Invitee resend(String inviteToken, CalendarAccessLevel accessLevel) {
        this.resendCount++;
        this.inviteToken = inviteToken;
        this.accessLevel = accessLevel;
        this.lastSentAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        return this;
    }

    public Invitee join(Long memberId) {
        this.acceptMemberId = memberId;
        this.status = InviteStatus.JOINED;
        return this;
    }

    public void changeStatus(InviteStatus status) {
        this.status = status;
    }

}
