package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.EventVisibility;
import com.soyeon.sharedcalendar.calendar.domain.EventVisibilityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventVisibilityRepository extends JpaRepository<EventVisibility, EventVisibilityId> {
}
