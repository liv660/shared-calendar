package com.soyeon.sharedcalendar.common.validator;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.exception.calendar.CalendarNotFound;
import com.soyeon.sharedcalendar.calendar.exception.category.CategoryNotFound;
import com.soyeon.sharedcalendar.calendar.exception.event.EventNotFound;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.exception.MemberNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidatorService {
    private final MemberRepository memberRepository;
    private final CalendarRepository calendarRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final CalendarCategoryRepository calendarCategoryRepository;

    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFound(memberId));
    }

    public Calendar validateCalendar(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarNotFound(calendarId));
    }

    public CalendarEvent validateEvent(Long calendarId, Long eventId) {
        validateCalendar(calendarId);
        return calendarEventRepository.findByCalendarEventIdAndCalendarId(eventId, calendarId)
                .orElseThrow(() -> new EventNotFound(calendarId, eventId));
    }

    public void validateCalendarAndMember(Long calendarId, Long memberId) {
        validateCalendar(calendarId);
        validateMember(memberId);
    }

    public void validateCategory(Long calendarId, Long categoryId) {
        calendarCategoryRepository.findByCategoryIdAndCalendarId(categoryId, calendarId)
                .orElseThrow(() -> new CategoryNotFound(calendarId, categoryId));
    }
}
