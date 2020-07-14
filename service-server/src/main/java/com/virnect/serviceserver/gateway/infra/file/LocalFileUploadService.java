package com.virnect.serviceserver.gateway.infra.file;

import com.google.common.io.Files;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Profile({"local", "test"})
@Slf4j
@Component
public class LocalFileUploadService implements FileUploadService {
    @Value("${upload.dir}")
    private String path;
    @Value("${upload.serverUrl}")
    private String url;

    @Override
    public String upload(MultipartFile file) throws IOException {
        final long MAX_USER_PROFILE_IMAGE_SIZE = 5242880;
        final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

        // 1. 빈 파일 여부 확인
        if (file.getSize() == 0) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_PROFILE_IMAGE_UPLOAD);
        }

        // 2. 확장자 검사
        String fileExtension = Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension)) {
            log.error("[FILE_UPLOAD_SERVICE] [UNSUPPORTED_FILE] [{}]", file.getOriginalFilename());
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_PROFILE_IMAGE_EXTENSION);
        }

        // 3. 파일 용량 검사
        if (file.getSize() >= MAX_USER_PROFILE_IMAGE_SIZE) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_PROFILE_IMAGE_SIZE_LIMIT);
        }

        log.info("UPLOAD SERVICE: ==> originName: [{}] , size: {}", file.getOriginalFilename(), file.getSize());

        String fileName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));

        log.info("{}, {}, {}", path, fileName, fileExtension);

        // 3. 파일 복사
        File convertFile = new File(path + fileName + fileExtension);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
        }

        // 4. 파일 경로 추출
        String filePath = String.format("%s%s", url, convertFile.getPath()).replace("\\", "/");
        log.info("SAVE FILE_URL: {}", filePath);
        return filePath;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws IOException {
        return null;
    }

    @Override
    public boolean delete(String url) {
        String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/users/";
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
    public File getFile(String url) {
        return null;
    }
}
