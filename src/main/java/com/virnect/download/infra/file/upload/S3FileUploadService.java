package com.virnect.download.infra.file.upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Profile(value = {"develop", "staging", "production"})
@RequiredArgsConstructor
public class S3FileUploadService implements FileUploadService {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;
    @Value("${file.upload-path}")
    private String fileUploadPath;
    @Value("#{'${file.allowed-extension}'.split(',')}")
    private List<String> allowedExtension;

    @Override
    public String upload(MultipartFile file) throws IOException {
        log.info("[AWS S3 UPLOADER] - UPLOAD BEGIN");
        if (file.getSize() <= 0) {
            throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_EMPTY_APPLICATION_FILE);
        }

        // 1. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!allowedExtension.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT);
        }

        // 2. 파일 메타데이터 생성
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setHeader("filename", file.getOriginalFilename());

        // 3. 스트림으로 aws s3에 업로드
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileUploadPath+file.getOriginalFilename(), file.getInputStream(), objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
            log.info("[AWS S3 UPLOADER] - UPLOAD END");
            String url = amazonS3Client.getUrl(bucketName, fileUploadPath+file.getOriginalFilename()).toExternalForm();
            log.info("[AWS S3 RESOURCE URL: {}]", url);
            return url;
        } catch (AmazonServiceException e) {
            log.error("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            log.error("Error Message:     {}", e.getMessage());
            log.error("HTTP Status Code:  {}", e.getStatusCode());
            log.error("AWS Error Code:    {}", e.getErrorCode());
            log.error("Error Type:        {}", e.getErrorType());
            log.error("Request ID:        {}", e.getRequestId());
            throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
        }
    }

    @Override
    public boolean delete(String url) {
        return false;
    }
}
