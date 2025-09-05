package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import com.soyeon.sharedcalendar.calendar.domain.MemberRole;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarMemberRepository;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarMemberService {
    private final CalendarMemberRepository calendarMemberRepository;

    /**
     * 회원이 소유 또는 공유 중인 캘린더가 있는지 조회한다.
     * @param memberId
     * @return
     */
    public boolean existsByMemberId(Long memberId) {
        return calendarMemberRepository.existsByMemberId(memberId);
    }

    /**
     * 캘린더의 사용자로 등록한다.
     * @param calendarId
     * @param role ADMIN / USER
     * @param accessLevel READ_ONLY, READ_WRITE, FULL_ACCESS
     */
    @Transactional
    public void addMember(Long calendarId, MemberRole role, CalendarAccessLevel accessLevel) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        CalendarMember member = CalendarMember.create(calendarId, memberId, role, accessLevel);
        calendarMemberRepository.save(member);
    }
}
