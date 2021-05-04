package com.virnect.content.infra.file.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"local", "develop", "onpremise","test"})
@Component
@RequiredArgsConstructor
public class MinioUploadService implements FileUploadService {
    private final MinioClient minioClient;

    private static final String CONTENT_DIRECTORY = "contents";
    private static final String REPORT_DIRECTORY = "report";
    private static final String REPORT_FILE_EXTENSION = ".png";
    private static final String VTARGET_FILE_NAME = "virnect_target.png";

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.bucket-resource}")
    private String bucketResource;

    @Value("#{'${upload.allowed-extension}'.split(',')}")
    private List<String> allowedExtension;

    @Value("${minio.server}")
    private String minioServer;


    @Override
    public boolean delete(String url) {
        if (url.equals("default") || FilenameUtils.getName(url).equals(VTARGET_FILE_NAME)) {
            log.info("기본 이미지는 삭제하지 않습니다. FILE PATH >> [{}]", url);
        } else {
            String objectName = bucketResource + CONTENT_DIRECTORY + "/" + FilenameUtils.getName(url);
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            try {
                minioClient.removeObject(removeObjectArgs);
                log.info(FilenameUtils.getName(url) + " 파일이 삭제되었습니다.");
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException |
                    NoSuchAlgorithmException | ServerException | XmlParserException exception) {
                log.error(exception.getMessage());
                //throw new ContentServiceException(ErrorCode.ERR_DELETE_CONTENT);
            }
        }
        return true;
    }

    @Override
    public String base64ImageUpload(String base64Image) {
        try {
            byte[] image = Base64.getDecoder().decode(base64Image);
            String randomFileName = String.format(
                    "%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
                    REPORT_FILE_EXTENSION
            );

            String objectName = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);

            InputStream inputStream = new ByteArrayInputStream(image);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .stream(inputStream, image.length, -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            log.info("[MINIO FILE INPUT STREAM UPLOADER] - UPLOAD END");
            String url = minioServer + "/" + bucketName + "/" + objectName;
            log.info("[RESOURCE URL: {}]", url);
            return url;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

    }

    @Override
    public String uploadByFileInputStream(MultipartFile file, String fileName) throws IOException {
        // 1. 파일 크기 확인
        log.info("[FILE INPUT STREAM UPLOADER] - UPLOAD FILE SIZE >> " + file.getSize());
        if (file.getSize() <= 0) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

        // 2. 파일 확장자 확인
        String fileExtension = String.format(
                ".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));

        if (!allowedExtension.contains(fileExtension)) {
            log.error("[AWS S3 FILE INPUT STREAM UPLOADER] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
        }

        String objectName = String.format("%s%s/%s%s", bucketResource, CONTENT_DIRECTORY, fileName, fileExtension);
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            minioClient.putObject(putObjectArgs);
            return minioServer + "/" + bucketName + "/" + objectName;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                ServerException | XmlParserException exception) {
            log.error(exception.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }

    @Override
    public String copyByFileObject(String sourceFileName, String destinationFileName) {
        String sourceObjectName = String.format("%s%s/%s", bucketResource, CONTENT_DIRECTORY, sourceFileName);
        String destinationObjectName = String.format("%s%s/%s", bucketResource, CONTENT_DIRECTORY, destinationFileName);
        log.info("[COPY FILE REQUEST] SOURCE : {}, DESTINATION : {}", sourceObjectName, destinationObjectName);
        CopySource copySource = CopySource.builder()
                .bucket(bucketName)
                .object(sourceObjectName)
                .build();
        CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(destinationObjectName)
                .source(copySource)
                .build();
        try {
            minioClient.copyObject(copyObjectArgs);
            return minioServer + "/" + bucketName + "/" + destinationObjectName;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException exception) {
            log.error(exception.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }
}
