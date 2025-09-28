package com.soyeon.sharedcalendar.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.common.img.dto.ImgRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CalendarRequest(String calendarName,
                              CalendarAccessLevel accessLevel,
                              ImgRequest imgMeta) {}

