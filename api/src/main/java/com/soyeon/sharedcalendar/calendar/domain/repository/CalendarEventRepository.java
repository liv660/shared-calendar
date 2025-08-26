package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends CrudRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByCalendarId(Long calendarId);

    @Query("""
        select ce
        from CalendarEvent ce
            inner join EventVisibility ev on ce.calendarEventId = ev.event.calendarEventId
        where ce.calendarId = :calendarId
            and (ce.visibility = 'PUBLIC'
                or (ce.visibility = 'PRIVATE' and ev.member.memberId = :memberId))
            and ce.startAt >= :from
            and ce.endAt <= :to
    """)
    List<CalendarEvent> findReadable(Long calendarId, Long memberId, LocalDateTime from, LocalDateTime to);

    CalendarEvent getCalendarEventByCalendarEventIdAndCalendarId(Long eventId, Long calendarId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CalendarEvent ce
            set ce.title = :#{#e.title},
                ce.contents = :#{#e.contents},
                ce.categoryId = :#{#e.categoryId},
                ce.visibility = :#{#e.visibility},
                ce.allDay = :#{#e.allDay},
                ce.color = :#{#e.color},
                ce.startAt = :#{#e.startAt},
                ce.endAt = :#{#e.endAt},
                ce.updatedBy = :memberId
    """)
    void update(Long memberId, @Param("e") CalendarEvent event);
}
