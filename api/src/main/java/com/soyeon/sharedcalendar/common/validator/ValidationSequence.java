package com.soyeon.sharedcalendar.common.validator;

import jakarta.validation.GroupSequence;

@GroupSequence({BasicChecks.class, BusinessRules.class})
public interface ValidationSequence {
}