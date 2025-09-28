package com.soyeon.sharedcalendar.invite.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "invitee_status_history")
public class InviteeStatusHistory {
    @Id
    @GeneratedValue @SnowflakeId
    private Long historyId;

    private Long inviteeId; //Calendar.calendarId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus toStatus;

    @Column(nullable = true)
    private Long actorMemberId; //Member.memberId

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public static InviteeStatusHistory init(Long inviteeId) {
        InviteeStatusHistory history = new InviteeStatusHistory();
        history.inviteeId = inviteeId;
        history.fromStatus = InviteStatus.NONE;
        history.toStatus = InviteStatus.WAIT;
        return history;
    }

    public static InviteeStatusHistory create(Long inviteeId, InviteStatus fromStatus, InviteStatus toStatus, Long memberId) {
        InviteeStatusHistory history = new InviteeStatusHistory();
        history.inviteeId = inviteeId;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.actorMemberId = memberId;
        return history;
    }
}
