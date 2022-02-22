package com.virnect.serviceserver.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.PutObjectOptions;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;

import com.virnect.data.domain.file.FileType;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.data.infra.utils.JsonUtil;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;


@Profile({"local", "onpremise", "develop"})
@Component
@RequiredArgsConstructor
public class LocalFileManagementService implements IFileManagementService {

    private static final String TAG = LocalFileManagementService.class.getSimpleName();

    private final MinioClient minioClient;

    private String bucketName;
    private String fileBucketName;
    private String profileBucketName;
    private String recordBucketName;

    private boolean policyLifeCycleEnabled;

    private final RemoteServiceConfig remoteServiceConfig;

    //private final RemoteStorageProperties remoteStorageProperties;

    /*@Qualifier(value = "remoteServiceConfig")
    @Autowired
    public void setRemoteServiceConfig(RemoteServiceConfig remoteServiceConfig) {
        this.remoteServiceConfig = remoteServiceConfig;
    }*/

    private List<String> fileAllowExtensionList = null;
    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/virnect-remote/";



    private static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

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

    private Path getPolicyFilePath(String pathProperty) {
        if (pathProperty.endsWith(".json")) {
            // Is file
            return Paths.get(pathProperty);
        } else if (pathProperty.endsWith("/")) {
            // Is folder
            return Paths.get(pathProperty + ".json");
        } else {
            // Is a folder not ending in "/"
            return Paths.get(pathProperty + "/.json");
        }

    }

