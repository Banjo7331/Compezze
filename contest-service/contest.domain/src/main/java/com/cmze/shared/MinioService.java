package com.cmze.shared;

import java.io.InputStream;

public interface MinioService {
    void uploadFile(String name, InputStream stream, long size, String contentType);
}
