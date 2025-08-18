package com.soyeon.sharedcalendar.member.domain.repository;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update Member m
            set m.refreshToken = :refreshToken,
                m.lastLoginAt = CURRENT TIMESTAMP
            where m.memberId = :memberId
    """)
    void updateRefreshToken(long memberId, String refreshToken);
}
