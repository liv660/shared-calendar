package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Calendar c
            set c.calendarName = :#{#cal.calendarName},
                c.defaultAccessLevel = :#{#cal.defaultAccessLevel},
                c.profileImgKey = :#{#cal.profileImgKey}
            where c.calendarId = :#{#cal.calendarId}
    """)
    void update(@Param("cal") Calendar calendar);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Calendar c
            set c.profileImgKey = :profileImgKey
            where c.calendarId = :calendarId
    """)
    void updateProfileImgKey(Long calendarId, String profileImgKey);

    @Query("""
        select c
        from Calendar c
            join CalendarMember cm
                on c.calendarId = cm.calendarId
        where cm.memberId = :memberId
    """)
    List<Calendar> findAllCalendarsByMemberId(Long memberId);
}
