package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.calendar.app.CalendarMemberService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import com.soyeon.sharedcalendar.member.dto.MeResponse;
import com.soyeon.sharedcalendar.member.exception.MemberNotFound;
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
    private final CalendarMemberService calendarMemberService;

    /**
     * 회원을 조회한다.
     * @param memberId
     * @return
     */
    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFound(memberId));
    }

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
        member.setRefreshToken(newHashToken);
        memberRepository.save(member);
    }

    /**
     * 회원의 프로필 사진을 업데이트한다.
     * @param member MemberImgMeta profileImg
     */
    public void updateProfileImage(Member member, MemberImgMeta meta) {
        member.updateProfileImage(meta);
        memberRepository.save(member);
    }

    /**
     * 회원 정보를 조회한다.
     * memberId, email, name, hasCalendar
     * @return
     */
    public MeResponse getCurrentMemberSummary() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Member m = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new MemberNotFound(memberId));
        boolean hasCalendar = calendarMemberService.existsByMemberId(memberId);
        return new MeResponse(m.getMemberId(),
                m.getName(),
                m.getEmail(),
                m.getProfileImgKey(),
                hasCalendar);
    }


}
