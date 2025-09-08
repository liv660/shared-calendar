package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarEventService;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.common.validate.ValidationSequence;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars/{calendarId}")
@Tag(name = "CalendarEvent", description = "일정 관련 API")
public class CalendarEventController {
    private final CalendarEventService calendarEventService;

    @Operation(summary = "일정 등록", description = "새로운 일정을 등록한다.")
    @PostMapping("/events")
    public void createEvent(@PathVariable Long calendarId, @Validated(ValidationSequence.class) @RequestBody CalendarEventRequest request) {
        calendarEventService.createEvent(calendarId, request);
    }

    @Operation(summary = "일정 삭제", description = "일정을 삭제한다.")
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long calendarId, @PathVariable Long eventId) {
        calendarEventService.deleteEvent(calendarId, eventId);
        return ResponseEntity.ok().body("삭제가 완료되었습니다.");
    }

    @Operation(summary = "일정 수정", description = "일정을 수정한다.")
    @PatchMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long calendarId, @PathVariable Long eventId, @RequestBody CalendarEventRequest request) {
        calendarEventService.updateEvent(calendarId, eventId, request);
        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }
}
