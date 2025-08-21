package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends CrudRepository<Calendar, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Calendar c
            set c.calendarName = :#{#cal.calendarName},
                c.defaultAccessLevel = :#{#cal.defaultAccessLevel},
                c.profileImgUrl = :#{#cal.profileImgUrl}
            where c.calendarId = :#{#cal.calendarId}
    """)
    void update(@Param("cal") Calendar calendar);
}
