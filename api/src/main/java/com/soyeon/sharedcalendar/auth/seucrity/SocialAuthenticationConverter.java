package com.soyeon.sharedcalendar.seucrity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.sharedcalendar.auth.dto.SocialLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SocialAuthenticationConverter implements AuthenticationConverter {
    private final ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!("/auth/login".equals(request.getRequestURI())
                && "POST".equalsIgnoreCase(request.getMethod()))) {
            return null;
        }

        try {
            SocialLoginRequest dto = objectMapper.readValue(request.getInputStream(), SocialLoginRequest.class);
            return new SocialAuthenticationToken(dto.provider(), dto.idToken());
        } catch (IOException e) {
            throw new BadCredentialsException("Invalid login request body", e);
        }
    }
}
