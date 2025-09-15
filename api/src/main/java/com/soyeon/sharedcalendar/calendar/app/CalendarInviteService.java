package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.dto.request.InviteRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.InvitesResponse;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.common.mail.AmazonMailService;
import com.soyeon.sharedcalendar.invite.domain.InviteStatus;
import com.soyeon.sharedcalendar.invite.domain.repository.InviteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarInviteService {
    private final ValidatorService validatorService;
    private final AmazonMailService mailService;
    private final InviteeRepository inviteeRepository;

    /**
     * 사용자에게 초대 메일을 전송한다.
     * @param calendarId
     * @param request
     */
    public void invite(Long calendarId, InviteRequest request) {
        Calendar c = validatorService.validateCalendar(calendarId);
        mailService.sendEmail(request, c);
    }

    /**
     * 사용자에게 전송한 초대 내역을 조회한다.
     * @param calendarId
     * @return
     */
    public List<InvitesResponse> getInviteeList(Long calendarId) {
        validatorService.validateCalendar(calendarId);
        return inviteeRepository.findByCalendarId(calendarId)
                .stream()
                .filter(i -> (!(i.getStatus().equals(InviteStatus.JOINED)) && !(i.getStatus().equals(InviteStatus.ACCEPTED_JOINED))))
                .map(InvitesResponse::from)
                .toList();
    }

    /**
     * 사용자에게 초대 메일을 재전송한다.
     * @param calendarId
     * @param request
     */
    public void resend(Long calendarId, List<InviteRequest> request) {
        Calendar c = validatorService.validateCalendar(calendarId);
        request.forEach(inviteRequest -> {
            mailService.sendEmail(inviteRequest, c);
        });
    }

    /**
     * 사용자 초대를 취소한다. (초대 내역 삭제)
     * @param calendarId
     * @param request
     */
    @Transactional
    public void cancel(Long calendarId, List<InviteRequest> request) {
        Calendar c = validatorService.validateCalendar(calendarId);
        request.forEach(inviteRequest -> {
            inviteeRepository.deleteByCalendarIdAndEmail(calendarId, inviteRequest.email());
        });
    }
}
