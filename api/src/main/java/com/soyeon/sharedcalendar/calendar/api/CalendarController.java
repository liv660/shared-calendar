package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarMemberService;
import com.soyeon.sharedcalendar.calendar.app.CalendarProfileImgService;
import com.soyeon.sharedcalendar.calendar.app.CalendarService;
import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarImgMeta;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarListResponse;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 관련 API")
public class CalendarController {
    private final CalendarService calendarService;
    private final CalendarMemberService calendarMemberService;
    private final CalendarProfileImgService calendarProfileImgService;

    @Operation(summary = "캘린더 생성", description = "새 캘린더를 생성한다.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void createCalendar(@RequestBody CalendarRequest request) {
        // 캘린더 생성
        Calendar calendar = calendarService.createCalendar(request);

        // 캘린더 사용자로 등록
        calendarMemberService.initMember(calendar.getCalendarId());

        // 이미지 메타 저장
        if (request.imgMeta() != null) {
            CalendarImgMeta meta = calendarProfileImgService.createMetaForUpload(calendar.getCalendarId(), request.imgMeta());
            calendarProfileImgService.save(meta);

            // 캘린더에 이미지 update
            calendarService.changeProfileImg(calendar, meta.getObjectKey());
        }
    }

    @Operation(summary = "공유 중인 캘린더 목록 조회", description = "공유 중인 캘린더 목록을 조회한다.")
    @GetMapping("/list")
    public List<CalendarListResponse> calendarList() {
        return calendarService.getCalendarList();
    }

    @Operation(summary = "캘린더 삭제", description = "캘린더를 삭제한다.")
    @DeleteMapping("/{calendarId}")
    public void deleteCalendar(@PathVariable Long calendarId) {
        calendarService.deleteCalendar(calendarId);
    }

    @Operation(summary = "캘린더 수정", description = "캘린더를 수정한다.")
    @PatchMapping("/{calendarId}")
    public CalendarResponse updateCalendar(@PathVariable Long calendarId, @RequestBody CalendarRequest request) {
        return calendarService.updateCalendar(calendarId, request);
    }

    @Operation(summary = "캘린더 조회", description = "캘린더를 조회한다. (상세 일정 포함)")
    @GetMapping("/{calendarId}")
    public CalendarResponse getCalendar(@PathVariable Long calendarId,
                                                        @RequestParam(required = false) LocalDateTime from,
                                                        @RequestParam(required = false) LocalDateTime to) {
        return calendarService.getCalendar(calendarId, from, to);
    }

    @Operation(summary = "캘린더 관리 권한 위임", description = "캘린더 관리자를 변경한다.")
    @PatchMapping("/{calendarId}/owner")
    public void changeOwner(@PathVariable Long calendarId, @RequestBody Long memberId) {
        calendarService.changeOwner(calendarId, memberId);
    }
}
