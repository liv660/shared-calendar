package com.soyeon.sharedcalendar.temp;

import com.soyeon.sharedcalendar.common.crypto.HashingService;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
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
    private final ValidatorService validatorService;

    public String issueToken() {
        Member member = validatorService.validateMember(memberId);
        TokenResponse tokens = tokenService.issueToken(member);

        String hashedRefreshToken = HashingService.hash(tokens.refreshToken());
        memberService.updateRefreshToken(member, hashedRefreshToken);
        return tokens.accessToken();
    }
}
