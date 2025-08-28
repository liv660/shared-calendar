package com.soyeon.sharedcalendar.token.api;

import com.soyeon.sharedcalendar.common.exception.ErrorResponse;
import com.soyeon.sharedcalendar.token.app.TokenService;
import com.soyeon.sharedcalendar.token.dto.request.RefreshRequest;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Tag(name = "Token", description = "jwt 인증 관련 API")
public class TokenController {
    private final TokenService tokenService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @Operation(summary = "JWT 재발급", description = "access token, refresh token을 재발급한다.")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> refreshToken(@Parameter(required = true) @Valid @RequestBody RefreshRequest token) {
        return ResponseEntity.ok(tokenService.reissueTokens(token.refreshToken()));
    }
}
