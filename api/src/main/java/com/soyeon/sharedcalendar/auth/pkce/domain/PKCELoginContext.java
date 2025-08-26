package com.soyeon.sharedcalendar.auth.pkce.domain;

public record PKCELoginContext(String codeVerifier,
                               String redirectUri,
                               long createdAtMillis,
                               String userAgent,
                               String ip) {
}
