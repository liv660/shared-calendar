package com.soyeon.sharedcalendar.auth.dto.request;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(@NotNull SocialProvider provider,
                                 @NotBlank String idToken,
                                 @NotBlank String accessToken) {
}
