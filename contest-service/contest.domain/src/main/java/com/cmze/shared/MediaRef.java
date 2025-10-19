package com.cmze.shared;

public record MediaRef(String bucket, String objectKey, String contentType, long bytes,
                       String etag, String versionId) {}
