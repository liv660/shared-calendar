package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.dto.SignupRequest;
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

    @Transactional
    public Member signup(SignupRequest request) {
        String profileImgUrl = request.profileImgUrl() == null ? defaultProfileImgUrl : request.profileImgUrl();
        Member member = Member.create(request, profileImgUrl);
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        memberRepository.updateRefreshToken(memberId, refreshToken);
    }
}
