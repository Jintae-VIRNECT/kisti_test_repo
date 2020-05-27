package com.virnect.content.infra.file.upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.io.Files;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Project: base
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Profile({"local", "develop", "staging", "production"})
@Component
@RequiredArgsConstructor
public class S3UploadService implements FileUploadService {
    private static String CONTENT_DIRECTORY = "contents";
    private static String REPORT_DIRECTORY = "report";
    private static String REPORT_FILE_EXTENSION = ".jpg";


    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.resource}")
    private String bucketResource;

    @Value("#{'${upload.allowed-extension}'.split(',')}")
    private List<String> allowedExtension;

    private final AmazonS3 amazonS3Client;

    @Override
    public String upload(MultipartFile file) {
        return null;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException {
        log.info("[AWS S3 UPLOADER] - UPLOAD BEGIN");
        if (file.getSize() <= 0) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
        String fileExtension = String.format(".%s", Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));

        if (!allowedExtension.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
        }

        File uploadFile = convert(file)
                .orElseThrow(() -> {
                    log.info("MultipartFile -> File 변환 실패");
                    return new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
                });

        String saveFileName = String.format("%s%s/%s%s", bucketResource, CONTENT_DIRECTORY, fileName, fileExtension);
        String uploadFileUrl = putS3(uploadFile, saveFileName);

        removeNewFile(uploadFile);

        return uploadFileUrl;
    }

    @Override
    public boolean delete(String url) {
        return true;
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
        try {
            byte[] image = Base64.getDecoder().decode(base64Image);
            String randomFileName = String.format("%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(), REPORT_FILE_EXTENSION);
            File convertImage = new File(randomFileName);
            FileOutputStream fos = new FileOutputStream(convertImage);
            fos.write(image);
            fos.close();

            String saveFileName = String.format("%s%s/%s", bucketResource, REPORT_DIRECTORY, randomFileName);
            String uploadFileUrl = putS3(convertImage, saveFileName);

            removeNewFile(convertImage);

            return uploadFileUrl;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }

    // 로컬 임시 파일 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    // 이미지 전송 요청을 받아 로컬 파일로 변환
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
     * AWS S3 이미지 업로드 요청 전송
     *
     * @param uploadFile - 업로드 대상 파일
     * @param fileName   - 파일 이름
     * @return 이미지 URL
     */
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }
}
