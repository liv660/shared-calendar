package com.soyeon.sharedcalendar.config.minio;

import com.soyeon.sharedcalendar.common.exception.BusinessException;

import static com.soyeon.sharedcalendar.common.exception.ErrorCode.MINIO_BUCKETS_ERROR;

public class MinioException extends BusinessException {

    public MinioException(String errorMessage, Exception e) {
        super(MINIO_BUCKETS_ERROR, errorMessage, e);
    }
}
