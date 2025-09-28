package com.soyeon.sharedcalendar.token.exception;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;
import lombok.Getter;


@Getter
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String errorMessage) {
        super(ErrorCode.INVALID_TOKEN, errorMessage);
    }

    public InvalidTokenException(String errorMessage, Exception e) {
        super(ErrorCode.INVALID_TOKEN, errorMessage,  e);
    }
}
