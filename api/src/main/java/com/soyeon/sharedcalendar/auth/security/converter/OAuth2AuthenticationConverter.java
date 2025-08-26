package com.soyeon.sharedcalendar.auth.security.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.sharedcalendar.auth.dto.request.OAuth2LoginRequest;
import com.soyeon.sharedcalendar.auth.security.OAuth2AuthenticationToken;
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
public class OAuth2AuthenticationConverter implements AuthenticationConverter {
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!("/auth/login".equals(request.getRequestURI())
            && "POST".equalsIgnoreCase(request.getMethod()))) {
            return null;
        }

        try {
            OAuth2LoginRequest dto = objectMapper.readValue(request.getInputStream(), OAuth2LoginRequest.class);
            Set<ConstraintViolation<OAuth2LoginRequest>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new BadCredentialsException(violations.iterator().next().getMessage());
            }
            return OAuth2AuthenticationToken.unAuth(dto);
        } catch (IOException e) {
            throw new BadCredentialsException("Invalid Social Login Request", e);
        }
    }
}
