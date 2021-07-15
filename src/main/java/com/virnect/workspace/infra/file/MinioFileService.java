package com.virnect.workspace.infra.file;

import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Project: PF-Workspace
 * DATE: 2020-09-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"local", "develop", "onpremise"})
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.resource}")
    private String resource;

    @Value("${minio.extension}")
    private String allowExtension;

    @Override
    public String upload(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

        if (!allowExtension.contains(extension)) {
            //log.error("Not Allow File Extension. Request File Extension >> {}", extension);
            //throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String objectName = resource + "/" + uniqueFileName;

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        log.info("[FILE UPLOAD] Upload File Info >> bucket : {}, resource : {}, filename : {}, fileSize(byte) : {}",
                bucket, resource,
                uniqueFileName, file.getSize()
        );
        try {
            bucketExistCheck(bucket);
            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
            String uploadPath = minioClient.getObjectUrl(bucket, objectName);
            log.info("[FILE UPLOAD] Upload Result path : [{}],", uploadPath);
            return uploadPath;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                ServerException | XmlParserException exception) {
            log.error(exception.getClass().toString());
            log.error(exception.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

    }

    @Override
    public void delete(String fileUrl) {
        String fileName = FilenameUtils.getName(fileUrl);
        String objectName = resource + "/" + fileName;
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();

        try {
            if (fileUrl.contains("workspace-profile")) {
                log.info("[FILE DELETE] Not Delete Default File Info >> bucket : {}, resource : {}, filename : {}", bucket, resource, fileName);
            } else {
                minioClient.removeObject(removeObjectArgs);
                log.info("[FILE DELETE] Delete File Info >> bucket : {}, resource : {}, filename : {}", bucket, resource, fileName);
            }

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
                NoSuchAlgorithmException | ServerException | XmlParserException exception) {
            log.error(exception.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        String objectName = resource + "/" + fileName;
        log.info("[FILE FIND] get file url request. file name : {}", fileName);
        //1. 버킷 확인
        bucketExistCheck(bucket);

        //2. object 존재 확인
        boolean objectExist = objectExistCheck(bucket, fileName);

        //3. metadata 경로에 있는 default 파일의 경우 없으면 올린다.
        File file = new File("metadata/" + fileName);

        if (!objectExist && !file.exists()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_FILE);
        }

        if (!objectExist && file.exists()) {
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            String mimeType = mimeTypesMap.getContentType(file);
            try (InputStream inputStream = new FileInputStream(file)) {
                long fileSize = file.length();
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .contentType(mimeType)
                        .stream(inputStream, file.length(), -1)
                        .build();
                log.info(
                        "[FILE FIND] Default file upload Request >> bucket : {}, resource : {}, filename : {}, size : {}, content-type : {}",
                        bucket, resource, fileName, fileSize, mimeType
                );
                minioClient.putObject(putObjectArgs);
                return getObjectUrl(bucket, objectName);
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
                    NoSuchAlgorithmException | ServerException | XmlParserException e) {
                log.error(e.getMessage());
                log.error(e.getClass().toString());
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }
        }
        return getObjectUrl(bucket, objectName);
    }

    private String getObjectUrl(String bucket, String objectName) {
        try {
            return minioClient.getObjectUrl(bucket, objectName);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
                NoSuchAlgorithmException | ServerException | XmlParserException exception) {
            log.error(exception.getMessage());
            log.error(exception.getClass().toString());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

    }

    private boolean objectExistCheck(String bucket, String fileName) {
        try {
            String objectName = resource + "/" + fileName;
            ObjectStat objectStat = minioClient.statObject(bucket, objectName);
            log.info(
                    "[FILE FIND] Find file Info >> bucket : {}, resource : {}, filename : {}, size : {}, content-type : {}",
                    objectStat.bucketName(),
                    resource, fileName, objectStat.length(), objectStat.contentType()
            );
            return true;
        } catch (ErrorResponseException | IllegalArgumentException | InsufficientDataException |
                InternalException | InvalidBucketNameException | InvalidKeyException |
                InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                XmlParserException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    private void bucketExistCheck(String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(bucketName);
            if (!bucketExists) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .objectLock(false)
                        .bucket(bucketName)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
                log.info(
                        "[BUCKET CREATE] Create Bucket success Info >> bucket : {}, objectLock >> {}",
                        makeBucketArgs.bucket(),
                        makeBucketArgs.objectLock()
                );
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | RegionConflictException e) {
            log.error(
                    "[BUCKET CREATE] Create Bucket fail Info >> bucket : {}", bucketName
            );
            log.error(e.getMessage());
            log.error(e.getClass().toString());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);

        }

    }

}