package com.soyeon.sharedcalendar.auth.dto;

public record TokenResponse(String tokenType, String accessToken, String refreshToken, long expiresIn) {
    public TokenResponse(String accessToken, String refreshToken, long expiresIn){
        this("Bearer", accessToken, refreshToken, expiresIn);
    }
}
