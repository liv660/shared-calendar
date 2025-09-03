package com.soyeon.sharedcalendar.member.domain;

import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import com.soyeon.sharedcalendar.security.oauth2.OAuth2Provider;
import com.soyeon.sharedcalendar.calendar.domain.EventVisibility;
import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "member")
@Getter
public class Member {
    @Id
    @GeneratedValue @SnowflakeId
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private OAuth2Provider provider;

    private String providerUserId;
    private String email;
    private String name;

    @Setter
    private String refreshToken;

    @Column(insertable = false, updatable = false)
    private boolean isActive;

    @Column(insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(insertable = false, updatable = false)
    private LocalDateTime lastLoginAt;

    private LocalDateTime withdrawnAt;

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    Set<EventVisibility> visibilityEvents = new HashSet<>();

    @Setter
    @OneToOne(mappedBy = "owner", cascade = {PERSIST, REMOVE})
    private MemberImgMeta profileImg;

    @Column(length = 512)
    private String profileImgKey;

    public static Member create(OAuth2Provider provider, String providerUserId, String email, String name) {
        Member m = new Member();
        m.memberId = null;
        m.provider = provider;
        m.providerUserId = providerUserId;
        m.email = email;
        m.name = name;
        return m;
    }

    public void updateProfileImage(MemberImgMeta meta) {
        profileImg = meta;
        profileImgKey = meta.getObjectKey();
    }
}
