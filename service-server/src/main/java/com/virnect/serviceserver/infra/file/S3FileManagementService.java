package com.virnect.serviceserver.infra.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;
import com.virnect.file.FileType;
import com.virnect.file.IFileManagementService;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.file}")
    private String fileBucketName;

    @Value("${cloud.aws.s3.bucket.profile}")
    private String profileBucketName;

    @Value("${cloud.aws.s3.bucket.record}")
    private String recordBucketName;

    private final AmazonS3 amazonS3Client;

    private List<String> fileAllowExtensionList = new ArrayList<>();
    String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/remote/";
    final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;

    @PostConstruct
    public void init() {
        fileAllowExtensionList.addAll(FILE_IMAGE_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_DOCUMENT_ALLOW_EXTENSION);
        fileAllowExtensionList.addAll(FILE_VIDEO_ALLOW_EXTENSION);
    }

    @Override
    public String upload(MultipartFile file, String dirPath, FileType fileType)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
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
                objectPath.append(dirPath).append(fileBucketName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(bucketName, file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.BucketOwnerRead);
                log.info("UPLOAD FILE::#upload:: {}, {}", objectPath.toString(), file.getContentType());
                break;
            }
            case RECORD: {
                objectPath.append(dirPath).append(recordBucketName).append("/").append(objectName);
                // Create headers
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(file.getContentType());
                objectMetadata.setContentLength(file.getSize());

                putObjectToAWSS3(objectPath.toString(), file, objectPath.toString(), objectMetadata,
                        CannedAccessControlList.BucketOwnerRead);
                log.info("SAVE FILE_URL: {}, {}", objectPath.toString(), file.getContentType());
                break;
            }
        }

        return objectName;
    }

    @Override
    public String uploadProfile(MultipartFile file, String dirPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // check file is dummy
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
        String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
        StringBuilder objectPath;
        objectPath = new StringBuilder();
        objectPath.append(dirPath).append("/").append(objectName).append(".").append(fileExtension);
        // Create headers
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        return putObjectToAWSS3(objectPath.toString(), file, objectName, objectMetadata,
                CannedAccessControlList.BucketOwnerRead);
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return null;
    }

    @Override
    public String uploadPolicyFile(MultipartFile file, String fileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return null;
    }

    @Override
    public boolean removeObject(String objectPathToName)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        amazonS3Client.deleteObject(bucketName, objectPathToName);
        return true;
    }

    @Override
    public boolean delete(String url) {
        return false;
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
    public String getFileExtension(String originFileName) {
        return null;
    }

    @Override
    public boolean isAllowFileExtension(String fileExtension) {
        return false;
    }

    @Override
    public File getFile(String url) {
        return null;
    }

    @Override
    public String base64ImageUpload(String base64Image) {
        return null;
    }

    @Override
    public byte[] fileDownload(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return new byte[0];
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

    @Override
    public void copyFileS3ToLocal(String fileName) {

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
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata)
                    .withCannedAcl(cannedAcl));
        } catch (IOException exception) {
            exception.printStackTrace();
            log.info("Upload error occurred:: {}", exception.getMessage());
        }
        return amazonS3Client.getUrl(bucketName, fileName).toString();

    }

    private void deleteObjectToAWSS3(String bucketName, String fileName) {
        // amazonS3Client.deleteObject();
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
