package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarEventService {
    private final CalendarEventRepository calendarEventRepository;

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
        CalendarEvent event = CalendarEvent.create(calendarId, memberId, request);
        if (event.getColor() == null || event.getColor().isBlank()) {
            event.changeColor(defaultColor);
        }
        if (request.visibility() == null) {
            event.changeVisibilityToPublic();
            event.allowAll();
        }
        calendarEventRepository.save(event);
    }
}
