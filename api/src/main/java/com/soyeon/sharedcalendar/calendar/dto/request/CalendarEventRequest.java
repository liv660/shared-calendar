package com.soyeon.sharedcalendar.calendar.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soyeon.sharedcalendar.calendar.domain.VisibilityType;
import com.soyeon.sharedcalendar.common.validate.BasicChecks;
import com.soyeon.sharedcalendar.common.validate.BusinessRules;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record
CalendarEventRequest(String title,
                     String contents,
                     Long categoryId,
                     VisibilityType visibility,
                     boolean allDay,
                     String color,
                     @NotNull(groups = BasicChecks.class) LocalDateTime startAt,
                     @NotNull(groups = BasicChecks.class) LocalDateTime endAt,
                     Set<String> visibleMemberIds) {

    @AssertTrue(groups = BusinessRules.class)
    public boolean isValidDateTime() {
        return startAt.isBefore(endAt);
    }
}
