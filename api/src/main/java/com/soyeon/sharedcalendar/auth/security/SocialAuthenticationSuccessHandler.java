package com.soyeon.sharedcalendar.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.soyeon.sharedcalendar.auth.app.TokenService;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import com.soyeon.sharedcalendar.auth.dto.response.TokenResponse;
import com.soyeon.sharedcalendar.member.app.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SocialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;
    private final SocialAuthenticationFailureHandler socialAuthenticationFailureHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();
        TokenResponse tokens = null;

        try {
            tokens = tokenService.issueToken(principal);
        } catch (JOSEException e) {
            socialAuthenticationFailureHandler.onAuthenticationFailure(request,
                    response,
                    new InternalAuthenticationServiceException(e.getLocalizedMessage(), e));
        }

        String hashedRefreshToken = null;
        if (tokens != null) {
            hashedRefreshToken = tokenService.getHashedRefreshToken(tokens.refreshToken());
        } else {
            socialAuthenticationFailureHandler.onAuthenticationFailure(request,
                    response,
                    new InternalAuthenticationServiceException("Missing Token"));
        }
        memberService.updateRefreshToken(principal.memberId(), hashedRefreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        objectMapper.writeValue(response.getWriter(), tokens);
    }
}
