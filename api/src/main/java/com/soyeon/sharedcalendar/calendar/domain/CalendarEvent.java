package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "calendar_event")
@Getter
public class CalendarEvent {
    @Id
    @GeneratedValue @SnowflakeId
    private Long calendarEventId;
    private Long calendarId;
    @Column(length = 128, nullable = false)
    private String title;
    private String contents;
    private Long categoryId;
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private VisibilityType visibility;
    private boolean allDay;
    @Column(length = 7, nullable = false)
    private String color;
    @Column(updatable = false)
    private LocalDateTime startAt;
    @Column(updatable = false)
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
                                       CalendarEventRequest request) {
        CalendarEvent event = new CalendarEvent();
        event.calendarId = calendarId;
        event.title = request.title();
        event.contents = request.contents();
        event.categoryId = request.categoryId();
        event.visibility = request.visibility();
        event.allDay = request.allDay();
        event.color = request.color();
        event.startAt = getDateTime(request.allDay(), request.startAt());
        event.endAt = getDateTime(request.allDay(), request.endAt());
        event.createdBy = memberId;
        event.updatedBy = memberId;
        event.visibilityMembers = new HashSet<>();
        return event;
    }

    public void changeColor(String color) {
        this.color = color;
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

    private static LocalDateTime getDateTime(boolean allDay, LocalDateTime localDateTime) {
        return allDay ? localDateTime.toLocalDate().atStartOfDay() : localDateTime;
    }
}
