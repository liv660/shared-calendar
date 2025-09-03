package com.soyeon.sharedcalendar.member.dto;

public record MeResponse(Long memberId,
                         String name,
                         String email,
                         String profileImgKey,
                         boolean hasCalendar) {
}
