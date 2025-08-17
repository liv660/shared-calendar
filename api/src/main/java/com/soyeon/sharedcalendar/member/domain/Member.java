package com.soyeon.sharedcalendar.member.domain;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
public class Member {
    @Id
    private long memberId;
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;
    private String providerUserId;
    private String email;
    private String name;
    private String refreshToken;
    private String profileImgUrl;
    @Column(insertable = false, updatable = false)
    private boolean isActive;
    @Column(insertable = false, updatable = false)
    private LocalDateTime joinedAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime lastLoginAt;
    private LocalDateTime withdrawnAt;

    public static Member create(long memberId, SocialProvider provider, String providerUserId, String email, String name, String profileImgUrl) {
        Member m =  new Member();
        m.memberId = memberId;
        m.provider = provider;
        m.providerUserId = providerUserId;
        m.email = email;
        m.name = name;
        m.profileImgUrl = profileImgUrl;
        return m;
    }
}