    private List<String> setFileAllowExtensionList(JsonObject policyObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        fileAllowExtensionList = new ArrayList<>();
        JsonObject resourceObject = policyObject.getAsJsonObject("resource");
        try {
            List<String> audioList = objectMapper.readValue(resourceObject.getAsJsonArray("audio").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(audioList);

            List<String> compressedList = objectMapper.readValue(resourceObject.getAsJsonArray("compressed").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(compressedList);

            List<String> docEtcList = objectMapper.readValue(resourceObject.getAsJsonArray("document_etc").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(docEtcList);

            List<String> docMSList = objectMapper.readValue(resourceObject.getAsJsonArray("document_hwp").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(docMSList);

            List<String> docHWPList = objectMapper.readValue(resourceObject.getAsJsonArray("document_ms").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(docHWPList);

            List<String> docPDFList = objectMapper.readValue(resourceObject.getAsJsonArray("document_pdf").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(docPDFList);

            List<String> executeList = objectMapper.readValue(resourceObject.getAsJsonArray("executable").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(executeList);

            List<String> fileModelList = objectMapper.readValue(resourceObject.getAsJsonArray("file_3d").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(fileModelList);

            List<String> imageList = objectMapper.readValue(resourceObject.getAsJsonArray("image").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(imageList);

            List<String> videoList = objectMapper.readValue(resourceObject.getAsJsonArray("video").toString(), new TypeReference<List<String>>(){});
            fileAllowExtensionList.addAll(videoList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return fileAllowExtensionList;
    }

    private List<String> setDefaultFileAllowExtensionList() {
        fileAllowExtensionList = new ArrayList<>();

        fileAllowExtensionList.addAll(FILE_IMAGE_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_DOCUMENT_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_VIDEO_ALLOW_EXTENSION);

        return fileAllowExtensionList;
    }

    private InputStream getFileFromResourceAsStream() {
        return getClass().getClassLoader().getResourceAsStream("policy/storagePolicy.json");
    }

    @Override
    public void loadStoragePolicy() {
        try {
            boolean policyEnabled = remoteServiceConfig.remoteStorageProperties.isPolicyEnabled();
            this.policyLifeCycleEnabled = remoteServiceConfig.remoteStorageProperties.getPolicyLifeCycle() > 0;
            if (policyEnabled) {
                String policyLocation = remoteServiceConfig.remoteStorageProperties.getPolicyLocation();
                JsonUtil jsonUtil;
                if (policyLocation == null || policyLocation.isEmpty()) {
                    LogMessage.formedInfo(
                            TAG,
                            "initialise local file service",
                            "init",
                            "storage policy file can not find, check file or directory path. " +
                                    "try to load policy file from resources directory",
                        policyLocation
                    );

                    InputStream inputStream = getFileFromResourceAsStream();
                    if (inputStream == null) {
                        LogMessage.formedInfo(
                                TAG,
                                "initialise local file service",
                                "init",
                                "cannot find storage policy file. try to set default policy.",
                                ""
                        );
                        fileAllowExtensionList = setDefaultFileAllowExtensionList();
                    } else {
                        jsonUtil = new JsonUtil();
                        JsonObject jsonObject;
                        jsonObject = jsonUtil.fromInputStreamToJsonObject(inputStream);
                        fileAllowExtensionList = setFileAllowExtensionList(jsonObject);
                        inputStream.close();
                    }
                } else {
                    Path path = getPolicyFilePath(policyLocation);
                    jsonUtil = new JsonUtil();
                    JsonObject jsonObject = jsonUtil.fromFileToJsonObject(path.toAbsolutePath().toString());
                    LogMessage.formedInfo(
                            TAG,
                            "initialise local file service",
                            "init",
                            "storage service policy is enabled",
                            String.valueOf(remoteServiceConfig.remoteStorageProperties.isEnabled())
                    );
                    fileAllowExtensionList = setFileAllowExtensionList(jsonObject);
                }
            }
        } catch (IOException e) {
            LogMessage.formedError(
                    TAG,
                    "initialise local file service",
                    "loadStoragePolicy",
                    "load storage policy failed",
                    e.getMessage()
            );
            fileAllowExtensionList = setDefaultFileAllowExtensionList();
        }
    }

    /*private void setBucketEncryption(String bucketName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            minioClient.setBucketEncryption(
                    SetBucketEncryptionArgs.builder()
                            .bucket(bucketName)
                            .config(SseConfiguration.newConfigWithSseS3Rule())
                            .build());
        } catch (MinioException e) {
            e.printStackTrace();
        }
    }*/

    private void setBucketLifeCycle(String bucketName) {
        // set bucket lifecycle
        /*String lifeCycle =
                "<LifecycleConfiguration>" +
                        "<Rule>" +
                        "<ID>expire-bucket</ID>" +
                        "<Prefix></Prefix>" +
                        "<Status>Disabled</Status>" +
                        "<Expiration>" +
                        "<Days>7</Days>" +
                        "</Expiration>"
                        + "</Rule>" +
                        "</LifecycleConfiguration>";*/

        /*List<LifecycleRule> rules = new LinkedList<>();
        rules.add(new LifecycleRule(
                Status.ENABLED,
                null,
                new Expiration((ZonedDateTime) null, 30, null),
                new RuleFilter("logs/"),
                "rule2",
                null,
                null,
                null
        ));
        LifecycleConfiguration lifecycleConfiguration = new LifecycleConfiguration(rules);
        try {
            minioClient.setBucketLifecycle(
                    SetBucketLifecycleArgs.builder()
                            .bucket(bucketName)
                            .config(lifecycleConfiguration)
                            .build()
            );
        } catch (MinioException e) {
            e.printStackTrace();
        }*/
    }

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeyException {
        if(remoteServiceConfig.remoteStorageProperties.isEnabled()) {
            LogMessage.formedInfo(
                    TAG,
                    "initialise local file service",
                    "init",
                    "storage service is enabled",
                    String.valueOf(remoteServiceConfig.remoteStorageProperties.isEnabled())
            );
            try {
                disableSslVerification();

                loadStoragePolicy();

                this.bucketName = remoteServiceConfig.remoteStorageProperties.getBucketName();
                this.fileBucketName = remoteServiceConfig.remoteStorageProperties.getBucketFileName();
                this.profileBucketName = remoteServiceConfig.remoteStorageProperties.getBucketProfileName();
                this.recordBucketName = remoteServiceConfig.remoteStorageProperties.getBucketRecordName();

                //String accessKey = this.remoteServiceConfig.remoteStorageProperties.getAccessKey();
                //String secretKey = this.remoteServiceConfig.remoteStorageProperties.getSecretKey();
                //String serverUrl = this.remoteServiceConfig.remoteStorageProperties.getServerUrl();
                LogMessage.formedInfo(
                        TAG,
                        "initialise local file service",
                        "init",
                        "LocalFileUploadService initialised",
                        "allow extension " + fileAllowExtensionList.toString()
                );

                /*minioClient = MinioClient.builder()
                        .endpoint(serverUrl)
                        .credentials(accessKey, secretKey)
                        .build();*/

                minioClient.ignoreCertCheck();
                if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                    LogMessage.formedInfo(
                            TAG,
                            "initialise local file service",
                            "init",
                            "Bucket is already exist.",
                            bucketName
                    );
                } else {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }

            } catch (ConnectException e) {
                LogMessage.formedError(
                        TAG,
                        "initialise local file service",
                        "init",
                        "Bucket ConnectException error occurred",
                        e.getMessage()
                );
                remoteServiceConfig.remoteStorageProperties.setEnabled(false);
            } catch (MinioException e) {
                LogMessage.formedError(
                        TAG,
                        "initialise local file service",
                        "init",
                        "Bucket error occurred",
                        e.getMessage()
                );
            } catch (KeyManagementException e) {
                LogMessage.formedError(
                        TAG,
                        "initialise local file service",
                        "init",
                        "KeyManagementException error occurred",
                        e.getMessage()
                );
            } catch (IOException e) {
                LogMessage.formedError(
                        TAG,
                        "initialise local file service",
                        "init",
                        "IOException error occurred",
                        e.getMessage()
                );
            }
        } else {
            LogMessage.formedInfo(
                    TAG,
                    "initialise local file service",
                    "init",
                    "Remote storage service is disabled",
                    ""
            );
        }
    }

    @Override
    public UploadResult upload(MultipartFile file, String dirPath, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // check is file dummy
        if (file.getSize() == 0) {
            LogMessage.formedError(
                    TAG,
                    "file upload",
                    "upload",
                    "this file maybe dummy",
                    String.valueOf(file.getSize())
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())).toLowerCase();
        if (!fileAllowExtensionList.contains(fileExtension)) {
            LogMessage.formedError(
                    TAG,
                    "file upload",
                    "upload",
                    "this file is not unsupported",
                    file.getOriginalFilename()
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // file upload to create a InputStream for object upload.
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
        StringBuilder objectPath = new StringBuilder();
        switch (fileType) {
            case FILE:
            case SHARE: {
                // check file size
                if (file.getSize() > MAX_FILE_SIZE) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "this file size over the max size",
                        String.valueOf(file.getSize())
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_SIZE_LIMIT);
                }
                try {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                        .stream(file.getInputStream(), file.getInputStream().available(), -1)
                        .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "Upload error occurred",
                        e.getMessage()
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
                }
                break;
            }
            case RECORD: {
                try {
                    objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName).append(".mp4");
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                            .stream(file.getInputStream(), file.getInputStream().available(), -1)
                            .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    LogMessage.formedError(
                            TAG,
                            "file upload",
                            "upload",
                            "Upload error occurred",
                            e.getMessage()
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
                }
                break;
            }
            case OBJECT: {
                try {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                        .stream(file.getInputStream(), file.getInputStream().available(), -1)
                        .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "Upload error occurred",
                        e.getMessage()
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
                }
                break;
            }
        }

        LogMessage.formedInfo(
                TAG,
                "file upload",
                "upload",
                "complete to upload file",
                "originName: " + file.getOriginalFilename() + ", "
                        + "name: " + file.getName() + ", "
                        + "size: " + file.getSize() + ", "
                        + "contentType: " + file.getContentType() + ", "
                        + "fileExtension: " + fileExtension + ", "
                        + "dirPath: " + dirPath + ", " + ", "
                        + "objectPath: " + objectPath + ", " + ", "
                        + "objectName: " + objectName
        );
        return new UploadResult(objectName, ErrorCode.ERR_SUCCESS);
    }

    @Override
    public UploadResult objectFileUpload(InputStream inputStream, String dirPath, String fileOriginalName, String objectName, String objectFilePath) throws
        IOException, NoSuchAlgorithmException, InvalidKeyException
    {
        // check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(fileOriginalName)).toLowerCase();
        if (!fileAllowExtensionList.contains(fileExtension)) {
            LogMessage.formedError(
                TAG,
                "file upload",
                "upload",
                "this file is not unsupported",
                fileOriginalName
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }
        // file upload to create a InputStream for object upload.
        StringBuilder objectPath = new StringBuilder();
        //String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
        try {
            objectPath.append(dirPath).append(fileBucketName).append("/").append(FilenameUtils.getBaseName(objectFilePath)).append("/").append(objectName);
                minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectPath.toString())
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("model/gltf-binary")
                    .build());
        } catch (MinioException e) {
            LogMessage.formedError(
                TAG,
                "file upload",
                "upload",
                "Upload error occurred",
                e.getMessage()
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
        }
        return new UploadResult(objectName, ErrorCode.ERR_SUCCESS);
    }

    @Override
    public UploadResult upload(
        MultipartFile file, String dirPath, FileType fileType, String objectName
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // check is file dummy
        if (file.getSize() == 0) {
            LogMessage.formedError(
                TAG,
                "file upload",
                "upload",
                "this file maybe dummy",
                String.valueOf(file.getSize())
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())).toLowerCase();
        if (!fileAllowExtensionList.contains(fileExtension)) {
            LogMessage.formedError(
                TAG,
                "file upload",
                "upload",
                "this file is not unsupported",
                file.getOriginalFilename()
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // file upload to create a InputStream for object upload.
        StringBuilder objectPath = new StringBuilder();
        switch (fileType) {
            case FILE: {
                // check file size
                if (file.getSize() > MAX_FILE_SIZE) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "this file size over the max size",
                        String.valueOf(file.getSize())
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_SIZE_LIMIT);
                }

                try {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                        .stream(file.getInputStream(), file.getInputStream().available(), -1)
                        .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "Upload error occurred",
                        e.getMessage()
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
                }
                break;
            }
            case RECORD: {
                try {
                    objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                    minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                        .stream(file.getInputStream(), file.getInputStream().available(), -1)
                        .contentType(file.getContentType()).build());
                } catch (MinioException e) {
                    LogMessage.formedError(
                        TAG,
                        "file upload",
                        "upload",
                        "Upload error occurred",
                        e.getMessage()
                    );
                    return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
                }
                break;
            }
        }

        LogMessage.formedInfo(
            TAG,
            "file upload",
            "upload",
            "complete to upload file",
            "originName: " + file.getOriginalFilename() + ", "
                + "name: " + file.getName() + ", "
                + "size: " + file.getSize() + ", "
                + "contentType: " + file.getContentType() + ", "
                + "fileExtension: " + fileExtension + ", "
                + "dirPath: " + dirPath + ", " + ", "
                + "objectPath: " + objectPath + ", " + ", "
                + "objectName: " + objectName
        );
        return new UploadResult(objectName, ErrorCode.ERR_SUCCESS);
    }

    @Override
    public UploadResult uploadProfile(MultipartFile file, String dirPath) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // check file is null
        if (file.getSize() == 0) {
            LogMessage.formedError(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "this file maybe dummy",
                    String.valueOf(file.getSize())
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_ASSUME_DUMMY);
        }

        // check file extension
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())).toLowerCase();
        if (!FILE_PROFILE_ALLOW_EXTENSION.contains(fileExtension)) {
            LogMessage.formedError(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "this file is not unsupported",
                    file.getOriginalFilename()
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UNSUPPORTED_EXTENSION);
        }

        // check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            LogMessage.formedError(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "this file size over the max size",
                    String.valueOf(file.getSize())
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_SIZE_LIMIT);
        }

        // check profile directory name or path
        if(dirPath == null) { dirPath = profileBucketName; };

        // file upload with create a InputStream for object upload.
        String fileUrl;
        StringBuilder objectPath = new StringBuilder();
        try {
            String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
            objectPath.append(dirPath).append("/").append(profileBucketName).append("/").append(objectName).append(".").append(fileExtension);
            // Create headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", file.getContentType());
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectPath.toString())
                    .headers(headers)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .contentType(file.getContentType()).build());

            // get file url
            fileUrl = minioClient.getObjectUrl(bucketName, objectPath.toString());
            LogMessage.formedInfo(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "complete to upload profile",
                    "originName: " + file.getOriginalFilename() + ", "
                            + "name: " + file.getName() + ", "
                            + "size: " + file.getSize() + ", "
                            + "contentType: " + file.getContentType() + ", "
                            + "fileExtension: " + fileExtension + ", "
                            + "dirPath: " + dirPath + ", " + ", "
                            + "fileUrl: " + fileUrl + ", "
            );
            return new UploadResult(fileUrl, ErrorCode.ERR_SUCCESS);
        } catch (MinioException e) {
            LogMessage.formedError(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "Upload error occurred",
                    e.getMessage()
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
        }
    }

    @Deprecated
    /*public String uploadPolicyFile(MultipartFile file, String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
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
        *//*
         * File convertFile = new File(path + fileName + fileExtension); log.info("{}",
         * convertFile.getAbsolutePath()); if (convertFile.createNewFile()) { try
         * (FileOutputStream fos = new FileOutputStream(convertFile)) {
         * fos.write(file.getBytes()); } }
         *//*

        // 4. 파일 경로 추출
        // String filePath = String.format("%s%s", url,
        // convertFile.getPath()).replace("\\", "/");
        // String filePath = "";

    }*/

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
    public void deleteProfile(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (DEFAULT_ROOM_PROFILE.equals(objectPathToName)) {
            LogMessage.formedInfo(
                    TAG,
                    "profile image delete",
                    "deleteProfile",
                    "do not delete default profile name",
                    objectPathToName
            );
        } else {
            boolean result;
            String objectName = objectPathToName.replaceAll(HOST_REGEX, "").replace("\\", "/");
            result = removeObject(objectName);
            LogMessage.formedInfo(
                    TAG,
                    "profile image delete",
                    "deleteProfile",
                    "for not using profile image anymore",
                    objectName + "::" + result
            );
        }
    }

    public void removeBucket(String bucketName, String dirPath, List<String> objects, FileType fileType) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(this.policyLifeCycleEnabled) {
            LogMessage.formedInfo(
                    TAG,
                    "delete bucket objects",
                    "removeBucket",
                    "not support delete object",
                    "life cycle is " + this.policyLifeCycleEnabled
            );
        } else {
            try {
                String targetBucket = bucketName != null ? bucketName : this.bucketName;
                String targetDir = null;
                switch (fileType) {
                    case FILE:
                        targetDir = this.fileBucketName;
                        break;
                    case RECORD:
                        targetBucket = this.recordBucketName;
                        break;
                }
                LogMessage.formedInfo(
                        TAG,
                        "delete bucket objects",
                        "removeBucket",
                        "invoked bucket info",
                        targetBucket + "::" + targetDir + "::" + dirPath + targetDir
                );

                List<DeleteObject> deleteObjects = new LinkedList<>();
                for (String objectName : objects) {
                    deleteObjects.add(new DeleteObject(dirPath + targetDir + "/" + objectName));
                }

                Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(targetBucket)
                                .objects(deleteObjects)
                                .build()
                );

                for (Result<DeleteError> result : results) {
                    DeleteError error = result.get();
                    LogMessage.formedError(
                            TAG,
                            "delete bucket objects",
                            "removeBucket",
                            "error delete object",
                            error.objectName() + "::" + error.message()
                    );
                }
            } catch (MinioException e) {
                e.printStackTrace();
            }
        }
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
            //log.info("SAVE FILE_URL: {}", filePath);
            return String.format("%s", convertImage.getPath()).replace("\\", "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String filePreSignedUrl(String dirPath, String objectName, int expiry, String fileName, FileType fileType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            StringBuilder objectPath = new StringBuilder();
            String url = null;
            switch (fileType) {
                case FILE:
                case SHARE: {
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                    url = minioClient.getObjectUrl(bucketName, objectPath.toString());
                    LogMessage.formedInfo(
                        TAG,
                        "download file url",
                        "filePreSignedUrl",
                        "file url result",
                        url
                    );
                    break;
                }
                case RECORD: {
                    objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName).append(".mp4");
                    url = minioClient.getObjectUrl(bucketName, objectPath.toString());
                    LogMessage.formedInfo(
                            TAG,
                            "download file url",
                            "filePreSignedUrl",
                            "record url result",
                            url
                    );
                    break;
                }
                case OBJECT:
                    objectPath.append(dirPath).append(fileBucketName).append("/").append(FilenameUtils.getBaseName(objectName)).append("/").append(objectName);
                    url = minioClient.getObjectUrl(bucketName, objectPath.toString());
                    LogMessage.formedInfo(
                        TAG,
                        "download file url",
                        "filePreSignedUrl",
                        "file url result",
                        url
                    );
                    break;
            }
            return url;
        } catch (MinioException e) {
            LogMessage.formedError(
                    TAG,
                    "download file url",
                    "filePreSignedUrl",
                    "download error occurred",
                    e.getMessage()
            );
            return null;
        }
    }

    @Override
    public String filePreSignedUrl(String bucketFolderName, String objectName, int expiry)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            String url = minioClient.getObjectUrl(bucketName, bucketFolderName + "/" + objectName);
            LogMessage.formedInfo(
                    TAG,
                    "download guide file url",
                    "filePreSignedUrl",
                    "file url result",
                    url
            );
            return url;
        } catch (MinioException e) {
            LogMessage.formedError(
                    TAG,
                    "download guide file url",
                    "filePreSignedUrl",
                    "download error occurred",
                    e.getMessage()
            );
            return null;
        }
    }
}
