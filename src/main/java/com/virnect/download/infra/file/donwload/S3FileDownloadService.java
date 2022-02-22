package com.virnect.download.infra.file.donwload;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"staging", "production","local", "develop"})
@Service
public class S3FileDownloadService implements FileDownloadService {

    @Value("${cloud.aws.s3.bucket.name:virnect-download}")
    private String bucketName;

    @Value("${file.upload-path:app/}")
    private String fileUploadPath;

    private AmazonS3 amazonS3Client;

    public S3FileDownloadService(AmazonS3 amazonS3) {
        this.amazonS3Client = amazonS3;
    }

    public byte[] fileDownload(String fileName) throws IOException {
        S3Object object = amazonS3Client.getObject(bucketName, fileUploadPath + fileName);
        S3ObjectInputStream inputStream = object.getObjectContent();
        return IOUtils.toByteArray(inputStream, object.getObjectMetadata().getContentLength());
    }

    @Override
    public String fileUpload(MultipartFile multipartFile, String fileName) throws IOException {
        String objectName = fileUploadPath + fileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setHeader("filename", fileName);
        objectMetadata.setContentDisposition("attachment; filename=\"" + fileName + "\"");

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3Client.putObject(putObjectRequest);
        log.info("FILE UPLOAD SUCCESS! >> bucket : {}, objectName : {}, fileSize : {}", bucketName, objectName, multipartFile.getSize());

        return amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
    }
}
