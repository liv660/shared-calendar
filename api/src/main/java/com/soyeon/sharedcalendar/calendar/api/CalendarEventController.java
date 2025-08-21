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
    public ResponseEntity<?> createEvent(@PathVariable Long calendarId, @Validated(ValidationSequence.class) @RequestBody CalendarEventRequest request) {
        calendarEventService.createEvent(calendarId, request);
        return ResponseEntity.ok().body("일정이 등록되었습니다.");
    }
}
