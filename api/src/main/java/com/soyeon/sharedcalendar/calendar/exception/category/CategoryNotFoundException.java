package com.soyeon.sharedcalendar.calendar.exception.category;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(Long calendarId, Long categoryId) {
        super(ErrorCode.CATEGORY_NOT_FOUND, "존재하지 않는 카테고리 입니다." +
                "(calendarId: " + calendarId +
                ", eventId: " + categoryId + ")");
    }
}
