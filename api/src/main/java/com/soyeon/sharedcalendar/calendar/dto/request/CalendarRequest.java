package com.soyeon.sharedcalendar.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CalendarRequest(@NotBlank String calendarName,
                              CalendarAccessLevel accessLevel,
                              String profileImgUrl ) {}

