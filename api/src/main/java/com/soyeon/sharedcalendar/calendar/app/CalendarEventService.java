package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.EventVisibilityRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
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
    @Transactional
    public void createEvent(Long calendarId, CalendarEventRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId();
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
}
