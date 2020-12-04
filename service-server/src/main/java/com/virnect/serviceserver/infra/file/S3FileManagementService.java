package com.virnect.serviceserver.infra.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;
import com.virnect.file.FileType;
import com.virnect.serviceserver.model.UploadResult;
import com.virnect.service.error.ErrorCode;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Profile({"staging", "production"})
@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileManagementService implements IFileManagementService {
    private static final String TAG = S3FileManagementService.class.getSimpleName();

    private String bucketName;
    private String fileBucketName;
    private String profileBucketName;
    private String recordBucketName;

    private final AmazonS3 amazonS3Client;

    private RemoteServiceConfig remoteServiceConfig;

    @Qualifier(value = "remoteServiceConfig")
    @Autowired
    public void setRemoteServiceConfig(RemoteServiceConfig remoteServiceConfig) {
        this.remoteServiceConfig = remoteServiceConfig;
    }

    private List<String> fileAllowExtensionList = new ArrayList<>();
    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/virnect-remote/";
    final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;

    @PostConstruct
    public void init() {
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            LogMessage.formedInfo(
                    TAG,
                    "post construct",
                    "init"
            );
        }
        this.bucketName = this.remoteServiceConfig.remoteStorageProperties.getBucketName();
        this.fileBucketName = this.remoteServiceConfig.remoteStorageProperties.getFileBucketName();
        this.profileBucketName = this.remoteServiceConfig.remoteStorageProperties.getProfileBucketName();
        this.recordBucketName = this.remoteServiceConfig.remoteStorageProperties.getRecordBucketName();

        fileAllowExtensionList.addAll(FILE_IMAGE_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_DOCUMENT_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_VIDEO_ALLOW_EXTENSION);
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
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
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

        // 3. check file size
        /*
         * if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) { throw new
         * RestServiceException(ErrorCode.ERR_FILE_SIZE_LIMIT); }
         */
        /*log.info("UPLOAD FILE::#upload::result => [originName: {}, name: {} , size: {}]",
                file.getOriginalFilename(),
                file.getName(),
                file.getSize());*/
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        //log.info("UPLOAD FILE::#upload::result => [{}, {}]", objectName, fileExtension);

        // 4. file upload
        // Create a InputStream for object upload.
        StringBuilder objectPath = new StringBuilder();
        switch (fileType) {
            case FILE: {
                objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.PublicRead);
                break;
            }
            case RECORD: {
                objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.PublicRead);
                break;
            }
        }
        return new UploadResult(objectName, ErrorCode.ERR_SUCCESS);
    }

    @Override
    public UploadResult uploadProfile(MultipartFile file, String dirPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
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
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
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
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
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
            dirPath = profileBucketName;

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
            return new UploadResult(fileUrl, ErrorCode.ERR_SUCCESS);
        } else {
            return new UploadResult(null, ErrorCode.ERR_FILE_UPLOAD_EXCEPTION);
        }
    }

    @Override
    public boolean removeObject(String objectPathToName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        amazonS3Client.deleteObject(bucketName, objectPathToName);
        return true;
    }

    @Override
    public void deleteProfile(String objectPathToName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (DEFAULT_ROOM_PROFILE.equals(objectPathToName)) {
            log.info("PROFILE REMOVE::#deleteProfile::do not delete default profile name");
        } else {
            String objectName = objectPathToName.replaceAll(HOST_REGEX, "").replace("\\", "/");
            removeObject(objectName);

            log.info("PROFILE REMOVE::#deleteProfile::for not using anymore => [{}]", objectName);
        }
    }

    @Override
    public String base64ImageUpload(String base64Image) {
        return null;
    }

    @Override
    public String filePreSignedUrl(String dirPath, String objectName, int expiry, String fileName, FileType fileType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder objectPath = new StringBuilder();
        String url = null;
        switch (fileType) {
            case FILE: {
                objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                url = amazonS3Client.getUrl(bucketName, objectPath.toString()).toString();
                log.info("DOWNLOAD FILE::#filePreSignedUrl::file result::[{}]", url);
                break;
            }

            case RECORD: {
                objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                url = amazonS3Client.getUrl(bucketName, objectPath.toString()).toString();
                log.info("DOWNLOAD FILE::#filePreSignedUrl::record result::[{}]", url);
                break;
            }
        }
        return url;
    }

    /**
     *
     * @param targetFile
     */
    @Deprecated
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
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
