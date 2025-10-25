package com.cmze.external.minio;

import com.cmze.shared.MediaRef;
import com.cmze.spi.minio.MinioService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient client;

    // TTL-e z application.properties (bez klas @ConfigurationProperties)
    @Value("${app.media.presigned-get-ttl:10m}")
    private Duration presignedGetTtl;

    @Value("${app.media.presigned-put-ttl:2m}")
    private Duration presignedPutTtl;

    public MinioServiceImpl(MinioClient client) {
        this.client = client;
    }

    // ========= API =========

    @Override
    public MediaRef upload(String bucket, String objectKey, InputStream in, long size, String contentType) {
        ensureBucketExists(bucket);
        // jeśli klucze są unikalne, możesz pominąć exists()
        if (exists(bucket, objectKey)) {
            throw new IllegalStateException("Obiekt już istnieje: " + objectKey);
        }
        ObjectWriteResponse resp = putObject(bucket, objectKey, in, size, contentType, Map.of());
        return new MediaRef(bucket, objectKey, contentType, size, resp.etag(), resp.versionId());
    }

    @Override
    public MediaRef replace(String bucket, String objectKey, InputStream in, long size, String contentType) {
        ensureBucketExists(bucket);
        ObjectWriteResponse resp = putObject(bucket, objectKey, in, size, contentType, Map.of());
        return new MediaRef(bucket, objectKey, contentType, size, resp.etag(), resp.versionId());
    }

    @Override
    public GetObjectResponse get(String bucket, String objectKey) {
        try {
            return client.getObject(
                    GetObjectArgs.builder().bucket(bucket).object(objectKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Pobranie nieudane: " + bucket + "/" + objectKey, e);
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (Exception e) {
            throw new RuntimeException("Usunięcie nieudane: " + bucket + "/" + objectKey, e);
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucket).object(objectKey).build());
            return true;
        } catch (io.minio.errors.ErrorResponseException e) {
            // brak obiektu → false, inne błędy → rzuć dalej
            String code = e.errorResponse().code();
            if ("NoSuchKey".equals(code) || "NotFound".equalsIgnoreCase(code)) return false;
            throw new RuntimeException("exists() failed for " + bucket + "/" + objectKey + ": " + code, e);
        } catch (Exception e) {
            throw new RuntimeException("exists() failed for " + bucket + "/" + objectKey, e);
        }
    }

    @Override
    public URL presignGet(String bucket, String objectKey, Duration expiry) {
        int seconds = toSecondsBounded(expiry != null ? expiry : presignedGetTtl);
        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(seconds)
                    .build());
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException("Presign GET nieudany dla: " + bucket + "/" + objectKey, e);
        }
    }

    // masz w interfejsie – zostawiam, choć nieużywane przy uploadzie przez backend
    @Override
    public URL presignPut(String bucket, String objectKey, Duration expiry) {
        int seconds = toSecondsBounded(expiry != null ? expiry : presignedPutTtl);
        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(seconds)
                    .build());
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException("Presign PUT nieudany dla: " + bucket + "/" + objectKey, e);
        }
    }

    // ========= helpers =========

    private ObjectWriteResponse putObject(String bucket,
                                          String objectKey,
                                          InputStream in,
                                          long size,
                                          String contentType,
                                          Map<String, String> headers) {
        try {
            PutObjectArgs.Builder b = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(in, size, -1)
                    .contentType(contentType);
            if (headers != null && !headers.isEmpty()) b.headers(headers);
            return client.putObject(b.build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException("MinIO error: " + e.errorResponse().message(), e);
        } catch (Exception e) {
            throw new RuntimeException("Zapis nieudany dla: " + bucket + "/" + objectKey, e);
        }
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się utworzyć/sprawdzić bucketu: " + bucket, e);
        }
    }

    private static int toSecondsBounded(Duration d) {
        long s = (d != null ? d.getSeconds() : 600);
        if (s < 1) s = 1;
        // AWS S3 ma limit 7 dni; MinIO akceptuje podobnie – trzymajmy się tego
        long max = 7L * 24 * 3600;
        if (s > max) s = max;
        return (int) s;
    }
}
