package com.soyeon.sharedcalendar.auth.dto;

import com.soyeon.sharedcalendar.auth.domain.ProviderType;

public record SocialLoginDTO(ProviderType provider, String idToken) {}
