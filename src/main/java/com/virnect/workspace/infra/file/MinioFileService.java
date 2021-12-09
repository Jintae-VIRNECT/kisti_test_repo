package com.virnect.workspace.infra.file;

import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
@Profile({"local", "develop", "onpremise", "test"})
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.server}")
    private String server;

    @Value("${minio.extension}")
    private String allowExtension;

	@Override
	public String upload(MultipartFile file, String workspaceUUID){
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		String objectName = String.format("workspace/%s/profile/%s", workspaceUUID, uniqueFileName);


        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build();
            log.info("[FILE UPLOAD] Upload File Info >> bucket : {}, object : {}, fileSize(byte) : {}",
                    bucket, objectName,
                    file.getSize()
            );

            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
            String uploadPath = minioClient.getObjectUrl(bucket, objectName);
            log.info("[FILE UPLOAD] Upload Result path : [{}],", uploadPath);
            return uploadPath;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException exception) {
            log.error(exception.getClass().toString());
            log.error(exception.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

    }

    @Override
    public void delete(String fileUrl) {
        String prefix = server + "/" + bucket + "/";
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }
        String[] fileSplit = fileUrl.split(prefix);
        String objectName = fileSplit[fileSplit.length - 1];

        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();

        try {
            if (fileUrl.contains(DefaultImageFile.WORKSPACE_PROFILE_IMG.getName())) {
                log.info(
                        "[FILE DELETE] Not Delete Default File Info >> bucket : {}, object : {}", bucket, objectName
                );
            } else {
                minioClient.removeObject(removeObjectArgs);
                log.info(
                        "[FILE DELETE] Delete File Info >> bucket : {}, object : {}", bucket, objectName
                );
            }

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException | InvalidKeyException | InvalidResponseException | IOException |
                NoSuchAlgorithmException | ServerException | XmlParserException exception) {
            log.error(exception.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
    }

    @Override
    public String getDefaultFileUrl(DefaultImageFile defaultImageFile) {
        String prefix = server + "/" + bucket + "/";
        return prefix + "workspace/" + defaultImageFile.getName();
    }
}