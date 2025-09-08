package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarCategoriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarCategoryService {
    private final CalendarCategoryRepository calendarCategoryRepository;

    public List<CalendarCategoriesResponse> getCategories(Long calendarId) {
        List<CalendarCategory> categories = calendarCategoryRepository.findByCalendarId(calendarId);
        List<CalendarCategoriesResponse> result = new ArrayList<>();
        for (CalendarCategory cc : categories) {
            result.add(CalendarCategoriesResponse.from(cc));
        }
        return result;
    }
}
