package com.soyeon.sharedcalendar.security.filter;

import com.soyeon.sharedcalendar.security.core.MemberAuthenticationToken;
import com.soyeon.sharedcalendar.security.core.MemberPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoder decoder;
    private final PathPatternParser parser = PathPatternParser.defaultInstance;
    private final List<PathPattern> skipMatcher = List.of(
            parser.parse("/v3/api-docs/**"),
            parser.parse("/swagger-ui/**"),
            parser.parse("/swagger-ui.html"),
            parser.parse("/temp/**"),
            parser.parse("/invite/accept/**")
    );

    public JwtCookieAuthenticationFilter(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        PathContainer path = PathContainer.parsePath(request.getRequestURI());
        return skipMatcher.stream().anyMatch(p -> p.matches(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getAccessToken(request.getCookies());
        Jwt jwt = decoder.decode(accessToken);
        MemberPrincipal principal = new MemberPrincipal(Long.valueOf(jwt.getSubject()),
                (String) jwt.getClaims().get("email"),
                (String) jwt.getClaims().get("name"));
        SecurityContextHolder.getContext().setAuthentication(MemberAuthenticationToken.auth(principal, jwt));
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("access_token"))
                .findFirst()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Access token not found"))
                .getValue();
    }
}
