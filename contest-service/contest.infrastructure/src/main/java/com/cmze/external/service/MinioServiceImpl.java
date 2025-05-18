package com.cmze.external.service;

import com.cmze.shared.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void uploadFile(String bucketName, MultipartFile multipartFile, String filename, String fileType) {
        try {
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                                    inputStream, -1, minioConfig.getFileSize())
                            .contentType(fileType)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    public Iterable<Result<Item>> listObjects(String bucketName) {

        LOGGER.info("MinioUtil | listObjects is called");

        boolean flag = bucketExists(bucketName);

        LOGGER.info("MinioUtil | listObjects | flag : " + flag);

        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }
}
