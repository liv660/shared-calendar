package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.VisibilityType;
import com.soyeon.sharedcalendar.member.dto.MemberDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// 일정 단건 조회용
public record CalendarEventDetailResponse(String calendarEventId,
                                          String calendarId,
                                          String title,
                                          String contents,
                                          String categoryId,
                                          List<CalendarCategoriesResponse> categories,
                                          VisibilityType visibility,
                                          Set<MemberDto> visibleMembers,
                                          boolean allDay,
                                          String color,
                                          LocalDateTime startAt,
                                          LocalDateTime endAt,
                                          boolean isCreateByMe) {
    public static CalendarEventDetailResponse from(CalendarEvent e, List<CalendarCategoriesResponse> categories, Set<MemberDto> visibleMembers, boolean isCreateByMe) {
        return new CalendarEventDetailResponse(String.valueOf(e.getCalendarEventId()),
                String.valueOf(e.getCalendarId()),
                e.getTitle(),
                e.getContents(),
                String.valueOf(e.getCategoryId()),
                categories,
                e.getVisibility(),
                visibleMembers,
                e.isAllDay(),
                e.getColor(),
                e.getStartAt(),
                e.getEndAt(),
                isCreateByMe);
    }
}
