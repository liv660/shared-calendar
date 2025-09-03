package com.soyeon.sharedcalendar.security.exception;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;

public class AuthenticationMissingException extends BusinessException {
    public AuthenticationMissingException(String errorMessage) {
        super(ErrorCode.AUTHENTICATION_MISSING, errorMessage);
    }
}
