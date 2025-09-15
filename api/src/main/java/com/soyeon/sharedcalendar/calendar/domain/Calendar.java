package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
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
    @GeneratedValue @SnowflakeId
    private Long calendarId;

    private Long ownerId;

    @Column(length = 50, nullable = false)
    private String calendarName;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private CalendarAccessLevel defaultAccessLevel;

    @Column(length = 2083)
    private String profileImgKey;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public static Calendar create(Long ownerId, String calendarName, CalendarAccessLevel accessLevel) {
        Calendar c = new Calendar();
        c.ownerId = ownerId;
        c.calendarName = calendarName;
        c.defaultAccessLevel = accessLevel;
        return c;
    }

    public void changeCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public void changeDefaultAccessLevel(CalendarAccessLevel accessLevel) {
        this.defaultAccessLevel = accessLevel;
    }

    public void changeProfileImg(String profileImgKey) {
        this.profileImgKey = profileImgKey;
    }

    public void changeOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
