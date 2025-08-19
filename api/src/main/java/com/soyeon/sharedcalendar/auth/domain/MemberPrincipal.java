package com.soyeon.sharedcalendar.auth.domain;

public record MemberPrincipal(Long memberId, String email, String name) {
}
