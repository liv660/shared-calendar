package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.VisibilityType;

import java.time.LocalDateTime;

public record CalendarEventResponse(Long calendarEventId,
                                    Long calendarId,
                                    String title,
                                    String contents,
                                    Long categoryId,
                                    VisibilityType visibility,
                                    boolean allDay,
                                    String color,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt) {
    public static CalendarEventResponse from(CalendarEvent e) {
        return new CalendarEventResponse(e.getCalendarEventId(),
                e.getCalendarId(),
                e.getTitle(),
                e.getContents(),
                e.getCategoryId(),
                e.getVisibility(),
                e.isAllDay(),
                e.getColor(),
                e.getStartAt(),
                e.getEndAt());
    }
}
