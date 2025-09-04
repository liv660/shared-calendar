package com.soyeon.sharedcalendar.common.img.app;

import com.soyeon.sharedcalendar.common.exception.ErrorCode;
import com.soyeon.sharedcalendar.common.img.dto.ImgUploadResponse;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.config.minio.MinioException;
import com.soyeon.sharedcalendar.config.minio.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class ImgUploadService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * MiniO에 upload
     * @param bytes
     * @param contentType
     * @param objectKey
     * @return
     */
    public Mono<Void> uploadToMiniO(byte[] bytes, String contentType, String objectKey) {
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
                throw new MinioException(ErrorCode.MINIO_BUCKETS_ERROR, "Image Upload Failed", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    /**
     * presigned url과 objectKey를 생성한다.
     * @param contentType
     * @return
     */
    public ImgUploadResponse getPresignedUrl(String contentType) {
        try {
            Long memberId = SecurityUtils.getCurrentMemberId();
            String bucket = minioProperties.getBucket();
            String objectKey = ObjectKeyGenerator.buildObjectKey(memberId, contentType);
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(60 * 5)
                            .build());
            return new ImgUploadResponse(objectKey, presignedUrl);
        } catch (ServerException
                 | InsufficientDataException
                 | ErrorResponseException
                 | IOException
                 | NoSuchAlgorithmException
                 | InvalidKeyException
                 | InvalidResponseException
                 | XmlParserException
                 | InternalException e) {
            throw new MinioException(ErrorCode.MINIO_PRESIGNED_FAIL, "Fail to create presigned url ", e);
        }
    }
}
