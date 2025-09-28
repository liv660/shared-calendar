package com.soyeon.sharedcalendar.member.dto;

public record MeResponse(String memberId,
                         String name,
                         String email,
                         String presignedUrl,
                         boolean hasCalendar) {
}
