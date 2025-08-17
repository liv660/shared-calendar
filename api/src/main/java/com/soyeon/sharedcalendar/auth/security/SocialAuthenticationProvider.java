package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import com.soyeon.sharedcalendar.auth.dto.userinfo.OAuthUserInfo;
import com.soyeon.sharedcalendar.auth.dto.userinfo.UserInfoRegistry;
import com.soyeon.sharedcalendar.auth.dto.userinfo.UserInfoRegistry.Meta;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialAuthenticationProvider implements AuthenticationProvider {
    private final AuthProperties props;
    private final IdTokenDecoderRegistry decoder;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final UserInfoRegistry userInfoRegistry;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof SocialAuthenticationToken auth) {
            SocialProvider provider = auth.getProvider();
            Jwt jwt = decoder.get(provider).decode(auth.getRawIdToken());
            validateIdToken(provider, jwt);

            Member member = memberRepository
                    .findByProviderAndProviderUserId(provider, jwt.getSubject())
                    .orElseGet(() -> registerMember(provider, auth.getAccessToken()));
            MemberPrincipal principal = new MemberPrincipal(member.getMemberId(), member.getEmail(), member.getName());
            return SocialAuthenticationToken.authed(provider, principal);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void validateIdToken(SocialProvider provider, Jwt jwt) {
        // 만료
        Instant exp = jwt.getExpiresAt();
        if (exp == null || exp.isBefore(Instant.now())) {
            throw new BadCredentialsException("Expired JWT token");
        }

        // issuer
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        String expectedIss = props.getProviders().get(provider).getIssuer();
        if (!expectedIss.equals(issuer)) {
            throw new BadCredentialsException("Invalid JWT token (issuer)");
        }

        // audience
        List<String> aud = jwt.getAudience();
        String clientId = props.getProviders().get(provider).getClientId();
        if (aud == null || !aud.contains(clientId)) {
            throw new BadCredentialsException("Invalid JWT token (audience)");
        }

    }

    private Member registerMember(SocialProvider provider, String accessToken) {
        Meta meta = userInfoRegistry.getMeta(provider);
        OAuthUserInfo userInfo = WebClient.builder()
                .baseUrl(meta.userInfoUri())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get().retrieve().bodyToMono(meta.provider()).block();
        SignupRequest request = new SignupRequest(provider, userInfo.getUserId(), userInfo.getEmail(), userInfo.getName(), userInfo.getProfileImgUrl());
        return memberService.signup(request);
    }
}
