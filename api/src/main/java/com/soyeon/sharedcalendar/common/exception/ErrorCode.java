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
    MINIO_BUCKETS_ERROR(HttpStatus.BAD_REQUEST, "MINIO_BUCKETS_ERROR"),
    MINIO_PRESIGNED_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MINIO_PRESIGNED_FAIL"),

    //calendar
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "CALENDAR_NOT_FOUND"),
    CALENDAR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CALENDAR_UNAUTHORIZED"),

    //calendar event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND"),
    EVENT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "EVENT_UNAUTHORIZED"),

    //calendar category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND"),
    CALENDAR_CATEGORY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CALENDAR_CATEGORY_UNAUTHORIZED"),

    //invite
    InviteeNotFound(HttpStatus.NOT_FOUND, "InviteeNotFound"),
    InviteeHistoryNotFound(HttpStatus.NOT_FOUND, "InviteeHistoryNotFound");
    public final HttpStatus status;
    public final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }
}
