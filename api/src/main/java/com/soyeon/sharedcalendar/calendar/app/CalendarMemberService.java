package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
