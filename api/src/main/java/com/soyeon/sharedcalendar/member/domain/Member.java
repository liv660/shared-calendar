package com.soyeon.sharedcalendar.member.domain;

import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.calendar.domain.EventVisibility;
import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.member.dto.SignupRequest;
import jakarta.persistence.*;
import lombok.Getter;

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
    private String refreshToken;
    @Column(length = 2083)
    private String profileImgUrl;
    @Column(insertable = false, updatable = false)
    private boolean isActive;
    @Column(insertable = false, updatable = false)
    private LocalDateTime joinedAt;
    @Column(insertable = false, updatable = false)
    private LocalDateTime lastLoginAt;
    private LocalDateTime withdrawnAt;

    @OneToMany(mappedBy = "member", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    Set<EventVisibility> visibilityEvents = new HashSet<>();

    public static Member create(SignupRequest request, String profileImgUrl) {
        Member m =  new Member();
        m.memberId = null;
        m.provider = request.provider();
        m.providerUserId = request.providerUserId();
        m.email = request.email();
        m.name = request.name();
        m.profileImgUrl = profileImgUrl;
        return m;
    }
}
