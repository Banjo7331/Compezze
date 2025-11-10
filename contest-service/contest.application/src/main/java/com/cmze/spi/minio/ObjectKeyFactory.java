package com.cmze.spi.minio;

public interface ObjectKeyFactory {

    MediaLocation generateForSubmission(String contestId, String userId, String originalFilename);

    MediaLocation generateForPreview(String contestId, String submissionId);

    MediaLocation generateForTemplate(String contestId, String originalFilename);

    MediaLocation generateForAvatar(String avatarName);

    MediaLocation generateForContestCover(String organizerId, String originalFilename);

    String getPublicBucket();
}
