package com.virnect.content.infra.file;

import com.google.common.io.Files;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Project: base
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Local Directory File Upload Service
 */
@Slf4j
@Service
public class LocalUploadService implements FileUploadService {
    @Value("${upload.dir}")
    private String path;
    @Value("${upload.serverUrl}")
    private String url;
    @Value("#{'${upload.allowed-extension}'.split(',')}")
    private List<String> allowedExtension;
    private String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/contents/";

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        return null;
    }

    @Override
    public String upload(MultipartFile multipartFile, String fileName) throws IOException {
        // 1. 빈 파일 여부 확인
        if (multipartFile.getSize() == 0) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

        // 2. 확장자 검사
        String fileExtension = "." + Files.getFileExtension(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if (!allowedExtension.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", multipartFile.getOriginalFilename());
            throw new ContentServiceException(ErrorCode.ERR_UNSUPPORTED_FILE_EXTENSION);
        }

        log.info("{}, {}, {}", path, fileName, fileExtension);

        // 3. 파일 복사
        File convertFile = new File(path + fileName + fileExtension);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
        }

        // 4. 파일 경로 추출
        String filePath = String.format("%s%s", url, convertFile.getPath()).replace("\\", "/");
        log.info("SAVE FILE_URL: {}", filePath);
        return filePath;
    }

    @Override
    public boolean delete(String fileUrl) {
        log.info("{}", fileUrl.replaceAll(HOST_REGEX, "").replace("\\", "/"));
        File file = new File(fileUrl.replaceAll(HOST_REGEX, "").replace('\\', '/'));
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
    public String base64ImageUpload(final String base64Image) {
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
