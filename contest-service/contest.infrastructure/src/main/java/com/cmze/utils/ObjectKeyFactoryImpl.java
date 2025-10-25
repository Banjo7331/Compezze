package com.cmze.utils;

import com.cmze.spi.minio.MediaLocation;
import com.cmze.spi.minio.ObjectKeyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ObjectKeyFactoryImpl implements ObjectKeyFactory {

    @Value("${app.media.public.bucket}")
    private String publicBucket;

    @Value("${app.media.private.bucket}")
    private String privateBucket;

    @Override
    public MediaLocation generateForSubmission(String contestId, String userId, String originalFilename) {
        String key = "contests/" + safe(contestId) + "/submissions/" + safe(userId) + "/"
                + UUID.randomUUID() + extOf(originalFilename);
        return new MediaLocation(privateBucket, key);
    }

    @Override
    public MediaLocation generateForPreview(String contestId, String submissionId) {
        String key = "contests/" + safe(contestId) + "/previews/" + safe(submissionId) + "/"
                + UUID.randomUUID() + ".jpg";
        return new MediaLocation(publicBucket, key);
    }

    @Override
    public MediaLocation generateForTemplate(String contestId, String originalFilename) {
        String key = "templates/" + safe(contestId) + "/" + safeFilename(originalFilename);
        return new MediaLocation(publicBucket, key);
    }

    @Override
    public MediaLocation generateForAvatar(String avatarName) {
        String key = "avatars/" + safe(avatarName) + ".webp";
        return new MediaLocation(publicBucket, key);
    }

    /* ===== helpers ===== */

    private static String safe(String v) {
        if (v == null) return "_";
        return v.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private static String extOf(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return (i >= 0) ? filename.substring(i).toLowerCase() : "";
    }

    private static String safeFilename(String filename) {
        if (filename == null) return UUID.randomUUID().toString();
        String base = filename.replace("\\", "/");
        base = base.substring(base.lastIndexOf('/') + 1);
        return base.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
