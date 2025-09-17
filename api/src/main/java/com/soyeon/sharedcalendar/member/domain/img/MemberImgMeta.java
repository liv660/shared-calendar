package com.soyeon.sharedcalendar.member.domain.img;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "member_img_meta")
public class MemberImgMeta {
    @Id
    @GeneratedValue @SnowflakeId
    private Long memberImgMetaId;

    private Long ownerId; // Member.memberId

    @Column(length = 512)
    private String objectKey;

    private String contentType;
    private int bytes;
    private int width;
    private int height;

    @Column(length = 64, nullable = false)
    private String contentHash;

    private String originalFilename;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public static MemberImgMeta createForOAuth(Long memberId, String objectKey, String contentType, byte[] bytes, String contentHash) {
        MemberImgMeta meta = new MemberImgMeta();
        meta.ownerId = memberId;
        meta.objectKey = objectKey;
        meta.contentType = contentType;
        meta.bytes = bytes.length;
        meta.contentHash = contentHash;
        meta.sourceType = SourceType.OAUTH;
        return meta;
    }

    public static MemberImgMeta createForUpload(Long memberId, String objectKey, String contentType, int bytes, int width, int height, String contentHash, String originalFilename) {
        MemberImgMeta meta = new MemberImgMeta();
        meta.ownerId = memberId;
        meta.objectKey = objectKey;
        meta.contentType = contentType;
        meta.bytes = bytes;
        meta.width = width;
        meta.height = height;
        meta.contentHash = contentHash;
        meta.originalFilename = originalFilename;
        meta.sourceType = SourceType.UPLOAD;
        return meta;
    }
}
