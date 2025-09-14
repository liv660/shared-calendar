package com.soyeon.sharedcalendar.invite.app;

import com.soyeon.sharedcalendar.calendar.app.CalendarMemberService;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.invite.domain.InviteStatus;
import com.soyeon.sharedcalendar.invite.domain.Invitee;
import com.soyeon.sharedcalendar.invite.domain.InviteeStatusHistory;
import com.soyeon.sharedcalendar.invite.domain.repository.InviteeRepository;
import com.soyeon.sharedcalendar.invite.domain.repository.InviteeStatusHistoryRepository;
import com.soyeon.sharedcalendar.invite.dto.InviteeAddRequest;
import com.soyeon.sharedcalendar.invite.exception.InviteeNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InviteService {
    private final ValidatorService validatorService;
    private final CalendarMemberService calendarMemberService;
    private final InviteeRepository inviteeRepository;
    private final InviteeStatusHistoryRepository inviteeStatusHistoryRepository;

    /**
     * 초대 내역을 저장한다.
     * @param request
     */
    @Transactional
    public Invitee addInvitee(InviteeAddRequest request) {
        Invitee invitee = Invitee.init(request);
        return inviteeRepository.save(invitee);
    }

    /**
     * 초대 내역 히스토리를 저장한다.
     * @param inviteId
     */
    @Transactional
    public void addInviteeStatusHistory(Long inviteId) {
        InviteeStatusHistory history = InviteeStatusHistory.init(inviteId);
        inviteeStatusHistoryRepository.save(history);
    }

    /**
     * 초대 상태를 업데이트한다. (WAIT -> ACCEPTED)
     * @param inviteToken
     */
    @Transactional
    public void accept(String inviteToken) {
        // 초대 상태 update
        Invitee invitee = validatorService.validateInviteToken(inviteToken);
        invitee.changeStatus(InviteStatus.ACCEPTED);
        inviteeRepository.save(invitee);

        // 초대 이력 update
        InviteeStatusHistory history = inviteeStatusHistoryRepository.findTopByInviteeIdOrderByCreatedAtDesc(invitee.getInviteeId())
                .orElseThrow(InviteeNotFound::new);
        InviteeStatusHistory newHistory = InviteeStatusHistory.create(
                invitee.getInviteeId(), history.getToStatus(), invitee.getStatus(), null);
        inviteeStatusHistoryRepository.save(newHistory);
    }

    /**
     * 초대 받은 이력이 있는지 조회한다.
     * @param email
     * @return
     */
    public boolean existsInviteFor(String email) {
        return inviteeRepository.existsByEmail(email);
    }

    /**
     * 회원 가입이 완료되어 초대 상태를 가입으로 처리한다.
     * @param email
     * @param memberId
     * @return calendarId
     */
    @Transactional
    public Long markInviteAsJoined(String email, Long memberId) {
        Invitee invitee = inviteeRepository.findByEmail(email)
                .orElseThrow(InviteeNotFound::new);
        Invitee joined = invitee.join(memberId);
        inviteeRepository.save(joined);

        InviteeStatusHistory history = inviteeStatusHistoryRepository.findTopByInviteeIdOrderByCreatedAtDesc(invitee.getInviteeId())
                .orElseThrow(InviteeNotFound::new);
        InviteeStatusHistory newHistory = InviteeStatusHistory.create(
                invitee.getInviteeId(), history.getToStatus(), invitee.getStatus(), memberId);
        inviteeStatusHistoryRepository.save(newHistory);

        // calendar member로 등록한다
        calendarMemberService.addMember(invitee.getCalendarId(), memberId, invitee.getAccessLevel());
        return invitee.getCalendarId();
    }

    /**
     * 재발송인 경우 resendCount, inviteToken, accessLevel, lastSentAt, expiresAt을 업데이트한다.
     * @param invited
     * @param inviteToken
     */
    @Transactional
    public void resendInvitee(Invitee invited, String inviteToken, CalendarAccessLevel accessLevel) {
        Invitee resend = invited.resend(inviteToken, accessLevel);
        inviteeRepository.save(resend);
    }


}
