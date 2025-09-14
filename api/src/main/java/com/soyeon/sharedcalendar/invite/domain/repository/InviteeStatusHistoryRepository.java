package com.soyeon.sharedcalendar.invite.domain.repository;

import com.soyeon.sharedcalendar.invite.domain.InviteeStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteeStatusHistoryRepository extends JpaRepository<InviteeStatusHistory, Long> {
    Optional<InviteeStatusHistory> findTopByInviteeIdOrderByCreatedAtDesc(Long inviteeId);
}
