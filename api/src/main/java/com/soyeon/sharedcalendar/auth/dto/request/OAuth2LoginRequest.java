package com.soyeon.sharedcalendar.auth.dto.request;

import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import jakarta.validation.constraints.NotNull;

public record OAuth2LoginRequest(@NotNull OAuth2Provider provider,
                                 String authCode) {
}
