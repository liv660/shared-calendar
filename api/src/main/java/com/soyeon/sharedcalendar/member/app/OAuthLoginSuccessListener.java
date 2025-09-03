package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.member.domain.OAuthLoginSuccessEvent;
import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessListener {
    private final MemberProfileImgService memberProfileImgService;
    private final MemberService memberService;

    @EventListener
    public void handleOAuthLoginSuccessEvent(OAuthLoginSuccessEvent event) {
        // 이미지 다운로드 -> minio로 업로드
        memberProfileImgService.ingestFromUrl(event.member().getMemberId(), event.profileImgUrl())
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    log.info("[handleOAuthLoginSuccessEvent] image upload success: {}", result);
                    // 이미지 meta 저장
                    MemberImgMeta meta = memberProfileImgService.createMetaForOAuthMember(result, event.member().getMemberId());
                    memberProfileImgService.save(meta);

                    // 이미지 update
                    memberService.updateProfileImage(event.member(), meta.getObjectKey());
                })
                .doOnError(error -> log.info("profile image save failed: {}", error.getMessage()))
                .subscribe();
    }
}
