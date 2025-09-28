package com.soyeon.sharedcalendar.common.mail;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.dto.request.InviteRequest;
import com.soyeon.sharedcalendar.config.ses.AmazonSesClient;
import com.soyeon.sharedcalendar.invite.domain.Invitee;
import com.soyeon.sharedcalendar.invite.domain.repository.InviteeRepository;
import com.soyeon.sharedcalendar.invite.dto.InviteeAddRequest;
import com.soyeon.sharedcalendar.invite.app.InviteService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.ses.model.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonMailService {
    private final AmazonSesClient client;
    private final TemplateEngine templateEngine;
    private final MemberRepository memberRepository;
    private final InviteService inviteService;

    private static final String CHAR_SET = String.valueOf(StandardCharsets.UTF_8);
    private static final String FROM = "test.for.sharedcalendar@gmail.com";
    private final InviteeRepository inviteeRepository;

    @Value("${app.invite.base-url}")
    private String baseUrl;

    /**
     * 메일 전송 후 history를 저장한다.
     * @param inviteRequest
     * @param calendar
     */
    public void sendEmail(InviteRequest inviteRequest, Calendar calendar) {
        Member owner = memberRepository.findById(calendar.getOwnerId()).orElseThrow(() -> new MemberNotFoundException(calendar.getOwnerId()));
        String subject = "공유캘린더: " + calendar.getCalendarName() + "에서 당신을 초대합니다.";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        String inviteToken = UUID.randomUUID().toString();

        try {
            // 1. 메일 전송
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(inviteRequest.email()).build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(subject)
                                    .charset(CHAR_SET)
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .charset(CHAR_SET)
                                            .data(getHtmlBody(owner, calendar, inviteToken))
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .source(FROM)
                    .build();
            SendEmailResponse response = client.sesClient().sendEmail(request);
            log.info("Email sent successfully: messageId={}", response.messageId());

            // 2.history 기록
            // 2-1. 같은 이메일로 보낸 적 있는지 확인
            Invitee invited = inviteeRepository
                    .findByCalendarIdAndEmail(calendar.getCalendarId(), inviteRequest.email())
                    .orElse(null);
            // 2-2. 보낸 적 있으면 상태 업데이트 (resend)
            if (invited != null) {
                inviteService.resendInvitee(invited, inviteToken, inviteRequest.accessLevel());
                inviteService.addInviteeStatusHistory(invited.getInviteeId());
            } else {
                Invitee invitee = inviteService.addInvitee(InviteeAddRequest.create(
                        calendar.getCalendarId(),
                        inviteRequest.email(),
                        inviteToken,
                        calendar.getOwnerId(),
                        inviteRequest.accessLevel(),
                        expiresAt));

                inviteService.addInviteeStatusHistory(invitee.getInviteeId());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getHtmlBody(Member owner, Calendar c, String inviteToken) {
        String inviteUrl = baseUrl + "/invite/accept/" + inviteToken;
        Context context = new Context();
        context.setVariable("ownerName", owner.getName());
        context.setVariable("calendarName", c.getCalendarName());
        context.setVariable("inviteUrl", inviteUrl);
        context.setVariable("qnaEmail", FROM);
        return templateEngine.process("invite-mail", context);
    }
}
