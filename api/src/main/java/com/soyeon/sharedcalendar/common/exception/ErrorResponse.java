package com.soyeon.sharedcalendar.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답")
public record ErrorResponse(@Schema(description = "에러 코드") String code,
                            @Schema(description = "에러 메시지") String message) {}
