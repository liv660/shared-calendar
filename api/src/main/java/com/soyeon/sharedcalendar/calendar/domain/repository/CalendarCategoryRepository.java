package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {
    List<CalendarCategory> findByCalendarId(Long calendarId);
}
