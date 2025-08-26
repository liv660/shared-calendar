package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.dto.request.OAuth2LoginRequest;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final OAuth2Provider provider;

    @Getter
    private final String authCode;

    private final MemberPrincipal principal;


    public static OAuth2AuthenticationToken unAuth(OAuth2LoginRequest request) {
        return new OAuth2AuthenticationToken(request);
    }

    public static OAuth2AuthenticationToken authed(OAuth2Provider provider, MemberPrincipal principal) {
        return new OAuth2AuthenticationToken(provider, principal);
    }


    private OAuth2AuthenticationToken(OAuth2LoginRequest request) {
        super(null);
        this.provider = request.provider();
        this.authCode = request.authCode();
        this.principal = null;
        setAuthenticated(false);
    }

    private OAuth2AuthenticationToken(OAuth2Provider provider, MemberPrincipal principal) {
        super(null);
        this.provider = provider;
        this.authCode = null;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return authCode;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
