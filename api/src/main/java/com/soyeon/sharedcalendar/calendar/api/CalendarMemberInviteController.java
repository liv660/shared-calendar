package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarMemberInviteService;
import com.soyeon.sharedcalendar.calendar.dto.request.InviteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars/{calendarId}/members/invite")
@Tag(name = "Calendar Member Invite", description = "캘린더 멤버 초대 관련 API")
public class CalendarMemberInviteController {
    private final CalendarMemberInviteService calendarMemberInviteService;

    @Operation(summary = "사용자 초대", description = "사용자를 초대한다.")
    @PostMapping
    public void invite(@PathVariable Long calendarId, @RequestBody InviteRequest request) {
        calendarMemberInviteService.invite(calendarId, request);

    }
}
