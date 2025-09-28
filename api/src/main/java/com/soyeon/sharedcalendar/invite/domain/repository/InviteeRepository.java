package com.soyeon.sharedcalendar.invite.domain.repository;

import com.soyeon.sharedcalendar.invite.domain.Invitee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteeRepository extends JpaRepository<Invitee, Long> {
    Optional<Invitee> findByInviteToken(String inviteToken);

    boolean existsByEmail(String email);

    Optional<Invitee> findByEmail(String email);

    Optional<Invitee> findByCalendarIdAndEmail(Long calendarId, String email);

    List<Invitee> findByCalendarId(Long calendarId);

    void deleteByCalendarIdAndEmail(Long calendarId, String email);

    Invitee findByAcceptMemberId(Long memberId);
}
