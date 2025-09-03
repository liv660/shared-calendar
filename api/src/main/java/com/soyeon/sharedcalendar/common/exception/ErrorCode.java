package com.soyeon.sharedcalendar.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"),

    //authentication
    AUTHENTICATION_MISSING(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_MISSING"),

    //member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND"),

    //minio
    MINIO_BUCKETS_ERROR(HttpStatus.BAD_REQUEST, "MINIO_BUCKETS_ERROR"),;

    public final HttpStatus status;
    public final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }
}
