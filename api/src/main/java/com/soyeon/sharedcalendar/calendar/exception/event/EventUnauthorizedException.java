package com.soyeon.sharedcalendar.calendar.exception.event;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class EventUnauthorizedException extends BusinessException {
    public EventUnauthorizedException(Long calendarId, Long eventId) {
        super(ErrorCode.EVENT_UNAUTHORIZED, "해당 일정을 생성한 사용자만 수정할 수 있습니다." +
                "(calendarId: " + calendarId +
                ", eventId: " + eventId + ")");
    }

    public EventUnauthorizedException(Long calendarId, Long eventId, String message) {
        super(ErrorCode.EVENT_UNAUTHORIZED, message +
                "(calendarId: " + calendarId +
                ", eventId: " + eventId + ")");
    }
}
