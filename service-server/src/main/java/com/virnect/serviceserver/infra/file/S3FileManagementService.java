package com.virnect.serviceserver.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

import com.virnect.data.domain.file.FileType;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;
import com.virnect.serviceserver.infra.utils.JsonUtil;
import com.virnect.serviceserver.infra.utils.LogMessage;

@Profile({"staging", "production"})
@Component
@RequiredArgsConstructor
public class S3FileManagementService implements IFileManagementService {
    private static final String TAG = S3FileManagementService.class.getSimpleName();

    private String bucketName;
    private String bucketFileName;
    private String bucketProfileName;
    private String bucketRecordName;

    private final AmazonS3 amazonS3Client;

    private final RemoteStorageProperties remoteStorageProperties;
    //private final RemoteServiceConfig remoteServiceConfig;

    /*@Qualifier(value = "remoteServiceConfig")
    @Autowired
    public void setRemoteServiceConfig(RemoteServiceConfig remoteServiceConfig) {
        this.remoteServiceConfig = remoteServiceConfig;
    }*/

    private List<String> fileAllowExtensionList = null;
    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/virnect-remote/";

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
            boolean policyEnabled = remoteStorageProperties.isPolicyEnabled();
            boolean policyLifeCycleEnabled = remoteStorageProperties.getPolicyLifeCycle() > 0;
            if (policyEnabled) {
                String policyLocation = remoteStorageProperties.getPolicyLocation();
                JsonUtil jsonUtil;
                if (policyLocation == null || policyLocation.isEmpty()) {
                    LogMessage.formedInfo(
                            TAG,
                            "initialise aws storage service",
                            "init",
                            "storage policy file can not find, check file or directory path. " +
                                    "try to load policy file from resources directory",
                            policyLocation
                    );

                    InputStream inputStream = getFileFromResourceAsStream();
                    if (inputStream == null) {
                        LogMessage.formedInfo(
                                TAG,
                                "initialise aws storage service",
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
                            "initialise aws storage service",
                            "init",
                            "storage service policy is enabled",
                            String.valueOf(remoteStorageProperties.isEnabled())
                    );
                    fileAllowExtensionList = setFileAllowExtensionList(jsonObject);
                }
            }
        } catch (IOException e) {
            LogMessage.formedError(
                    TAG,
                    "initialise aws storage service",
                    "loadStoragePolicy",
                    "load storage policy failed",
                    e.getMessage()
            );
            fileAllowExtensionList = setDefaultFileAllowExtensionList();
        }

    }

    @PostConstruct
    public void init() {
        if(remoteStorageProperties.isEnabled()) {
            LogMessage.formedInfo(
                    TAG,
                    "initialise aws storage service",
                    "init",
                    "storage service is enabled",
                    String.valueOf(remoteStorageProperties.isEnabled())
            );
            loadStoragePolicy();

            this.bucketName = remoteStorageProperties.getBucketName();
            this.bucketFileName = remoteStorageProperties.getBucketFileName();
            this.bucketProfileName = remoteStorageProperties.getBucketProfileName();
            this.bucketRecordName = remoteStorageProperties.getBucketRecordName();

            LogMessage.formedInfo(
                    TAG,
                    "initialise aws storage service",
                    "init",
                    "S3FileManagementService initialised",
                    "allow extension " + fileAllowExtensionList.toString()
            );
        } else {
            LogMessage.formedInfo(
                    TAG,
                    "initialise aws storage service",
                    "init",
                    "Remote storage service is disabled",
                    ""
            );
        }
    }

    @Override
    public UploadResult upload(MultipartFile file, String dirPath, FileType fileType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. check is file dummy
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

        // 2. check file extension
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

        // file upload create a InputStream for object upload.
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
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

                objectPath.append(dirPath).append(bucketFileName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.PublicRead);
                break;
            }
            case RECORD: {
                objectPath.append(dirPath).append(bucketRecordName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.PublicRead);
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
    public UploadResult uploadProfile(MultipartFile file, String dirPath) {
        // check file is dummy
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
        if (!FILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
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
        if (dirPath == null)
            dirPath = bucketProfileName;

        // file upload with create a InputStream for object upload.
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
        StringBuilder objectPath;
        objectPath = new StringBuilder();
        objectPath.append(dirPath).append("/").append(objectName).append(".").append(fileExtension);
        // Create headers
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String fileUrl = putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                CannedAccessControlList.PublicRead);
        if(fileUrl != null) {
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
        } else {
            LogMessage.formedError(
                    TAG,
                    "profile image upload",
                    "uploadProfile",
                    "Upload error occurred",
                    ""
            );
            return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
        }
    }

    @Override
    public boolean removeObject(String objectPathToName) {
        amazonS3Client.deleteObject(bucketName, objectPathToName);
        return true;
    }

    @Override
    public void deleteProfile(String objectPathToName) {
        if (DEFAULT_ROOM_PROFILE.equals(objectPathToName)) {
            LogMessage.formedInfo(
                    TAG,
                    "profile image delete",
                    "deleteProfile",
                    "do not delete default profile name",
                    objectPathToName
            );
        } else {
            String objectName = objectPathToName.replaceAll(HOST_REGEX, "").replace("\\", "/");
            removeObject(objectName);
            LogMessage.formedInfo(
                    TAG,
                    "profile image delete",
                    "deleteProfile",
                    "for not using profile image anymore",
                    objectName
            );
        }
    }

    @Override
    public void removeBucket(String bucketName, String dirPath, List<String> objects, FileType fileType) {

        boolean policyLifeCycleEnabled = remoteStorageProperties.getPolicyLifeCycle() > 0;

        if(policyLifeCycleEnabled) {
            LogMessage.formedInfo(
                    TAG,
                    "delete bucket objects",
                    "removeBucket",
                    "not support delete object",
                    "life cycle is " + policyLifeCycleEnabled
            );
        } else {
            try {
                String targetBucket = bucketName != null ? bucketName : this.bucketName;
                String targetDir = null;
                switch (fileType) {
                    case FILE:
                        targetDir = this.bucketFileName;
                        break;
                    case RECORD:
                        targetBucket = this.bucketRecordName;
                        break;
                }
                LogMessage.formedInfo(
                        TAG,
                        "delete bucket objects",
                        "removeBucket",
                        "invoked bucket info",
                        targetBucket + "::" + targetDir + "::" + dirPath + targetDir
                );

                ArrayList<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>();
                for (String objectName : objects) {
                    keyVersions.add(new DeleteObjectsRequest.KeyVersion(dirPath + targetDir + "/" + objectName));
                }

                // Delete the objects.
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(targetBucket)
                        .withKeys(keyVersions)
                        .withQuiet(false);

                // Verify that the objects were deleted successfully.
                DeleteObjectsResult deleteObjectsResult = amazonS3Client.deleteObjects(deleteObjectsRequest);
                int successfulDeletes = deleteObjectsResult.getDeletedObjects().size();
                LogMessage.formedInfo(
                        TAG,
                        "delete bucket objects",
                        "removeBucket",
                        "objects successfully deleted",
                        "object size" + "::" + successfulDeletes
                );
            } catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
                LogMessage.formedError(
                        TAG,
                        "delete bucket objects",
                        "removeBucket",
                        "AmazonServiceException occurred",
                        e.getMessage()
                );
            } catch (SdkClientException e) {
                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                e.printStackTrace();
                LogMessage.formedError(
                        TAG,
                        "delete bucket objects",
                        "removeBucket",
                        "SdkClientException occurred",
                        e.getMessage()
                );
            }
        }

    }

    @Override
    public String base64ImageUpload(String base64Image) {
        return null;
    }

    @Override
    public String filePreSignedUrl(String dirPath, String objectName, int expiry, String fileName, FileType fileType) {
        StringBuilder objectPath = new StringBuilder();
        String url = null;
        switch (fileType) {
            case FILE: {
                objectPath.append(dirPath).append(bucketFileName).append("/").append(objectName);
                url = amazonS3Client.getUrl(bucketName, objectPath.toString()).toString();
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
                objectPath.append(dirPath).append(bucketRecordName).append("/").append(objectName);
                url = amazonS3Client.getUrl(bucketName, objectPath.toString()).toString();
                LogMessage.formedInfo(
                        TAG,
                        "download file url",
                        "filePreSignedUrl",
                        "record url result",
                        url
                );
                break;
            }
        }
        return url;
    }

    @Override
    public String filePreSignedUrl(String bucketFolderName, String objectName, int expiry) {
        String url = amazonS3Client.getUrl(bucketName, bucketFolderName + "/" + objectName).toString();
        LogMessage.formedInfo(
                TAG,
                "download file url",
                "filePreSignedUrl",
                "file url result",
                url
        );
        return url;
    }

    /**
     *
     * @param targetFile
     */
    @Deprecated
    private void removeNewFile(File targetFile) {
        targetFile.delete();//log.info("파일이 삭제되었습니다.");
        //log.info("파일이 삭제되지 못했습니다.");
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Deprecated
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    /**
     * Request upload object to aws s3
     * 
     * @param bucketName     The name of an existing bucket to which the new object
     *                       will be uploaded.
     * @param file           multipart file
     * @param fileName       fileName is the The key under which to store the new
     *                       object.
     * @param objectMetadata Represents the object metadata that is stored with
     *                       Amazon S3. This includes custom user-supplied metadata,
     *                       as well as the standard HTTP headers that Amazon S3
     *                       sends and receives (Content-Length, ETag, Content-MD5,
     *                       etc.).
     *
     * @param cannedAcl      Canned access control lists are commonly used access
     *                       control lists (ACL) that can be used as a shortcut when
     *                       applying an access control list to Amazon S3 buckets
     *                       and objects.
     *
     * @return Returns an URL for the object stored in the specified bucket and key.
     */
    private String putObjectToAWSS3(String bucketName, MultipartFile file, String fileName,
            ObjectMetadata objectMetadata, CannedAccessControlList cannedAcl) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    objectMetadata)
                    .withCannedAcl(cannedAcl));

            LogMessage.formedInfo(
                    TAG,
                    "file upload",
                    "putObjectToAWSS3",
                    "complete to upload profile",
                    "originName: " + file.getOriginalFilename() + ", "
                            + "name: " + file.getName() + ", "
                            + "size: " + file.getSize() + ", "
                            + "contentType: " + file.getContentType() + ", "
            );
        } catch (IOException exception) {
            exception.printStackTrace();
            LogMessage.formedError(
                    TAG,
                    "file upload",
                    "putObjectToAWSS3",
                    "Upload error occurred",
                    exception.getMessage()
            );
        }
        return amazonS3Client.getUrl(bucketName, fileName).toString();

    }

    @Deprecated
    private String putS3(String bucketName, File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    @Deprecated
    private String putS3(String bucketName, File uploadFile, String fileName, ObjectMetadata objectMetadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.BucketOwnerRead);
        putObjectRequest.setMetadata(objectMetadata);
        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

}
