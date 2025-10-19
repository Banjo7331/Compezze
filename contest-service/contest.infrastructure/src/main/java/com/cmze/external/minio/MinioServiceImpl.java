package com.cmze.external.minio;

import com.cmze.shared.MediaRef;
import com.cmze.spi.minio.MediaProperties;
import com.cmze.spi.minio.MinioProperties;
import com.cmze.spi.minio.MinioService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class MinioServiceImpl implements MinioService {



    private final MinioClient client;
    private final com.cmze.spi.minio.MinioProperties minioProps;
    private final MediaProperties mediaProps;

    public MinioServiceImpl(MinioClient client, MinioProperties minioProps, MediaProperties mediaProps) {
        this.client = client;
        this.minioProps = minioProps;
        this.mediaProps = mediaProps;
    }

    @PostConstruct
    void ensureBucket() {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProps.getDefaultBucket()).build()
            );
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(minioProps.getDefaultBucket()).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć/sprawdzić bucketu: " + minioProps.getDefaultBucket(), e);
        }
    }

    public MediaRef upload(String objectKey, InputStream in, long size, String contentType) {
        validate(contentType, size);
        if (exists(objectKey)) throw new IllegalStateException("Obiekt już istnieje: " + objectKey);
        ObjectWriteResponse resp = putObject(objectKey, in, size, contentType, Map.of(
                "x-amz-meta-kind", mediaKind(contentType)
        ));
        return new MediaRef(minioProps.getDefaultBucket(), objectKey, contentType, size, resp.etag(), resp.versionId());
    }

    public MediaRef replace(String objectKey, InputStream in, long size, String contentType) {
        validate(contentType, size);
        ObjectWriteResponse resp = putObject(objectKey, in, size, contentType, Map.of(
                "x-amz-meta-kind", mediaKind(contentType)
        ));
        return new MediaRef(minioProps.getDefaultBucket(), objectKey, contentType, size, resp.etag(), resp.versionId());
    }

    public GetObjectResponse get(String objectKey) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Pobranie nieudane: " + objectKey, e);
        }
    }

    public void delete(String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Usunięcie nieudane: " + objectKey, e);
        }
    }

    public boolean exists(String objectKey) {
        try {
            client.statObject(StatObjectArgs.builder()
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public URL presignGet(String objectKey, Duration expiry) {
        int seconds = (int) (expiry != null ? expiry.toSeconds() : minioProps.getPresignExpirySeconds());
        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .expiry(seconds)
                    .build());
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException("Presign GET nieudany dla: " + objectKey, e);
        }
    }

    public URL presignPut(String objectKey, Duration expiry) {
        int seconds = (int) (expiry != null ? expiry.toSeconds() : minioProps.getPresignExpirySeconds());
        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .expiry(seconds)
                    .build());
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException("Presign PUT nieudany dla: " + objectKey, e);
        }
    }

    private ObjectWriteResponse putObject(String objectKey, InputStream in, long size, String contentType,
                                          Map<String,String> headers) {
        try {
            PutObjectArgs.Builder b = PutObjectArgs.builder()
                    .bucket(minioProps.getDefaultBucket())
                    .object(objectKey)
                    .stream(in, size, -1)
                    .contentType(contentType);
            if (headers != null && !headers.isEmpty()) b.headers(headers);
            return client.putObject(b.build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException("MinIO error: " + e.errorResponse().message(), e);
        } catch (Exception e) {
            throw new RuntimeException("Zapis nieudany dla: " + objectKey, e);
        }
    }

    private void validate(String contentType, long size) {
        String kind = mediaKind(contentType);
        if ("image".equals(kind)) {
            require(mediaProps.getAllowedImageTypes(), contentType, "Niedozwolony typ obrazka");
            if (size > mediaProps.getMaxImageBytes()) throw new IllegalArgumentException("Za duży obrazek: " + size);
        } else if ("video".equals(kind)) {
            require(mediaProps.getAllowedImageTypes(), contentType, "Niedozwolony typ wideo");
            if (size > mediaProps.getMaxVideoBytes()) throw new IllegalArgumentException("Za duże wideo: " + size);
        } else {
            throw new IllegalArgumentException("Nieobsługiwany contentType: " + contentType);
        }
    }

    private static void require(List<String> allowed, String value, String msg) {
        if (value == null || !allowed.contains(value)) throw new IllegalArgumentException(msg + ": " + value);
    }

    private String mediaKind(String contentType) {
        if (contentType == null) return "unknown";
        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("video/")) return "video";
        return "unknown";
    }
}
