package com.soyeon.sharedcalendar.calendar.exception.event;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class EventNotFoundException extends BusinessException {
    public EventNotFoundException(Long calendarId, Long eventId) {
        super(ErrorCode.EVENT_NOT_FOUND, "존재하지 않는 일정 입니다." +
                "(calendarId: " + calendarId +
                ", eventId: " + eventId + ")");
    }
}
