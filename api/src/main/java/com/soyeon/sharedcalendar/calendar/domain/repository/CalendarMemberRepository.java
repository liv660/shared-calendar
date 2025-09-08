package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarMemberRepository extends JpaRepository<CalendarMember, Long> {
    boolean existsByMemberId(Long memberId);

    CalendarMember findCalendarMemberByCalendarIdAndMemberId(Long calendarId, Long memberId);

    List<CalendarMember> findMemberIdsByCalendarId(Long calendarId);
}
