package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.EventVisibility;
import com.soyeon.sharedcalendar.calendar.domain.EventVisibilityId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventVisibilityRepository extends CrudRepository<EventVisibility, EventVisibilityId> {
    EventVisibility findById_CalendarEventIdAndId_MemberId(Long calendarEventId, Long memberId);
}
