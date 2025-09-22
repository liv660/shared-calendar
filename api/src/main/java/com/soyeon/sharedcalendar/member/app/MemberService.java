package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarMemberRepository;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import com.soyeon.sharedcalendar.member.domain.repository.MemberImgMetaRepository;
import com.soyeon.sharedcalendar.member.dto.MeRequest;
import com.soyeon.sharedcalendar.member.dto.MeResponse;
import com.soyeon.sharedcalendar.security.oauth2.AppOAuth2User;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.security.oauth2.OAuth2Provider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final CalendarMemberRepository calendarMemberRepository;
    private final ValidatorService validatorService;
    private final ImgService imgService;
    private final MemberProfileImgService memberProfileImgService;
    private final MemberImgMetaRepository memberImgMetaRepository;

    /**
     * 회원을 조회한다.
     * @param provider
     * @param providerUserId
     * @return
     */
    public Member findMember(OAuth2Provider provider, String providerUserId) {
        return memberRepository.findByProviderAndProviderUserId(provider, providerUserId).orElse(null);
    }

    /**
     * 회원 정보를 저장한다.
     * @param principal
     * @return
     */
    public Member createMember(AppOAuth2User principal) {
        Member member = Member.create(principal.getProvider(),
                principal.getProviderUserId(),
                principal.getEmail(),
                principal.getName());
        memberRepository.save(member);
        return member;
    }

    /**
     * 재발급한 refreshToken 해시 저장
     * @param member
     * @param newHashToken
     */
    @Transactional
    public void updateRefreshToken(Member member, String newHashToken) {
        member.updateRefreshToken(newHashToken);
        memberRepository.save(member);
    }

    /**
     * 회원의 프로필 사진을 업데이트한다.
     * @param member MemberImgMeta profileImg
     */
    public void updateProfileImageForOAuth(Member member, String objectKey) {
        member.updateProfileImage(objectKey);
        memberRepository.save(member);
    }

    /**
     * 회원 정보를 조회한다.
     * email, name, imgUrl, hasCalendar
     * @return
     */
    public MeResponse getCurrentMemberSummary() {
        Member me = validatorService.validateMember(SecurityUtils.getCurrentMemberId());
        boolean hasCalendar = calendarMemberRepository.existsByMember(me);
        return new MeResponse(String.valueOf(me.getMemberId()),
                me.getName(),
                me.getEmail(),
                imgService.getPresignedUrlByObjectKey(me.getProfileImgKey()),
                hasCalendar);
    }

    /**
     * 회원 정보를 수정한다.
     * @param request
     * @return
     */
    @Transactional
    public void updateMe(MeRequest request) {
        Member member = validatorService.validateMember(SecurityUtils.getCurrentMemberId());
        // 이름 수정
        if (request.name() != null && !request.name().isEmpty()) {
            member.changeName(request.name());
        }

        // 프로필 사진 수정
        if (request.imgMeta() != null) {
            MemberImgMeta meta = memberProfileImgService.createMetaForUpload(request.imgMeta());
            memberImgMetaRepository.save(meta);

            member.updateProfileImage(meta.getObjectKey());
            memberRepository.save(member);
        }
    }
}
