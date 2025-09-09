package com.soyeon.sharedcalendar.calendar.exception.calendar;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class CalendarNotFound extends BusinessException {
    public CalendarNotFound(Long calendarId) {
        super(ErrorCode.CALENDAR_NOT_FOUND, "존재하지 않는 캘린더 입니다." +
                "(calendarId: " + calendarId + ")");
    }
}
