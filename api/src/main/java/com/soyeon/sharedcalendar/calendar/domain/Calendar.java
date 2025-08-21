package com.soyeon.sharedcalendar.calendar.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar")
@Getter
@DynamicUpdate
public class Calendar {
    @Id
    private Long calendarId;
    private Long ownerId;
    private String calendarName;
    @Enumerated(EnumType.STRING)
    private CalendarAccessLevel defaultAccessLevel;
    private String profileImgUrl;
    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static Calendar create(Long calendarId, Long ownerId, String calendarName, CalendarAccessLevel accessLevel, String profileImgUrl) {
        Calendar c = new Calendar();
        c.calendarId = calendarId;
        c.ownerId = ownerId;
        c.calendarName = calendarName;
        c.defaultAccessLevel = accessLevel;
        c.profileImgUrl = profileImgUrl;
        return c;
    }

    public void changeCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public void changeDefaultAccessLevel(CalendarAccessLevel accessLevel) {
        this.defaultAccessLevel = accessLevel;
    }

    public void changeProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }
}
