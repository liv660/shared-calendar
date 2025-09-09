package com.soyeon.sharedcalendar.member.dto;

public record MemberDto (String name, String email, String presignedUrl) {

    public static MemberDto create(String name, String email, String presignedUrl) {
        return new MemberDto(name, email, presignedUrl);
    }
}
