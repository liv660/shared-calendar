package com.soyeon.sharedcalendar.common.exception;

import com.soyeon.sharedcalendar.token.exception.InvalidTokenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity
                .status(e.getErrorCode().status)
                .body(new ErrorResponse(
                        e.getErrorCode().code,
                        e.getMessage()));
    }

}
