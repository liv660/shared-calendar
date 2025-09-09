package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarCategoryRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarCategoriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarCategoryService {
    private final CalendarCategoryRepository calendarCategoryRepository;

    /**
     * 카테고리를 조회한다.
     * @param calendarId
     * @return
     */
    public List<CalendarCategoriesResponse> getCategories(Long calendarId) {
        List<CalendarCategory> categories = calendarCategoryRepository.findByCalendarId(calendarId);
        List<CalendarCategoriesResponse> result = new ArrayList<>();
        for (CalendarCategory cc : categories) {
            result.add(CalendarCategoriesResponse.from(cc));
        }
        return result;
    }

    /**
     * 카테고리를 생성한다.
     * @param calendarId
     * @param request
     */
    @Transactional
    public void addCategory(Long calendarId, CalendarCategoryRequest request) {
        CalendarCategory cc = CalendarCategory.create(calendarId, request.categoryName(), request.categoryColor());
        calendarCategoryRepository.save(cc);
    }
}
