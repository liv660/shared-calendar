package com.soyeon.sharedcalendar.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.sharedcalendar.auth.dto.request.SocialLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SocialAuthenticationConverter implements AuthenticationConverter {
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!("/auth/login".equals(request.getRequestURI())
            && "POST".equalsIgnoreCase(request.getMethod()))) {
            return null;
        }

        try {
            SocialLoginRequest dto = objectMapper.readValue(request.getInputStream(), SocialLoginRequest.class);
            Set<ConstraintViolation<SocialLoginRequest>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new BadCredentialsException(violations.iterator().next().getMessage());
            }
            return SocialAuthenticationToken.unAuth(dto.provider(), dto.idToken(), dto.accessToken());
        } catch (IOException e) {
            throw new BadCredentialsException("Invalid Social Login Request", e);
        }
    }
}
