package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.common.id.SnowflakeIdGenerator;
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
    private final SnowflakeIdGenerator idGenerator;

    @Value("${member.default-profile-img}")
    private String defaultProfileImgUrl;

    public Member signup(SignupRequest request) {
        String profileImgUrl = request.profileImgUrl();
        Member member = Member.create(idGenerator.nextId(),
                request.provider(),
                request.providerUserId(),
                request.email(),
                request.name(),
                profileImgUrl == null ? defaultProfileImgUrl : profileImgUrl);
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void updateRefreshToken(long memberId, String refreshToken) {
        memberRepository.updateRefreshToken(memberId, refreshToken);
    }
}
