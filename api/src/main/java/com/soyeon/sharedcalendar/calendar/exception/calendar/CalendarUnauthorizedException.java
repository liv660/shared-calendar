package com.soyeon.sharedcalendar.calendar.exception.calendar;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class CalendarUnauthorizedException extends BusinessException {
    public CalendarUnauthorizedException(Long calendarId) {
        super(ErrorCode.CALENDAR_UNAUTHORIZED, "캘린더 관리자만 할 수 있습니다." +
                "(calendarId: " + calendarId + ")");
    }
}
