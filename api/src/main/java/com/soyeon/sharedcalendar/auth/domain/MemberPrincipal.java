package com.soyeon.sharedcalendar.auth.dto;

import com.soyeon.sharedcalendar.auth.domain.ProviderType;

public record MemberPrincipal (Long memberId, String email, String name, ProviderType provider) {}
