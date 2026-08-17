package com.project.documents_service.storage;

import com.project.documents_service.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioStorageProvider implements StorageProvider {

    private static final String PROVIDER = "MINIO";

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public StoredObject store(MultipartFile file, String folder) {
        try {
            ensureBucket();
            String extension = getExtension(file.getOriginalFilename());
            String objectKey = "%s/%s/%s%s".formatted(
                    normalizeFolder(folder),
                    LocalDate.now(),
                    UUID.randomUUID(),
                    extension
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream inputStream = new DigestInputStream(file.getInputStream(), digest)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(objectKey)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(Optional.ofNullable(file.getContentType()).orElse("application/octet-stream"))
                        .build());
            }

            String checksum = HexFormat.of().formatHex(digest.digest());
            return new StoredObject(PROVIDER, objectKey, buildObjectUrl(objectKey), checksum);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot store file to MinIO", e);
        }
    }

    @Override
    public String getPresignedUrl(String storageKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(properties.getBucket())
                    .object(storageKey)
                    .method(Method.GET)
                    .expiry(properties.getPresignedUrlExpirySeconds())
                    .build());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create MinIO presigned URL", e);
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(properties.getBucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(properties.getBucket())
                    .build());
        }
    }

    private String buildObjectUrl(String objectKey) {
        String endpoint = Optional.ofNullable(properties.getPublicEndpoint()).orElse(properties.getEndpoint());
        return endpoint.replaceAll("/+$", "") + "/" + properties.getBucket() + "/" + objectKey;
    }

    private String normalizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "documents";
        }
        return folder.replaceAll("^/+|/+$", "");
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
