package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_member")
@Getter
public class CalendarMember {
    @Id
    @GeneratedValue @SnowflakeId
    private Long calendarMemberId;
    private Long calendarId; //Calendar.calendarId
    private Long memberId; //Member.memberId
    @Enumerated(EnumType.STRING)
    private MemberRole roleCode;
    @Enumerated(EnumType.STRING)
    private CalendarAccessLevel accessLevel;
    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime joinedAt;
    private LocalDateTime deletedAt;

    public static CalendarMember create(Long calendarId, Long memberId, MemberRole roleCode, CalendarAccessLevel accessLevel) {
        CalendarMember cm = new CalendarMember();
        cm.calendarId = calendarId;
        cm.memberId = memberId;
        cm.roleCode = roleCode;
        cm.accessLevel = accessLevel;
        return cm;
    }
}
