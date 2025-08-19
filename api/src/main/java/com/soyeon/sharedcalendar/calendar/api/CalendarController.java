package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarService;
import com.soyeon.sharedcalendar.calendar.dto.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.CalendarResponse;
import com.soyeon.sharedcalendar.common.dto.DeleteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 관련 API")
public class CalendarController {
    private final CalendarService calendarService;

    @Operation(summary = "캘린더 생성", description = "새 캘린더를 생성한다.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CalendarResponse> createCalendar(@RequestBody CalendarRequest request) {
        CalendarResponse response = calendarService.createCalendar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "캘린더 삭제", description = "캘린더를 삭제한다.")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<DeleteResponse> deleteCalendar(@PathVariable Long calendarId) {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok(new DeleteResponse(String.valueOf(calendarId),"캘린더 삭제 완료"));
    }
}
