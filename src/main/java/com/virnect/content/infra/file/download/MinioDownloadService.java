package com.virnect.content.infra.file.download;

import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-10-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"develop", "onpremise","test","local"})
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MinioDownloadService implements FileDownloadService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.bucket-resource}")
    private String bucketResource;

    @Value("${minio.server}")
    private String minioServer;


    @Override
    public ResponseEntity<byte[]> fileDownload(String fileName, String range) {
        try {
            String resourcePath = fileName.split(bucketResource)[1];
            log.info("PARSER - RESOURCE PATH: [{}]", resourcePath);
            String[] resources = resourcePath.split("/");
            for (String url : resources) {
                log.info("PARSER - RESOURCE URL: [{}]", url);
            }
            String objectName = bucketResource + resourcePath;

            Map<String, String> headers = new HashMap<>();
            if (StringUtils.hasText(range)) {
                range = range.trim();
                if (!range.matches("^bytes=\\d*-\\d*$")) {
                    log.error("Invalid Http Range : {}", range);
                    throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
                }
                headers.put("Range", range);
            }
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .extraHeaders(headers)
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            try (GetObjectResponse objectResponse = minioClient.getObject(getObjectArgs)) {
                byte[] bytes = IOUtils.toByteArray((InputStream) objectResponse);
                HttpHeaders httpHeaders = new HttpHeaders();
                String contentRange = objectResponse.headers().get("Content-Range");
                if (StringUtils.hasText(contentRange)) {
                    httpHeaders.set("Content-Range", contentRange);
                }
                httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                httpHeaders.setContentLength(bytes.length);
                httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(resources[1]).build());
                return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                    ServerException | XmlParserException exception) {
                log.error(exception.getMessage());
                throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    @Override
    public void copyFileS3ToLocal(String fileName) {
        try {
            String resourcePath = "contents/" + fileName;
            String objectName = bucketResource + resourcePath;

            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            InputStream inputStream = minioClient.getObject(getObjectArgs);

            File file = new File("upload/" + fileName);

            FileOutputStream fos = new FileOutputStream(file);
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = inputStream.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            inputStream.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFilePath(String bucketResource, String fileName) {
        String objectName = bucketResource + fileName;
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            minioClient.getObject(getObjectArgs);
            return minioServer + "/" + bucketName + "/" + objectName;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                ServerException | XmlParserException | IOException exception) {
            log.error(exception.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }
    }

    @Override
    public MultipartFile getMultipartfile(String fileName) {

        String resourcePath = "contents/" + fileName;
        String objectName = bucketResource + resourcePath;

        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        InputStream inputStream;

        try {
            inputStream = minioClient.getObject(getObjectArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                ServerException | XmlParserException | IOException e) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
        }

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
                return "application/octet-stream";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {

                try {
                    InputStream inputStream = minioClient.getObject(getObjectArgs);
                    return IOUtils.toByteArray(inputStream).length;
                } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                        ServerException | XmlParserException | IOException e) {
                    throw new ContentServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
                }
            }

            @Override
            public byte[] getBytes() throws IOException {
                return IOUtils.toByteArray(inputStream);
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return inputStream;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {

            }
        };
        log.info(
                "[CONVERT INPUTSTREAM TO MULTIPARTFILE] Convert success. uploaded url : [{}], contentType : [{}], file size : [{}], originalFileName : [{}],"
                , getFilePath("contents", fileName)
                , multipartFile.getContentType()
                , multipartFile.getSize()
                , multipartFile.getOriginalFilename()
        );
        return multipartFile;
    }
}
