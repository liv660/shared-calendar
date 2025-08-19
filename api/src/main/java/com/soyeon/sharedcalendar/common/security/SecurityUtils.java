package com.soyeon.sharedcalendar.common.security;

import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Long.valueOf(jwt.getSubject());
        }

        if (authentication.getPrincipal() instanceof MemberPrincipal principal) {
            return principal.memberId();
        }

        return null;
    }
}
