package com.soyeon.sharedcalendar.temp;

import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.exception.MemberNotFound;
import com.soyeon.sharedcalendar.token.app.TokenService;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempTokenService {
    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final Long memberId = 86861631958552576L;
    private final MemberService memberService;

    public String issueToken() {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFound(memberId));
        TokenResponse tokens = tokenService.issueToken(member);

        String hashedRefreshToken = tokenService.getHashedRefreshToken(tokens.refreshToken());
        memberService.updateRefreshToken(member.getMemberId(), hashedRefreshToken);
        return tokens.accessToken();
    }
}
