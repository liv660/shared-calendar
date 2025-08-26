package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "event_visibility")
@Getter
public class EventVisibility {

    @EmbeddedId
    private EventVisibilityId id = new EventVisibilityId();

    @MapsId(value = "calendarEventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_event_id")
    private CalendarEvent event;
    @MapsId(value = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static EventVisibility create(CalendarEvent calendarEvent, Member member) {
        EventVisibility ev = new EventVisibility();
        ev.event = calendarEvent;
        ev.member = member;
        return ev;
    }
}