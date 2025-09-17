package com.soyeon.sharedcalendar.common.img.dto;

public record ImgRequest(String objectKey,
                         String contentType,
                         int bytes,
                         int width,
                         int height,
                         String contentHash,
                         String originalFilename) {}