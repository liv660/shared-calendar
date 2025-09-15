package com.soyeon.sharedcalendar.member.domain.repository;

import com.soyeon.sharedcalendar.security.oauth2.OAuth2Provider;
import com.soyeon.sharedcalendar.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Member m
            set m.refreshToken = :refreshToken,
                m.lastLoginAt = CURRENT TIMESTAMP
            where m.memberId = :memberId
    """)
    void updateRefreshToken(Long memberId, String refreshToken);

    Optional<Member> findByEmail(String email);
}
