package com.soyeon.sharedcalendar.calendar.dto;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;

public record CalendarRequest(String calendarName,
                              CalendarAccessLevel accessLevel,
                              String profileImgUrl ) {}

