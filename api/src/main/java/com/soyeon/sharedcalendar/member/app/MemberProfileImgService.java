package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.common.crypto.HashingService;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.img.app.ObjectKeyGenerator;
import com.soyeon.sharedcalendar.common.img.dto.ImgRequest;
import com.soyeon.sharedcalendar.common.img.dto.UploadResult;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.member.domain.img.MemberImgMeta;
import com.soyeon.sharedcalendar.member.domain.img.SourceType;
import com.soyeon.sharedcalendar.member.domain.repository.MemberImgMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberProfileImgService {
    private final WebClient webClient = WebClient.create();
    private final ImgService imgUploadService;
    private final MemberImgMetaRepository memberImgMetaRepository;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    /**
     * 이미지 다운로드 후 성공 시 minio에 업로드
     *
     * @param memberId
     * @param profileImgUrl provider 에서 조회한 프로필 사진 url
     * @return
     */
    public Mono<UploadResult> ingestFromUrl(Long memberId, String profileImgUrl) {
       return webClient.get()
               .uri(profileImgUrl)
               .accept(MediaType.ALL)
               .exchangeToMono(res -> {
                   if (res.statusCode().is2xxSuccessful()) {
                       String contentType = res.headers()
                               .contentType()
                               .map(MediaType::toString)
                               .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                       // content type check
                       if (!ALLOWED_TYPES.contains(contentType)) {
                           throw new IllegalArgumentException("Invalid content type: " + contentType);
                       }
                       // success -> upload
                       return res.bodyToMono(byte[].class)
                               .flatMap(bytes -> {
                                   if (bytes.length == 0 || bytes.length > MAX_BYTES) {
                                       return Mono.error(new IllegalArgumentException("Invalid bytes length: " + bytes.length));
                                   }

                                   String objectKey = ObjectKeyGenerator.buildObjectKey(memberId, contentType);
                                   return imgUploadService.uploadToMiniO(bytes, contentType, objectKey).thenReturn(new UploadResult(objectKey, contentType, bytes));
                               });
                   } else {
                       return res.bodyToMono(String.class)
                               .defaultIfEmpty("")
                               .flatMap(body -> Mono.error(new IllegalArgumentException("Download Failed: " + res.statusCode() + " " + body)));

                   }
               });
    }

    /**
     * 회원 이미지 메타를 생성한다.
     * @param result
     * @return
     */
    public MemberImgMeta createMetaForOAuthMember(UploadResult result, Long memberId) {
        return MemberImgMeta.createForOAuth(memberId,
                result.objectKey(),
                result.contentType(),
                result.bytes(),
                HashingService.hash(result.bytes()));
    }

    public MemberImgMeta createMetaForUpload(ImgRequest request) {
        return MemberImgMeta.createForUpload(SecurityUtils.getCurrentMemberId(),
                request.objectKey(), request.contentType(), request.bytes(), request.width(), request.height(), request.contentHash(), request.originalFilename());
    }

    /**
     * 회원 이미지 메타를 DB에 저장한다.
     * @param meta
     */
    public void save(MemberImgMeta meta) {
        memberImgMetaRepository.save(meta);
    }
}
