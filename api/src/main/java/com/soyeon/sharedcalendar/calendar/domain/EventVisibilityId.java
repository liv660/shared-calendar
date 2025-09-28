package com.soyeon.sharedcalendar.calendar.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@EqualsAndHashCode
public class EventVisibilityId implements Serializable {
    private Long calendarEventId;
    private Long memberId;

    public EventVisibilityId() {}

    public EventVisibilityId(Long eventId, Long memberId) {
        this.calendarEventId = eventId;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        EventVisibilityId that = (EventVisibilityId) object;
        return Objects.equals(calendarEventId, that.calendarEventId) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calendarEventId, memberId);
    }
}
