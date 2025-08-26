package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.EventVisibilityRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soyeon.sharedcalendar.calendar.domain.VisibilityType.*;
import static com.soyeon.sharedcalendar.common.security.SecurityUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarEventService {
    private final MemberRepository memberRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final EventVisibilityRepository eventVisibilityRepository;

    @Value("${category.default-color}")
    private String defaultColor;

    /**
     * 새로운 일정을 등록한다.
     * @param calendarId
     * @param request
     */
    public void createEvent(Long calendarId, CalendarEventRequest request) {
        Long memberId = getCurrentMemberId();
        memberRepository.findById(Objects.requireNonNull(memberId)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        CalendarEvent event = CalendarEvent.create(calendarId, memberId, request);
        if (event.getColor() == null || event.getColor().isBlank()) {
            event.changeColor(defaultColor);
        }
        if (request.visibility() == null) { // PUBLIC
            event.changeVisibilityToPublic();
            event.allowAll();
        } else { // PRIVATE
            Set<Member> visibleMembers = request.visibleMemberIds()
                    .stream()
                    .map(memberRepository::getReferenceById)
                    .collect(Collectors.toSet());
            event.allowOnly(visibleMembers);
        }
        calendarEventRepository.save(event);
    }

    /**
     * 일정을 삭제한다. (일정을 생성한 사람이 삭제할 수 있음)
     * @param calendarId
     * @param eventId
     */
    @Transactional
    public void deleteEvent(Long calendarId, Long eventId) {
        Long memberId = getCurrentMemberId();
        CalendarEvent event = calendarEventRepository.getCalendarEventByCalendarEventIdAndCalendarId(eventId, calendarId);
        if (!event.getCreatedBy().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 일정을 생성한 사용자만 삭제할 수 있습니다.");
        }
        calendarEventRepository.delete(event);
    }

    /**
     * 일정을 수정한다. (일정을 생성 또는 공유중인 사용자가 수정할 수 있음)
     * @param calendarId
     * @param eventId
     * @param request
     */
    @Transactional
    public void updateEvent(Long calendarId, Long eventId, CalendarEventRequest request) {
        Long memberId = getCurrentMemberId();
        CalendarEvent event = calendarEventRepository.getCalendarEventByCalendarEventIdAndCalendarId(eventId, calendarId);
        if (event.getVisibility() == PUBLIC
            && !event.getCreatedBy().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "해당 일정을 생성한 사용자만 수정할 수 있습니다.");
        }
        //TODO 수정 기능 구현
        CalendarEvent updated = CalendarEvent.create(calendarId, memberId, request);
        calendarEventRepository.update(memberId, updated);


    }
}
