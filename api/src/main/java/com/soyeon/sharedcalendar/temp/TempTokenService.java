package com.soyeon.sharedcalendar.temp;

import com.soyeon.sharedcalendar.common.crypto.HashingService;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.token.app.TokenService;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempTokenService {
    private final TokenService tokenService;
    private final Long memberId = 86861631958552576L;
    private final MemberService memberService;

    public String issueToken() {
        Member member = memberService.findByMemberId(memberId);
        TokenResponse tokens = tokenService.issueToken(member);

        String hashedRefreshToken = HashingService.hash(tokens.refreshToken());
        memberService.updateRefreshToken(member, hashedRefreshToken);
        return tokens.accessToken();
    }
}
