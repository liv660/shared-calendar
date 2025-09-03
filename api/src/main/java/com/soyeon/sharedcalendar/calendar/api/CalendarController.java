package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarProfileImgService;
import com.soyeon.sharedcalendar.calendar.app.CalendarService;
import com.soyeon.sharedcalendar.calendar.domain.CalendarImgMeta;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarResponse;
import com.soyeon.sharedcalendar.common.dto.DeleteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 관련 API")
public class CalendarController {
    private final CalendarService calendarService;
    private final CalendarProfileImgService calendarProfileImgService;

    @Operation(summary = "캘린더 생성", description = "새 캘린더를 생성한다.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CalendarResponse createCalendar(@RequestBody CalendarRequest request) {
        // 캘린더 생성
        CalendarResponse response = calendarService.createCalendar(request);
        // 이미지 메타 저장
        CalendarImgMeta meta = calendarProfileImgService.createMetaForUpload(response.getCalendarId(), request.imgMeta());
        calendarProfileImgService.save(meta);

        response.updateProfileImgKey(meta.getObjectKey());
        return response;
    }

    @Operation(summary = "캘린더 삭제", description = "캘린더를 삭제한다.")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<DeleteResponse> deleteCalendar(@PathVariable Long calendarId) {
        calendarService.deleteCalendar(calendarId);
        return ResponseEntity.ok(new DeleteResponse(String.valueOf(calendarId),"캘린더 삭제 완료"));
    }

    @Operation(summary = "캘린더 수정", description = "캘린더를 수정한다.")
    @PatchMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> updateCalendar(@PathVariable Long calendarId, @RequestBody CalendarRequest request) {
        return ResponseEntity.ok(calendarService.updateCalendar(calendarId, request));
    }

    @Operation(summary = "캘린더 조회", description = "캘린더를 조회한다.")
    @GetMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> getCalendar(@PathVariable Long calendarId,
                                                        @RequestParam(required = false) LocalDateTime from,
                                                        @RequestParam(required = false) LocalDateTime to) {
        return ResponseEntity.ok(calendarService.getCalendar(calendarId, from, to));
    }
}
