package com.soyeon.sharedcalendar.calendar.dto.response;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;

public record CalendarCategoriesResponse(String categoryId, String categoryName, String categoryColor) {
    public static CalendarCategoriesResponse from(CalendarCategory categories) {
        return new CalendarCategoriesResponse(
                String.valueOf(categories.getCategoryId()),
                categories.getCategoryName(),
                categories.getCategoryColor());
    }
}
