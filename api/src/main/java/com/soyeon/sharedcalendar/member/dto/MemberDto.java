package com.soyeon.sharedcalendar.member.dto;

public record MemberDto(String memberId, String name, String email, String presignedUrl) {

    public static MemberDto create(Long memberId, String name, String email, String presignedUrl) {
        return new MemberDto(String.valueOf(memberId), name, email, presignedUrl);
    }
}
