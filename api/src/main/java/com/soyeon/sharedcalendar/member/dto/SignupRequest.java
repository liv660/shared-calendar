package com.soyeon.sharedcalendar.member.dto;

import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;

public record SignupRequest(OAuth2Provider provider, String providerUserId, String email, String name, String profileImgUrl) {
}
