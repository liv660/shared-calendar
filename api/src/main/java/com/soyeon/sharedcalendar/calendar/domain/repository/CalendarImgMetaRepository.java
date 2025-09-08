package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarImgMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarImgMetaRepository extends JpaRepository<CalendarImgMeta, Long> {
}
