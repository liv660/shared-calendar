package com.soyeon.sharedcalendar.calendar.domain;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "calendar_img_meta")
public class CalendarImgMeta {
    @Id
    @GeneratedValue @SnowflakeId
    private Long calendarImgMetaId;

    private Long ownerId; // Member.memberId
    private Long calendarId; // Calendar.calendarId

    @Column(length = 512, nullable = false)
    private String objectKey;

    private String contentType;
    private int bytes;
    private int width;
    private int height;

    @Column(length = 64, nullable = false)
    private String contentHash;

    private String originalFilename;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public static CalendarImgMeta create(Long memberId, Long calendarId, String objectKey,
                                         String contentType, int bytes, int width, int height,
                                         String contentHash, String originalFilename) {
        CalendarImgMeta c =  new CalendarImgMeta();
        c.ownerId = memberId;
        c.calendarId = calendarId;
        c.objectKey = objectKey;
        c.contentType = contentType;
        c.bytes = bytes;
        c.width = width;
        c.height = height;
        c.contentHash = contentHash;
        c.originalFilename = originalFilename;
        return c;
    }
}
