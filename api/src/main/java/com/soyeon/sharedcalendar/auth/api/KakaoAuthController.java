package com.soyeon.sharedcalendar.auth.api;

import com.nimbusds.jose.JOSEException;
import com.soyeon.sharedcalendar.auth.app.AuthService;
import com.soyeon.sharedcalendar.auth.app.TokenService;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.dto.response.AuthTokenResponse;
import com.soyeon.sharedcalendar.auth.dto.response.OAuth2TokenResponse;
import com.soyeon.sharedcalendar.auth.pkce.dto.PKCEInitResponse;
import com.soyeon.sharedcalendar.auth.pkce.service.PKCEService;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.soyeon.sharedcalendar.auth.domain.OAuth2Provider.*;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
@Tag(name = "iOS 카카오 인증", description = "카카오 인증 관련 API")
public class KakaoAuthController {
    private final PKCEService pkceService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final OAuth2Provider provider = KAKAO;

    @Operation(summary = "Kakao Code Verifier 생성", description = "인가 코드 발급을 위해 code verifier를 생성한다")
    @GetMapping(value = "/init")
    public ResponseEntity<PKCEInitResponse> createVerifier() {
        return ResponseEntity.ok(pkceService.createVerifier());
    }

    @Operation(summary = "Kakao ID Token 발급 및 사용자 정보 저장", description = "id_token을 발급 받아서 사용자 정보를 저장한다.")
    @GetMapping(value = "/callback")
    public ResponseEntity<AuthTokenResponse> getTokenFromKakao(@RequestParam String code, @RequestParam String state) throws JOSEException {
        //TODO 카카오 로그인 검증
        OAuth2TokenResponse tokens = authService.exchangeCodeForTokens(provider, code, state);
        Jwt jwt = authService.validateIdToken(provider, tokens);
        Member member = memberService.findOrCreateMember(provider, tokens, jwt);
        return ResponseEntity.ok(tokenService.handleKakaoLoginSuccess(member));
    }
}
