package com.soyeon.sharedcalendar.common.security;

import com.soyeon.sharedcalendar.security.core.MemberPrincipal;
import com.soyeon.sharedcalendar.security.exception.AuthenticationMissingException;
import com.soyeon.sharedcalendar.token.exception.InvalidTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationMissingException("[getCurrentMemberId] context not exists");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Long.valueOf(jwt.getSubject());
        }
        throw new InvalidTokenException("[getCurrentMemberId] Invalid token: missing member");
    }

    public static MemberPrincipal getCurrentMemberPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationMissingException("[getCurrentMemberPrincipal] context not exists");
        };

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return new MemberPrincipal(Long.valueOf(jwt.getSubject()),
                    (String) jwt.getClaims().get("email"),
                    (String) jwt.getClaims().get("name"));
        }
        throw new InvalidTokenException("[getCurrentMemberPrincipal] Invalid token: missing member");
    }
}
