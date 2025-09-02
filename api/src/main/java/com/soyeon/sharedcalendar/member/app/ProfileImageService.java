package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.config.minio.MinioException;
import com.soyeon.sharedcalendar.config.minio.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final WebClient webClient = WebClient.create();
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");
    private static final long MAX_BYTES = 5L * 1024 * 1024;

    /**
     * 이미지 다운로드 후 성공 시 minio에 업로드
     * @param memberId
     * @param profileImgUrl
     * @return
     */
    public Mono<String> ingestFromUrl(Long memberId, String profileImgUrl) {
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
                       //success -> upload
                       return res.bodyToMono(byte[].class)
                               .flatMap(bytes -> {
                                   if (bytes.length == 0 || bytes.length > MAX_BYTES) {
                                       return Mono.error(new IllegalArgumentException("Invalid bytes length: " + bytes.length));
                                   }

                                   String objectKey = buildObjectKey(memberId, contentType);
                                   log.info("[ingestFromUrl] object key: {}", objectKey);
                                   return uploadToMiniO(bytes, contentType, objectKey).thenReturn(objectKey);
                               });
                   } else {
                       return res.bodyToMono(String.class)
                               .defaultIfEmpty("")
                               .flatMap(body -> Mono.error(new IllegalArgumentException("Download Failed: " + res.statusCode() + " " + body)));

                   }
               });
    }

    /**
     * object key 생성
     * @param memberId
     * @param contentType
     * @return
     */
    private String buildObjectKey(Long memberId, String contentType) {
        String ext = switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "bin";
        };

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "%s/%s/%s.%s".formatted(memberId, date, UUID.randomUUID(), ext);
    }

    /**
     * MiniO에 upload
     * @param bytes
     * @param contentType
     * @param objectKey
     * @return
     */
    private Mono<Void> uploadToMiniO(byte[] bytes, String contentType, String objectKey) {
        return Mono.fromRunnable(() -> {
          try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectKey)
                        .contentType(contentType)
                        .stream(bis, bytes.length, -1)
                        .build());
          } catch (ServerException
                   | InsufficientDataException
                   | ErrorResponseException
                   | IOException
                   | NoSuchAlgorithmException
                   | InvalidKeyException
                   | InvalidResponseException
                   | XmlParserException
                   | InternalException e) {
              throw new MinioException("Image Upload Failed", e);
          }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
