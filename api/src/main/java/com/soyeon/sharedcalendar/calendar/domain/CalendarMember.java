package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.member.domain.Member;
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
//    private Long memberId; //Member.memberId

    @Enumerated(EnumType.STRING)
    private MemberRole roleCode;

    @Enumerated(EnumType.STRING)
    private CalendarAccessLevel accessLevel;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static CalendarMember create(Long calendarId, Member member, MemberRole roleCode, CalendarAccessLevel accessLevel) {
        CalendarMember cm = new CalendarMember();
        cm.calendarId = calendarId;
        cm.member = member;
        cm.roleCode = roleCode;
        cm.accessLevel = accessLevel;
        return cm;
    }

    public void changeAccessLevel(CalendarAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void changeMemberRole(MemberRole role) {
        this.roleCode = role;
    }
}
