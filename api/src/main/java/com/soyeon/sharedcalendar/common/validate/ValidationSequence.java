package com.soyeon.sharedcalendar.common.validate;

import jakarta.validation.GroupSequence;

@GroupSequence({BasicChecks.class, BusinessRules.class})
public interface ValidationSequence {
}