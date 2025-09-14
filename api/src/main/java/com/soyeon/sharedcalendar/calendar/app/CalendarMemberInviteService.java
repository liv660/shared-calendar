package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.dto.request.InviteRequest;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.common.mail.AmazonMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarMemberInviteService {
    private final ValidatorService validatorService;
    private final AmazonMailService mailService;

    /**
     * 사용자에게 초대 메일을 전송한다.
     * @param calendarId
     * @param request
     */
    public void invite(Long calendarId, InviteRequest request) {
        Calendar c = validatorService.validateCalendar(calendarId);
        mailService.sendEmail(request, c);
    }
}
