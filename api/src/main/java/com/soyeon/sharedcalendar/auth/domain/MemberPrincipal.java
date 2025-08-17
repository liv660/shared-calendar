package com.soyeon.sharedcalendar.auth.domain;

public record MemberPrincipal(long memberId, String email, String name) {
}
