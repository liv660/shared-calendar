package com.soyeon.sharedcalendar.member.dto;

public record MeResponse(String name,
                         String email,
                         String profileImgKey,
                         boolean hasCalendar) {
}
