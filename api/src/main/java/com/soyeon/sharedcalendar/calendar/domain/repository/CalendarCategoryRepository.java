package com.soyeon.sharedcalendar.calendar.domain.repository;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarCategoryRepository extends JpaRepository<CalendarCategory, Long> {
    List<CalendarCategory> findByCalendarId(Long calendarId);

    Optional<CalendarCategory> findByCategoryIdAndCalendarId(Long categoryId, Long calendarId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update CalendarCategory cc
            set cc.categoryName = :#{#c.categoryName},
                cc.categoryColor = :#{#c.categoryColor}
            where cc.categoryId = :#{#c.categoryId}
                and cc.calendarId = :#{#c.calendarId}
    """)
    void update(@Param("c") CalendarCategory category);
}
