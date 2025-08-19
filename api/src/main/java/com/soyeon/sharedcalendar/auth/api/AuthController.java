package com.soyeon.sharedcalendar.auth.api;

import com.nimbusds.jose.JOSEException;
import com.soyeon.sharedcalendar.auth.app.TokenService;
import com.soyeon.sharedcalendar.auth.dto.RefreshRequest;
import com.soyeon.sharedcalendar.auth.dto.TokenResponse;
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
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final TokenService tokenService;

    @Operation(hidden = true)
    @PostMapping("/login")
    public void login() {}

    @Operation(hidden = true)
    @GetMapping("/callback/kakao")
    public void callback() {}

    @Operation(summary = "JWT 재발급", description = "access token, refresh token을 재발급한다.")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> refreshToken(@Parameter(required = true) @Valid @RequestBody RefreshRequest token) throws ParseException, JOSEException {
        return ResponseEntity.ok(tokenService.reissueTokens(token.refreshToken()));
    }
}
