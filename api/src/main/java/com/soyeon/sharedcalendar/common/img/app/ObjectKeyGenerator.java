package com.soyeon.sharedcalendar.common.img.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ObjectKeyGenerator {

    /**
     * object key를 생성한다.
     * @param id memberId or calendarId
     * @param contentType
     * @return
     */
    public static String buildObjectKey(Long id, String contentType) {
        String ext = switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "%s/%s/%s.%s".formatted(id, date, UUID.randomUUID(), ext);
    }
}
