package com.soyeon.sharedcalendar.calendar.api;

import com.soyeon.sharedcalendar.calendar.app.CalendarInviteService;
import com.soyeon.sharedcalendar.calendar.dto.request.InviteRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.InvitesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars/{calendarId}/invites")
@Tag(name = "Calendar Member Invite", description = "캘린더 멤버 초대 관련 API")
public class CalendarInviteController {
    private final CalendarInviteService calendarInviteService;

    @Operation(summary = "사용자 초대", description = "사용자를 초대한다.")
    @PostMapping
    public void invite(@PathVariable Long calendarId, @RequestBody InviteRequest request) {
        calendarInviteService.invite(calendarId, request);
    }

    @Operation(summary = "사용자 초대 내역 조회", description = "사용자에게 보낸 초대 내역을 조회한다.")
    @GetMapping
    public List<InvitesResponse> invites(@PathVariable Long calendarId) {
        return calendarInviteService.getInviteeList(calendarId);
    }

    @Operation(summary = "사용자 재초대", description = "사용자에게 초대 메일을 재발송한다.")
    @PostMapping("/resend")
    public void resend(@PathVariable Long calendarId, @RequestBody List<InviteRequest> request) {
        calendarInviteService.resend(calendarId, request);
    }

    @Operation(summary = "사용자 초대 취소", description = "사용자 초대를 취소한다.")
    @PostMapping("/cancel")
    public void cancel(@PathVariable Long calendarId, @RequestBody List<InviteRequest> request) {
        calendarInviteService.cancel(calendarId, request);
    }
}
