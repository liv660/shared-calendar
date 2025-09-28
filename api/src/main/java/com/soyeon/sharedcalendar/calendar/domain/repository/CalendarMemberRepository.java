package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import com.soyeon.sharedcalendar.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarMemberRepository extends JpaRepository<CalendarMember, Long> {
    boolean existsByMember(Member member);

    CalendarMember findCalendarMemberByCalendarIdAndMember(Long calendarId, Member member);


    @Query("""
        select cm
        from CalendarMember cm
            join fetch cm.member
        where cm.calendarId = :calendarId
    """)
    List<CalendarMember> findAllMemberByCalendarId(Long calendarId);

    void deleteByCalendarIdAndMember(Long calendarId, Member member);
}
