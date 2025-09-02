package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.member.domain.OAuthLoginSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessListener {
    private final ProfileImageService profileImageService;
    private final MemberService memberService;

    @EventListener
    public void handleOAuthLoginSuccessEvent(OAuthLoginSuccessEvent event) {
        //이미지 다운로드 -> minio로 업로드
        profileImageService.ingestFromUrl(event.memberId(), event.profileImgUrl())
                .doOnSuccess(key -> {
                    log.info("[EventHandler] profile image saved (key): {}", key);
//                    memberService.updateProfileImage(memberId, key);
                })
                .doOnError(error -> log.info("profile image save failed: {}", error.getMessage()))
                .subscribe();
    }
}
