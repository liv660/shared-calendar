package com.soyeon.sharedcalendar.common.exception;

import com.nimbusds.jose.JOSEException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JOSEException.class)
    public void handleJOSEException(JOSEException e) throws JOSEException {
        throw e;
    }

    @ExceptionHandler(ParseException.class)
    public void handleParseException(ParseException e) throws ParseException {
        throw e;
    }
}
