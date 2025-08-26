package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.auth.security.AppOAuth2User;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Value("${profile.default-member}")
    private String defaultProfileImgUrl;


    /**
     * 회원 정보가 없으면 저장한다.
     * @param principal
     * @return
     */
    public Member findOrCreate(AppOAuth2User principal) {
        String profileImgUrl = principal.getProfileImgUrl() == null ? defaultProfileImgUrl : principal.getProfileImgUrl();
        Member member = memberRepository
                .findByProviderAndProviderUserId(principal.getProvider(), principal.getProviderUserId())
                .orElseGet(() ->
                     Member.create(principal.getProvider(),
                            principal.getProviderUserId(),
                            principal.getEmail(),
                            principal.getName(),
                            profileImgUrl));
        if (member.getMemberId() == null) {
            memberRepository.save(member);
        }
        return member;
    }

    /**
     * 재발급된 refreshToken 저장
     * @param memberId
     * @param refreshToken
     */
    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        memberRepository.updateRefreshToken(memberId, refreshToken);
    }
}
