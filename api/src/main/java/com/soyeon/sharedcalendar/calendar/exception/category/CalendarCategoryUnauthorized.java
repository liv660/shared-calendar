package com.soyeon.sharedcalendar.calendar.exception.category;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class CalendarCategoryUnauthorized extends BusinessException {
    public CalendarCategoryUnauthorized(Long calendarId, Long memberId) {
        super(ErrorCode.CALENDAR_CATEGORY_UNAUTHORIZED, "캘린더 관리자만 할 수 있습니다." +
                "(calendarId: " + calendarId + ", memberId: " + memberId + ")");
    }
}
