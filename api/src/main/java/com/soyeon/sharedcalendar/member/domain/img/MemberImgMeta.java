package com.soyeon.sharedcalendar.member.domain.img;

import com.soyeon.sharedcalendar.common.id.SnowflakeId;
import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "member_img_meta")
public class MemberImgMeta {
    @Id
    @GeneratedValue @SnowflakeId
    private Long memberImgMetaId;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

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

    public static MemberImgMeta create(String objectKey, String contentType, byte[] bytes, String contentHash, SourceType sourceType) {
        MemberImgMeta meta = new MemberImgMeta();
        meta.objectKey = objectKey;
        meta.contentType = contentType;
        meta.bytes = bytes.length;
        meta.contentHash = contentHash;
        meta.sourceType = sourceType;
        return meta;
    }
}
