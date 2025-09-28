package com.soyeon.sharedcalendar.config.minio;

import com.soyeon.sharedcalendar.common.exception.BusinessException;
import com.soyeon.sharedcalendar.common.exception.ErrorCode;


public class MinioException extends BusinessException {

    public MinioException(ErrorCode errorCode, String errorMessage, Exception e) {
        super(errorCode, errorMessage, e);
    }
}
