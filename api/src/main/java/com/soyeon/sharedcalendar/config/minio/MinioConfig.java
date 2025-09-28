package com.soyeon.sharedcalendar.config.minio;

import com.soyeon.sharedcalendar.common.exception.ErrorCode;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties props;

    @Bean
    public MinioClient minioClient(MinioProperties props) {
        return MinioClient.builder()
                .endpoint(props.getUrl())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }

    @Bean
    public ApplicationRunner minioBucketInitializer(MinioClient minioClient) {
        return args -> {
            try {
                boolean bucketExists = minioClient
                        .bucketExists(BucketExistsArgs.builder()
                                .bucket(props.getBucket())
                                .build());
                log.info("[init] bucket exists: {}", bucketExists);
                if (!bucketExists) {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(props.getBucket())
                            .build());
                }
                log.info("[init] bucket created");
            } catch (ErrorResponseException
                     | InsufficientDataException
                     | InternalException
                     | InvalidKeyException
                     | InvalidResponseException
                     | IOException
                     | NoSuchAlgorithmException
                     | ServerException
                     | XmlParserException e) {
                throw new MinioException(ErrorCode.MINIO_BUCKETS_ERROR, "Bucket Initialized Error", e);
            }
        };
    }
}
