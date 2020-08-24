package com.virnect.serviceserver.infra.file;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Profile({"local", "test"})
@Slf4j
@Component
public class LocalFileUploadService implements FileUploadService {

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

    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/remote/";
    final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;
    final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        try {
            log.info("LocalFileUploadService initialised");
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
    public String upload(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. 빈 파일 여부 확인
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. 파일 용량 검사
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(), file.getName(), file.getSize());

        String fileName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        log.info("{}, {}, {}", path, fileName, fileExtension);


        String filePath = "";
        // 4. file upload
        // Create a InputStream for object upload.
        //ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
        try {
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType())
                    .build());
            filePath = "";
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
        }

        // 3. 파일 복사
        /*File convertFile = new File(path + fileName + fileExtension);
        log.info("{}", convertFile.getAbsolutePath());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
        }*/

        // 4. 파일 경로 추출
        //String filePath = String.format("%s%s", url, convertFile.getPath()).replace("\\", "/");
        //String filePath = "";
        log.info("SAVE FILE_URL: {}", filePath);
        return filePath;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. 빈 파일 여부 확인
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. 파일 용량 검사
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(), file.getName(), file.getSize());

        //String fileName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        log.info("{}, {}, {}", path, fileName, fileExtension);


        String filePath = null;
        // 4. file upload
        // Create a InputStream for object upload.
        //ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
        try {
            filePath = fileName + file.getOriginalFilename();
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
        }

        // 3. 파일 복사
        /*File convertFile = new File(path + fileName + fileExtension);
        log.info("{}", convertFile.getAbsolutePath());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
        }*/

        // 4. 파일 경로 추출
        //String filePath = String.format("%s%s", url, convertFile.getPath()).replace("\\", "/");
        //String filePath = "";
        log.info("SAVE FILE_URL: {}", filePath);
        return filePath;
    }

    @Override
    public boolean delete(String url) {

        log.info("{}", url.replaceAll(HOST_REGEX, "").replace("\\", "/"));
        File file = new File(url.replaceAll(HOST_REGEX, "").replace('\\', '/'));

        if (file.delete()) {
            log.info("{} 파일이 삭제되었습니다.", file.getName());
            return true;
        } else {
            log.info("{} 파일을 삭제하지 못했습니다.", file.getName());
            return false;
        }
    }

    @Override
    public String getFileExtension(String originFileName) {
        return null;
    }

    @Override
    public boolean isAllowFileExtension(String fileExtension) {
        return false;
    }

    @Override
    public String base64ImageUpload(String base64Image) {
        try {
            byte[] image = Base64.getDecoder().decode(base64Image);
            String randomFileName = String.format("%s_%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase());
            File convertImage = new File(path + randomFileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(convertImage);
            fos.write(image);
            // 4. 파일 경로 추출
            String filePath = String.format("%s%s", url, convertImage.getPath()).replace("\\", "/");
            log.info("SAVE FILE_URL: {}", filePath);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File getFile(String fileUrl) {
        log.info("{}", fileUrl.replaceAll(HOST_REGEX, "").replace("\\", "/"));
        return new File(fileUrl.replaceAll(HOST_REGEX, "").replace('\\', '/'));
    }
}
