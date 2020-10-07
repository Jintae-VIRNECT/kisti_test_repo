package com.virnect.serviceserver.infra.file;

import com.google.common.io.Files;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;


@Profile({"local", "onpremise","develop"})
@Slf4j
@Component
public class LocalFileManagementService implements IFileManagementService {

    private String fileBucketName;
    private String profileBucketName;
    private String rootDirPath;

    @Autowired
    private RemoteServiceConfig remoteServiceConfig;

    private List<String> fileAllowExtensionList = new ArrayList<>();

    private MinioClient minioClient = null;

    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/remote/";
    final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            log.info("Remote storage service is enabled");
            this.fileBucketName = this.remoteServiceConfig.remoteStorageProperties.getFileBucketName();
            this.profileBucketName = this.remoteServiceConfig.remoteStorageProperties.getProfileBucketName();
            this.rootDirPath = this.remoteServiceConfig.remoteStorageProperties.getRootDirPath();

            String accessKey = this.remoteServiceConfig.remoteStorageProperties.getAccessKey();
            String secretKey = this.remoteServiceConfig.remoteStorageProperties.getSecretKey();
            String serverUrl = this.remoteServiceConfig.remoteStorageProperties.getServerUrl();

            try {
                fileAllowExtensionList.addAll(FILE_IMAGE_ALLOW_EXTENSION);
                fileAllowExtensionList.addAll(FILE_DOCUMENT_ALLOW_EXTENSION);
                fileAllowExtensionList.addAll(FILE_VIDEO_ALLOW_EXTENSION);

                log.info(LOG_MESSAGE_TAG + "{}", "LocalFileUploadService initialised");
                log.info(LOG_MESSAGE_TAG + "LocalFileUploadService allow extension {}", fileAllowExtensionList);
                minioClient = MinioClient.builder()
                        .endpoint(serverUrl)
                        .credentials(accessKey, secretKey)
                        .build();

                boolean isBucketExist = false;

                //create file bucket
                isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(fileBucketName).build());
                if (isBucketExist) {
                    log.info("Bucket {} is already exist.", fileBucketName);
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(fileBucketName).build());
                }
                // set bucket lifecycle
                String lifeCycle =
                        "<LifecycleConfiguration>" +
                                "<Rule>" +
                                "<ID>expire-bucket</ID>" +
                                "<Prefix></Prefix>" +
                                "<Status>Disabled</Status>" +
                                "<Expiration>" +
                                "<Days>7</Days>" +
                                "</Expiration>"
                                + "</Rule>" +
                                "</LifecycleConfiguration>";

                minioClient.setBucketLifeCycle(SetBucketLifeCycleArgs.builder().bucket(fileBucketName).config(lifeCycle).build());

                //create file bucket
                isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(profileBucketName).build());
                if (isBucketExist) {
                    log.info("Bucket {} is already exist.", profileBucketName);
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(profileBucketName).build());
                }
            } catch (ConnectException e) {
                log.info("Bucket ConnectException error occured:: {}", e.getMessage());
                this.remoteServiceConfig.remoteStorageProperties.setServiceEnabled(false);
            } catch (MinioException e) {
                log.info("Bucket error occured:: {}", e.getMessage());
            }
        } else {
            log.info("Remote storage service is disabled");
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
        if (!fileAllowExtensionList.contains(fileExtension)) {
            log.error(LOG_MESSAGE_TAG + "[UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. 파일 용량 검사
        /*
         * if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) { throw new
         * RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT); }
         */

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(),
                file.getName(), file.getSize());

        String fileName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        log.info("{}, {}, {}", rootDirPath, fileName, fileExtension);

        String filePath = "";
        // 4. file upload
        // Create a InputStream for object upload.
        // ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
        try {
            ObjectWriteResponse objectWriteResponse = minioClient
                    .putObject(PutObjectArgs.builder().bucket(fileBucketName).object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getInputStream().available(), -1)
                            .contentType(file.getContentType()).build());
            filePath = "";
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
        }

        // 3. 파일 복사
        /*
         * File convertFile = new File(path + fileName + fileExtension); log.info("{}",
         * convertFile.getAbsolutePath()); if (convertFile.createNewFile()) { try
         * (FileOutputStream fos = new FileOutputStream(convertFile)) {
         * fos.write(file.getBytes()); } }
         */

        // 4. 파일 경로 추출
        // String filePath = String.format("%s%s", url,
        // convertFile.getPath()).replace("\\", "/");
        // String filePath = "";
        log.info("SAVE FILE_URL: {}", filePath);
        return filePath;
    }

    @Override
    public String upload(MultipartFile file, String dirPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. check is file dummy
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileAllowExtensionList.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. check file size
        /*
         * if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) { throw new
         * RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT); }
         */

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(),
                file.getName(), file.getSize());
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
        log.info("{}, {}, {}", rootDirPath, objectName, fileExtension);

        // 4. file upload
        // Create a InputStream for object upload.
        StringBuilder objectPath = new StringBuilder();
        try {
            objectPath.append(dirPath).append(objectName);
            minioClient.putObject(PutObjectArgs.builder().bucket(fileBucketName).object(objectPath.toString())
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType()).build());
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
            return null;
        }

        log.info("SAVE FILE_URL: {}, {}", objectPath.toString(), file.getContentType());
        return objectName;
        // return objectPath.toString();
    }

    @Override
    public String uploadProfile(MultipartFile file, String dirPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. 빈 파일 여부 확인
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!FILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. 파일 용량 검사
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(),
                file.getName(), file.getSize());
        log.info("{}, {}, {}", rootDirPath, dirPath, fileExtension);

        // 4. file upload
        // Create a InputStream for object upload.
        String fileUrl = null;
        StringBuilder objectPath = new StringBuilder();
        try {
            String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
            // objectPath.append(dirPath).append(PROFILE_DIRECTORY).append("/").append(objectName);
            objectPath.append(dirPath).append("/").append(objectName);
            // filePath = fileName + file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder().bucket(profileBucketName).object(objectPath.toString())
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType()).build());
            // 5. get file url
            fileUrl = minioClient.getObjectUrl(profileBucketName, objectPath.toString());
            log.info("SAVE PROFILE FILE_URL: {}, {}", fileUrl, file.getContentType());
            return fileUrl;
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
            return null;
        }

    }

    @Override
    public String uploadFile(MultipartFile file, String fileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return null;
    }

    @Override
    public String uploadPolicyFile(MultipartFile file, String fileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. 빈 파일 여부 확인
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileAllowExtensionList.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. 파일 용량 검사
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}", file.getOriginalFilename(),
                file.getName(), file.getSize());

        // String fileName = String.format("%s_%s", LocalDate.now(),
        // RandomStringUtils.randomAlphabetic(20));

        log.info("{}, {}, {}", rootDirPath, fileName, fileExtension);
        try {
            // Create new PostPolicy object for 'my-bucketname', 'my-objectname' and 7 days
            // expire time
            // from now.
            PostPolicy policy = new PostPolicy(fileBucketName, fileName, ZonedDateTime.now().plusDays(EXPIRE_DAY));
            // 'my-objectname' should be 'image/png' content type
            policy.setContentType("image/png");
            // set success action status to 201 because we want the client to notify us with
            // the S3 key
            // where the file was uploaded to.
            policy.setSuccessActionStatus(201);
            Map<String, String> formData = minioClient.presignedPostPolicy(policy);

            // Print a curl command that can be executable with the file /tmp/userpic.png
            // and the file
            // will be uploaded.
            log.info(LOG_MESSAGE_TAG + "{}", "curl -X POST ");
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                log.info(LOG_MESSAGE_TAG + "{}", " -F " + entry.getKey() + "=" + entry.getValue());
            }
            log.info(LOG_MESSAGE_TAG + "{}", " -F file=@/tmp/userpic.png https://play.min.io/my-bucketname");

            String filePath = null;
            // 4. file upload
            // Create a InputStream for object upload.
            // ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());

            filePath = fileName + file.getOriginalFilename();
            ObjectWriteResponse objectWriteResponse = minioClient
                    .putObject(PutObjectArgs.builder().bucket(fileBucketName).object(filePath)
                            .stream(file.getInputStream(), file.getInputStream().available(), -1)
                            .contentType(file.getContentType()).build());

            log.info("SAVE FILE_URL: {}, {}", filePath, file.getContentType());
            return filePath;
        } catch (MinioException e) {
            log.info("Upload error occurred:: {}", e.getMessage());
            return null;
        }

        // 3. 파일 복사
        /*
         * File convertFile = new File(path + fileName + fileExtension); log.info("{}",
         * convertFile.getAbsolutePath()); if (convertFile.createNewFile()) { try
         * (FileOutputStream fos = new FileOutputStream(convertFile)) {
         * fos.write(file.getBytes()); } }
         */

        // 4. 파일 경로 추출
        // String filePath = String.format("%s%s", url,
        // convertFile.getPath()).replace("\\", "/");
        // String filePath = "";

    }

    @Override
    public boolean removeObject(String objectPathToName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            minioClient
                    .removeObject(RemoveObjectArgs.builder().bucket(fileBucketName).object(objectPathToName).build());
            return true;
        } catch (MinioException e) {
            e.printStackTrace();
            return false;
        }
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
    public boolean deleteProfile(String url) {
        return false;
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
            String randomFileName = String.format("%s_%s", LocalDate.now().toString(),
                    RandomStringUtils.randomAlphanumeric(10).toLowerCase());
            File convertImage = new File(rootDirPath + randomFileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(convertImage);
            fos.write(image);
            // 4. 파일 경로 추출
            String filePath = String.format("%s%s", rootDirPath, convertImage.getPath()).replace("\\", "/");
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

    @Override
    public byte[] fileDownload(String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // Get input stream to have content of 'my-objectname' from 'my-bucketname'
        InputStream stream = null;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder().bucket(fileBucketName).object(filePath).build());
            log.info("fileDownload input stream:: {}_{}", stream.available(), stream.read());
            byte[] bytes = IOUtils.toByteArray(stream);
            stream.close();
            return bytes;
        } catch (MinioException e) {
            log.info("Download error occurred:: {}", e.getMessage());
            throw new RestServiceException(ErrorCode.ERR_FILE_DOWNLOAD_FAILED);
        }
    }

    @Override
    public String filePreSignedUrl(String objectPathToName, int expiry)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET)
                    .bucket(fileBucketName).object(objectPathToName).expiry(expiry).build());
            log.info("filePreSignedUrl:: {}", url);
            return url;
        } catch (MinioException e) {
            log.info("Download error occurred:: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void copyFileS3ToLocal(String fileName) {

    }
}
