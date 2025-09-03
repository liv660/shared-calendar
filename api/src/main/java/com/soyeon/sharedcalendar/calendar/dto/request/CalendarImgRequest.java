package com.soyeon.sharedcalendar.calendar.dto.request;

public record CalendarImgRequest(String objectKey,
                              String contentType,
                              int bytes,
                              int width,
                              int height,
                              String contentHash,
                              String originalFilename) {}