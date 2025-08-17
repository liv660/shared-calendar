package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.app.TokenService;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.member.app.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.soyeon.sharedcalendar.auth.app.TokenService.*;

@Component
@RequiredArgsConstructor
public class SocialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        Tokens tokens = tokenService.issueToken(principal);

        String hashedRefreshToken = tokenService.getHashedRefreshToken(tokens.refreshToken());
        memberService.updateRefreshToken(principal.memberId(), hashedRefreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                    {"accessToken":"%s", "refreshToken":"%s", "expiresIn":"%d"}
                """.formatted(tokens.accessToken(), tokens.refreshToken(), tokenService.getAccessExpires()));
    }
}
