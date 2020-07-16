package com.virnect.content.infra.file.download;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
@Slf4j
@Profile({"local", "develop", "staging", "production", "test"})
@Component
@RequiredArgsConstructor
public class S3FileDownloadService implements FileDownloadService {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.resource}")
    private String bucketResource;

    @Override
    public ResponseEntity<byte[]> fileDownload(String fileName) {
        try {
            String resourcePath = fileName.split(bucketResource)[1];
            log.info("PARSER - RESOURCE PATH: [{}]", resourcePath);
            String[] resources = resourcePath.split("/");
            for (String url : resources) {
                log.info("PARSER - RESOURCE URL: [{}]", url);
            }
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketResource + resourcePath);
            S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(objectInputStream);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", resources[1]);
            objectInputStream.close();

            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    @Override
    public void copyFileS3ToLocal(String fileName) {
        try {
            String resourcePath = "contents/" + fileName;
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketResource + resourcePath);
            S3Object o = amazonS3Client.getObject(getObjectRequest);
            S3ObjectInputStream s3is = o.getObjectContent();


            File file = new File("upload/" + fileName);

            FileOutputStream fos = new FileOutputStream(file);
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
