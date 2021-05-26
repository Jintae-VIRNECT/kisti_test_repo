package com.virnect.workspace.infra.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"staging", "production"})
@Slf4j
@Service
public class S3FileService implements FileService {

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.resource}")
    private String resource;

    @Value("${cloud.aws.s3.bucket.extension}")
    private String allowExtension;

    private AmazonS3 amazonS3Client;

    public S3FileService(AmazonS3 amazonS3) {
        this.amazonS3Client = amazonS3;
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!allowExtension.contains(extension)) {
            //log.error("Not Allow File Extension. Request File Extension >> {}", extension);
            //throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String objectName = resource + "/" + uniqueFileName;
        log.info("[FILE UPLOAD] Upload File Info >> bucket : {}, resource : {}, filename : {}, fileSize : {}", bucket, resource,
                uniqueFileName, file.getSize()
        );

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setHeader("filename", uniqueFileName);
        objectMetadata.setContentDisposition(String.format("attachment; filename=\"%s\"", uniqueFileName));

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket, objectName, file.getInputStream(), objectMetadata);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3Client.putObject(putObjectRequest);
            String uploadPath = amazonS3Client.getUrl(bucket, objectName).toExternalForm();
            log.info("[FILE UPLOAD] Upload Result path : [{}],", uploadPath);
            return uploadPath;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

    }

    @Override
    public void delete(String fileUrl) {
        String fileName = FilenameUtils.getName(fileUrl);
        String endPoint = bucket + "/" + resource;
        if (fileUrl.contains("workspace-profile")) {
            log.info("Not Delete Default File Info >> bucket : {}, resource : {}, filename : {}", bucket, resource, fileName);
        } else {
            amazonS3Client.deleteObject(endPoint, fileName);
            log.info("Delete File Info >> bucket : {}, resource : {}, filename : {}", bucket, resource, fileName);
        }

    }

    @Override
    public String getFileUrl(String fileName) {
        String objectName = resource + "/" + fileName;
        return amazonS3Client.getUrl(bucket, objectName).toExternalForm();
    }
}
