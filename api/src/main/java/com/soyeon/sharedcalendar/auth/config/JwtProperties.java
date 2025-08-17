package com.soyeon.sharedcalendar.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String issuer, String audience,Duration accessTtl, Duration refreshTtl, String hs256Secret) {
}
