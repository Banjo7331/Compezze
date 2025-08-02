package com.cmze.controller;

import com.cmze.response.CreateContestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("contest")
public class ContestController {

    @PostMapping()
    public ResponseEntity<CreateContestResponse> createContest(){

    }

    @PostMapping("/{id}/participate")
    public ResponseEntity<JoinContestResponse> joinContest(MultipartFile file, String bucketName) {

        String fileType = FileTypeUtils.getFileType(file);

        LOGGER.info("MinioController | uploadFile | fileType : " + fileType);

        if (fileType != null) {
            return minioService.putObject(file, bucketName, fileType);
        }
        throw new FileResponseException("File cannot be Upload");
    }
}
