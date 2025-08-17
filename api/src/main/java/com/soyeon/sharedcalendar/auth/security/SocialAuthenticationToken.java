package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final SocialProvider provider;

    @Getter
    private final String rawIdToken;

    @Getter
    private final String accessToken;

    private MemberPrincipal principal;


    public static SocialAuthenticationToken unAuth(SocialProvider provider, String idToken, String accessToken) {
        return new SocialAuthenticationToken(provider, idToken, accessToken);
    }

    public static SocialAuthenticationToken authed(SocialProvider provider, MemberPrincipal principal) {
        return new SocialAuthenticationToken(provider, principal);
    }


    private SocialAuthenticationToken(SocialProvider provider, String idToken, String accessToken) {
        super(null);
        this.provider = provider;
        this.rawIdToken = idToken;
        this.accessToken = accessToken;
        this.principal = null;
        setAuthenticated(false);
    }

    private SocialAuthenticationToken(SocialProvider provider, MemberPrincipal principal) {
        super(null);
        this.provider = provider;
        this.rawIdToken = null;
        this.accessToken = null;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawIdToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
