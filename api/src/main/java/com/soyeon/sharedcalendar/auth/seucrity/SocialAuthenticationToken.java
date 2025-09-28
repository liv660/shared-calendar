package com.soyeon.sharedcalendar.seucrity;

import com.soyeon.sharedcalendar.auth.domain.ProviderType;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final ProviderType provider;

    @Getter
    private final String rawIdToken;

    private MemberPrincipal principal;

    // 인증 전 (로그인 요청 시)
    public SocialAuthenticationToken(ProviderType provider, String rawIdToken) {
        super(null);
        this.provider = provider;
        this.rawIdToken = rawIdToken;
        this.principal = null;
        setAuthenticated(false);
    }

    // 인증 후 (성공 시)
    public SocialAuthenticationToken(Collection<? extends GrantedAuthority> authorities, ProviderType provider, String rawIdToken) {
        super(authorities);
        this.provider = provider;
        this.rawIdToken = null;
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
