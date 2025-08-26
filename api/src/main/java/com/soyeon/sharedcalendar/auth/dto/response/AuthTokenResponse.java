package com.soyeon.sharedcalendar.auth.dto.response;

public record AuthTokenResponse(String tokenType, String accessToken, String refreshToken, long expiresIn) {
    public AuthTokenResponse(String accessToken, String refreshToken, long expiresIn){
        this("Bearer", accessToken, refreshToken, expiresIn);
    }
}
