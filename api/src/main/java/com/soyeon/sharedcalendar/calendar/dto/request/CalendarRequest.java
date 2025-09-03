package com.soyeon.sharedcalendar.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CalendarRequest(String calendarName,
                              CalendarAccessLevel accessLevel,
                              CalendarImgRequest imgMeta) {}

