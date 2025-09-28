package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarCategoryRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarCategoriesResponse;
import com.soyeon.sharedcalendar.calendar.exception.category.CalendarCategoryUnauthorizedException;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarCategoryService {
    private final ValidatorService validatorService;
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
     * 카테고리를 생성한다. (캘린더 생성자만 가능)
     * @param calendarId
     * @param request
     */
    @Transactional
    public void addCategory(Long calendarId, CalendarCategoryRequest request) {
        Calendar c = validatorService.validateCalendar(calendarId);
        Long memberId = SecurityUtils.getCurrentMemberId();

        if (!c.getOwnerId().equals(memberId)) {
            throw new CalendarCategoryUnauthorizedException(calendarId, memberId);
        }

        CalendarCategory cc = CalendarCategory.create(calendarId, request.categoryName(), request.categoryColor());
        calendarCategoryRepository.save(cc);
    }

    /**
     * 카테고리를 삭제한다.
     * @param calendarId
     * @param categoryId
     */
    @Transactional
    public void deleteCategory(Long calendarId, Long categoryId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Calendar c = validatorService.validateCalendar(calendarId);
        validatorService.validateCategory(calendarId, categoryId);

        if (c.getOwnerId().equals(memberId)) {
            calendarCategoryRepository.deleteById(categoryId);
        }
        throw new CalendarCategoryUnauthorizedException(calendarId, memberId);
    }

    /**
     * 카테고리를 수정한다.
     * @param calendarId
     * @param request
     */
    @Transactional
    public void updateCategory(Long calendarId, CalendarCategoryRequest request) {
        validatorService.validateCalendar(calendarId);
        CalendarCategory category = CalendarCategory.from(calendarId, request);
        calendarCategoryRepository.update(category);
    }
}
