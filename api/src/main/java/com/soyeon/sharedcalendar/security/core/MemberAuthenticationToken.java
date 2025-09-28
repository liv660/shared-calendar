package com.soyeon.sharedcalendar.security.core;


import org.springframework.security.authentication.AbstractAuthenticationToken;


public class MemberAuthenticationToken extends AbstractAuthenticationToken {
    private final MemberPrincipal principal;
    private final Object token;

    public static MemberAuthenticationToken auth(MemberPrincipal principal, Object token) {
       return new MemberAuthenticationToken(principal, token);
    }

    private MemberAuthenticationToken(MemberPrincipal principal, Object token) {
        super(null);
        this.principal = principal;
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
