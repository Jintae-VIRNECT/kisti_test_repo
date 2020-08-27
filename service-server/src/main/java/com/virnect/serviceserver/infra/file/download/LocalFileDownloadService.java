package com.virnect.serviceserver.infra.file.download;

import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Profile({"local", "test"})
@Slf4j
@Component
public class LocalFileDownloadService implements IFileDownload {

    @Value("${cms.bucket-name}")
    private String bucketName;
    @Value("${cms.access-key}")
    private String accessKey;
    @Value("${cms.secret-key}")
    private String secretKey;
    @Value("${upload.serverUrl}")
    private String url;
    @Value("${upload.dir}")
    private String path;

    private MinioClient minioClient = null;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            log.info("LocalFileDownloadService initialised");
            minioClient = MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, secretKey)
                    .build();

            boolean isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if(isBucketExist) {
                log.info("Bucket {} is already exist.", bucketName);
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (MinioException e) {
            log.info("Bucket error occured:: {}", e.getMessage());
        }
    }

    @Override
    //public ResponseEntity<byte[]> fileDownload(String path) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    public byte[] fileDownload(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // Get input stream to have content of 'my-objectname' from 'my-bucketname'
        InputStream stream = null;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            log.info("fileDownload input stream:: {}_{}", stream.available(), stream.read());
            //byte[] bytes = IOUtils.toByteArray(stream, stream.available());
            byte[] bytes = IOUtils.toByteArray(stream);
            stream.close();
            return bytes;
            /*HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(bytes.length);
            httpHeaders.setContentDispositionFormData("attachment", "");
            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);*/
        } catch (MinioException e) {
            log.info("Download error occurred:: {}", e.getMessage());
            throw new RestServiceException(ErrorCode.ERR_FILE_DOWNLOAD_FAILED);
        }
    }

    /*private Path load(final String fileName) {
        // Get input stream to have content of 'my-objectname' from 'my-bucketname'
        InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket("my-bucketname")
                .object("my-objectname")
                .build());
        Path rootLocation = Paths.get(uploadPath);
        return rootLocation.resolve(fileName);
    }*/

    @Override
    public void copyFileS3ToLocal(String fileName) {

    }
}
