package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "calendar_event")
@Getter
@DynamicUpdate
public class CalendarEvent {
    @Id
    @GeneratedValue @SnowflakeId
    private Long calendarEventId;

    private Long calendarId;

    @Column(length = 128, updatable = false)
    private String title;

    private String contents;
    private Long categoryId; //CaeldnarCategory.categoryId

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private VisibilityType visibility;

    private boolean allDay;

    @Column(length = 7, nullable = false)
    private String color;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(updatable = false)
    private Long createdBy;

    private Long updatedBy;

    @OneToMany(mappedBy = "event", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    Set<EventVisibility> visibilityMembers = new HashSet<>();

    public static CalendarEvent create(Long calendarId,
                                       Long memberId,
                                       String color,
                                       CalendarEventRequest request) {
        CalendarEvent event = new CalendarEvent();
        event.calendarId = calendarId;
        event.title = request.title();
        event.contents = request.contents();
        event.categoryId = request.categoryId();
        event.visibility = request.visibility();
        event.allDay = request.allDay();
        event.color = color;
        event.startAt = getStartDateTime(request.allDay(), request.startAt());
        event.endAt = getEndDateTime(request.allDay(), request.endAt());
        event.createdBy = memberId;
        event.updatedBy = memberId;
        event.visibilityMembers = new HashSet<>();
        return event;
    }

    public void changeVisibilityToPublic() {
        visibility = VisibilityType.PUBLIC;
    }

    public void allowAll() {
        visibilityMembers.clear();
    }

    public void allowOnly(Collection<Member> members) {
        visibilityMembers.clear();
        for (Member m : members) {
            visibilityMembers.add(EventVisibility.create(this, m));
        }
    }

    private static LocalDateTime getStartDateTime(boolean allDay, LocalDateTime startAt) {
        return allDay ? startAt.toLocalDate().atStartOfDay() : startAt;
    }

    private static LocalDateTime getEndDateTime(boolean allDay, LocalDateTime endAt) {
        return allDay ? endAt.toLocalDate().atTime(LocalTime.of(23, 59, 59)) : endAt;
    }

    public void changeColor(String categoryColor) {
        color = categoryColor;
    }
}
