package com.soyeon.sharedcalendar.member.dto;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;

public record SignupRequest(SocialProvider provider, String providerUserId, String email, String name, String profileImgUrl) {
}
