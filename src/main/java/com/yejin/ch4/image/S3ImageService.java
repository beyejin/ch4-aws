package com.yejin.ch4.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3ImageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final String cloudFrontDomain;

    public S3ImageService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.cloudfront.domain}") String cloudFrontDomain
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
        this.cloudFrontDomain = cloudFrontDomain;
    }

    public String uploadProfileImage(Long memberId, MultipartFile file) {
        String key = createProfileImageKey(memberId, file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new IllegalStateException("프로필 이미지 업로드에 실패했습니다.", e);
        }

        return createCloudFrontUrl(key);
    }

    private String createProfileImageKey(Long memberId, String originalFilename) {
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return "members/" + memberId + "/profile-" + UUID.randomUUID() + extension;
    }

    private String createCloudFrontUrl(String key) {
        return "https://" + cloudFrontDomain + "/" + key;
    }

    public String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}