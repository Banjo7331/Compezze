package com.cmze.spi.minio;

import com.cmze.shared.MediaRef;
import io.minio.GetObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface MinioService {
    public MediaRef upload(String objectKey, InputStream in, long size, String contentType);

    MediaRef replace(String objectKey, InputStream in, long size, String contentType);

    GetObjectResponse get(String objectKey);

    void delete(String objectKey);

    boolean exists(String objectKey);

    URL presignGet(String objectKey, Duration expiry);

    URL presignPut(String objectKey, Duration expiry);

}


