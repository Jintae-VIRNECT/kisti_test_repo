package com.virnect.serviceserver.infra.file;

import com.google.common.io.Files;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;


@Profile({"local", "onpremise", "develop"})
@Slf4j
@Component
public class LocalFileManagementService implements IFileManagementService {

    private String bucketName;
    private String fileBucketName;
    private String profileBucketName;
    private String recordBucketName;

    @Autowired
    private RemoteServiceConfig remoteServiceConfig;

    private List<String> fileAllowExtensionList = new ArrayList<>();

    private MinioClient minioClient = null;

    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/virnect-remote/";
    final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;


    private static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier((s, sslSession) -> true);
        builder.build();
        /*HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);*/
    }



    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            log.info("Remote storage service is enabled");
            try {
                disableSslVerification();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            this.bucketName = this.remoteServiceConfig.remoteStorageProperties.getBucketName();
            this.fileBucketName = this.remoteServiceConfig.remoteStorageProperties.getFileBucketName();
            this.profileBucketName = this.remoteServiceConfig.remoteStorageProperties.getProfileBucketName();
            this.recordBucketName = this.remoteServiceConfig.remoteStorageProperties.getRecordBucketName();

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

                minioClient.ignoreCertCheck();

                boolean isBucketExist = false;

                //create file bucket
                isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (isBucketExist) {
                    log.info("Bucket {} is already exist.", bucketName);
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
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

                minioClient.setBucketLifeCycle(SetBucketLifeCycleArgs.builder().bucket(bucketName).config(lifeCycle).build());

                //create file bucket
                /*isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(profileBucketName).build());
                if (isBucketExist) {
                    log.info("Bucket {} is already exist.", profileBucketName);
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(profileBucketName).build());
                }*/
            } catch (ConnectException e) {
                log.info("Bucket ConnectException error occured:: {}", e.getMessage());
                this.remoteServiceConfig.remoteStorageProperties.setServiceEnabled(false);
            } catch (MinioException e) {
                log.info("Bucket error occured:: {}", e.getMessage());
            } catch (KeyManagementException e) {
                log.info("KeyManagementException error occured:: {}", e.getMessage());
            }
        } else {
            log.info("Remote storage service is disabled");
        }
    }

    @Override
    public String upload(MultipartFile file, String dirPath, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. check is file dummy
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // 2. check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileAllowExtensionList.contains(fileExtension)) {
            log.error("UPLOAD FILE::#upload::unsupported_file [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // 3. check file size
        /*
         * if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) { throw new
         * RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT); }
         */
        log.info("UPLOAD FILE::#upload::result => [originName: {}, name: {} , size: {}]",
                file.getOriginalFilename(),
                file.getName(),
                file.getSize());
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        log.info("UPLOAD FILE::#upload::result => [{}, {}]", objectName, fileExtension);

        // 4. file upload
        // Create a InputStream for object upload.
        StringBuilder objectPath = new StringBuilder();
        switch (fileType) {
            case FILE: {
                try {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                            .stream(file.getInputStream(), file.getInputStream().available(), -1)
                            .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    log.info("UPLOAD FILE::#upload::upload error occurred::exception=> {}", e.getMessage());
                    return null;
                }

                log.info("UPLOAD FILE::#upload:: {}, {}", objectPath.toString(), file.getContentType());
                break;
            }

            case RECORD: {
                try {
                    objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                            .stream(file.getInputStream(), file.getInputStream().available(), -1)
                            .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    log.info("Upload error occurred:: {}", e.getMessage());
                    return null;
                }

                log.info("SAVE FILE_URL: {}, {}", objectPath.toString(), file.getContentType());
                break;
            }
        }

        return objectName;
    }

    @Override
    public String uploadProfile(MultipartFile file, String dirPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // check file is null
        if (file.getSize() == 0) {
            throw new RestServiceException(ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!FILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RestServiceException(ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // check file size
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        // check profile directory name or path
        if(dirPath == null)
            dirPath = profileBucketName;

        log.info("UPLOAD SERVICE: ==> originName: [{}], name: {} , size: {}",
                file.getOriginalFilename(),
                file.getName(),
                file.getSize());

        log.info("{}, {}",dirPath, fileExtension);

        // file upload with create a InputStream for object upload.
        String fileUrl;
        StringBuilder objectPath = new StringBuilder();
        try {
            String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
            objectPath.append(dirPath).append("/").append(objectName).append(".").append(fileExtension);
            // Create headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", file.getContentType());
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                    .headers(headers)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType()).build());

            // get file url
            fileUrl = minioClient.getObjectUrl(bucketName, objectPath.toString());
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

        log.info("{}, {}", fileName, fileExtension);
        try {
            // Create new PostPolicy object for 'my-bucketname', 'my-objectname' and 7 days
            // expire time
            // from now.
            PostPolicy policy = new PostPolicy(bucketName, fileName, ZonedDateTime.now().plusDays(EXPIRE_DAY));
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
                    .putObject(PutObjectArgs.builder().bucket(bucketName).object(filePath)
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
    public boolean removeObject(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectPathToName).build());
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
    public void deleteProfile(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (Default.ROOM_PROFILE.isValueEquals(objectPathToName)) {
            log.info("PROFILE REMOVE::#deleteProfile::do not delete default profile name");
        } else {
            boolean result;
            String objectName = objectPathToName.replaceAll(HOST_REGEX, "").replace("\\", "/");
            result = removeObject(objectName);
            log.info("PROFILE REMOVE::#deleteProfile::for not using anymore::boolean => [{}, {}]", objectName, result);
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
            String randomFileName = String.format("%s_%s", LocalDate.now().toString(),
                    RandomStringUtils.randomAlphanumeric(10).toLowerCase());
            File convertImage = new File( randomFileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(convertImage);
            fos.write(image);
            // 4. 파일 경로 추출
            String filePath = String.format("%s", convertImage.getPath()).replace("\\", "/");
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
            stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(filePath).build());
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
    public String filePreSignedUrl(String dirPath, String objectName, int expiry, String fileName, FileType fileType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create headers
            HttpHeaders httpHeaders = new HttpHeaders();
            //httpHeaders.setContentLength(byteArray.length);
            httpHeaders.setContentDispositionFormData("attachment", fileName);
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

            StringBuilder objectPath = new StringBuilder();
            String url = null;
            switch (fileType) {
                case FILE: {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    //url = minioClient.getObjectUrl(bucketName, objectPath.toString());
                    url = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs
                                    .builder()
                                    .method(Method.GET)
                                    .extraHeaders(httpHeaders.toSingleValueMap())
                                    .bucket(bucketName).object(objectPath.toString())
                                    .expiry(expiry)
                                    .build());
                    log.info("DOWNLOAD FILE::#filePreSignedUrl::file result::[{}]", url);
                    break;
                }

                case RECORD: {
                    objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                    url = minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs
                                    .builder()
                                    .method(Method.GET)
                                    .extraHeaders(httpHeaders.toSingleValueMap())
                                    .bucket(bucketName)
                                    .object(objectPath.toString())
                                    .expiry(expiry)
                                    .build());
                    log.info("DOWNLOAD FILE::#filePreSignedUrl::record result::[{}]", url);
                    break;
                }
            }
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
