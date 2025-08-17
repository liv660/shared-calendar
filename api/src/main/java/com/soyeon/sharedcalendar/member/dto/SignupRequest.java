package com.soyeon.sharedcalendar.member.dto;

import com.soyeon.sharedcalendar.auth.domain.ProviderType;

public record Signup(ProviderType provider, String providerUserId, String email, String name, String refreshToken, String profileImgUrl) {
}
