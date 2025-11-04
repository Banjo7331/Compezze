package com.cmze.spi.minio;

import com.cmze.shared.MediaRef;
import io.minio.GetObjectResponse;
import io.minio.messages.Item;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public interface MinioService {

    /** Upload przez backend (zapis do MinIO). Rzuca, jeśli obiekt istnieje. */
    MediaRef upload(String bucket, String objectKey, InputStream in, long size, String contentType);

    /** Nadpisanie/replace przez backend (bez sprawdzania istnienia). */
    MediaRef replace(String bucket, String objectKey, InputStream in, long size, String contentType);

    /** Pobranie obiektu strumieniem (zwykle niepotrzebne przy presigned GET, ale dostępne). */
    GetObjectResponse get(String bucket, String objectKey);

    /** Usunięcie obiektu. */
    void delete(String bucket, String objectKey);

    /** Sprawdzenie istnienia obiektu. */
    boolean exists(String bucket, String objectKey);

    /** Podpisany URL do odczytu (np. do <img>/<video>), krótkotrwały. */
    URL presignGet(String bucket, String objectKey, Duration expiry);

    /** (Opcjonalnie) Podpisany URL do uploadu bezpośrednio z frontu. */
    URL presignPut(String bucket, String objectKey, Duration expiry);

    List<Item> listObjects(String bucket, String prefix);
}


