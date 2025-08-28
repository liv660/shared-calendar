package com.soyeon.sharedcalendar.security.core;

public record MemberPrincipal(Long memberId, String email, String name) {
}
