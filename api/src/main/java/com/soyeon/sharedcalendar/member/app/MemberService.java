package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.common.id.SnowflakeIdGenerator;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.dto.SignupRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final SnowflakeIdGenerator idGenerator;

    public Member signup(SignupRequest request) {
        Member member = Member.create(idGenerator.nextId(),
                request.provider(),
                request.providerUserId(),
                request.email(),
                request.name(),
                request.profileImgUrl());
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public void updateRefreshToken(long memberId, String refreshToken) {
        memberRepository.updateRefreshToken(memberId, refreshToken);
    }
}
