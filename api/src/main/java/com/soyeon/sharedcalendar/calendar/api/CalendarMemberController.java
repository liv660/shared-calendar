package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarMemberService;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarMemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calendars/{calendarId}")
@RequiredArgsConstructor
@Tag(name = "Calendar Member", description = "캘린더를 공유 중인 사용자 관련 API")
public class CalendarMemberController {
    private final CalendarMemberService calendarMemberService;

    @Operation(summary = "사용자 목록 조회", description = "캘린더 내 사용자 목록을 조회한다.")
    @GetMapping("/members")
    public List<CalendarMemberResponse> getMembers(@PathVariable Long calendarId) {
        return calendarMemberService.getMembers(calendarId);
    }
}
