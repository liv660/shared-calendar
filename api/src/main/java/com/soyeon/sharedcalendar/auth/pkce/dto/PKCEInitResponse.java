package com.soyeon.sharedcalendar.auth.pkce.dto;

public record PKCEInitResponse(String authorizeUrl,
                               String state,
                               long expiresIn) {
}
