package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarMemberRepository extends CrudRepository<CalendarMember, Long> {
    boolean existsByMemberId(Long memberId);
}
