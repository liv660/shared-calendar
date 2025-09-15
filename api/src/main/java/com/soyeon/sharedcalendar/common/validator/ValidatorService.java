package com.soyeon.sharedcalendar.common.validator;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.exception.calendar.CalendarNotFoundException;
import com.soyeon.sharedcalendar.calendar.exception.calendar.CalendarUnauthorizedException;
import com.soyeon.sharedcalendar.calendar.exception.category.CategoryNotFoundException;
import com.soyeon.sharedcalendar.calendar.exception.event.EventNotFoundException;
import com.soyeon.sharedcalendar.invite.domain.Invitee;
import com.soyeon.sharedcalendar.invite.domain.repository.InviteeRepository;
import com.soyeon.sharedcalendar.invite.exception.InviteeNotFoundException;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidatorService {
    private final MemberRepository memberRepository;
    private final CalendarRepository calendarRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final CalendarCategoryRepository calendarCategoryRepository;
    private final InviteeRepository inviteeRepository;

    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    public Calendar validateCalendar(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarNotFoundException(calendarId));
    }

    public CalendarEvent validateEvent(Long calendarId, Long eventId) {
        validateCalendar(calendarId);
        return calendarEventRepository.findByCalendarEventIdAndCalendarId(eventId, calendarId)
                .orElseThrow(() -> new EventNotFoundException(calendarId, eventId));
    }

    public void validateCalendarAndMember(Long calendarId, Long memberId) {
        validateCalendar(calendarId);
        validateMember(memberId);
    }

    public void validateCategory(Long calendarId, Long categoryId) {
        calendarCategoryRepository.findByCategoryIdAndCalendarId(categoryId, calendarId)
                .orElseThrow(() -> new CategoryNotFoundException(calendarId, categoryId));
    }

    public Invitee validateInviteToken(String inviteToken) {
        return inviteeRepository.findByInviteToken(inviteToken)
                .orElseThrow(InviteeNotFoundException::new);
    }

    public boolean isOwner(Calendar calendar, Long memberId) {
        boolean isOwner = calendar.getOwnerId().equals(memberId);
        if (!isOwner) {
            throw new CalendarUnauthorizedException(calendar.getCalendarId());
        }
        return true;
    }
}
