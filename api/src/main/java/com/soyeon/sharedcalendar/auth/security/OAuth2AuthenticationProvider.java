package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.app.AuthService;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.dto.response.OAuth2TokenResponse;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final MemberService memberService;
    private final AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof OAuth2AuthenticationToken auth) {
            OAuth2Provider provider = auth.getProvider();
            OAuth2TokenResponse tokens = authService.exchangeCodeForTokens(provider, auth.getAuthCode(), null);
            if (tokens == null) {
                throw new InternalAuthenticationServiceException("failed to exchange token");
            }
            Jwt jwt = authService.validateIdToken(provider, tokens);
            Member member = memberService.findOrCreateMember(provider, tokens, jwt);
            MemberPrincipal principal = new MemberPrincipal(member.getMemberId(), member.getEmail(), member.getName());
            return OAuth2AuthenticationToken.authed(provider, principal);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
