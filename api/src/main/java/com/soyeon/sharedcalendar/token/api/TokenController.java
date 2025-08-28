package com.soyeon.sharedcalendar.token.api;

import com.nimbusds.jose.JOSEException;
import com.soyeon.sharedcalendar.token.app.TokenService;
import com.soyeon.sharedcalendar.token.dto.request.RefreshRequest;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Token", description = "jwt 인증 관련 API")
public class TokenController {
    private final TokenService tokenService;

    @Operation(summary = "JWT 재발급", description = "access token, refresh token을 재발급한다.")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> refreshToken(@Parameter(required = true) @Valid @RequestBody RefreshRequest token) throws ParseException, JOSEException {
        return ResponseEntity.ok(tokenService.reissueTokens(token.refreshToken()));
    }
}
