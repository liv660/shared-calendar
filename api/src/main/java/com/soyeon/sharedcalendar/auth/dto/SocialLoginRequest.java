package com.soyeon.sharedcalendar.auth.dto;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;

public record SocialLoginRequest(SocialProvider provider, String idToken, String accessToken) {
}
