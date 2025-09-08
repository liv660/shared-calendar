package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "calendar_category")
public class CalendarCategory {
    @Id
    @GeneratedValue @SnowflakeId
    private Long categoryId;

    private Long calendarId; //Calendar.calendarId
    private String categoryName;
    private String categoryColor;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public static CalendarCategory create(Long calendarId, String categoryName, String categoryColor) {
        CalendarCategory cc = new CalendarCategory();
        cc.calendarId = calendarId;
        cc.categoryName = categoryName;
        cc.categoryColor = categoryColor;
        return cc;
    }
}
