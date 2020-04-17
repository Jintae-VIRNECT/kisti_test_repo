package com.virnect.workspace.infra.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"staging","production"})
@Slf4j
@Service
public class S3FileUploadService implements FileUploadService {

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.resource}")
    private String bucketResource;

    @Value("${file.allow-extension}")
    private String allowExtension;

    @Value("${file.url}")
    private String fileUrl;

    private AmazonS3 amazonS3Client;

    public S3FileUploadService(AmazonS3 amazonS3) {
        this.amazonS3Client = amazonS3;
    }

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        if (!allowExtension.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        String uniqueFileName = UUID.randomUUID().toString().replace("-", "");

        String fileName = bucketResource + "/" + uniqueFileName + getFileExtension(multipartFile.getOriginalFilename());

        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;

    }

    private String putS3(File uploadFile, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile).withCannedAcl(CannedAccessControlList.BucketOwnerRead));
            log.info(fileName + " 파일이 AWS S3(" + bucketName +"/"+bucketResource + ")에 업로드 되었습니다.");

        } catch (Exception e) {
            removeNewFile(uploadFile);
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        return fileUrl +"/"+ fileName;
    }

    // 로컬 임시 파일 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    @Override
    public void delete(String fileName) {
        String endPoint = bucketName + "/" + bucketResource;
        amazonS3Client.deleteObject(endPoint, fileName);
        log.info(fileName + " 파일이 AWS S3(" + endPoint + ")에서 삭제되었습니다.");

    }

    @Override
    public String getFileExtension(String originFileName) {
        int lastIndexOf = originFileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        String extension = originFileName.substring(lastIndexOf);
        return extension;
    }

    @Override
    public boolean isAllowFileExtension(String fileExtension) {
        return false;
    }

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
}
