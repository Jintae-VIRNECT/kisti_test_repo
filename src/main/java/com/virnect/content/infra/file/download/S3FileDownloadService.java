package com.virnect.content.infra.file.download;

import com.amazonaws.services.s3.AmazonS3;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
@Slf4j
@Profile({"staging", "production", "test"})
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
        String resourcePath = fileName.split(bucketResource)[1];
        log.info("PARSER - RESOURCE PATH: [{}]", resourcePath);
        String[] resources = resourcePath.split("/");
        for (String url : resources) {
            log.info("PARSER - RESOURCE URL: [{}]", url);
        }
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketResource + resourcePath);
        try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
             S3ObjectInputStream objectInputStream = s3Object.getObjectContent()) {
            byte[] bytes = IOUtils.toByteArray(objectInputStream, s3Object.getObjectMetadata().getContentLength());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", resources[1]);
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error Message:     {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    @Override
    public void copyFileS3ToLocal(String fileName) {
        String resourcePath = "contents/" + fileName;
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketResource + resourcePath);
        File file = new File("upload/" + fileName);
        try (S3Object o = amazonS3Client.getObject(getObjectRequest);
             S3ObjectInputStream s3is = o.getObjectContent();
             FileOutputStream fos = new FileOutputStream(file)) {

            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
        } catch (Exception e) {
            log.error("Error Message:     {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    public MultipartFile getMultipartfile(String fileName) {
        String resourcePath = "contents/" + fileName;
        String key = bucketResource + resourcePath;
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        try (S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
             S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();) {
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "content";
                }

                @Override
                public String getOriginalFilename() {
                    return fileName;
                }

                @Override
                public String getContentType() {
                    return s3Object.getObjectMetadata().getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return s3Object.getObjectMetadata().getContentLength();
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return IOUtils.toByteArray(s3ObjectInputStream);
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return s3ObjectInputStream;
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                }
            };
            log.info(
                    "[CONVERT INPUTSTREAM TO MULTIPARTFILE] Convert success. uploaded url : [{}], contentType : [{}], file size : [{}], originalFileName : [{}],"
                    , amazonS3Client.getUrl(bucketName, key).toExternalForm()
                    , multipartFile.getContentType()
                    , multipartFile.getSize()
                    , multipartFile.getOriginalFilename()
            );
            return multipartFile;
        } catch (IOException e) {
            log.error("Error Message:     {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }


    }

    @Override
    public String getFilePath(String bucketResource, String fileName) {
        log.info("[GET FILE PATH] Request BucketResource >> [{}], fileName >> [{}]", bucketResource, fileName);
        String objectName = bucketResource + fileName;
        String filePath = amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
        log.info("[GET FILE PATH] Response file Path >> [{}]", filePath);
        return filePath;

    }
}
