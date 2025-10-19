package com.cmze.spi.minio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "media")
public class MediaProperties {

    @NotBlank
    private long maxImageBytes;

    @NotBlank
    private long maxVideoBytes;

    @Size(min = 1)
    private List<String> allowedImageTypes = new ArrayList<>();

    @Size(min = 1)
    private List<String> allowedVideoTypes = new ArrayList<>();


}
